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
        // We subscribe the mod to the event ModelRegisterEvent to register models
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerModels);
    }

    private void registerModels(final ModelRegisterEvent event) {
        // Basic config with most defaults
        ModelConfig baseModel = new ModelConfig(
            new ResourceLocation(MOD_ID + ":models/engine.gltf"),
            ItemTransforms.BLOCK_DEFAULT
        );

        // For each direction we register a different model
        // Note: different models using the same model file are loaded only once from disk
        for (Direction dir : Direction.values()) {
            // We get the name of this state
            String stateName = "facing=" + dir.toString().toLowerCase();

            // We create a copy of the model config with a variation in the rotation,
            // the function 'withDirection' automatically applies the correct rotation
            // for each direction, the original model looks towards east, so we apply
            // an extra X90_Y90 rotation
            ModelConfig model = baseModel.withDirection(dir, ModelRotation.X90_Y90);

            // We register this block model using the correct state name
            event.registerModel(MOD_ID, "engine", stateName, model);
        }
        // We register the item model, which is the same, 'inventory' is used to target the item model
        event.registerModel(MOD_ID, "engine", "inventory", baseModel);
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MOD_ID)
    public static class RegistryEvents {

        /**
         * Reference to the Engine block
         */
        private static Block ENGINE = new EngineBlock(Block.Properties.create(Material.IRON));

        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
            // Sets the Block registry name, if we don't add it the game crashes
            ENGINE.setRegistryName(MOD_ID, "engine");
            // We register the block
            event.getRegistry().register(ENGINE);
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
            // We create a BlockItem to be able to place the block in inventories
            Item engineItem = new BlockItem(ENGINE, new Item.Properties().group(ItemGroup.REDSTONE));
            // We use the same registry name as the block, and make sure itś not null
            engineItem.setRegistryName(Objects.requireNonNull(ENGINE.getRegistryName()));
            // We register the item
            event.getRegistry().register(engineItem);
        }
    }

    /**
     * We create a custom block class to add a blockstate for each direction.
     * The block extends AbstractGlassBlock to be considered transparent and
     * render the model on the empty space
     */
    public static class EngineBlock extends AbstractGlassBlock {

        /**
         * The rotation property, stores the direction that the engine is facing
         */
        public static final DirectionProperty FACING = DirectionalBlock.FACING;

        public EngineBlock(Properties properties) {
            super(properties);
            // Default state used in the inventory
            setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
        }

        /**
         * Makes the block transparent so other block sides don't become invisible.
         */
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
