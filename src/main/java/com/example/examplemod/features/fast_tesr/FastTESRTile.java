package com.example.examplemod.features.fast_tesr;

import net.minecraft.tileentity.TileEntity;

public class FastTESRTile extends TileEntity {

    @Override
    public boolean hasFastRenderer() {
        return true;
    }
}
