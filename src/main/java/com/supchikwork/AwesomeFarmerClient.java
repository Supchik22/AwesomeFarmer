package com.supchikwork;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BeetrootsBlock;
import net.minecraft.block.CropBlock;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.block.TorchflowerBlock;
import net.minecraft.block.PitcherCropBlock;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class AwesomeFarmerClient implements ClientModInitializer {
    public boolean isFarmerModeEnabled = false;
    private final KeyBinding toggleFarmerMode = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.awesomefarmer.farmer_mode",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "category.awesomefarmer"
    ));





    private final Map<Block, Predicate<BlockState>> CROPS = Map.of(
            Blocks.BEETROOTS, state -> state.get(BeetrootsBlock.AGE) != 3,
            Blocks.CARROTS, state -> state.get(CropBlock.AGE) != 7,
            Blocks.COCOA, state -> state.get(CocoaBlock.AGE) != 2,
            Blocks.NETHER_WART, state -> state.get(NetherWartBlock.AGE) != 3,
            Blocks.POTATOES, state -> state.get(CropBlock.AGE) != 7,
            Blocks.SWEET_BERRY_BUSH, state -> state.get(SweetBerryBushBlock.AGE) != 3,
            Blocks.WHEAT, state -> state.get(CropBlock.AGE) != 7,
            Blocks.TORCHFLOWER, state -> state.get(TorchflowerBlock.AGE) != 2,
            Blocks.PITCHER_CROP, state -> state.get(PitcherCropBlock.AGE) != 4
    );

    private final Map<Block, BiPredicate<BlockPos, World>> TALL_PLANTS = Map.of(
            Blocks.BAMBOO, (pos, world) -> world.getBlockState(pos.down(1)).getBlock() != world.getBlockState(pos).getBlock(),
            Blocks.CACTUS, (pos, world) -> world.getBlockState(pos.down(1)).getBlock() != world.getBlockState(pos).getBlock(),
            Blocks.KELP, (pos, world) -> {
                    if (world.getBlockState(pos.down(1)).getBlock() == world.getBlockState(pos).getBlock()) {
                        return false;
                    }

                    return world.getBlockState(pos.down(1)).getBlock() != Blocks.KELP_PLANT;
                },
            Blocks.KELP_PLANT, (pos, world) -> world.getBlockState(pos.down(1)).getBlock() != world.getBlockState(pos).getBlock(),
            Blocks.SUGAR_CANE, (pos, world) -> world.getBlockState(pos.down(1)).getBlock() != world.getBlockState(pos).getBlock()
    );

    private final List<Block> CUCURBITS = List.of(
            Blocks.PUMPKIN,
            Blocks.MELON
    );

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(new AwesomeFarmerOverlay(this));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (this.toggleFarmerMode.wasPressed() && client.currentScreen == null) {
                this.isFarmerModeEnabled = !this.isFarmerModeEnabled;
            }
        });

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (this.isFarmerModeEnabled) {
                final BlockState state = world.getBlockState(pos);
                final Block block = state.getBlock();

                if (CROPS.containsKey(block) || TALL_PLANTS.containsKey(block)) {
                    Predicate<BlockState> cropPredicate = CROPS.get(block);
                    BiPredicate<BlockPos, World> tallPlantPredicate = TALL_PLANTS.get(block);

                    if ((cropPredicate != null && cropPredicate.test(state)) || tallPlantPredicate != null && tallPlantPredicate.test(pos, world)) {
                        return ActionResult.FAIL;
                    } else {
                        return ActionResult.PASS;
                    }
                } else if (CUCURBITS.contains(block)) {
                    return ActionResult.PASS;
                }

                return ActionResult.FAIL;
            }

            return ActionResult.PASS;
        });
    }
}
