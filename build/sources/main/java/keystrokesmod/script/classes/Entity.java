package keystrokesmod.script.classes;

import keystrokesmod.utility.Reflection;
import keystrokesmod.utility.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Entity {
    public net.minecraft.entity.Entity entity;
    public String type;
    public int entityId;
    public boolean isLiving;
    public boolean isPlayer;
    public boolean isUser;
    private static HashMap<Integer, Entity> cache = new HashMap<>();

    public Entity(net.minecraft.entity.Entity entity) {
        this.entity = entity;
        if (entity == null) {
            return;
        }
        this.type = entity.getClass().getSimpleName();
        this.entityId = entity.getEntityId();
        this.isLiving = entity instanceof EntityLivingBase;
        this.isPlayer = entity instanceof EntityPlayer;
        if (this.isPlayer && Minecraft.getMinecraft().thePlayer != null && entity.getUniqueID().equals(Minecraft.getMinecraft().thePlayer.getUniqueID())) {
            this.isUser = true;
        }
    }

    public static Entity convert(net.minecraft.entity.Entity entity) {
        if (entity == null) {
            return null;
        }
        int id = entity.getEntityId() + System.identityHashCode(entity);
        Entity cachedEntity = cache.get(id);

        if (cachedEntity == null) {
            cachedEntity = new Entity(entity);
            cache.put(id, cachedEntity);
        }

        return cachedEntity;
    }

    public static void clearCache() {
        cache.clear();
    }

    public boolean allowEditing() {
        if (!(entity instanceof EntityPlayer)) {
            return false;
        }
        return (((EntityPlayer) entity).capabilities.allowEdit);
    }

    public double distanceTo(Vec3 position) {
        return entity.getDistance(position.x, position.y, position.z);
    }

    public double distanceToSq(Vec3 position) {
        return entity.getDistanceSq(position.x, position.y, position.z);
    }

    public double distanceToGround() {
        return Utils.distanceToGround(entity);
    }

    public boolean isHoldingBlock() {
        if (this.isLiving) {
            return ((EntityLivingBase) this.entity).getHeldItem() != null && ((EntityLivingBase) this.entity).getHeldItem().getItem() instanceof ItemBlock;
        }
        return false;
    }

    public float getAbsorption() {
        if (!(entity instanceof EntityLivingBase)) {
            return -1;
        }
        return (((EntityLivingBase) entity).getAbsorptionAmount());
    }

    public Vec3 getBlockPosition() {
        return new Vec3(entity.getPosition().getX(), entity.getPosition().getY(), entity.getPosition().getZ());
    }

    public String getDisplayName() {
        return entity.getDisplayName().getFormattedText();
    }

    public float getFallDistance() {
        return entity.fallDistance;
    }

    public String getUUID() {
        if (!(entity instanceof EntityPlayer)) {
            return entity.getUniqueID().toString();
        }
        return getNetworkPlayer().getUUID();
    }

    public double getBPS() {
        return Utils.gbps(this.entity, 0);
    }

    public String getFacing() {
        return this.entity.getHorizontalFacing().name();
    }

    public float getHealth() {
        if (!(entity instanceof EntityLivingBase)) {
            return -1;
        }
        return ((EntityLivingBase) entity).getHealth();
    }

    public boolean isSleeping() {
        if (this.isPlayer) {
            return ((EntityPlayer) this.entity).isPlayerSleeping();
        }
        return false;
    }

    public float getEyeHeight() {
        return entity.getEyeHeight();
    }

    public float getHeight() {
        return entity.height;
    }

    public float getWidth() {
        return entity.width;
    }

    public ItemStack getHeldItem() {
        if (entity instanceof EntityItem) {
            net.minecraft.item.ItemStack item = ((EntityItem) entity).getEntityItem();
            if (item == null) {
                return null;
            }
            return new ItemStack(item);
        }
        else if (!(entity instanceof EntityLivingBase)) {
            return null;
        }
        net.minecraft.item.ItemStack stack = ((EntityLivingBase) entity).getHeldItem();
        if (stack == null) {
            return null;
        }
        return new ItemStack(stack);
    }

    public int getHurtTime() {
        if (!(entity instanceof EntityLivingBase)) {
            return -1;
        }
        return ((EntityLivingBase) entity).hurtTime;
    }

    public boolean isConsuming() {
        return this.entity.isEating();
    }

    public Vec3 getLastPosition() {
        return new Vec3(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ);
    }

    public float getMaxHealth() {
        if (!(entity instanceof EntityLivingBase)) {
            return -1;
        }
        return ((EntityLivingBase) entity).getMaxHealth();
    }

    public int getMaxHurtTime() {
        if (!(entity instanceof EntityLivingBase)) {
            return -1;
        }
        return ((EntityLivingBase) entity).maxHurtTime;
    }

    public String getName() {
        if (entity instanceof EntityItem) {
            return ((EntityItem) entity).getEntityItem().getItem().getRegistryName().substring(10);
        }
        return entity.getName();
    }

    public NetworkPlayer getNetworkPlayer() {
        if (!(entity instanceof EntityPlayer)) {
            return null;
        }
        NetworkPlayer networkPlayer = null;
        try {
            networkPlayer = new NetworkPlayer((NetworkPlayerInfo) Reflection.getPlayerInfo.invoke(entity));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return networkPlayer;
    }

    public float getPitch() {
        return entity.rotationPitch;
    }

    public Vec3 getPosition() {
        if (entity == null) {
            return null;
        }
        return new Vec3(entity.posX, entity.posY, entity.posZ);
    }

    public List<Object[]> getPotionEffects() {
        List<Object[]> potionEffects = new ArrayList<>();
        if (!(entity instanceof EntityLivingBase)) {
            return potionEffects;
        }
        for (PotionEffect potionEffect : ((EntityLivingBase) entity).getActivePotionEffects()) {
            Object[] potionData = new Object[]{potionEffect.getPotionID(), potionEffect.getEffectName(), potionEffect.getAmplifier(), potionEffect.getDuration()};
            potionEffects.add(potionData);
        }
        return potionEffects;
    }

    public ItemStack getArmorInSlot(int slot) {
        if (!(entity instanceof EntityLivingBase)) {
            return null;
        }
        return ItemStack.convert(((EntityLivingBase) entity).getCurrentArmor(slot));
    }

    public double getSpeed() {
        return Utils.getHorizontalSpeed(entity);
    }

    public double getSwingProgress() {
        if (!(entity instanceof EntityLivingBase)) {
            return -1;
        }
        return ((EntityLivingBase) entity).swingProgress;
    }

    public int getTicksExisted() {
        return entity.ticksExisted;
    }

    public float getYaw() {
        return entity.rotationYaw;
    }

    public boolean isCreative() {
        if (!(entity instanceof EntityPlayer)) {
            return false;
        }
        return (((EntityPlayer) entity).capabilities.isCreativeMode);
    }

    public boolean isCollided() {
        return entity.isCollided;
    }

    public boolean isCollidedHorizontally() {
        return entity.isCollidedHorizontally;
    }

    public boolean isCollidedVertically() {
        return entity.isCollidedVertically;
    }

    public boolean isDead() {
        return entity.isDead;
    }

    public boolean isInvisible() {
        return entity.isInvisible();
    }

    public boolean isInWater() {
        return entity.isInWater();
    }

    public boolean isInLava() {
        return entity.isInLava();
    }

    public boolean isInLiquid() {
        return !this.entity.isOffsetPositionInLiquid(0, 0, 0);
    }

    public boolean isOnLadder() {
        if (this.isLiving) {
            if (((EntityLivingBase) this.entity).isOnLadder()) {
                return true;
            }
        }
        return false;
    }

    public boolean isOnEdge() {
        return Utils.onEdge(this.entity);
    }

    public boolean isSprinting() {
        return entity.isSprinting();
    }

    public boolean isSneaking() {
        return entity.isSneaking();
    }

    public boolean isUsingItem() {
        if (!(entity instanceof EntityPlayer)) {
            return false;
        }
        return (((EntityPlayer) entity).isUsingItem());
    }

    public boolean onGround() {
        return entity.onGround;
    }

    public void setMotion(double x, double y, double z) {
        entity.motionX = x;
        entity.motionY = y;
        entity.motionZ = z;
    }

    public Vec3 getMotion() {
        return new Vec3(entity.motionX, entity.motionY, entity.motionZ);
    }

    public void setPitch(float pitch) {
        entity.rotationPitch = pitch;
    }

    public void setYaw(float yaw) {
        entity.rotationYaw = yaw;
    }

    public void setPosition(Vec3 position) {
        entity.setPosition(position.x, position.y, position.z);
    }
}
