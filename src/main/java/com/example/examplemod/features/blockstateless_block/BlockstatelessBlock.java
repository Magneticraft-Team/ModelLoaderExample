package com.example.examplemod.features.blockstateless_block;

import com.cout970.modelloader.api.ModelLoaderApi;
import com.example.examplemod.ExampleMod;
import com.example.examplemod.features.IModelRegisterer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

/**
 * This block is an example of a block that just have a static and non-animated gltf model, like the simple_block, but
 * the model is not specified in a blockstate file, instead it's registered directly.
 * <p>
 * To register a model directly you can use the following function: `ModelLoaderApi.INSTANCE.registerModel(modelId,
 * modelLocation, bake);`
 * <p>
 * The first parameter is the modelId, the id can be used to get the model back with
 * `ModelLoaderApi.INSTANCE.getModelEntry(modelId);`
 * <p>
 * The second parameter is the modelLocation, the path where the model is located
 * <p>
 * The third parameter is a flag that indicates if the model should be baked and injected into the model registry. If
 * the flag is true, all the model textures will be added to the block texture map, the model will be baked and injected
 * into the vanilla ModelLoader, using the modelId
 * <p>
 * If you want to use the model for an item or a block (not a TileEntitySpecialRenderer) you will need to set this flag
 * to true. Since the model will be injected into the model registry the modelId must match the block registry name and
 * the variant must match the blockstate name.
 */
public class BlockstatelessBlock extends Block implements IModelRegisterer {

    public BlockstatelessBlock() {
        super(Material.ROCK);
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        setUnlocalizedName("blockstateless_block");
        setRegistryName(ExampleMod.MOD_ID, "blockstateless_block");
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public void registerModels() {
        ResourceLocation registryName = getRegistryName();
        if (registryName == null) {
            throw new IllegalStateException("Block doesn't have a registry name: " + this.getClass());
        }

        ModelResourceLocation blockModel = new ModelResourceLocation(registryName, "normal");
        ModelResourceLocation itemModel = new ModelResourceLocation(registryName, "inventory");
        ResourceLocation modelLocation = new ResourceLocation(ExampleMod.MOD_ID,
                                                              "models/block/blockstateless_block.gltf");

        ModelLoaderApi.INSTANCE.registerModel(blockModel, modelLocation, true);
        ModelLoaderApi.INSTANCE.registerModel(itemModel, modelLocation, true);

        // Register item model
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, itemModel);
    }
}
