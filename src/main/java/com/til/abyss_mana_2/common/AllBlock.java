package com.til.abyss_mana_2.common;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.common.capability.ITileEntityType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AllBlock {

    public static class ModBlock extends Block {

        public ModBlock(Material material, MapColor blockMapColorIn, SoundType soundType, CreativeTabs creativeTab, ResourceLocation name, String toolClass, int level, float hardness, float resistance) {
            super(material, blockMapColorIn);
            setRegistryName(name);
            setUnlocalizedName(name.getResourceDomain() + "." + name.getResourcePath());
            setSoundType(soundType);
            setHarvestLevel(toolClass, level);
            setHardness(hardness);
            setResistance(resistance);
            setCreativeTab(creativeTab);
        }

        @SideOnly(Side.CLIENT)
        @Override
        public  BlockRenderLayer getBlockLayer() {
            return BlockRenderLayer.CUTOUT_MIPPED;
        }

    }

    public static class FallBlockBasic extends BlockFalling {

        public FallBlockBasic(Material material,  SoundType soundType, CreativeTabs creativeTab, ResourceLocation name, String toolClass, int level, float hardness, float resistance) {
            super(material);
            setRegistryName(name);
            setUnlocalizedName(name.getResourceDomain() + "." + name.getResourcePath());
            setSoundType(soundType);
            setHarvestLevel(toolClass, level);
            setHardness(hardness);
            setResistance(resistance);
            setCreativeTab(creativeTab);
        }

        @SideOnly(Side.CLIENT)
        @Override
        public  BlockRenderLayer getBlockLayer() {
            return BlockRenderLayer.CUTOUT_MIPPED;
        }

    }

    public abstract static class MechanicsBlock extends ModBlock implements ITileEntityProvider {

        public MechanicsBlock(Material material, MapColor blockMapColorIn, SoundType soundType, CreativeTabs creativeTab, ResourceLocation name, String toolClass, int level, float hardness, float resistance) {
            super(material, blockMapColorIn, soundType, creativeTab, name, toolClass, level, hardness, resistance);
            translucent = true;
        }

        public MechanicsBlock(String name) {
            super(Material.GLASS, MapColor.GRASS, SoundType.GLASS, ModTab.TAB, new ResourceLocation(AbyssMana2.MODID, name), "pickaxe", 2, 2.25f, 12);
            translucent = true;
        }

        @Override
        public boolean isOpaqueCube(IBlockState state) {
            return false;
        }

        @Override
        public boolean hasTileEntity(IBlockState state) {
            return true;
        }


        @SideOnly(Side.CLIENT)
        @Override
        public BlockRenderLayer getBlockLayer() {
            return BlockRenderLayer.TRANSLUCENT;
        }


    }
}