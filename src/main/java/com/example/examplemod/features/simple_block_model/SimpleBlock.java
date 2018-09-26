package com.example.examplemod.features.simple_block_model;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.features.IModelRegisterer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

/**
 * This block is an example of a block that just have a static and non-animated gltf model.
 * <p>
 * The model is specified in the blockstate file that can be found at: `resources/assets/examplemod/blockstates/simple_block.json`
 * <p>
 * Note that the model property ("model": "examplemod:simple_block.gltf") has an extension, the library will load all
 * models with the `gltf` or `mcx` extensions.
 * <p>
 * For this example to work you need to register the mod id in the library with: `ModelLoaderApi.INSTANCE.registerDomain(MOD_ID);`
 */
public class SimpleBlock extends Block implements IModelRegisterer {

    public SimpleBlock() {
        super(Material.ROCK);
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        setUnlocalizedName("simple_block");
        setRegistryName(ExampleMod.MOD_ID, "simple_block");
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

        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0,
                                                   new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}