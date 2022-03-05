package com.til.abyss_mana_2.common;

import com.til.abyss_mana_2.AbyssMana2;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModTab {

    public static final CreativeTabs TAB = new CreativeTabs(AbyssMana2.MODID) {
        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem() {
            return new ItemStack(AllItem.bindStaff);
        }
    };

}
