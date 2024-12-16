package keystrokesmod.clickgui.components.impl;

import keystrokesmod.Raven;
import keystrokesmod.clickgui.components.Component;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.client.Gui;
import keystrokesmod.utility.RenderUtils;
import keystrokesmod.utility.Timer;
import keystrokesmod.utility.Utils;
import keystrokesmod.utility.profile.Manager;
import keystrokesmod.utility.profile.Profile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CategoryComponent {
    public List<ModuleComponent> modules = new CopyOnWriteArrayList<>();
    public Module.category categoryName;
    public boolean opened;
    private int width;
    private int y;
    private int x;
    private int titleHeight;
    public boolean dragging;
    public int xx;
    public int yy;
    public boolean n4m = false;
    public String pvp;
    public boolean pin = false;
    public boolean hovering = false;
    public boolean hoveringOverCategory = false;
    public Timer smoothTimer;
    private Timer textTimer;
    public Timer moduleSmoothTimer;
    public ScaledResolution scale;
    private float big;
    private float bigSettings;
    private final int translucentBackground = new Color(0, 0, 0, 110).getRGB();
    private final  int regularOutline = new Color(81, 99, 149).getRGB();
    private final  int regularOutline2 = new Color(97, 67, 133).getRGB();
    private final  int categoryNameColor = new Color(220, 220, 220).getRGB();
    private float lastHeight;
    public int moduleY;
    private int lastModuleY;
    private int screenHeight;
    private boolean scrolled;

    public CategoryComponent(Module.category category) {
        this.categoryName = category;
        this.width = 92;
        this.x = 5;
        this.moduleY = this.y = 5;
        this.titleHeight = 13;
        this.smoothTimer = null;
        this.textTimer = null;
        this.xx = 0;
        this.opened = false;
        this.dragging = false;
        int moduleRenderX = this.titleHeight + 3;
        this.scale = new ScaledResolution(Minecraft.getMinecraft());

        for (Iterator var3 = Raven.getModuleManager().inCategory(this.categoryName).iterator(); var3.hasNext(); moduleRenderX += 16) {
            Module mod = (Module) var3.next();
            ModuleComponent b = new ModuleComponent(mod, this, moduleRenderX);
            this.modules.add(b);
        }
    }

    public List<ModuleComponent> getModules() {
        return this.modules;
    }

    public void reloadModules(boolean isProfile) {
        this.modules.clear();
        this.titleHeight = 13;
        int moduleRenderY = this.titleHeight + 3;

        if ((this.categoryName == Module.category.profiles && isProfile) || (this.categoryName == Module.category.scripts && !isProfile)) {
            ModuleComponent manager = new ModuleComponent(isProfile ? new Manager() : new keystrokesmod.script.Manager(), this, moduleRenderY);
            this.modules.add(manager);

            if ((Raven.profileManager == null && isProfile) || (Raven.scriptManager == null && !isProfile)) {
                return;
            }

            if (isProfile) {
                for (Profile profile : Raven.profileManager.profiles) {
                    moduleRenderY += 16;
                    ModuleComponent b = new ModuleComponent(profile.getModule(), this, moduleRenderY);
                    this.modules.add(b);
                }
            }
            else {
                for (Module module : Raven.scriptManager.scripts.values()) {
                    moduleRenderY += 16;
                    ModuleComponent b = new ModuleComponent(module, this, moduleRenderY);
                    this.modules.add(b);
                }
            }
        }
    }

    public void setX(int n) {
        this.x = n;
    }

    public void setY(int y) {
        this.moduleY = this.y = y;
    }

    public void overTitle(boolean d) {
        this.dragging = d;
    }

    public boolean p() {
        return this.pin;
    }

    public void cv(boolean on) {
        this.pin = on;
    }

    public boolean isOpened() {
        return this.opened;
    }

    public void mouseClicked(boolean on) {
        this.opened = on;
        (this.smoothTimer = new Timer(300)).start();
        (this.textTimer = new Timer(200)).start();
    }

    public void openModule(ModuleComponent component) {
        (this.smoothTimer = new Timer(300)).start();
    }

    public void onScroll(int mouseScrollInput) {
        if (!hoveringOverCategory || !this.opened) {
            return;
        }
        int scrollSpeed = (int) Gui.scrollSpeed.getInput();
        if (mouseScrollInput > 0) {
            this.moduleY += scrollSpeed;
        }
        else if (mouseScrollInput < 0) {
            this.moduleY -= scrollSpeed;
        }
        scrolled = true;

        (moduleSmoothTimer = new Timer(200)).start();
    }

    public void render(FontRenderer renderer) {
        this.moduleY = Math.min(this.moduleY, this.y);
        if (this.moduleY + this.bigSettings < this.y + this.big + this.titleHeight) {
            this.moduleY = (int) (this.y + this.big - this.bigSettings);
        }
        this.width = 92;
        int modulesHeight = 0;
        int settingsHeight = 0;
        if (!this.modules.isEmpty() && this.opened) {
            Iterator<ModuleComponent> iterator = this.modules.iterator();
            while (iterator.hasNext()) {
                ModuleComponent c = iterator.next();
                settingsHeight += c.getHeight();
                if (modulesHeight + c.getHeight() > this.screenHeight - 40) { // max category height
                    continue;
                }
                modulesHeight += c.getHeight();
            }
            big = modulesHeight;
            bigSettings = settingsHeight;
        }

        float middlePos = (float) (this.x + this.width / 2 - Minecraft.getMinecraft().fontRendererObj.getStringWidth(this.categoryName.name()) / 2);
        float xPos = opened ? middlePos : this.x + 12;
        float extra = this.y + this.titleHeight + modulesHeight + 4;
        if (smoothTimer != null && System.currentTimeMillis() - smoothTimer.last >= 400) {
            smoothTimer = null;
        }
        if (extra != lastHeight && smoothTimer != null) {
            double diff = lastHeight - extra;
            if (diff < 0) {
                extra = smoothTimer.getValueFloat(lastHeight, this.y + this.titleHeight + modulesHeight + 4, 1);
            }
            else if (diff > 0) {
                extra = (this.y + this.titleHeight + 4 + big) - smoothTimer.getValueFloat(0, big, 1);
            }
        }
        float namePos = textTimer == null ? xPos : textTimer.getValueFloat(this.x + 12, middlePos, 1);
        if (!this.opened) {
            namePos = textTimer == null ? xPos : middlePos - textTimer.getValueFloat(0, this.width / 2 - Minecraft.getMinecraft().fontRendererObj.getStringWidth(this.categoryName.name()) / 2 - 12, 1);
        }
        if (scrolled && lastModuleY != moduleY) {
            moduleY = (int) moduleSmoothTimer.getValueFloat(lastModuleY, moduleY, 1);
        }
        else {
            scrolled = false;
            lastModuleY = moduleY;
        }
        lastHeight = extra;
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtils.scissor(0, this.y - 2, this.x + this.width + 4, extra - this.y + 4);
        RenderUtils.drawRoundedGradientOutlinedRectangle(this.x - 2, this.y, this.x + this.width + 2, extra, 9, translucentBackground,
                ((opened || hovering) && Gui.rainBowOutlines.isToggled()) ? RenderUtils.setAlpha(Utils.getChroma(2, 0), 0.5) : regularOutline, ((opened || hovering) && Gui.rainBowOutlines.isToggled()) ? RenderUtils.setAlpha(Utils.getChroma(2, 700), 0.5) : regularOutline2);
        renderItemForCategory(this.categoryName, this.x + 1, this.y + 4, opened || hovering);
        renderer.drawString(this.n4m ? this.pvp : this.categoryName.name(), namePos, (float) (this.y + 4), categoryNameColor, false);
        RenderUtils.scissor(0, this.y + this.titleHeight + 3, this.x + this.width + 4, extra - this.y - 4 - this.titleHeight);
        if (!this.n4m) {
            int prevY = this.y;
            this.y = (int) this.moduleY;
            if ((this.opened || smoothTimer != null) && !this.modules.isEmpty()) {
                Iterator var5 = this.modules.iterator();

                while (var5.hasNext()) {
                    Component c2 = (Component) var5.next();
                    c2.render();
                }
            }
            this.y = prevY;
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopMatrix();
    }

    public void render() {
        int o = this.titleHeight + 3;

        Component component;
        for (Iterator var2 = this.modules.iterator(); var2.hasNext(); o += component.getHeight()) {
            component = (Component) var2.next();
            component.so(o);
        }

    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getModuleY() {
        return this.moduleY;
    }

    public int getWidth() {
        return this.width;
    }

    public void mousePosition(int x, int y) {
        if (this.dragging) {
            this.setX(x - this.xx);
            this.setY(y - this.yy);
        }
        hoveringOverCategory = overCategory(x, y);
        hovering = overTitle(x, y);
    }

    public boolean i(int x, int y) {
        return x >= this.x + 92 - 13 && x <= this.x + this.width && (float) y >= (float) this.y + 2.0F && y <= this.y + this.titleHeight + 1;
    }

    public boolean overTitle(int x, int y) {
        return x >= this.x && x <= this.x + this.width && (float) y >= (float) this.y + 2.0F && y <= this.y + this.titleHeight + 1;
    }

    public boolean overCategory(int x, int y) {
        return x >= this.x - 2 && x <= this.x + this.width + 2 && (float) y >= (float) this.y + 2.0F && y <= this.y + this.titleHeight + big + 1;
    }

    public boolean v(int x, int y) {
        return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.titleHeight;
    }

    private void renderItemForCategory(Module.category category, int x, int y, boolean enchant) {
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        double scale = 0.55;
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);
        ItemStack itemStack = null;
        switch (category) {
            case combat:
                itemStack = new ItemStack(Items.diamond_sword);
                break;
            case movement:
                itemStack = new ItemStack(Items.diamond_boots);
                break;
            case player:
                itemStack = new ItemStack(Items.golden_apple);
                break;
            case world:
                itemStack = new ItemStack(Items.map);
                break;
            case render:
                itemStack = new ItemStack(Items.ender_eye);
                break;
            case minigames:
                itemStack = new ItemStack(Items.gold_ingot);
                break;
            case fun:
                itemStack = new ItemStack(Items.slime_ball);
                break;
            case other:
                itemStack = new ItemStack(Items.clock);
                break;
            case client:
                itemStack = new ItemStack(Items.compass);
                break;
            case profiles:
                itemStack = new ItemStack(Items.book);
                break;
            case scripts:
                itemStack = new ItemStack(Items.redstone);
                break;
        }
        if (itemStack != null) {
            if (enchant) {
                if (category != Module.category.player) {
                    itemStack.addEnchantment(Enchantment.unbreaking, 2);
                }
                else {
                    itemStack.setItemDamage(1);
                }
            }
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.disableBlend();
            renderItem.renderItemAndEffectIntoGUI(itemStack, (int) (x / scale), (int) (y / scale));
            GlStateManager.enableBlend();
            RenderHelper.disableStandardItemLighting();
        }
        GlStateManager.scale(1, 1, 1);
        GlStateManager.popMatrix();
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }
}
