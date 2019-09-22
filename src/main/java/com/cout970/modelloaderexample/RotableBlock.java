package com.cout970.modelloaderexample;

import com.cout970.modelloader.api.ItemTransforms;
import com.cout970.modelloader.api.ModelConfig;
import com.cout970.modelloader.api.ModelRegisterEvent;
import java.util.Objects;
import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RotableBlock.MOD_ID)
public class RotableBlock {

    public static final String MOD_ID = "rotable_block";

    public RotableBlock() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerModels);
    }

    private void registerModels(final ModelRegisterEvent event) {
        ModelConfig baseModel = new ModelConfig(
            new ResourceLocation(MOD_ID + ":models/engine.gltf"),
            ItemTransforms.BLOCK_DEFAULT
        );

        for (Direction dir : Direction.values()) {
            String name = "facing=" + dir.toString().toLowerCase();
            // The original model looks towards east, so we apply an extra X90_Y90 rotation
            ModelConfig model = baseModel.withDirection(dir, ModelRotation.X90_Y90);

            event.registerModel(MOD_ID, "engine", name, model);
        }
        event.registerModel(MOD_ID, "engine", "inventory", baseModel);
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MOD_ID)
    public static class RegistryEvents {

        private static Block ENGINE = new OrientableBlock(Block.Properties.create(Material.IRON));

        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
            ENGINE.setRegistryName(MOD_ID, "engine");
            event.getRegistry().register(ENGINE);
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
            Item engineItem = new BlockItem(ENGINE, new Item.Properties().group(ItemGroup.REDSTONE));
            engineItem.setRegistryName(Objects.requireNonNull(ENGINE.getRegistryName()));
            event.getRegistry().register(engineItem);
        }
    }

    public static class OrientableBlock extends AbstractGlassBlock {

        public static final DirectionProperty FACING = DirectionalBlock.FACING;

        public OrientableBlock(Properties properties) {
            super(properties);
            setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
        }

//        @Override
//        public boolean isSolid(BlockState state) {
//            return false;
//        }

        @Override
        public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
            return VoxelShapes.empty();
        }

        /**
         * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the
         * passed blockstate.
         */
        @Override
        public BlockState rotate(BlockState state, Rotation rot) {
            return state.with(FACING, rot.rotate(state.get(FACING)));
        }

        /**
         * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
         * blockstate.
         */
        @Override
        public BlockState mirror(BlockState state, Mirror mirrorIn) {
            return state.rotate(mirrorIn.toRotation(state.get(FACING)));
        }

        /**
         * Returns the block state (with rotation) to place based on the player looking direction
         */
        public BlockState getStateForPlacement(BlockItemUseContext context) {
            return this.getDefaultState().with(FACING, context.getNearestLookingDirection());
        }

        /**
         * Register all the properties for the state container
         */
        @Override
        protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
            builder.add(FACING);
        }
    }
}
