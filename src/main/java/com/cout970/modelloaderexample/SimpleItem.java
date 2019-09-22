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
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerModels);
    }

    private void registerModels(final ModelRegisterEvent event) {
        ModelConfig itemModel = new ModelConfig(
            new ResourceLocation(MOD_ID + ":models/staff.gltf"),
            ItemTransforms.TOOL_DEFAULT
        );

        event.registerModel(MOD_ID, "staff", "inventory", itemModel);
        event.registerModel(MOD_ID, "staff_normal", "inventory",
                                       itemModel.withItemTransforms(ItemTransforms.ITEM_DEFAULT)
        );
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MOD_ID)
    public static class RegistryEvents {

        private static Item STAFF = new Item(new Item.Properties().group(ItemGroup.REDSTONE));
        private static Item STAFF_NORMAL = new Item(new Item.Properties().group(ItemGroup.REDSTONE));

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
            STAFF.setRegistryName(MOD_ID, "staff");
            event.getRegistry().register(STAFF);
            STAFF_NORMAL.setRegistryName(MOD_ID, "staff_normal");
            event.getRegistry().register(STAFF_NORMAL);
        }
    }
}
