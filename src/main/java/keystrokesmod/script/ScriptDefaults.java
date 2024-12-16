package keystrokesmod.script;

import keystrokesmod.Raven;
import keystrokesmod.clickgui.ClickGui;
import keystrokesmod.clickgui.components.impl.CategoryComponent;
import keystrokesmod.clickgui.components.impl.ModuleComponent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.combat.KillAura;
import keystrokesmod.module.setting.Setting;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.script.classes.*;
import keystrokesmod.script.classes.Vec3;
import keystrokesmod.script.packets.serverbound.CPacket;
import keystrokesmod.script.packets.serverbound.PacketHandler;
import keystrokesmod.utility.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.network.Packet;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ScriptDefaults {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final World world = new World();
    public static final Bridge bridge = new Bridge();
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    public static class client {
        public static boolean allowFlying() {
            return mc.thePlayer.capabilities.allowFlying;
        }

        public static void removePotionEffect(int id) {
            if (mc.thePlayer == null) {
                return;
            }
            mc.thePlayer.removePotionEffectClient(id);
        }

        public static int getUID() {
            return 8;
        }

        public static void addEnemy(String username) {
            Utils.addEnemy(username);
        }

        public static void addFriend(String username) {
            Utils.addFriend(username);
        }

        public static void async(Runnable method) {
            executor.execute(method);
        }

        public static int getFPS() {
            return Minecraft.getDebugFPS();
        }

        public static void chat(String message) {
            mc.thePlayer.sendChatMessage(message);
        }

        public static void print(String string) {
            Utils.sendRawMessage(string);
        }

        public static void print(Object object) {
            String s = String.valueOf(object);
            Utils.sendRawMessage(s);
        }

        public static boolean isDiagonal() {
            return Utils.isDiagonal(false);
        }

        public static boolean isHoldingWeapon() {
            return Utils.holdingWeapon();
        }

        public static void setTimer(float timer) {
            Utils.getTimer().timerSpeed = timer;
        }

        public static boolean isCreative() {
            return mc.thePlayer.capabilities.isCreativeMode;
        }

        public static boolean isFlying() {
            return mc.thePlayer.capabilities.isFlying;
        }

        public static void attack(Entity entity) {
            Utils.attackEntity(entity.entity, true, true);
        }

        public static boolean isSinglePlayer() {
            return mc.isSingleplayer();
        }

        public static void setFlying(boolean flying) {
            mc.thePlayer.capabilities.isFlying = flying;
        }

        public static void setJump(boolean jump) {
            mc.thePlayer.movementInput.jump = jump;
        }

        public static void setJumping(boolean jump) {
            mc.thePlayer.setJumping(jump);
        }

        public static void setRenderArmPitch(float pitch) {
            mc.thePlayer.renderArmPitch = pitch;
        }

        public static float getRenderArmPitch() {
            return mc.thePlayer.renderArmPitch;
        }

        public static long getTotalMemory() {
            return Runtime.getRuntime().totalMemory();
        }

        public static long getFreeMemory() {
            return Runtime.getRuntime().freeMemory();
        }

        public static long getMaxMemory() {
            return Runtime.getRuntime().maxMemory();
        }

        public static void jump() {
            mc.thePlayer.jump();
        }

        public static void log(String message) {
            System.out.println(message);
        }

        public static boolean isMouseDown(int button) {
            return Mouse.isButtonDown(button);
        }

        public static boolean isKeyDown(int key) {
            return Keyboard.isKeyDown(key);
        }

        public static void setSneaking(boolean sneak) {
            mc.thePlayer.setSneaking(sneak);
        }

        public static void setSneak(boolean sneak) {
            mc.thePlayer.movementInput.sneak = sneak;
        }

        public static boolean isSneak() {
            return mc.thePlayer.movementInput.sneak;
        }

        public static Entity getPlayer() {
            if (mc == null || mc.thePlayer == null) {
                return null;
            }
            return Entity.convert(mc.thePlayer);
        }

        public static void removeEnemy(String username) {
            Utils.removeEnemy(username);
        }

        public static void removeFriend(String username) {
            Utils.removeFriend(username);
        }

        public static boolean isRiding() {
            return mc.thePlayer.isRiding();
        }

        public static Vec3 getMotion() {
            return new Vec3(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ);
        }

        public static void sleep(long ms) {
            try {
                Thread.sleep(ms);
            }
            catch (InterruptedException e) {
            }
        }

        public static void ping() {
            mc.thePlayer.playSound("note.pling", 1.0f, 1.0f);
        }

        public static void playSound(String name, float volume, float pitch) {
            mc.thePlayer.playSound(name, volume, pitch);
        }

        public static boolean isMoving() {
            return Utils.isMoving();
        }

        public static boolean isJump() {
            return mc.thePlayer.movementInput.jump;
        }

        public static float getStrafe() {
            return mc.thePlayer.movementInput.moveStrafe;
        }

        public static void sleep(int ms) {
            try {
                Thread.sleep(ms);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static float getForward() {
            return mc.thePlayer.movementInput.moveForward;
        }

        public static void closeScreen() {
            mc.thePlayer.closeScreen();
        }

        public static String getScreen() {
            return mc.currentScreen == null ? "" : mc.currentScreen.getClass().getSimpleName();
        }

        public static float[] getRotationsToEntity(Entity entity) {
            return RotationUtils.getRotations(entity.entity);
        }

        public static void sendPacket(CPacket packet) {
            Packet packet1 = PacketHandler.convertCPacket(packet);
            if (packet1 == null) {
                return;
            }
            mc.thePlayer.sendQueue.addToSendQueue(packet1);
        }

        public static void sendPacketNoEvent(CPacket packet) {
            Packet packet1 = PacketHandler.convertCPacket(packet);
            if (packet1 == null) {
                return;
            }
            PacketUtils.sendPacketNoEvent(packet1);
        }

        public static void dropItem(boolean dropStack) {
            mc.thePlayer.dropOneItem(dropStack);
        }

        public static void setMotion(double x, double y, double z) {
            mc.thePlayer.motionX = x;
            mc.thePlayer.motionY = y;
            mc.thePlayer.motionZ = z;
        }

        public static void setSpeed(double speed) {
            Utils.setSpeed(speed);
        }

        public static void setForward(float forward) {
            mc.thePlayer.movementInput.moveForward = forward;
        }

        public static void setStrafe(float strafe) {
            mc.thePlayer.movementInput.moveStrafe = strafe;
        }

        public static String getServerIP() {
            if (mc.getCurrentServerData() == null) {
                return "";
            }
            return mc.getCurrentServerData().serverIP;
        }

        public static int[] getDisplaySize() {
            final ScaledResolution scaledResolution = new ScaledResolution(mc);
            return new int[]{scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), scaledResolution.getScaleFactor()};
        }

        public static boolean placeBlock(Vec3 targetPos, String side, Vec3 hitVec) {
            return mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), Vec3.getBlockPos(targetPos), Utils.getEnum(EnumFacing.class, side), Vec3.getVec3(hitVec));
        }

        public static float[] getRotationsToBlock(Vec3 position) {
            return RotationUtils.getRotations(new BlockPos(position.x, position.y, position.z));
        }

        public static void setSprinting(boolean sprinting) {
            mc.thePlayer.setSprinting(sprinting);
        }

        public static void swing() {
            mc.thePlayer.swingItem();
        }

        public static World getWorld() {
            return world;
        }

        public static long time() {
            return System.currentTimeMillis();
        }

        public static boolean isFriend(Entity entity) {
            return Utils.isFriended(entity.getName());
        }

        public static boolean isEnemy(Entity entity) {
            return Utils.isEnemy(entity.getName());
        }
    }

    public static class modules {
        private String superName;

        public modules(String superName) {
            this.superName = superName;
        }
        private Module getModule(String moduleName) {
            boolean found = false;
            for (Module module : Raven.getModuleManager().getModules()) {
                if (module.getName().equals(moduleName)) {
                    return module;
                }
            }
            if (!found) {
                for (Module module : Raven.scriptManager.scripts.values()) {
                    if (module.getName().equals(moduleName)) {
                        return module;
                    }
                }
            }
            return null;
        }

        private Module getScript(String name) {
            for (Module module : Raven.scriptManager.scripts.values()) {
                if (module.getName().equals(name)) {
                    return module;
                }
            }
            return null;
        }

        private Setting getSetting(Module module, String settingName) {
            if (module == null) {
                return null;
            }
            for (Setting setting : module.getSettings()) {
                if (setting.getName().equals(settingName)) {
                    return setting;
                }
            }
            return null;
        }

        public void enable(String moduleName) {
            if (getModule(moduleName) == null) {
                return;
            }
            getModule(moduleName).enable();
        }

        public void disable(String moduleName) {
            if (getModule(moduleName) == null) {
                return;
            }
            getModule(moduleName).disable();
        }

        public boolean isEnabled(String moduleName) {
            if (getModule(moduleName) == null) {
                return false;
            }
            return getModule(moduleName).isEnabled();
        }

        public Entity getKillAuraTarget() {
            if (KillAura.target == null) {
                return null;
            }
            return Entity.convert(KillAura.target);
        }

        public Map<String, List<String>> getCategories() {
            Map<String, List<String>> categories = new HashMap<>();
            for (CategoryComponent categoryComponent : ClickGui.categories) {
                List<String> modules = new ArrayList<>();
                for (ModuleComponent module : categoryComponent.modules) {
                    modules.add(module.mod.getName());
                }
                categories.put(categoryComponent.categoryName.name(), modules);
            }
            return categories;
        }

        public Vec3 getBedAuraPosition() {
            BlockPos blockPos = ModuleManager.bedAura.currentBlock;
            if (ModuleManager.bedAura == null || !ModuleManager.bedAura.isEnabled() || ModuleManager.bedAura.currentBlock == null) {
                return null;
            }
            return new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        }

        public boolean isScaffolding() {
            return ModuleManager.scaffold.isEnabled() && ModuleManager.scaffold.tower.isToggled();
        }

        public boolean isTowering() {
            return ModuleManager.tower.canTower();
        }

        public boolean isHidden(String moduleName) {
            Module module = getModule(moduleName);
            if (module != null) {
                return module.isHidden();
            }
            return false;
        }

        public float[] getBedAuraProgress() {
            if (ModuleManager.bedAura != null && ModuleManager.bedAura.isEnabled()) {
                return new float[]{ModuleManager.bedAura.breakProgress, ModuleManager.bedAura.vanillaProgress};
            }
            return new float[]{0, 0};
        }

        public void registerButton(String name, boolean defaultValue) {
            getScript(this.superName).registerSetting(new ButtonSetting(name, defaultValue));
        }

        public void registerSlider(String name, double defaultValue, double minimum, double maximum, double interval) {
            this.registerSlider(name, "", defaultValue, minimum, maximum, interval);
        }

        public void registerSlider(String name,  int defaultValue, String[] stringArray) {
            this.registerSlider(name, "", defaultValue, stringArray);
        }

        public void registerSlider(String name, String suffix, double defaultValue, double minimum, double maximum, double interval) {
            getScript(this.superName).registerSetting(new SliderSetting(name, defaultValue, minimum, maximum, interval));
        }

        public void registerSlider(String name, String suffix, int defaultValue, String[] stringArray) {
            getScript(this.superName).registerSetting(new SliderSetting(name, defaultValue, stringArray));
        }

        public void registerDescription(String description) {
            getScript(this.superName).registerSetting(new DescriptionSetting(description));
        }

        public boolean getButton(String moduleName, String name) {
            ButtonSetting setting = (ButtonSetting) getSetting(getModule(moduleName), name);
            if (setting == null) {
                return false;
            }
            boolean buttonState = setting.isToggled();
            return buttonState;
        }

        public double getSlider(String moduleName, String name) {
            SliderSetting setting = ((SliderSetting) getSetting(getModule(moduleName), name));
            if (setting == null) {
                return 0;
            }
            double sliderValue = setting.getInput();
            return sliderValue;
        }

        public void setButton(String moduleName, String name, boolean value) {
            ButtonSetting setting = (ButtonSetting) getSetting(getModule(moduleName), name);
            if (setting == null) {
                return;
            }
            setting.setEnabled(value);
        }

        public void setSlider(String moduleName, String name, double value) {
            SliderSetting setting = ((SliderSetting) getSetting(getModule(moduleName), name));
            if (setting == null) {
                return;
            }
            setting.setValue(value);
        }
    }

    public static class gl {
        public static void alpha(boolean alpha) {
            if (alpha) {
                GlStateManager.enableAlpha();
            }
            else {
                GlStateManager.disableAlpha();
            }
        }

        public static void begin(int mode) {
            GL11.glBegin(mode);
        }

        public static void blend(boolean blend) {
            if (blend) {
                GlStateManager.enableBlend();
            }
            else {
                GlStateManager.disableBlend();
            }
        }

        public static void color(float r, float g, float b, float a) {
            GlStateManager.color(r, g, b, a);
        }

        public static void cull(boolean cull) {
            if (cull) {
                GlStateManager.enableCull();
            }
            else {
                GlStateManager.disableCull();
            }
        }

        public static void depth(boolean depth) {
            if (depth) {
                GlStateManager.enableDepth();
            }
            else {
                GlStateManager.disableDepth();
            }
        }

        public static void depthMask(boolean depthMask) {
            GlStateManager.depthMask(depthMask);
        }

        public static void disable(int cap) {
            GL11.glDisable(cap);
        }

        public static void disableItemLighting() {
            RenderHelper.disableStandardItemLighting();
        }

        public static void enable(int cap) {
            GL11.glEnable(cap);
        }

        public static void enableItemLighting(boolean gui) {
            if (gui) {
                RenderHelper.enableGUIStandardItemLighting();
            }
            else {
                RenderHelper.enableStandardItemLighting();
            }
        }

        public static void end() {
            GL11.glEnd();
        }

        public static void lighting(boolean lighting) {
            if (lighting) {
                GlStateManager.enableLighting();
            }
            else {
                GlStateManager.disableLighting();
            }
        }

        public static void lineSmooth(boolean lineSmooth) {
            setGLEnable(GL11.GL_LINE_SMOOTH, lineSmooth);
        }

        public static void lineWidth(float width) {
            GL11.glLineWidth(width);
        }

        public static void normal(float x, float y, float z) {
            GL11.glNormal3f(x, y, z);
        }

        public static void pop() {
            GL11.glPopMatrix();
        }

        public static void push() {
            GL11.glPushMatrix();
        }

        public static void rotate(float angle, float x, float y, float z) {
            GL11.glRotatef(angle, x, y, z);
        }

        public static void scale(float x, float y, float z) {
            GL11.glScalef(x, y, z);
        }

        public static void scissor(int x, int y, int width, int height) {
            GL11.glScissor(x, y, width, height);
        }

        public static void scissor(boolean scissor) {
            setGLEnable(GL11.GL_SCISSOR_TEST, scissor);
        }

        public static void texture2d(boolean texture2d) {
            if (texture2d) {
                GlStateManager.enableTexture2D();
            }
            else {
                GlStateManager.disableTexture2D();
            }
        }

        public static void translate(float x, float y, float z) {
            GL11.glTranslatef(x, y, z);
        }

        public static void vertex2(float x, float y) {
            GL11.glVertex2f(x, y);
        }

        public static void vertex3(float x, float y, float z) {
            GL11.glVertex3f(x, y, z);
        }

        private static void setGLEnable(int cap, boolean enable) {
            if (enable) {
                GL11.glEnable(cap);
            }
            else {
                GL11.glDisable(cap);
            }
        }
    }

    public static class render {

        public static void block(Vec3 position, int color, boolean outline, boolean shade) {
            RenderUtils.renderBlock(new BlockPos(position.x, position.y, position.z), color, outline, shade);
        }

        public static void entityGui(Entity en, int x, int y, float mouseX, float mouseY, int scale) {
            GuiInventory.drawEntityOnScreen(x, y, scale, mouseX, mouseY, (EntityLivingBase) en.entity);
        }

        public static void text(String text, float x, float y, int color, boolean shadow) {
            mc.fontRendererObj.drawString(text, x, y, color, shadow);
        }

        public static void tracer(Entity entity, float lineWidth, int color, float partialTicks) {
            RenderUtils.drawTracerLine(entity.entity, color, lineWidth, partialTicks);
        }

        public static void item(ItemStack item, float x, float y, float scale) {
            GlStateManager.scale(scale, scale, scale);
            mc.getRenderItem().renderItemAndEffectIntoGUI(item.itemStack, (int) x, (int) y);
            GlStateManager.scale(1, 1, 1);
        }

        public static void roundedRect(float startX, float startY, float endX, float endY, float radius, int color) {
            RenderUtils.drawRoundedRectangle(startX, startY, endX, endY, radius, color);
        }

        public static int getFontWidth(String text) {
            return mc.fontRendererObj.getStringWidth(text);
        }

        public static int getFontHeight() {
            return mc.fontRendererObj.FONT_HEIGHT;
        }

        public static Vec3 getPosition() {
            return new Vec3(mc.getRenderManager().viewerPosX, mc.getRenderManager().viewerPosY, mc.getRenderManager().viewerPosZ);
        }

        public static void text(String text, float x, float y, double scale, int color, boolean shadow) {
            GlStateManager.pushMatrix();
            GL11.glScaled(scale, scale, scale);
            mc.fontRendererObj.drawString(text, x, y, color, shadow);
            GlStateManager.popMatrix();
        }

        public static void player(Entity entity, int color, float partialTicks, boolean outline, boolean shade) {
            net.minecraft.entity.Entity e = entity.entity;
            if (e instanceof EntityLivingBase) {
                double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * partialTicks - mc.getRenderManager().viewerPosX;
                double y = e.lastTickPosY + (e.posY - e.lastTickPosY) * partialTicks - mc.getRenderManager().viewerPosY;
                double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * partialTicks - mc.getRenderManager().viewerPosZ;
                GlStateManager.pushMatrix();
                float a = (float) (color >> 24 & 255) / 255.0F;
                float r = (float) (color >> 16 & 255) / 255.0F;
                float g = (float) (color >> 8 & 255) / 255.0F;
                float b = (float) (color & 255) / 255.0F;
                AxisAlignedBB bbox = e.getEntityBoundingBox().expand(0.1D, 0.1D, 0.1D);
                AxisAlignedBB axis = new AxisAlignedBB(bbox.minX - e.posX + x, bbox.minY - e.posY + y, bbox.minZ - e.posZ + z, bbox.maxX - e.posX + x, bbox.maxY - e.posY + y, bbox.maxZ - e.posZ + z);
                GL11.glBlendFunc(770, 771);
                GL11.glEnable(3042);
                GL11.glDisable(3553);
                GL11.glDisable(2929);
                GL11.glDepthMask(false);
                GL11.glLineWidth(2.0F);
                GL11.glColor4f(r, g, b, a);
                if (outline) {
                    RenderGlobal.drawSelectionBoundingBox(axis);
                }
                if (shade) {
                    RenderUtils.drawBoundingBox(axis, r, g, b);
                }
                GL11.glEnable(3553);
                GL11.glEnable(2929);
                GL11.glDepthMask(true);
                GL11.glDisable(3042);
                GlStateManager.popMatrix();
            }
        }

        public static void rect(float startX, float startY, float endX, float endY, int color) {
            RenderUtils.drawRectangleGL(startX, startY, endX, endY, color);
        }

        public static void line2D(double startX, double startY, double endX, double endY, float lineWidth, int color) {
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            RenderUtils.glColor(color);
            GL11.glLineWidth(lineWidth);
            GL11.glBegin(GL11.GL_LINES);
            GL11.glVertex2d(startX, startY);
            GL11.glVertex2d(endX, endY);
            GL11.glEnd();
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glPopMatrix();
        }

        public static boolean isInView(Entity en) {
            return RenderUtils.isInViewFrustum(en.entity);
        }
    }

    public static class inventory {

        public static int getSlot() {
            return mc.thePlayer.inventory.currentItem;
        }

        public static void setSlot(int slot) {
            mc.thePlayer.inventory.currentItem = slot;
        }

        public static void click(int slot, int button, int mode) {
            mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, slot, button, mode, mc.thePlayer);
        }

        public static List<String> getBookContents() {
            if (mc.currentScreen instanceof GuiScreenBook) {
                try {
                    List<String> contents = new ArrayList<>();
                    int max = Math.min(128 / mc.fontRendererObj.FONT_HEIGHT, ((List<IChatComponent>) Reflection.bookContents.get(mc.currentScreen)).size());
                    for (int line = 0; line < max; ++line) {
                        IChatComponent lineStr = ((List<IChatComponent>) Reflection.bookContents.get(mc.currentScreen)).get(line);
                        contents.add(lineStr.getUnformattedText());
                        Utils.sendMessage(lineStr.getUnformattedText());
                    }
                    if (!contents.isEmpty()) {
                        return contents;
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        public static String getChest() {
            if (mc.thePlayer.openContainer instanceof ContainerChest) {
                ContainerChest chest = (ContainerChest) mc.thePlayer.openContainer;
                if (chest == null) {
                    return "";
                }
                return chest.getLowerChestInventory().getDisplayName().getUnformattedText();
            }
            return "";
        }

        public static String getContainer() {
            if (mc.currentScreen instanceof GuiContainerCreative) {
                CreativeTabs creativetabs = CreativeTabs.creativeTabArray[((GuiContainerCreative) mc.currentScreen).getSelectedTabIndex()];
                if (creativetabs != null) {
                    return I18n.format(creativetabs.getTranslatedTabLabel());
                }
            }
            else if (mc.currentScreen != null) {
                try {
                    return ((IInventory) Reflection.containerInventoryPlayer.get(mc.currentScreen.getClass()).get(mc.currentScreen)).getDisplayName().getUnformattedText();
                } catch (Exception e) {
                }
            }
            return "";
        }

        public static int getSize() {
            return mc.thePlayer.inventory.getSizeInventory();
        }

        public static int getChestSize() {
            if (mc.thePlayer.openContainer instanceof ContainerChest) {
                ContainerChest chest = (ContainerChest) mc.thePlayer.openContainer;
                if (chest == null) {
                    return -1;
                }
                return chest.getLowerChestInventory().getSizeInventory();
            }
            return -1;
        }

        public static ItemStack getStackInSlot(int slot) {
            if (mc.thePlayer.inventory.getStackInSlot(slot) == null) {
                return null;
            }
            return new ItemStack(mc.thePlayer.inventory.getStackInSlot(slot));
        }

        public static ItemStack getStackInChestSlot(int slot) {
            if (mc.thePlayer.openContainer instanceof ContainerChest) {
                ContainerChest chest = (ContainerChest) mc.thePlayer.openContainer;
                if (chest.getLowerChestInventory().getStackInSlot(slot) == null) {
                    return null;
                }
                return new ItemStack(chest.getLowerChestInventory().getStackInSlot(slot));
            }
            return null;
        }

        public static void open() {
            KeyBinding inventoryKey = mc.gameSettings.keyBindInventory;
            int originalKeyCode = inventoryKey.getKeyCode();

            if (originalKeyCode == 0) {
                inventoryKey.setKeyCode(13);
                KeyBinding.resetKeyBindingArrayAndHash();
            }

            KeyBinding.setKeyBindState(inventoryKey.getKeyCode(), true);
            KeyBinding.onTick(inventoryKey.getKeyCode());
            KeyBinding.setKeyBindState(inventoryKey.getKeyCode(), false);

            if (originalKeyCode == 0) {
                inventoryKey.setKeyCode(0);
                KeyBinding.resetKeyBindingArrayAndHash();
            }
        }
    }

    public static class keybinds {
        public static int[] getMousePosition() {
            return new int[] { Mouse.getX(), Mouse.getY() };
        }
        public static boolean isPressed(String key) {
            for (Map.Entry<KeyBinding, String> map : Reflection.keyBindings.entrySet()) {
                if (map.getValue().equals(key)) {
                    return map.getKey().isKeyDown();
                }
            }
            return false;
        }

        public static void setPressed(String key, boolean pressed) {
            for (Map.Entry<KeyBinding, String> map : Reflection.keyBindings.entrySet()) {
                if (map.getValue().equals(key)) {
                    KeyBinding.setKeyBindState(map.getKey().getKeyCode(), pressed);
                }
            }
        }

        public static int getKeycode(String key) {
            return Keyboard.getKeyIndex(key);
        }
        public static boolean isMouseDown(int mouseButton) {
            return Mouse.isButtonDown(mouseButton);
        }

        public static boolean isKeyDown(int key) {
            return Keyboard.isKeyDown(key);
        }

        public static void rightClick() {
            Reflection.rightClick();
        }

        public static void leftClick() {
            Reflection.clickMouse();
        }
    }

    public static class util {
        public static final String colorSymbol = "§";

        public static String color(String message) {
            return Utils.formatColor(message);
        }
        public static String strip(String string) {
            return Utils.stripColor(string);
        }

        public static double round(double value, int decimals) {
            return Utils.round(value, decimals);
        }

        public static int randomInt(int min, int max) {
            return Utils.randomizeInt(min, max);
        }

        public static double randomDouble(double min, double max) {
            return Utils.randomizeDouble(min, max);
        }
    }
}
