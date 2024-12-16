package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.player.Freecam;
import keystrokesmod.module.impl.world.AntiBot;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.RenderUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Nametags extends Module {
    private SliderSetting scale;
    private ButtonSetting autoScale;
    private ButtonSetting drawBackground;
    private ButtonSetting onlyRenderName;
    private ButtonSetting dropShadow;
    private ButtonSetting showDistance;
    private ButtonSetting showHealth;
    private ButtonSetting showHitsToKill;
    private ButtonSetting showInvis;
    private ButtonSetting removeTags;
    private ButtonSetting renderSelf;
    private ButtonSetting showArmor;
    private ButtonSetting showEnchants;
    private ButtonSetting showDurability;
    private ButtonSetting showStackSize;
    private Map<EntityPlayer, double[]> entityPositions = new HashMap();
    private int backGroundColor = new Color(0, 0, 0, 65).getRGB();
    private int friendColor = new Color(0, 255, 0, 255).getRGB();
    private int enemyColor = new Color(255, 0, 0, 255).getRGB();

    public Nametags() {
        super("Nametags", category.render, 0);
        this.registerSetting(scale = new SliderSetting("Scale", 1.0, 0.5, 5.0, 0.1));
        this.registerSetting(autoScale = new ButtonSetting("Auto-scale", true));
        this.registerSetting(drawBackground = new ButtonSetting("Draw background", true));
        this.registerSetting(onlyRenderName = new ButtonSetting("Only render name", false));
        this.registerSetting(renderSelf = new ButtonSetting("Render self", false));
        this.registerSetting(dropShadow = new ButtonSetting("Drop shadow", true));
        this.registerSetting(showDistance = new ButtonSetting("Show distance", false));
        this.registerSetting(showHealth = new ButtonSetting("Show health", true));
        this.registerSetting(showHitsToKill = new ButtonSetting("Show hits to kill", false));
        this.registerSetting(showInvis = new ButtonSetting("Show invis", true));
        this.registerSetting(removeTags = new ButtonSetting("Remove tags", false));
        this.registerSetting(new DescriptionSetting("Armor settings"));
        this.registerSetting(showArmor = new ButtonSetting("Show armor", false));
        this.registerSetting(showEnchants = new ButtonSetting("Show enchants", true));
        this.registerSetting(showDurability = new ButtonSetting("Show durability", true));
        this.registerSetting(showStackSize = new ButtonSetting("Show stack size", true));
    }

    @SubscribeEvent
    public void onRenderTick(RenderGameOverlayEvent.Post ev) {
        if (!Utils.nullCheck()) {
            return;
        }
        if (ev.type != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        if (removeTags.isToggled()) {
            return;
        }

        GlStateManager.pushMatrix();
        ScaledResolution scaledRes = new ScaledResolution(mc);
        double twoDScale = scaledRes.getScaleFactor() / Math.pow(scaledRes.getScaleFactor(), 2.0D);
        GlStateManager.scale(twoDScale, twoDScale, twoDScale);
        for (Map.Entry<EntityPlayer, double[]> entry : entityPositions.entrySet()) {
            EntityPlayer entityPlayer = entry.getKey();

            GlStateManager.pushMatrix();
            String name;
            if (onlyRenderName.isToggled()) {
                name = entityPlayer.getName();
            }
            else {
                name = entityPlayer.getDisplayName().getFormattedText();
            }
            if (showHealth.isToggled()) {
                name = name + " " + Utils.getHealthStr(entityPlayer, false);
            }
            if (showHitsToKill.isToggled()) {
                name = name + " " + Utils.getHitsToKill(entityPlayer, mc.thePlayer.getCurrentEquippedItem());
            }
            if (showDistance.isToggled()) {
                int distance = Math.round(mc.thePlayer.getDistanceToEntity(entityPlayer));
                String color = "§";
                if (distance < 8) {
                    color += "c";
                }
                else if (distance < 30) {
                    color += "6";
                }
                else if (distance < 60) {
                    color += "e";
                }
                else if (distance < 90) {
                    color += "a";
                }
                else {
                    color += "2";
                }
                name = color + distance + "m§r " + name;
            }
            if (ModuleManager.skyWars.isEnabled() && ModuleManager.skyWars.strengthIndicator.isToggled() && !ModuleManager.skyWars.strengthPlayers.isEmpty() && ModuleManager.skyWars.strengthPlayers.get(entityPlayer) != null) {
                double startTime = ModuleManager.skyWars.strengthPlayers.get(entityPlayer);
                double timePassed = (System.currentTimeMillis() - startTime) / 1000;
                double strengthRemaining = Math.max(0, Utils.round(5.0 - timePassed, 1));
                String strengthInfo = "§4" + (Utils.isWholeNumber(strengthRemaining) ? (int) strengthRemaining + "" : strengthRemaining) + "s§r ";
                name = strengthInfo + name;
            }
            double[] renderPositions = entry.getValue();
            GlStateManager.translate(renderPositions[0], renderPositions[1], 0);
            int strWidth = mc.fontRendererObj.getStringWidth(name) / 2;
            GlStateManager.color(0.0F, 0.0F, 0.0F);
            double rawScaleSetting = scale.getInput();
            double scaleSetting = rawScaleSetting * 10;
            double nameTagScale = twoDScale * scaleSetting;
            final float renderPartialTicks = Utils.getTimer().renderPartialTicks;
            final EntityPlayer player = (Freecam.freeEntity == null) ? mc.thePlayer : Freecam.freeEntity;
            final double deltaX = player.lastTickPosX + (player.posX - player.lastTickPosX) * renderPartialTicks - (entityPlayer.lastTickPosX + (entityPlayer.posX - entityPlayer.lastTickPosX) * renderPartialTicks);
            final double deltaY = player.lastTickPosY + (player.posY - player.lastTickPosY) * renderPartialTicks - (entityPlayer.lastTickPosY + (entityPlayer.posY - entityPlayer.lastTickPosY) * renderPartialTicks);
            final double deltaZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * renderPartialTicks - (entityPlayer.lastTickPosZ + (entityPlayer.posZ - entityPlayer.lastTickPosZ) * renderPartialTicks);
            double distance = MathHelper.sqrt_double(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
            if (!autoScale.isToggled()) {
                if (renderSelf.isToggled() && entityPlayer == mc.thePlayer) {
                    distance = 3;
                }
                nameTagScale = rawScaleSetting / (Math.max(distance, 3) / 10);
            }
            else {
                if (distance < 3 && entityPlayer == mc.thePlayer) {
                    distance = Math.pow(1.02, -9.65 + 20);
                }
                else {
                    distance = Math.max(0.7, Math.pow(1.05, -distance + 10));
                }
                nameTagScale *= distance;
            }
            GlStateManager.scale(nameTagScale, nameTagScale, nameTagScale);
            int x1 = -strWidth - 1;
            int y1 = -10;
            int x2 = strWidth + 1;
            int y2 = 8 - 9;
            if (drawBackground.isToggled()) {
                RenderUtils.drawRect(x1, y1, x2, y2, backGroundColor);
            }
            if (Utils.isFriended(entityPlayer)) {
                RenderUtils.drawOutline(x1, y1, x2, y2, 2, friendColor);
            }
            else if (Utils.isEnemy(entityPlayer)) {
                RenderUtils.drawOutline(x1, y1, x2, y2, 2, enemyColor);
            }
            mc.fontRendererObj.drawString(name, -strWidth, -9, -1, dropShadow.isToggled());
            if (showArmor.isToggled()) {
                renderArmor(entityPlayer);
            }

            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent renderWorldLastEvent) {
        if (!Utils.nullCheck()) {
            return;
        }
        if (removeTags.isToggled()) {
            return;
        }
        updatePositions();
    }

    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent.Specials.Pre e) {
        if (e.entity instanceof EntityPlayer && (e.entity != mc.thePlayer || renderSelf.isToggled()) && e.entity.deathTime == 0) {
            EntityPlayer entityPlayer = (EntityPlayer) e.entity;
            if (!showInvis.isToggled() && entityPlayer.isInvisible()) {
                return;
            }
            if (entityPlayer.getDisplayNameString().isEmpty() || (entityPlayer != mc.thePlayer && AntiBot.isBot(entityPlayer))) {
                return;
            }
            e.setCanceled(true);
        }
    }

    private void updatePositions() {
        entityPositions.clear();
        final float pTicks = Utils.getTimer().renderPartialTicks;
        for (EntityPlayer entityPlayer : mc.theWorld.playerEntities) {
            if (!showInvis.isToggled() && entityPlayer.isInvisible()) {
                continue;
            }
            if (entityPlayer == mc.thePlayer && (!renderSelf.isToggled() || mc.gameSettings.thirdPersonView == 0)) {
                continue;
            }
            if (entityPlayer.getDisplayNameString().isEmpty() || (entityPlayer != mc.thePlayer && AntiBot.isBot(entityPlayer))) {
                continue;
            }

            double interpolatedX = entityPlayer.lastTickPosX + (entityPlayer.posX - entityPlayer.lastTickPosX) * pTicks - mc.getRenderManager().viewerPosX;
            double interpolatedY = entityPlayer.lastTickPosY + (entityPlayer.posY - entityPlayer.lastTickPosY) * pTicks - mc.getRenderManager().viewerPosY;
            double interpolatedZ = entityPlayer.lastTickPosZ + (entityPlayer.posZ - entityPlayer.lastTickPosZ) * pTicks - mc.getRenderManager().viewerPosZ;

            interpolatedY += entityPlayer.isSneaking() ? entityPlayer.height - 0.05 : entityPlayer.height + 0.27;

            double[] convertedPosition = RenderUtils.convertTo2D(interpolatedX, interpolatedY, interpolatedZ);
            if (convertedPosition == null) {
                continue;
            }
            if (convertedPosition[2] >= 0.0D && convertedPosition[2] < 1.0D) {
                double[] headConvertedPosition = RenderUtils.convertTo2D(interpolatedX, interpolatedY + 1.0D, interpolatedZ);
                if (headConvertedPosition == null) {
                    continue;
                }
                double height = Math.abs(headConvertedPosition[1] - convertedPosition[1]);
                entityPositions.put(entityPlayer, new double[]{convertedPosition[0], convertedPosition[1], height, convertedPosition[2]});
            }
        }
    }

    private void renderArmor(EntityPlayer e) {
        int pos = 0;
        for (ItemStack is : e.inventory.armorInventory) {
            if (is != null) {
                pos -= 8;
            }
        }
        if (e.getHeldItem() != null) {
            pos -= 8;
            ItemStack item = e.getHeldItem().copy();
            if (item.hasEffect() && (item.getItem() instanceof ItemTool || item.getItem() instanceof ItemArmor)) {
                item.stackSize = 1;
            }
            renderItemStack(item, pos, -20);
            pos += 16;
        }
        for (int i = 3; i >= 0; --i) {
            ItemStack stack = e.inventory.armorInventory[i];
            if (stack != null) {
                renderItemStack(stack, pos, -20);
                pos += 16;
            }
        }
    }

    private void renderItemStack(ItemStack stack, int xPos, int yPos) {
        GlStateManager.pushMatrix();
        mc.getRenderItem().zLevel = -150.0F;
        GlStateManager.enableDepth();
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, xPos, yPos - 8);
        mc.getRenderItem().zLevel = 0.0F;
        GlStateManager.disableDepth();
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.translate(0, -10, 0);
        renderText(stack, xPos, yPos);
        GlStateManager.enableDepth();
        GlStateManager.scale(2, 2, 2);
        GlStateManager.popMatrix();
    }

    private void renderText(ItemStack stack, int xPos, int yPos) {
        int newYPos = yPos - 24;
        if (showDurability.isToggled() && stack.getItem() instanceof ItemArmor) {
            int remainingDurability = stack.getMaxDamage() - stack.getItemDamage();
            mc.fontRendererObj.drawString(String.valueOf(remainingDurability), (float) (xPos * 2), (float) yPos, 16777215, dropShadow.isToggled());
        }
        if (showEnchants.isToggled() && stack.getEnchantmentTagList() != null && stack.getEnchantmentTagList().tagCount() < 6) {
            if (stack.getItem() instanceof ItemTool || stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemBow || stack.getItem() instanceof ItemArmor) {
                NBTTagList nbttaglist = stack.getEnchantmentTagList();
                for(int i = 0; i < nbttaglist.tagCount(); ++i) {
                    int id = nbttaglist.getCompoundTagAt(i).getShort("id");
                    int lvl = nbttaglist.getCompoundTagAt(i).getShort("lvl");
                    if (lvl > 0) {
                        String abbreviated = getEnchantmentAbbreviated(id);
                        mc.fontRendererObj.drawString(abbreviated + lvl, (float) (xPos * 2), (float) newYPos, -1, dropShadow.isToggled());
                        newYPos += 8;
                    }
                }
            }
        }
        if (showStackSize.isToggled() && !(stack.getItem() instanceof ItemSword) && !(stack.getItem() instanceof ItemBow) && !(stack.getItem() instanceof ItemTool) && !(stack.getItem() instanceof ItemArmor)) {
            mc.fontRendererObj.drawString(stack.stackSize + "x", (float) (xPos * 2), (float) yPos, -1, dropShadow.isToggled());
        }
    }

    private String getEnchantmentAbbreviated(int id) {
        switch (id) {
            case 0:
                return "pt";   // Protection
            case 1:
                return "frp";   // Fire Protection
            case 2:
                return "ff";    // Feather Falling
            case 3:
                return "blp";   // Blast Protection
            case 4:
                return "prp";   // Projectile Protection
            case 5:
                return "thr";   // Thorns
            case 6:
                return "res";   // Respiration
            case 7:
                return "aa";    // Aqua Affinity
            case 16:
                return "sh";   // Sharpness
            case 17:
                return "smt";   // Smite
            case 18:
                return "ban";   // Bane of Arthropods
            case 19:
                return "kb";    // Knockback
            case 20:
                return "fa";    // Fire Aspect
            case 21:
                return "lot";  // Looting
            case 32:
                return "eff";   // Efficiency
            case 33:
                return "sil";   // Silk Touch
            case 34:
                return "ub";   // Unbreaking
            case 35:
                return "for";   // Fortune
            case 48:
                return "pow";   // Power
            case 49:
                return "pun";   // Punch
            case 50:
                return "flm";   // Flame
            case 51:
                return "inf";   // Infinity
            default:
                return null;
        }
    }
}
