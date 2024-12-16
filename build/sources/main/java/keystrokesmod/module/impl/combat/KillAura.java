package keystrokesmod.module.impl.combat;

import com.google.common.base.Predicates;
import keystrokesmod.Raven;
import keystrokesmod.event.*;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.client.Settings;
import keystrokesmod.module.impl.world.AntiBot;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.*;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.*;
import net.minecraft.util.*;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.minecraft.util.EnumFacing.DOWN;

public class KillAura extends Module {
    public static EntityLivingBase target;
    private SliderSetting aps;
    public SliderSetting autoBlockMode;
    private SliderSetting fov;
    private SliderSetting attackRange;
    private SliderSetting swingRange;
    private SliderSetting blockRange;
    private SliderSetting rotationMode;
    private SliderSetting rotationSmoothing;
    private SliderSetting sortMode;
    private SliderSetting switchDelay;
    private SliderSetting targets;
    private ButtonSetting targetInvis;
    private ButtonSetting disableInInventory;
    private ButtonSetting disableWhileBlocking;
    private ButtonSetting disableWhileMining;
    private ButtonSetting hitThroughBlocks;
    private ButtonSetting ignoreTeammates;
    public ButtonSetting manualBlock;
    private ButtonSetting prioritizeEnemies;
    private ButtonSetting requireMouseDown;
    private ButtonSetting silentSwing;
    private ButtonSetting weaponOnly;
    private String[] autoBlockModes = new String[]{"Manual", "Vanilla", "Post", "Swap", "Interact", "Hypixel", "Fake", "Partial"};
    private String[] rotationModes = new String[]{"None", "Silent", "Lock view"};
    private String[] sortModes = new String[]{"Health", "Hurttime", "Distance", "Yaw"};
    private List<EntityLivingBase> availableTargets = new ArrayList<>();
    public AtomicBoolean block = new AtomicBoolean();
    private long lastSwitched = System.currentTimeMillis();
    private boolean switchTargets;
    private byte entityIndex;
    public boolean swing;
    // autoclicker vars
    private long i;
    private long j;
    private long k;
    private long l;
    private double m;
    private boolean n;
    private Random rand;
    // autoclicker vars end
    private boolean attack;
    private boolean blocking;
    public AtomicBoolean blinking = new AtomicBoolean();
    public boolean lag;
    private boolean swapped;
    public boolean rmbDown;
    private float[] prevRotations;
    private boolean startSmoothing;
    private boolean aiming;
    private ConcurrentLinkedQueue<Packet> blinkedPackets = new ConcurrentLinkedQueue<>();
    private float[] currentRotations;
    private int blinkAutoBlockTicks;
    private String[] swapBlacklist = { "compass", "snowball", "spawn", "skull" };

    public KillAura() {
        super("KillAura", category.combat);
        this.registerSetting(aps = new SliderSetting("APS", 16.0, 1.0, 20.0, 0.5));
        this.registerSetting(autoBlockMode = new SliderSetting("Autoblock", 0, autoBlockModes));
        this.registerSetting(fov = new SliderSetting("FOV", 360.0, 30.0, 360.0, 4.0));
        this.registerSetting(attackRange = new SliderSetting("Range (attack)", 3.0, 3.0, 6.0, 0.05));
        this.registerSetting(swingRange = new SliderSetting("Range (swing)", 3.3, 3.0, 8.0, 0.05));
        this.registerSetting(blockRange = new SliderSetting("Range (block)", 6.0, 3.0, 12.0, 0.05));
        this.registerSetting(rotationMode = new SliderSetting("Rotation mode", 0, rotationModes));
        this.registerSetting(rotationSmoothing = new SliderSetting("Rotation smoothing", 0, 0, 10, 0.1));
        this.registerSetting(sortMode = new SliderSetting("Sort mode", 0, sortModes));
        this.registerSetting(switchDelay = new SliderSetting("Switch delay", "ms", 200.0, 50.0, 1000.0, 25.0));
        this.registerSetting(targets = new SliderSetting("Targets", 3.0, 1.0, 10.0, 1.0));
        this.registerSetting(targetInvis = new ButtonSetting("Target invis", true));
        this.registerSetting(disableInInventory = new ButtonSetting("Disable in inventory", true));
        this.registerSetting(disableWhileBlocking = new ButtonSetting("Disable while blocking", false));
        this.registerSetting(disableWhileMining = new ButtonSetting("Disable while mining", false));
        this.registerSetting(hitThroughBlocks = new ButtonSetting("Hit through blocks", true));
        this.registerSetting(ignoreTeammates = new ButtonSetting("Ignore teammates", true));
        this.registerSetting(manualBlock = new ButtonSetting("Manual block", false));
        this.registerSetting(prioritizeEnemies = new ButtonSetting("Prioritize enemies", false));
        this.registerSetting(requireMouseDown = new ButtonSetting("Require mouse down", false));
        this.registerSetting(silentSwing = new ButtonSetting("Silent swing while blocking", false));
        this.registerSetting(weaponOnly = new ButtonSetting("Weapon only", false));
    }

    public void onEnable() {
        this.rand = new Random();
    }

    public void onDisable() {
        reset();
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent ev) {
        if (!Utils.nullCheck()) {
            return;
        }
        if (ev.phase != TickEvent.Phase.START) {
            return;
        }
        if (canAttack()) {
            attack = true;
        }
        if (target != null && rotationMode.getInput() == 2) {
            float[] rotations = RotationUtils.getRotations(target, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
            if (rotationSmoothing.getInput() > 0) {
                float[] speed = new float[]{(float) ((rotations[0] - mc.thePlayer.rotationYaw) / ((101 - rotationSmoothing.getInput()) * 3.634542)), (float) ((rotations[1] - mc.thePlayer.rotationPitch) / ((101 - rotationSmoothing.getInput()) * 5.1853))};
                mc.thePlayer.rotationYaw += speed[0];
                mc.thePlayer.rotationPitch += speed[1];
            }
            else {
                mc.thePlayer.rotationYaw = rotations[0];
                mc.thePlayer.rotationPitch = rotations[1];
            }
        }
    }

    @SubscribeEvent
    public void onPreUpdate(PreUpdateEvent e) {
        if (!basicCondition() || !settingCondition()) {
            reset();
            return;
        }

        block();

        if (ModuleManager.bedAura != null && ModuleManager.bedAura.isEnabled() && !ModuleManager.bedAura.allowAura.isToggled() && ModuleManager.bedAura.currentBlock != null) {
            resetBlinkState(true);
            return;
        }
        int autoBlock = (int) autoBlockMode.getInput();
        if ((mc.thePlayer.isBlocking() || (block.get() && autoBlock != 7)) && disableWhileBlocking.isToggled()) {
            resetBlinkState(true);
            return;
        }
        boolean swingWhileBlocking = !silentSwing.isToggled() || !block.get();
        if (swing && attack) {
            if (swingWhileBlocking) {
                mc.thePlayer.swingItem();
            }
            else {
                mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
            }
        }
        if (block.get() && (autoBlock  == 3 || autoBlock  == 4 || autoBlock  == 5) && Utils.holdingSword()) {
            if (ModuleManager.bedAura.stopAutoblock) {
                resetBlinkState(false);
                ModuleManager.bedAura.stopAutoblock = false;
                block.set(false);
                blocking = true;
                return;
            }
            setBlockState(block.get(), false, false);
            blinkAutoBlockTicks++;
            switch ((int) autoBlockMode.getInput()) {
                case 3:
                    // SWAP
                    if (lag) {
                        blinking.set(true);
                        int bestSwapSlot = getBestSwapSlot();
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange( bestSwapSlot));
                        Raven.badPacketsHandler.playerSlot.set(bestSwapSlot);
                        swapped = true;
                        lag = false;
                    }
                    else {
                        if (blinkAutoBlockTicks <= 0 && Raven.badPacketsHandler.delayAttack.get()) {
                            return;
                        }
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        Raven.badPacketsHandler.playerSlot.set(mc.thePlayer.inventory.currentItem);
                        swapped = false;
                        attackAndInteract(target, swingWhileBlocking, true);
                        sendBlock();
                        releasePackets();
                        lag = true;
                    }
                    break;
                case 4:
                case 5:
                    if (lag) {
                        blinking.set(true);
                        unBlock();
                        lag = false;
                    }
                    else {
                        attackAndInteract(target, swingWhileBlocking, autoBlockMode.getInput() == 5); // attack while blinked
                        sendBlock(); // block after releasing unblock
                        releasePackets(); // release
                        lag = true;
                    }
                    break;
            }
            return;
        }
        else if (blinking.get() || lag) {
            resetBlinkState(true);
        }
        if (target == null) {
            return;
        }
        if (attack && aiming) {
            resetBlinkState(true);
            attack = false;
            if (!aimingEntity()) {
                return;
            }
            if (ModuleManager.bedAura.rotate) {
                return;
            }
            switchTargets = true;
            aiming = false;
            Utils.attackEntity(target, swingWhileBlocking, !swingWhileBlocking);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPreMotion(PreMotionEvent e) {
        if (!basicCondition() || !settingCondition()) {
            reset();
            return;
        }
        setTarget();
        if (target != null && rotationMode.getInput() == 1) {
            float[] rotations = RotationUtils.getRotations(target, e.getYaw(), e.getPitch());
            if (rotationSmoothing.getInput() > 0) {
                if (!startSmoothing) {
                    prevRotations = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
                    startSmoothing = true;
                }
                float[] speed = new float[]{(float) ((rotations[0] - prevRotations[0]) / Math.max(((rotationSmoothing.getInput()) * 0.262843), 1.5)), (float) ((rotations[1] - prevRotations[1]) / Math.max(((rotationSmoothing.getInput()) * 0.1637), 1.5))};
                prevRotations[0] += speed[0];
                prevRotations[1] += speed[1];
                if (prevRotations[1] > 90) {
                    prevRotations[1] = 90;
                }
                else if (prevRotations[1] < -90) {
                    prevRotations[1] = -90;
                }
                e.setYaw(prevRotations[0]);
                e.setPitch(prevRotations[1]);
            }
            else {
                e.setYaw(rotations[0]);
                e.setPitch(rotations[1]);
            }
            aiming = true;
        }
        else {
            startSmoothing = false;
        }
        if (autoBlockMode.getInput() == 2 && block.get() && Utils.holdingSword()) {
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }
        currentRotations = new float[] { e.getYaw(), e.getPitch() };
    }

    @SubscribeEvent
    public void onPostMotion(PostMotionEvent e) {
        if (autoBlockMode.getInput() == 2 && block.get() && Utils.holdingSword()) {
            sendBlock();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onSendPacket(SendPacketEvent e) {
        if (!Utils.nullCheck() || !blinking.get()) {
            return;
        }
        Packet packet = e.getPacket();
        if (packet.getClass().getSimpleName().startsWith("S")) {
            return;
        }
        if (packet instanceof C00PacketKeepAlive || packet instanceof C00PacketLoginStart || packet instanceof C00Handshake) {
            return;
        }
        blinkedPackets.add(e.getPacket());
        e.setCanceled(true);
    }

    @SubscribeEvent
    public void onMouse(final MouseEvent mouseEvent) {
        if (mouseEvent.button == 0 && mouseEvent.buttonstate) {
            if (target != null || swing) {
                mouseEvent.setCanceled(true);
            }
        }
        else if (mouseEvent.button == 1) {
            rmbDown = mouseEvent.buttonstate;
            if (autoBlockMode.getInput() >= 1 && Utils.holdingSword() && block.get() && autoBlockMode.getInput() != 7) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                if (target == null && mc.objectMouseOver != null) {
                    if (mc.objectMouseOver.entityHit != null && AntiBot.isBot(mc.objectMouseOver.entityHit)) {
                        return;
                    }
                    final BlockPos getBlockPos = mc.objectMouseOver.getBlockPos();
                    if (getBlockPos != null && (BlockUtils.check(getBlockPos, Blocks.chest) || BlockUtils.check(getBlockPos, Blocks.ender_chest))) {
                        return;
                    }
                }
                mouseEvent.setCanceled(true);
            }
        }
    }

    @Override
    public String getInfo() {
        return rotationModes[(int) rotationMode.getInput()];
    }

    private boolean aimingEntity() {
        if (rotationMode.getInput() > 0 && rotationSmoothing.getInput() > 0) {
            Object[] raycast = Reach.getEntity(attackRange.getInput(), 0, rotationMode.getInput() == 1 ? prevRotations : null);
            if (raycast == null || raycast[0] != target) {
                return false;
            }
        }
        return true;
    }

    private void reset() {
        target = null;
        availableTargets.clear();
        block.set(false);
        ModuleManager.targetHUD.renderEntity = null;
        startSmoothing = false;
        swing = false;
        blinkAutoBlockTicks = 0;
        rmbDown = false;
        currentRotations = null;
        attack = false;
        aiming = false;
        this.i = 0L;
        this.j = 0L;
        block();
        resetBlinkState(true);
        swapped = false;
    }

    private void block() {
        if (!block.get() && !blocking) {
            return;
        }
        if (manualBlock.isToggled() && !rmbDown) {
            block.set(false);
        }
        if (!Utils.holdingSword()) {
            block.set(false);
        }
        switch ((int) autoBlockMode.getInput()) {
            case 0:
                setBlockState(false, false, true);
                break;
            case 1: // vanilla
                setBlockState(block.get(), true, true);
                break;
            case 2: // post
                setBlockState(block.get(), false, true);
                break;
            case 3: // interact
            case 4:
            case 5:
                setBlockState(block.get(), false, false);
                break;
            case 6: // fake
                setBlockState(block.get(), false, false);
                break;
            case 7: // partial
                boolean down = (target == null || target.hurtTime >= 5) && block.get();
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), down);
                Reflection.setButton(1, down);
                blocking = down;
                break;
        }
    }

    private int getBestSwapSlot() {
        int currentSlot = mc.thePlayer.inventory.currentItem;
        int bestSlot = -1;
        double bestDamage = -1;
        for (int i = 0; i < 9; ++i) {
            if (i == currentSlot) {
                continue;
            }
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            double damage = Utils.getDamage(stack);
            if (damage != 0) {
                if (damage > bestDamage) {
                    bestDamage = damage;
                    bestSlot = i;
                }
            }
        }
        if (bestSlot == -1) {
            for (int i = 0; i < 9; ++i) {
                if (i == currentSlot) {
                    continue;
                }
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                if (stack == null || Arrays.stream(swapBlacklist).noneMatch(stack.getUnlocalizedName().toLowerCase()::contains)) {
                    bestSlot = i;
                    break;
                }
            }
        }

        return bestSlot;
    }

    private void setBlockState(boolean state, boolean sendBlock, boolean sendUnBlock) {
        if (Utils.holdingSword()) {
            if (sendBlock && !blocking && state && Utils.holdingSword() && !Raven.badPacketsHandler.C07.get()) {
                sendBlock();
            } else if (sendUnBlock && blocking && !state) {
                unBlock();
            }
        }
        blocking = Reflection.setBlocking(state);
    }

    private void setTarget() {
        availableTargets.clear();
        block.set(false);
        ModuleManager.targetHUD.renderEntity = null;
        swing = false;
        double checkDistance = (blockRange.getMax() * blockRange.getMax()) + 4;
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (availableTargets.size() > targets.getInput()) {
                continue;
            }
            if (entity == null) {
                continue;
            }
            if (entity == mc.thePlayer) {
                continue;
            }
            if (!(entity instanceof EntityLivingBase)) {
                continue;
            }
            if (entity instanceof EntityPlayer) {
                if (Utils.isFriended((EntityPlayer) entity)) {
                    continue;
                }
                if (((EntityPlayer) entity).deathTime != 0) {
                    continue;
                }
                if (AntiBot.isBot(entity) || (Utils.isTeamMate(entity) && ignoreTeammates.isToggled())) {
                    continue;
                }
            }
            else {
                continue;
            }
            if (entity.isInvisible() && !targetInvis.isToggled()) {
                continue;
            }
            if (!hitThroughBlocks.isToggled() && !mc.thePlayer.canEntityBeSeen(entity)) {
                continue;
            }
            final float n = (float) fov.getInput();
            if (n != 360.0f && !Utils.inFov(n, entity)) {
                continue;
            }
            if (mc.thePlayer.getDistanceSqToEntity(entity) >= checkDistance) { // if more than block range + 2 sq then continue on to next loop, else continue with more accurate checks
                continue;
            }
            float[] rotations = RotationUtils.getRotations(entity);
            if (inRange(entity, rotations, blockRange.getInput()) && autoBlockMode.getInput() > 0 && Utils.holdingSword()) {
                if (autoBlockMode.getInput() != 7) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                }
                block.set(true);
                ModuleManager.targetHUD.renderEntity = (EntityLivingBase) entity;
            }
            if (inRange(entity, rotations, swingRange.getInput())) {
                swing = true;
                ModuleManager.targetHUD.renderEntity = (EntityLivingBase) entity;
            }
            if (!inRange(entity, rotations, attackRange.getInput())) {
                continue;
            }
            availableTargets.add((EntityLivingBase) entity);
        }
        if (Math.abs(System.currentTimeMillis() - lastSwitched) > switchDelay.getInput() && switchTargets) {
            switchTargets = false;
            if (entityIndex < availableTargets.size() - 1) {
                entityIndex++;
            } else {
                entityIndex = 0;
            }
            lastSwitched = System.currentTimeMillis();
        }
        if (!availableTargets.isEmpty()) {
            List<EntityLivingBase> enemies = new ArrayList<>();
            if (prioritizeEnemies.isToggled()) {
                for (EntityLivingBase entity : availableTargets) {
                    if (Utils.isEnemy((EntityPlayer) entity)) {
                        enemies.add(entity);
                    }
                }
                if (!enemies.isEmpty()) {
                    availableTargets = enemies;
                }
            }
            Comparator<EntityLivingBase> comparator = null;
            switch ((int) sortMode.getInput()) {
                case 0:
                    comparator = Comparator.comparingDouble(entityPlayer -> (double)entityPlayer.getHealth());
                    break;
                case 1:
                    comparator = Comparator.comparingDouble(entityPlayer2 -> (double)entityPlayer2.hurtTime);
                    break;
                case 2:
                    comparator = Comparator.comparingDouble(entity -> getDistanceToBoundingBox(entity)); // use a less resource intensive check
                    break;
                case 3:
                    comparator = Comparator.comparingDouble(entity2 -> RotationUtils.distanceFromYaw(entity2, false));
                    break;
            }
            Collections.sort(availableTargets, comparator);
            if (entityIndex > availableTargets.size() - 1) {
                entityIndex = 0;
            }
            target = availableTargets.get(entityIndex);
        } else {
            target = null;
        }
    }

    private boolean basicCondition() {
        if (!Utils.nullCheck()) {
            return false;
        }
        if (mc.thePlayer.isDead) {
            return false;
        }
        return true;
    }

    private boolean settingCondition() {
        if (!Mouse.isButtonDown(0) && requireMouseDown.isToggled()) {
            return false;
        }
        else if (weaponOnly.isToggled() && !Utils.holdingWeapon()) {
            return false;
        }
        else if (disableWhileMining.isToggled() && isMining()) {
            return false;
        }
        else if (disableInInventory.isToggled() && Settings.inInventory()) {
            return false;
        }
        return true;
    }

    private void attackAndInteract(EntityLivingBase target, boolean swingWhileBlocking, boolean sendInteractAt) {
        if (target != null && attack && aiming) {
            attack = false;
            if (!aimingEntity()) {
                return;
            }
            if (ModuleManager.bedAura.rotate) {
                return;
            }
            aiming = false;
            switchTargets = true;
            Utils.attackEntity(target, !swing && swingWhileBlocking, !swingWhileBlocking);
            if (sendInteractAt && currentRotations != null) {
                MovingObjectPosition hitObject = rayTrace(Utils.getTimer().renderPartialTicks, currentRotations);
                if (hitObject != null && hitObject.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && hitObject.entityHit == target) {
                    Vec3 hitVec = hitObject.hitVec;
                    hitVec = new Vec3(hitVec.xCoord - target.posX, hitVec.yCoord - target.posY, hitVec.zCoord - target.posZ);
                    mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, hitVec));
                }
            }

            mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.INTERACT));
        }
        else if (ModuleManager.antiFireball != null && ModuleManager.antiFireball.isEnabled() && ModuleManager.antiFireball.fireball != null && ModuleManager.antiFireball.attack) {
            if (ModuleManager.bedAura.rotate) {
                return;
            }
            Utils.attackEntity(ModuleManager.antiFireball.fireball, !ModuleManager.antiFireball.silentSwing.isToggled(), ModuleManager.antiFireball.silentSwing.isToggled());
            mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(ModuleManager.antiFireball.fireball, C02PacketUseEntity.Action.INTERACT));
        }
    }

    private boolean isMining() {
        return Mouse.isButtonDown(0) && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK;
    }

    private boolean canAttack() {
        if (this.j > 0L && this.i > 0L) {
            if (System.currentTimeMillis() > this.j) {
                this.gd();
                return true;
            } else if (System.currentTimeMillis() > this.i) {
                return false;
            }
        } else {
            this.gd();
        }
        return false;
    }

    public MovingObjectPosition rayTrace(float p_getMouseOver_1_, float[] rotations) {
        Entity targetEntity = null;
        MovingObjectPosition hitObject;
        double d0 = attackRange.getInput();
        hitObject = RotationUtils.rayTraceCustom(d0, rotations[0], rotations[1]);
        double d1 = d0;
        Vec3 vec3 = mc.thePlayer.getPositionEyes(p_getMouseOver_1_);
        if (this.mc.playerController.extendedReach()) {
            d0 = 6.0;
            d1 = 6.0;
        }

        if (hitObject != null) {
            d1 = hitObject.hitVec.distanceTo(vec3);
        }

        Vec3 vec31 = RotationUtils.getVectorForRotation(rotations[1], rotations[0]);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
        Vec3 vec33 = null;
        float f = 1.0F;
        List<Entity> list = this.mc.theWorld.getEntitiesInAABBexcluding(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand(f, f, f), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
        double d2 = d1;

        for(int j = 0; j < list.size(); ++j) {
            Entity entity1 = list.get(j);
            float f1 = entity1.getCollisionBorderSize();
            AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(f1, f1, f1);
            MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
            if (axisalignedbb.isVecInside(vec3)) {
                if (d2 >= 0.0) {
                    targetEntity = entity1;
                    vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                    d2 = 0.0;
                }
            }
            else if (movingobjectposition != null) {
                double d3 = vec3.distanceTo(movingobjectposition.hitVec);
                if (d3 < d2 || d2 == 0.0) {
                    if (entity1 == mc.thePlayer.ridingEntity && !mc.thePlayer.canRiderInteract()) {
                        if (d2 == 0.0) {
                            targetEntity = entity1;
                            vec33 = movingobjectposition.hitVec;
                        }
                    }
                    else {
                        targetEntity = entity1;
                        vec33 = movingobjectposition.hitVec;
                        d2 = d3;
                    }
                }
            }
        }
        if (targetEntity != null && d2 < d1) {
            return new MovingObjectPosition(targetEntity, vec33);
        }
        return null;
    }

    public void gd() {
        double c = aps.getInput() + 0.4D * this.rand.nextDouble();
        long d = (int) Math.round(1000.0D / c);
        if (System.currentTimeMillis() > this.k) {
            if (!this.n && this.rand.nextInt(100) >= 85) {
                this.n = true;
                this.m = 1.1D + this.rand.nextDouble() * 0.15D;
            } else {
                this.n = false;
            }

            this.k = System.currentTimeMillis() + 500L + (long) this.rand.nextInt(1500);
        }

        if (this.n) {
            d = (long) ((double) d * this.m);
        }

        if (System.currentTimeMillis() > this.l) {
            if (this.rand.nextInt(100) >= 80) {
                d += 50L + (long) this.rand.nextInt(100);
            }

            this.l = System.currentTimeMillis() + 500L + (long) this.rand.nextInt(1500);
        }

        this.j = System.currentTimeMillis() + d;
        this.i = System.currentTimeMillis() + d / 2L - (long) this.rand.nextInt(10);
    }

    private void unBlock() {
        if (!Utils.holdingSword()) {
            return;
        }
        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, DOWN));
    }

    public void resetBlinkState(boolean unblock) {
        releasePackets();
        blocking = false;
        if (Raven.badPacketsHandler.playerSlot.get() != mc.thePlayer.inventory.currentItem && swapped) {
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            Raven.badPacketsHandler.playerSlot.set(mc.thePlayer.inventory.currentItem);
            swapped = false;
        }
        if (lag && unblock) {
            unBlock();
        }
        lag = false;
    }

    private void releasePackets() {
        try {
            synchronized (blinkedPackets) {
                for (Packet packet : blinkedPackets) {
                    Raven.badPacketsHandler.handlePacket(packet);
                    PacketUtils.sendPacketNoEvent(packet);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Utils.sendModuleMessage(this, "&cThere was an error releasing blinked packets");
        }
        blinkedPackets.clear();
        blinking.set(false);
    }

    private void sendBlock() {
        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
    }

    private boolean inRange(Entity target, float[] rotations, double range) {
        double expand = 0;
        Vec3 vec3 = mc.thePlayer.getPositionEyes(1);
        Vec3 vec31 = RotationUtils.getVectorForRotation(rotations[1], rotations[0]);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range);
        AxisAlignedBB axisalignedbb = target.getEntityBoundingBox().expand(expand, expand, expand);
        MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
        if (movingobjectposition != null && vec3 != null && vec3.distanceTo(movingobjectposition.hitVec) <= range) {
            return true;
        }
        return false;
    }

    private double getDistanceToBoundingBox(Entity target) {
        Vec3 playerEyePos = mc.thePlayer.getPositionEyes(Utils.getTimer().renderPartialTicks);
        AxisAlignedBB boundingBox = target.getEntityBoundingBox();
        double nearestX = MathHelper.clamp_double(playerEyePos.xCoord, boundingBox.minX, boundingBox.maxX);
        double nearestY = MathHelper.clamp_double(playerEyePos.yCoord, boundingBox.minY, boundingBox.maxY);
        double nearestZ = MathHelper.clamp_double(playerEyePos.zCoord, boundingBox.minZ, boundingBox.maxZ);
        Vec3 nearestPoint = new Vec3(nearestX, nearestY, nearestZ);
        return playerEyePos.distanceTo(nearestPoint);
    }
}
