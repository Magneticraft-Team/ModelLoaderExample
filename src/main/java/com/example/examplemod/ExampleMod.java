package com.example.examplemod;

import com.cout970.modelloader.api.ModelLoaderApi;
import com.example.examplemod.features.blockstateless_block.BlockstatelessBlock;
import com.example.examplemod.features.simple_block_model.SimpleBlock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.Logger;

@Mod(
    modid = ExampleMod.MOD_ID,
    name = ExampleMod.NAME,
    version = ExampleMod.VERSION,
    dependencies = "required-after:modelloader@[1.1.4,)"
)
public class ExampleMod {

    public static final String MOD_ID = "examplemod";
    public static final String NAME = "Example Mod";
    public static final String VERSION = "1.0";

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();

        MinecraftForge.EVENT_BUS.register(this);

        // This allows the library to load models specified in the json files of the mod (in this example is not used)
        ModelLoaderApi.INSTANCE.registerDomain(MOD_ID);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    }


    @SubscribeEvent
    public void onBlockRegistryCreated(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> reg = event.getRegistry();

        reg.register(new SimpleBlock());
        reg.register(new BlockstatelessBlock().registerModels());
    }

    @SubscribeEvent
    public void onItemRegistryCreated(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> reg = event.getRegistry();

        reg.register(withRegistryName(new ItemBlock(ModBlocks.simpleBlock)));
        reg.register(withRegistryName(new ItemBlock(ModBlocks.blockstatelessBlock)));
    }

    private ItemBlock withRegistryName(ItemBlock item) {
        item.setRegistryName(item.getBlock().getRegistryName());
        return item;
    }

    @ObjectHolder(MOD_ID)
    static class ModBlocks {

        @ObjectHolder("simple_block")
        public static final SimpleBlock simpleBlock = null;

        @ObjectHolder("blockstateless_block")
        public static final BlockstatelessBlock blockstatelessBlock = null;
    }
}
