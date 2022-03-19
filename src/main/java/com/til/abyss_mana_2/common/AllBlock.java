package com.til.abyss_mana_2.common;

import com.til.abyss_mana_2.AbyssMana2;
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
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

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
        public BlockRenderLayer getBlockLayer() {
            return BlockRenderLayer.CUTOUT_MIPPED;
        }

    }

    public static class FallBlockBasic extends BlockFalling {

        public FallBlockBasic(Material material, SoundType soundType, CreativeTabs creativeTab, ResourceLocation name, String toolClass, int level, float hardness, float resistance) {
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
        public BlockRenderLayer getBlockLayer() {
            return BlockRenderLayer.CUTOUT_MIPPED;
        }

    }

    public static class TranslucentBlock extends ModBlock {
        public TranslucentBlock(String name) {
            super(Material.GLASS, MapColor.GRASS, SoundType.GLASS, ModTab.TAB, new ResourceLocation(AbyssMana2.MODID, name), "pickaxe", 2, 2.25f, 12);
            translucent = true;
            setLightLevel(1);
            setLightOpacity(1);
        }

        @Override
        public boolean isOpaqueCube(IBlockState state) {
            return false;
        }

        @SideOnly(Side.CLIENT)
        @Override
        public BlockRenderLayer getBlockLayer() {
            return BlockRenderLayer.TRANSLUCENT;
        }
    }

    public abstract static class MechanicsBlock extends ModBlock implements ITileEntityProvider {

        public MechanicsBlock(Material material, MapColor blockMapColorIn, SoundType soundType, CreativeTabs creativeTab, ResourceLocation name, String toolClass, int level, float hardness, float resistance) {
            super(material, blockMapColorIn, soundType, creativeTab, name, toolClass, level, hardness, resistance);
            translucent = true;
            setLightLevel(1);
            setLightOpacity(0);
        }

        public MechanicsBlock(String name) {
            this(Material.GLASS, MapColor.GRASS, SoundType.GLASS, ModTab.TAB, new ResourceLocation(AbyssMana2.MODID, name), "pickaxe", 2, 2.25f, 12);
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

        public static class ShapedTypeMaterial extends MechanicsBlock {

            public Class<? extends TileEntity> tileEntityClass;

            public ShapedTypeMaterial(String name, Class<? extends TileEntity> tileEntityClass) {
                super(name);
                this.tileEntityClass = tileEntityClass;
                GameRegistry.registerTileEntity(tileEntityClass, new ResourceLocation(AbyssMana2.MODID, tileEntityClass.getName()));
            }

            @Nullable
            @Override
            public TileEntity createNewTileEntity(World worldIn, int meta) {
                try {
                    return tileEntityClass.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    AbyssMana2.logger.throwing(e);
                    return new TileEntity() {
                    };
                }
            }
        }
    }
}
