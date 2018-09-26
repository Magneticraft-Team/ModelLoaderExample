package com.example.examplemod.features.animated_block;

import com.cout970.modelloader.api.Model;
import com.cout970.modelloader.api.Model.Gltf;
import com.cout970.modelloader.api.ModelEntry;
import com.cout970.modelloader.api.ModelLoaderApi;
import com.cout970.modelloader.api.animation.IAnimatedModel;
import com.cout970.modelloader.api.formats.gltf.GltfAnimationBuilder;
import com.cout970.modelloader.api.formats.gltf.GltfModel;
import com.example.examplemod.ExampleMod;
import com.example.examplemod.features.IModelReloadListener;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class AnimatedRenderer extends TileEntitySpecialRenderer<AnimatedTile> implements IModelReloadListener {

    public static final AnimatedRenderer INSTANCE = new AnimatedRenderer();

    private AnimatedRenderer() {
    }

    private Map<String, IAnimatedModel> models = new HashMap<>();

    @Override
    public void render(AnimatedTile te, double x, double y, double z, float partialTicks, int destroyStage,
                       float alpha) {

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        float time = ((float) (getWorld().getTotalWorldTime() & 0xFFFFFF)) + partialTicks;
        models.get("animation").render(time);
        GlStateManager.popMatrix();
    }

    @Override
    public void reloadModels() {
        ModelResourceLocation modelId = new ModelResourceLocation(ExampleMod.MOD_ID + ":animated_block", "normal");
        ModelEntry entry = ModelLoaderApi.INSTANCE.getModelEntry(modelId);
        if (entry == null) {
            throw new IllegalStateException("Model not found: " + modelId);
        }

        Model raw = entry.getRaw();

        if (raw instanceof Gltf) {
            GltfModel data = ((Gltf) raw).getData();
            GltfAnimationBuilder builder = new GltfAnimationBuilder();
            builder.setUseTextureAtlas(true);

            models.clear();
            builder.build(data).forEach(pair -> models.put(pair.getFirst(), pair.getSecond()));
        } else {
            throw new IllegalStateException(
                "Model with invalid format (expected gltf): id = " + modelId + " model: " + raw);
        }
    }
}
