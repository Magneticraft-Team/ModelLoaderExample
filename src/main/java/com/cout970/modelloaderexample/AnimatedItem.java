package com.cout970.modelloaderexample;

import com.cout970.modelloader.animation.AnimatedModel;
import com.cout970.modelloader.api.ItemTransforms;
import com.cout970.modelloader.api.ModelConfig;
import com.cout970.modelloader.api.ModelRegisterEvent;
import com.cout970.modelloader.api.ModelRetrieveEvent;
import com.cout970.modelloader.api.Utilities;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AnimatedItem.MOD_ID)
public class AnimatedItem {

    public static final String MOD_ID = "animated_item";

    public AnimatedItem() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerModels);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::retrieveModels);
    }

    private void retrieveModels(final ModelRetrieveEvent event) {
        TileStaffRenderer.INSTANCE.loadModels(event);
    }

    private void registerModels(final ModelRegisterEvent event) {
        ModelConfig itemModel = new ModelConfig(
            new ResourceLocation(MOD_ID + ":models/staff.gltf"),
            ItemTransforms.BLOCK_DEFAULT
        ).withAnimation(true)
            .withItemRenderer(true)
            .withBake(false);

        event.registerModel(MOD_ID, "staff", "inventory", itemModel);
        event.registerModel(MOD_ID, "staff_normal", "inventory",
                            itemModel.withItemTransforms(ItemTransforms.ITEM_DEFAULT)
        );
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MOD_ID)
    public static class RegistryEvents {

        private static Item STAFF = new Item(
            new Item.Properties()
                .group(ItemGroup.REDSTONE)
                .setTEISR(() -> () -> TileStaffRenderer.INSTANCE));

        private static Item STAFF_NORMAL = new Item(
            new Item.Properties()
                .group(ItemGroup.REDSTONE)
                .setTEISR(() -> () -> TileStaffRenderer.INSTANCE));

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
            STAFF.setRegistryName(MOD_ID, "staff");
            event.getRegistry().register(STAFF);
            STAFF_NORMAL.setRegistryName(MOD_ID, "staff_normal");
            event.getRegistry().register(STAFF_NORMAL);
        }
    }

    public static class TileStaffRenderer extends ItemStackTileEntityRenderer {

        private static final TileStaffRenderer INSTANCE = new TileStaffRenderer();
        private AnimatedModel model;

        public void loadModels(final ModelRetrieveEvent event) {
            Map<String, AnimatedModel> animations = event.getAnimations(MOD_ID, "staff", "inventory");
            this.model = animations.get("Rotation");
            Objects.requireNonNull(this.model);
        }

        @Override
        public void renderByItem(ItemStack itemStackIn) {
            float now = Utilities.worldTime();
            model.render(now / 20f);
        }
    }
}
