package com.cout970.modelloaderexample;

import com.cout970.modelloader.api.ItemTransforms;
import com.cout970.modelloader.api.ModelConfig;
import com.cout970.modelloader.api.ModelRegisterEvent;
import java.util.Objects;
import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SimpleBlock.MOD_ID)
public class SimpleBlock {

    public static final String MOD_ID = "simple_block";

    public SimpleBlock() {
        // We subscribe the mod to the event ModelRegisterEvent to register models
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerModels);
    }

    private void registerModels(final ModelRegisterEvent event) {
        // Basic config with most defaults
        //  First argument is the location of the model file
        //  Second argument is the transformation to apply on the item
        //  Third argument is an additional rotation, since the model was made looking towards east,
        //      we rotate it to look upwards.
        ModelConfig model = new ModelConfig(
            new ResourceLocation(MOD_ID + ":models/engine.gltf"),
            ItemTransforms.BLOCK_DEFAULT,
            ModelRotation.X90_Y90
        );

        // We register the block model, empty string is used for the default block state
        event.registerModel(MOD_ID, "engine", "", model);
        // We register the item model, which is the same, 'inventory' is used to target the item model
        event.registerModel(MOD_ID, "engine", "inventory", model);
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MOD_ID)
    public static class RegistryEvents {

        /**
         * Reference to the Engine block, which extends AbstractGlassBlock to be considered transparent
         */
        private static Block ENGINE = new AbstractGlassBlock(Block.Properties.create(Material.IRON)) {
            /**
             * Makes the block transparent so other block sides don't become invisible.
             */
            @Override
            public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
                return VoxelShapes.empty();
            }
        };

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
            // We use the same registry name as the block, and make sure it's not null
            engineItem.setRegistryName(Objects.requireNonNull(ENGINE.getRegistryName()));
            // We register the item
            event.getRegistry().register(engineItem);
        }
    }
}
