package com.cout970.modelloaderexample;

import com.cout970.modelloader.ModelManager;
import com.cout970.modelloader.api.ItemTransforms;
import com.cout970.modelloader.api.PreLoadModel;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("simple_block")
public class SimpleBlock {

    public SimpleBlock() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClient);
    }

    private void onClient(final FMLClientSetupEvent event) {
        PreLoadModel blockModel = new PreLoadModel(
            new ModelResourceLocation("simple_block:engine", ""),
            new ResourceLocation("simple_block:models/engine.gltf"),
            ItemTransforms.BLOCK_DEFAULT
        );
        PreLoadModel itemModel = blockModel.withModelId(
            new ModelResourceLocation("simple_block:engine", "inventory")
        );

        ModelManager.INSTANCE.register(blockModel);
        ModelManager.INSTANCE.register(itemModel);
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = "simple_block")
    public static class RegistryEvents {

        private static Block ENGINE = new Block(Block.Properties.create(Material.IRON));

        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
            ENGINE.setRegistryName("simple_block", "engine");
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
