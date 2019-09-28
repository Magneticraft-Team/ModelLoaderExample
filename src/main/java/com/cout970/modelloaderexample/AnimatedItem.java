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
        // We subscribe the mod to the event ModelRegisterEvent to register models
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerModels);
        // We subscribe the mod to the event ModelRetrieveEvent to get the models back
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::retrieveModels);
    }

    private void retrieveModels(final ModelRetrieveEvent event) {
        // In the TileEntityRenderer we add the models to be rendered
        TileStaffRenderer.INSTANCE.loadModels(event);
    }

    private void registerModels(final ModelRegisterEvent event) {
        // Basic config with most defaults
        ModelConfig itemModel = new ModelConfig(
            new ResourceLocation(MOD_ID + ":models/staff.gltf"),
            ItemTransforms.BLOCK_DEFAULT
        );
        // We enable the load of animations and enable the model to be rendered with a ItemRenderer
        itemModel = itemModel.withAnimation(true).withItemRenderer(true);

        // We register the item model, 'inventory' is used to target the item model
        event.registerModel(MOD_ID, "staff", "inventory", itemModel);
        // We register the item model, 'inventory' is used to target the item model
        // withItemTransforms makes a copy changing the ItemTransforms
        event.registerModel(MOD_ID, "staff_normal", "inventory",
                            itemModel.withItemTransforms(ItemTransforms.ITEM_DEFAULT)
        );
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MOD_ID)
    public static class RegistryEvents {

        /**
         * Reference to the Staff item
         * <p>
         * setTEISR registers the TileStaffRenderer as renderer for the item, the model must have itemRenderer in order
         * to work
         */
        private static Item STAFF = new Item(
            new Item.Properties()
                .group(ItemGroup.REDSTONE)
                .setTEISR(() -> () -> TileStaffRenderer.INSTANCE));

        /**
         * Reference to the alternative Staff item
         * <p>
         * setTEISR registers the TileStaffRenderer as renderer for the item, the model must have itemRenderer in order
         * to work
         */
        private static Item STAFF_NORMAL = new Item(
            new Item.Properties()
                .group(ItemGroup.REDSTONE)
                .setTEISR(() -> () -> TileStaffRenderer.INSTANCE));

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
            // Sets the Item registry name, if we don't add it the game crashes
            STAFF.setRegistryName(MOD_ID, "staff");
            // We register the item
            event.getRegistry().register(STAFF);

            // Sets the Item registry name, if we don't add it the game crashes
            STAFF_NORMAL.setRegistryName(MOD_ID, "staff_normal");
            // We register the item
            event.getRegistry().register(STAFF_NORMAL);
        }
    }

    /**
     * The ItemStackTileEntityRenderer is called every tick to render the item.
     */
    public static class TileStaffRenderer extends ItemStackTileEntityRenderer {

        private static final TileStaffRenderer INSTANCE = new TileStaffRenderer();
        private AnimatedModel model;

        public void loadModels(final ModelRetrieveEvent event) {
            // Retrieves all the animations from the event
            Map<String, AnimatedModel> animations = event.getAnimations(MOD_ID, "staff", "inventory");

            // We free the memory of the old model
            if (this.model != null) {
                this.model.close();
            }

            // We choose which animation to render
            this.model = animations.get("Rotation");
            // Make sure the model is not null, you may want a more sophisticated method, this is just an example
            Objects.requireNonNull(this.model);
        }

        @Override
        public void renderByItem(ItemStack itemStackIn) {
            // Current time (wrapped), that can be used for animations
            float now = Utilities.worldTime();
            // We finally render the model, the time is in ticks, we divide it by 20 to convert it to seconds
            model.render(now / 20f);
        }
    }
}
