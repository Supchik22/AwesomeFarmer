package com.supchikwork;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.BeetrootsBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.block.CocoaBlock;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class AwesomeFarmerClient implements ClientModInitializer {

    private final Map<Block, Predicate<BlockState>> PLANTS = Map.of(
            Blocks.WHEAT, state -> state.get(CropBlock.AGE) != 7,
            Blocks.CARROTS, state -> state.get(CropBlock.AGE) != 7,
            Blocks.POTATOES, state -> state.get(CropBlock.AGE) != 7,
            Blocks.BEETROOTS, state -> state.get(BeetrootsBlock.AGE) != 3,
            Blocks.NETHER_WART, state -> state.get(NetherWartBlock.AGE) != 3,
            Blocks.SWEET_BERRY_BUSH, state -> state.get(SweetBerryBushBlock.AGE) != 3,
            Blocks.COCOA, state -> state.get(CocoaBlock.AGE) != 2
    );

    private final Map<Block, BiPredicate<BlockPos, World>> TALL_PLANTS = Map.of(
            Blocks.SUGAR_CANE, (pos, world) -> world.getBlockState(pos.down(1)).getBlock() != world.getBlockState(pos).getBlock(),
            Blocks.BAMBOO, (pos, world) -> world.getBlockState(pos.down(1)).getBlock() != world.getBlockState(pos).getBlock(),
            Blocks.CACTUS, (pos, world) -> world.getBlockState(pos.down(1)).getBlock() != world.getBlockState(pos).getBlock(),
            Blocks.KELP_PLANT, (pos, world) -> world.getBlockState(pos.down(1)).getBlock() != world.getBlockState(pos).getBlock(),
            Blocks.KELP, (pos, world) -> world.getBlockState(pos.down(1)).getBlock() != Blocks.KELP_PLANT
    );

    @Override
    public void onInitializeClient() {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            final BlockState state = world.getBlockState(pos);
            final Block block = state.getBlock();

            if (PLANTS.containsKey(block) && PLANTS.get(block).test(state) && !player.isSneaking()) {
                return ActionResult.FAIL;
            }

            if (TALL_PLANTS.containsKey(block) && TALL_PLANTS.get(block).test(pos, world) && !player.isSneaking()) {
                return ActionResult.FAIL;
            }

            return ActionResult.PASS;
        });
    }
}