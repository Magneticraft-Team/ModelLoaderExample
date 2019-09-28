package com.cout970.modelloaderexample;

import com.cout970.modelloader.api.IRenderCache;
import com.cout970.modelloader.api.ItemTransforms;
import com.cout970.modelloader.api.ModelConfig;
import com.cout970.modelloader.api.ModelRegisterEvent;
import com.cout970.modelloader.api.ModelRetrieveEvent;
import com.cout970.modelloader.api.Utilities;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.tileentity.TileEntityType.Builder;
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
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

@Mod(TileBlock.MOD_ID)
public class TileBlock {

    public static final String MOD_ID = "tile_block";

    public TileBlock() {
        // We subscribe the mod to the event ModelRegisterEvent to register models
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerModels);
        // We subscribe the mod to the event ModelRetrieveEvent to get the models back
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::retrieveModels);
        // The FMLClientSetupEvent is fired only in the client, we use it to access ClientRegistry
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClient);
    }

    private void onClient(final FMLClientSetupEvent event) {
        // The TileEngineRenderer get registered to be able to render instances of TileEngine
        ClientRegistry.bindTileEntitySpecialRenderer(TileEngine.class, new TileEngineRenderer());
    }

    private void retrieveModels(final ModelRetrieveEvent event) {
        // For each TileEntityRenderer we add the models to be rendered
        for (BaseTileEntityRenderer renderer : BaseTileEntityRenderer.RENDERERS) {
            renderer.loadModels(event);
        }
    }

    private void registerModels(final ModelRegisterEvent event) {
        // Basic config with most defaults
        ModelConfig baseModel = new ModelConfig(
            new ResourceLocation(MOD_ID + ":models/engine.gltf"),
            ItemTransforms.BLOCK_DEFAULT
        );

        // We register the item model, which is the same, 'inventory' is used to target the item model
        event.registerModel(MOD_ID, "engine", "inventory", baseModel);

        // We share the item model for the block states so the particles are correct
        event.shareModel(MOD_ID, "engine", "inventory", MOD_ID, "engine", "facing=down");
        event.shareModel(MOD_ID, "engine", "inventory", MOD_ID, "engine", "facing=up");
        event.shareModel(MOD_ID, "engine", "inventory", MOD_ID, "engine", "facing=north");
        event.shareModel(MOD_ID, "engine", "inventory", MOD_ID, "engine", "facing=south");
        event.shareModel(MOD_ID, "engine", "inventory", MOD_ID, "engine", "facing=east");
        event.shareModel(MOD_ID, "engine", "inventory", MOD_ID, "engine", "facing=west");
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MOD_ID)
    public static class RegistryEvents {

        /**
         * Reference to the Engine block
         */
        private static Block ENGINE = new EngineBlock(Block.Properties.create(Material.IRON));
        /**
         * Reference to the TileEngine type
         */
        private static TileEntityType<TileEngine> TILE_ENGINE = Builder.create(TileEngine::new, ENGINE).build(null);

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

        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
            // Sets the TileEntityType registry name, if we don't add it the game crashes
            TILE_ENGINE.setRegistryName(MOD_ID, "engine");
            // We register the TileEntityType
            event.getRegistry().register(TILE_ENGINE);
        }
    }

    /**
     * We create a custom block class to add a blockstate for each direction and override createNewTileEntity to return
     * instances of TileEngine
     * <p>
     * The block extends ContainerBlock to reuse TileEntity related functionalities
     */
    public static class EngineBlock extends ContainerBlock {

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
         * Overrides the light opacity of the block
         */
        @Override
        public float func_220080_a(BlockState state, IBlockReader worldIn, BlockPos pos) {
            return 1.0F;
        }

        /**
         * Makes the block look like glass
         */
        @Override
        public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
            return true;
        }

        /**
         * Disable suffocation when the player is partially inside the block
         */
        @Override
        public boolean causesSuffocation(BlockState state, IBlockReader worldIn, BlockPos pos) {
            return false;
        }

        /**
         * Mark that the block is not a solid opaque cube
         */
        @Override
        public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
            return false;
        }

        /**
         * Disable mobs spawn on top
         */
        @Override
        public boolean canEntitySpawn(BlockState state, IBlockReader worldIn, BlockPos pos, EntityType<?> type) {
            return false;
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

        @Nullable
        @Override
        public TileEntity createNewTileEntity(@NotNull IBlockReader worldIn) {
            return RegistryEvents.TILE_ENGINE.create();
        }
    }

    /**
     * The TileEntity that can store extra information of the block, If you implement ITickableTileEntity the
     * tick(method)
     */
    public static class TileEngine extends TileEntity {

        public TileEngine() {
            super(RegistryEvents.TILE_ENGINE);
        }
    }

    /**
     * The TileEntityRenderer is called every tick to render the TileEntity.
     * <p>
     * We need this for animations, because block models are static, they can only change when the chunk is rebuilt, the
     * rebuild takes time, but once is done rendering the chunk is very efficient.
     * <p>
     * TileEntityRenderers are a lot more flexible, but less efficient (they technically can be better than chunk
     * rendering if you know how a few tricks)
     */
    public static class TileEngineRenderer extends BaseTileEntityRenderer<TileEngine> {

        private IRenderCache model;

        public void loadModels(final ModelRetrieveEvent event) {
            // Retrieves the item model from the event
            IBakedModel model = event.getModel(MOD_ID, "engine", "inventory");
            // Make sure the model is not null, you may want a more sophisticated method, this is just an example
            Objects.requireNonNull(model);

            // We free the memory of the old model
            if (this.model != null) {
                this.model.close();
            }
            // We cache the model to render it more efficiently
            this.model = Utilities.cacheModel(model);
        }

        @Override
        public void render(TileEngine te, float partialTicks, int destroyStage) {
            // We get the facing of the block
            Direction dir = te.getBlockState().get(EngineBlock.FACING);
            // We rotate the model using the block rotation
            Utilities.rotateAroundCenter(dir);
            // We rotate the model to look up (because the model looks towards east)
            Utilities.customRotate(0, 0, -90, Utilities.BLOCK_CENTER);
            // We finally render the model
            model.render();
        }
    }

    /**
     * Base class for TileEntityRenderer removes most boilerplate code
     */
    public static abstract class BaseTileEntityRenderer<T extends TileEntity> extends TileEntityRenderer<T> {

        // List of all TileEntityRenderers
        public static List<BaseTileEntityRenderer> RENDERERS = new ArrayList<>();

        public BaseTileEntityRenderer() {
            RENDERERS.add(this);
        }

        // Function called to update models
        public abstract void loadModels(final ModelRetrieveEvent event);

        // Function called every tick to render the model
        public abstract void render(T te, float partialTicks, int destroyStage);

        @Override
        public void render(T te, double x, double y, double z, float partialTicks, int destroyStage) {
            // Sets the default state to render the model
            // Set color override to default
            GlStateManager.color4f(1f, 1f, 1f, 1f);
            // Stores current transformation matrix
            GlStateManager.pushMatrix();
            // We move the model to his place
            GlStateManager.translated(x, y, z);

            // Set the correct mode for shading
            if (Minecraft.isAmbientOcclusionEnabled()) {
                GlStateManager.shadeModel(GL11.GL_SMOOTH);
            } else {
                GlStateManager.shadeModel(GL11.GL_FLAT);
            }

            // Render the model
            render(te, partialTicks, destroyStage);

            // Restores the matrix saved at pushMatrix()
            GlStateManager.popMatrix();
        }
    }
}
