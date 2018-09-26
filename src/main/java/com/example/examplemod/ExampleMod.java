package com.example.examplemod;

import com.cout970.modelloader.api.ModelLoaderApi;
import com.example.examplemod.features.blockstateless_block.BlockstatelessBlock;
import com.example.examplemod.features.simple_block_model.SimpleBlock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
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

        // Registers this class so forge calls onBlockRegistryCreated and onItemRegistryCreated
        MinecraftForge.EVENT_BUS.register(this);

        // This allows the library to load models specified in the json files of the mod (in this example is not used)
        ModelLoaderApi.INSTANCE.registerDomain(MOD_ID);
    }

    @SubscribeEvent
    public void onBlockRegistryCreated(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> reg = event.getRegistry();

        // Registers all blocks
        reg.register(new SimpleBlock());
        reg.register(new BlockstatelessBlock());
    }

    @SubscribeEvent
    public void onItemRegistryCreated(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> reg = event.getRegistry();

        // Registers all items and itemblocks
        reg.register(createItemBlock(ModBlocks.simpleBlock));
        reg.register(createItemBlock(ModBlocks.blockstatelessBlock));

        // At this point all blocks and items are registered so it's safe to register the models
        // they need to be registered here because model loading happens before init
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            ModBlocks.simpleBlock.registerModels();
            ModBlocks.blockstatelessBlock.registerModels();
        }
    }

    private ItemBlock createItemBlock(Block block) {
        ItemBlock item = new ItemBlock(block);
        item.setRegistryName(block.getRegistryName());
        return item;
    }

    @ObjectHolder(MOD_ID)
    public static class ModBlocks {

        @ObjectHolder("simple_block")
        public static final SimpleBlock simpleBlock = null;

        @ObjectHolder("blockstateless_block")
        public static final BlockstatelessBlock blockstatelessBlock = null;
    }
}
