package com.cout970.modelloaderexample;

import com.cout970.modelloader.animation.AnimatedModel;
import com.cout970.modelloader.api.ItemTransforms;
import com.cout970.modelloader.api.ModelConfig;
import com.cout970.modelloader.api.ModelRegisterEvent;
import com.cout970.modelloader.api.ModelRetrieveEvent;
import com.cout970.modelloader.api.Utilities;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.texture.AtlasTexture;
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

@Mod(AnimatedBlock.MOD_ID)
public class AnimatedBlock {

    public static final String MOD_ID = "animated_block";

    public AnimatedBlock() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerModels);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::retrieveModels);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClient);
    }

    private void onClient(final FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEngine.class, new TileEngineRenderer());
    }

    private void retrieveModels(final ModelRetrieveEvent event){
        for (BaseTileEntityRenderer renderer : BaseTileEntityRenderer.RENDERERS) {
            renderer.loadModels(event);
        }
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
        event.registerModel(MOD_ID, "engine", "inventory", baseModel.withAnimation(true));
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MOD_ID)
    public static class RegistryEvents {

        private static Block ENGINE = new EngineBlock(Block.Properties.create(Material.IRON));
        private static TileEntityType<TileEngine> TILE_ENGINE = Builder.create(TileEngine::new, ENGINE).build(null);

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

        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
            TILE_ENGINE.setRegistryName(MOD_ID, "engine");
            event.getRegistry().register(TILE_ENGINE);
        }
    }

    /**
     * Engine block example
     */
    public static class EngineBlock extends ContainerBlock {

        public static final DirectionProperty FACING = DirectionalBlock.FACING;

        public EngineBlock(Properties properties) {
            super(properties);
            setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
        }

        @Override
        public float func_220080_a(BlockState state, IBlockReader worldIn, BlockPos pos) {
            return 1.0F;
        }

        @Override
        public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
            return true;
        }

        @Override
        public boolean causesSuffocation(BlockState state, IBlockReader worldIn, BlockPos pos) {
            return false;
        }

        @Override
        public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
            return false;
        }

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
     * The TileEntity where stuff happens
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

        private AnimatedModel model;

        public void loadModels(final ModelRetrieveEvent event) {
            Map<String, AnimatedModel> animations = event.getAnimations(MOD_ID, "engine", "inventory");
            this.model = animations.get("animation");
            Objects.requireNonNull(this.model);
        }

        @Override
        public void render(TileEngine te, float partialTicks, int destroyStage) {
            Direction dir = te.getBlockState().get(EngineBlock.FACING);
            Utilities.rotateAroundCenter(dir);
            Utilities.customRotate(0, 0, -90, Utilities.BLOCK_CENTER);
            float now = Utilities.worldTime(te.getWorld(), partialTicks);
//            GlStateManager.scalef(1/16f, 1/16f, 1/16f);
//            GlStateManager.scalef(1f, 1/16f, 1/16f);
            bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            model.render(now / 20f);
        }
    }

    public static abstract class BaseTileEntityRenderer<T extends TileEntity> extends TileEntityRenderer<T> {

        public static List<BaseTileEntityRenderer> RENDERERS = new ArrayList<>();

        public BaseTileEntityRenderer() {
            RENDERERS.add(this);
        }

        public abstract void loadModels(final ModelRetrieveEvent event);

        public abstract void render(T te, float partialTicks, int destroyStage);

        @Override
        public void render(T te, double x, double y, double z, float partialTicks, int destroyStage) {

            GlStateManager.color4f(1f, 1f, 1f, 1f);
            GlStateManager.pushMatrix();
            GlStateManager.translated(x, y, z);

            if (Minecraft.isAmbientOcclusionEnabled()) {
                GlStateManager.shadeModel(GL11.GL_SMOOTH);
            } else {
                GlStateManager.shadeModel(GL11.GL_FLAT);
            }

            render(te, partialTicks, destroyStage);

            GlStateManager.popMatrix();
        }
    }
}
