package com.cout970.modelloaderexample;

import com.cout970.modelloader.api.ItemTransforms;
import com.cout970.modelloader.api.ModelConfig;
import com.cout970.modelloader.api.ModelRegisterEvent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SimpleItem.MOD_ID)
public class SimpleItem {

    public static final String MOD_ID = "simple_item";

    public SimpleItem() {
        // We subscribe the mod to the event ModelRegisterEvent to register models
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerModels);
    }

    private void registerModels(final ModelRegisterEvent event) {
        // Basic config with most defaults
        //  First argument is the location of the model file
        //  Second argument is the transformation to apply on the item
        ModelConfig itemModel = new ModelConfig(
            new ResourceLocation(MOD_ID + ":models/staff.gltf"),
            ItemTransforms.BLOCK_DEFAULT
        );

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
         */
        private static Item STAFF = new Item(new Item.Properties().group(ItemGroup.REDSTONE));
        /**
         * Reference to the alternative Staff item
         */
        private static Item STAFF_NORMAL = new Item(new Item.Properties().group(ItemGroup.REDSTONE));

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
}
