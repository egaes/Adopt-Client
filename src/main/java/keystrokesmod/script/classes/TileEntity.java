package keystrokesmod.script.classes;
public class TileEntity {
    private net.minecraft.tileentity.TileEntity tileEntity;
    private Vec3 position;

    public String type;
    public String name;

    protected TileEntity(net.minecraft.tileentity.TileEntity tileEntity) {
        this.tileEntity = tileEntity;
        this.position = new Vec3(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ());
        this.type = tileEntity.getBlockType().getClass().getSimpleName();
        this.name = tileEntity.getBlockType().getRegistryName().replace("minecraft:", "");
    }
}
