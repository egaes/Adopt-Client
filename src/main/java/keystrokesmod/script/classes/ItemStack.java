package keystrokesmod.script.classes;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemBlock;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.List;

public class ItemStack {
    public String type;
    public String name;
    public String displayName;
    public int stackSize;
    public int maxStackSize;
    public int durability;
    public int maxDurability;
    public boolean isBlock;
    public net.minecraft.item.ItemStack itemStack;

    public ItemStack(net.minecraft.item.ItemStack itemStack) {
        if (itemStack == null) {
            return;
        }
        this.itemStack = itemStack;
        this.isBlock = itemStack.getItem() instanceof ItemBlock;
        this.type = isBlock ? ((ItemBlock) itemStack.getItem()).getBlock().getClass().getSimpleName() : itemStack.getItem().getClass().getSimpleName();
        this.name = itemStack.getItem().getRegistryName().substring(10); // substring 10 to remove "minecraft:"
        this.displayName = itemStack.getDisplayName();
        this.stackSize = itemStack.stackSize;
        this.maxStackSize = itemStack.getMaxStackSize();
        this.durability = itemStack.getItemDamage();
        this.maxDurability = itemStack.getMaxDamage();
    }

    public List<String> getToolTip() {
        if (this.itemStack == null) {
            return new ArrayList<>();
        }
        return this.itemStack.getTooltip(Minecraft.getMinecraft().thePlayer, false);
    }

    public List<Object[]> getEnchantments() {
        List<Object[]> enchantments = new ArrayList<>();
        if (this.itemStack != null && itemStack.getEnchantmentTagList() != null && !itemStack.getEnchantmentTagList().hasNoTags()) {
            for (int i = 0; i < itemStack.getEnchantmentTagList().tagCount(); i++) {
                NBTTagCompound tagCompound = itemStack.getEnchantmentTagList().getCompoundTagAt(i);
                short enchantmentId = -1;
                if (tagCompound.hasKey("ench")) {
                    enchantmentId = tagCompound.getShort("ench");
                }
                else if (tagCompound.hasKey("id")) {
                    enchantmentId = tagCompound.getShort("id");
                }
                if (enchantmentId == -1) {
                    continue;
                }
                short enchantLevel = 0;
                if (enchantmentId != -1) {
                    enchantLevel = tagCompound.getShort("lvl");
                }
                Enchantment enchantment = Enchantment.getEnchantmentById(enchantmentId);
                if (enchantment == null) {
                    continue;
                }
                String enchantmentStr = enchantment.getName().substring(12);
                enchantments.add(new Object[] { enchantmentStr, enchantLevel });
            }
        }
        return enchantments;
    }

    public static ItemStack convert(net.minecraft.item.ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }
        return new ItemStack(itemStack);
    }

    @Override
    public String toString() {
        return this.itemStack == null ? "" : this.itemStack.getItem().toString();
    }
}
