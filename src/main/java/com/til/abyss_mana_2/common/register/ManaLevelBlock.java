package com.til.abyss_mana_2.common.register;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.common.AllBlock;
import com.til.abyss_mana_2.common.capability.*;
import com.til.abyss_mana_2.util.extension.Extension;
import com.til.abyss_mana_2.util.extension.List;
import com.til.abyss_mana_2.util.extension.Map;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class ManaLevelBlock extends RegisterBasics<ManaLevelBlock> {

    public static IForgeRegistry<ManaLevelBlock> register = null;

    public ManaLevelBlock(String name) {
        this(new ResourceLocation(AbyssMana2.MODID, name));
    }

    public ManaLevelBlock(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    public abstract Block getBlock(ManaLevel manaLevel);

    public int getLayer() {
        return 0;
    }

    @SubscribeEvent
    public void register(RegistryEvent.Register<ManaLevelBlock> event) {
        event.getRegistry().register(this);
    }

    public static class Mechanics extends ManaLevelBlock {

        public final Class<? extends TileEntity> tileClass;

        public Mechanics(String name, Class<? extends TileEntity> tileClass) {
            this(new ResourceLocation(AbyssMana2.MODID, name), tileClass);
        }

        public Mechanics(ResourceLocation resourceLocation, Class<? extends TileEntity> tileClass) {
            super(resourceLocation);
            this.tileClass = tileClass;
        }

        public Class<? extends TileEntity> getBlockTileEntity() {
            return tileClass;
        }

        @Override
        public Block getBlock(ManaLevel manaLevel) {
            return new AllBlock.MechanicsBlock.ShapedTypeMaterial(Objects.requireNonNull(manaLevel.getRegistryName()).getResourcePath() + "_" + Objects.requireNonNull(getRegistryName()).getResourcePath(), getBlockTileEntity());
        }
    }

    /***
     * 中继器
     * 在提取相应方块能力
     */
    public static ManaLevelBlock repeater;

    /***
     * 回旋升压
     */
    public static Mechanics whirlBoost;

    /***
     * 研磨
     */
    public static Mechanics grind;

    /***
     * 聚灵
     */
    public static Mechanics gatherMana;

    /***
     * 灵气提提取器
     */
    public static Mechanics extractMana;

    /***
     * 洗涤
     */
    public static Mechanics wash;

    /***
     * 离心
     */
    public static Mechanics centrifugal;

    /***
     * 打包
     */
    public static ManaLevelBlock pack;

    /***
     * 解包
     */
    public static ManaLevelBlock unpack;

    public static void init() {
        repeater = (ManaLevelBlock) new ManaLevelBlock("repeater") {
            @Override
            public Block getBlock(ManaLevel manaLevel) {
                GameRegistry.registerTileEntity(RepeaterTile.class, new ResourceLocation(AbyssMana2.MODID, RepeaterTile.class.getName()));
                return new AllBlock.MechanicsBlock(Objects.requireNonNull(manaLevel.getRegistryName()).getResourcePath() + "_" + Objects.requireNonNull(getRegistryName()).getResourcePath()) {
                    @Override
                    public TileEntity createNewTileEntity(World worldIn, int meta) {
                        return new RepeaterTile();
                    }

                    @Override
                    public IBlockState withRotation(IBlockState state, Rotation rot) {
                        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
                    }

                    @Override
                    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
                        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
                    }

                    @Override
                    public IBlockState getStateFromMeta(int metadata) {
                        return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(metadata));
                    }

                    @Override
                    public int getMetaFromState(IBlockState state) {
                        return state.getValue(FACING).getIndex();
                    }

                    @Override
                    protected BlockStateContainer createBlockState() {
                        return new BlockStateContainer(this, FACING);
                    }

                    @Override
                    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
                        return this.getDefaultState().withProperty(FACING, facing.getOpposite());
                    }

                }.setLightLevel(1).setLightOpacity(0);
            }
        }.setOrePrefix("Repeater");
        whirlBoost = (Mechanics) new Mechanics("whirl_boost", WhirlBoostTileEntity.class).setOrePrefix("WhirlBoost");
        grind = (Mechanics) new Mechanics("grind", RunTileEntity.GrindTileEntity.class).setOrePrefix("Grind");
        gatherMana = (Mechanics) new Mechanics("gather_mana", GatherManaTileEntity.class).setOrePrefix("GatherMana");
        extractMana = (Mechanics) new Mechanics("extract_mana", RunTileEntity.ExtractMana.class).setOrePrefix("ExtractMana");
        wash = (Mechanics) new Mechanics("wash", RunTileEntity.Wash.class).setOrePrefix("Wash");
        centrifugal = (Mechanics) new Mechanics("centrifugal", RunTileEntity.Centrifugal.class).setOrePrefix("Centrifugal");
        pack = (Mechanics) new Mechanics("pack", RunTileEntity.Pack.class).setOrePrefix("Pack");
        unpack = (Mechanics) new Mechanics("unpack", RunTileEntity.UnPack.Pack.class).setOrePrefix("UnPack");
    }

    public static final PropertyDirection FACING = PropertyDirection.create("facing");

    public static class RepeaterTile extends TileEntity {

        @Nullable
        @Override
        public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
            if (facing == null) {
                int blockMeta = getBlockMetadata();
                EnumFacing enumFacing = EnumFacing.getFront(blockMeta);
                TileEntity _tileEntity = getWorld().getTileEntity(this.getPos().add(enumFacing.getDirectionVec()));
                if (_tileEntity != null && _tileEntity != this) {
                    return _tileEntity.getCapability(capability, enumFacing.getOpposite());
                }
            }
            return super.getCapability(capability, facing);
        }

    }

    public abstract static class RunTileEntity extends TileEntity implements ITileEntityType, ITickable {
        IControl iControl;
        IHandle iHandle;
        IManaLevel iManaLevel;
        IClockTime iClockTime;

        @Override
        public void update() {
            if (!world.isRemote) {
                iHandle.update();
            }
        }

        @Override
        public Map<Capability<?>, Object> getAllCapabilities(AttachCapabilitiesEvent<TileEntity> event, Map<Capability<?>, Object> map) {
            iManaLevel = new IManaLevel.GetManaLevel(event.getObject());
            iControl = new IControl.Control(event.getObject(), iManaLevel);
            iClockTime = new IClockTime.ClockTime(iManaLevel);
            iHandle = new IHandle.Handle(event.getObject(), new List<ShapedType>().add_chainable(getShapedType()), iControl, iManaLevel, iClockTime);
            map.put(AllCapability.I_CONTROL, iControl);
            map.put(AllCapability.I_HANDEL, iHandle);
            map.put(AllCapability.I_MANA_LEVEL, iManaLevel);
            return map;
        }

        public abstract ShapedType getShapedType();

        public static class GrindTileEntity extends RunTileEntity {

            @Override
            public ShapedType getShapedType() {
                return ShapedType.grind;
            }
        }

        public static class ExtractMana extends RunTileEntity {

            @Override
            public ShapedType getShapedType() {
                return ShapedType.extractMana;
            }
        }

        public static class Wash extends RunTileEntity {

            @Override
            public ShapedType getShapedType() {
                return ShapedType.wash;
            }
        }

        public static class Centrifugal extends RunTileEntity {

            @Override
            public ShapedType getShapedType() {
                return ShapedType.centrifugal;
            }
        }

        public static class UnPack extends RunTileEntity {

            @Override
            public ShapedType getShapedType() {
                return ShapedType.unpack;
            }
        }

        public static class Pack extends RunTileEntity {

            @Override
            public ShapedType getShapedType() {
                return ShapedType.pack;
            }
        }

    }

    public static class GatherManaTileEntity extends TileEntity implements ITileEntityType {
        @Override
        public Map<Capability<?>, Object> getAllCapabilities(AttachCapabilitiesEvent<TileEntity> event, Map<Capability<?>, Object> map) {
            IManaLevel iManaLevel = new IManaLevel.GetManaLevel(event.getObject());
            IManaHandle iManaHandle = new IManaHandle.ManaHandle(event.getObject(), iManaLevel);
            map.put(AllCapability.I_MANA_LEVEL, iManaLevel);
            map.put(AllCapability.I_MANA_HANDEL, iManaHandle);
            return map;
        }
    }

    public static class WhirlBoostTileEntity extends TileEntity implements ITileEntityType, ITickable {

        IManaHandle.ManaHandle.WhirlBoostManaHandle iManaHandle;

        @Override
        public void update() {
            if (!world.isRemote) {
                iManaHandle.update();
            }
        }

        @Override
        public Map<Capability<?>, Object> getAllCapabilities(AttachCapabilitiesEvent<TileEntity> event, Map<Capability<?>, Object> map) {
            IManaLevel iManaLevel = new IManaLevel.GetManaLevel(event.getObject());
            IControl iControl = new IControl.Control(event.getObject(), iManaLevel);
            iManaHandle = new IManaHandle.ManaHandle.WhirlBoostManaHandle(event.getObject(), iManaLevel, iControl);
            map.put(AllCapability.I_MANA_LEVEL, iManaLevel);
            map.put(AllCapability.I_MANA_HANDEL, iManaHandle);
            map.put(AllCapability.I_CONTROL, iControl);
            return map;
        }
    }

}
