### ModelLoaderExample
This project is set of mods using [ModelLoader](https://github.com/Magneticraft-Team/ModelLoader)
to showcase how to implement common usecases.

## Examples

### Simple Block
It's a block with the default blockstate and static model.

You can check it out [here](src/main/java/com/cout970/modelloaderexample/SimpleBlock.java). 

### Rotable Block
It's a block with 6 states, each one encodes the rotation of 
the block placed in different directions. each direction has
a different static model rotated. 

You can check it out [here](src/main/java/com/cout970/modelloaderexample/RotableBlock.java). 

### Tile Block
This block has rotation like the rotable block, but uses a 
TileEntityRenderer instead of an static model. 
This approach is slower that using a static model, but its 
more flexible, you can change which model is rendered, which 
texture is used, you can render dynamic models like fluids,
special particles, etc. 

You can check it out [here](src/main/java/com/cout970/modelloaderexample/TileBlock.java). 

### Animated Block
This example extends the previous one rendering the model 
with an animation.

You can check it out [here](src/main/java/com/cout970/modelloaderexample/AnimatedBlock.java). 

### Simple Item
This example adds 2 items, both have an static model, each
model uses a different set of ItemTransforms that you can 
check decide which one you prefer.

You can check it out [here](src/main/java/com/cout970/modelloaderexample/SimpleItem.java). 

### Animated Item
This example adds 2 items, both are rendered using an
ItemStackTileEntityRenderer which works the same as a
TileEntityRenderer. Both items have an animation and it's
rendered the same way as a block animation.

You can check it out [here](src/main/java/com/cout970/modelloaderexample/AnimatedItem.java). 

