/*
 * Copyright (c) 2016 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration. All Rights Reserved.
 */

package gov.nasa.worldwind.render;

import gov.nasa.worldwind.geom.Matrix3;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.util.Level;
import gov.nasa.worldwind.util.Tile;

public class ImageTile extends Tile implements SurfaceTile {

    protected ImageSource imageSource;

    protected ImageTile fallbackTile;

    public ImageTile(Sector sector, Level level, int row, int column) {
        super(sector, level, row, column);
    }

    public ImageSource getImageSource() {
        return imageSource;
    }

    public void setImageSource(ImageSource imageSource) {
        this.imageSource = imageSource;
    }

    public ImageTile getFallbackTile() {
        return fallbackTile;
    }

    public void setFallbackTile(ImageTile fallbackTile) {
        this.fallbackTile = fallbackTile;
    }

    @Override
    public Sector getSector() {
        return this.sector;
    }

    public boolean hasTexture(DrawContext dc) {
        if (this.imageSource != null) {
            GpuTexture texture = (GpuTexture) dc.gpuObjectCache.get(this.imageSource);
            return (texture != null) && texture.hasTexture();
        }

        return false;
    }

    @Override
    public boolean bindTexture(DrawContext dc, int texUnit) {
        if (this.imageSource != null) {
            GpuTexture texture = (GpuTexture) dc.gpuObjectCache.get(this.imageSource);
            if (texture == null) {
                texture = new GpuTexture(dc, this.imageSource); // adds itself to the GPU object cache
            }
            if (texture.bindTexture(dc, texUnit)) {
                return true;
            }
        }

        return (this.fallbackTile != null) && this.fallbackTile.bindTexture(dc, texUnit);
    }

    @Override
    public boolean applyTexCoordTransform(DrawContext dc, Matrix3 result) {
        if (this.imageSource != null) {
            GpuTexture texture = (GpuTexture) dc.gpuObjectCache.get(this.imageSource);
            if (texture != null && texture.applyTexCoordTransform(result)) {
                return true; // use this surface tile's tex coord transform
            }
        }

        // Use the fallback tile's tex coord transform, adjusted to the fallback image into this tile's sector.
        if (this.fallbackTile != null && this.fallbackTile.applyTexCoordTransform(dc, result)) {
            result.multiplyByTileTransform(this.sector, this.fallbackTile.sector);
        }

        return false;
    }
}
