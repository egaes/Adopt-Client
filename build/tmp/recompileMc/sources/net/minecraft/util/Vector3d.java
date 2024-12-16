package net.minecraft.util;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Vector3d
{
    /** The X coordinate */
    public double x;
    /** The Y coordinate */
    public double y;
    /** The Z coordinate */
    public double z;

    public Vector3d()
    {
        this.x = this.y = this.z = 0.0D;
    }
}