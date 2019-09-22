package com.cout970.modelloaderexample;

import com.cout970.modelloader.api.ItemTransforms;
import com.cout970.modelloader.api.ModelConfig;
import com.cout970.modelloader.api.ModelRegisterEvent;
import java.util.Objects;
import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SimpleBlock.MOD_ID)
public class SimpleBlock {

    public static final String MOD_ID = "simple_block";

    public SimpleBlock() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerModels);
    }

    private void registerModels(final ModelRegisterEvent event) {
        ModelConfig model = new ModelConfig(
            new ResourceLocation(MOD_ID + ":models/engine.gltf"),
            ItemTransforms.BLOCK_DEFAULT,
            ModelRotation.X90_Y90 // The original model looks towards east, so we apply an extra X90_Y90 rotation
        );

        event.registerModel(MOD_ID, "engine", "", model);
        event.registerModel(MOD_ID, "engine", "inventory", model);
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MOD_ID)
    public static class RegistryEvents {

        private static Block ENGINE = new AbstractGlassBlock(Block.Properties.create(Material.IRON)){};

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
}
