package com.til.abyss_mana_2.common.register;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.common.AllBlock;
import com.til.abyss_mana_2.common.capability.*;
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

public class ManaLevelBlock extends RegisterBasics<ManaLevelBlock> {

    public static IForgeRegistry<ManaLevelBlock> register = null;

    public ManaLevelBlock(String name) {
        this(new ResourceLocation(AbyssMana2.MODID, name));
    }

    public ManaLevelBlock(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    public Block getBlock(ManaLevel manaLevel) {
        return new AllBlock.TranslucentBlock(Objects.requireNonNull(manaLevel.getRegistryName()).getResourcePath() + "_" + Objects.requireNonNull(getRegistryName()).getResourcePath());
    }

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
     * 基础框架
     */
    public static ManaLevelBlock frameBasic;

    /***
     * 回旋升压
     */
    public static Mechanics whirlBoost;

    //_______________

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
    public static Mechanics pack;

    /***
     * 解包
     */
    public static Mechanics unpack;

    /***
     * 组装机
     */
    public static Mechanics assemble;

    /***
     * 冲压机
     */
    public static Mechanics stampingMachine;

    /***
     * 扎线机
     */
    public static Mechanics tieWire;

    /***
     * 灵气凝结晶体
     */
    public static Mechanics manaCoagulation;

    /***
     * 升灵晶体
     */
    public static Mechanics upMana;

    /***
     * 灵气灌注
     */
    public static Mechanics manaPerfusion;

    /***
     * 高压融合
     */
    public static Mechanics highPressureFuse;

    /***
     * 溶解
     */
    public static Mechanics dissolution;

    /***
     * 凝固
     */
    public static Mechanics freezing;

    /***
     * 结晶
     */
    public static Mechanics crystallizing;

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
        frameBasic = (ManaLevelBlock) new ManaLevelBlock("frame_basic").setOrePrefix("FrameBasic");
        whirlBoost = (Mechanics) new Mechanics("whirl_boost", WhirlBoostTileEntity.class).setOrePrefix("WhirlBoost");
        grind = (Mechanics) new Mechanics("grind", RunTileEntity.GrindTileEntity.class).setOrePrefix("Grind");
        gatherMana = (Mechanics) new Mechanics("gather_mana", GatherManaTileEntity.class).setOrePrefix("GatherMana");
        extractMana = (Mechanics) new Mechanics("extract_mana", RunTileEntity.ExtractMana.class).setOrePrefix("ExtractMana");
        wash = (Mechanics) new Mechanics("wash", RunTileEntity.Wash.class).setOrePrefix("Wash");
        centrifugal = (Mechanics) new Mechanics("centrifugal", RunTileEntity.Centrifugal.class).setOrePrefix("Centrifugal");
        pack = (Mechanics) new Mechanics("pack", RunTileEntity.Pack.class).setOrePrefix("Pack");
        unpack = (Mechanics) new Mechanics("unpack", RunTileEntity.Pack.class).setOrePrefix("UnPack");
        assemble = (Mechanics) new Mechanics("assemble", RunTileEntity.Assemble.class).setOrePrefix("Assemble");
        stampingMachine = (Mechanics) new Mechanics("stamping_machine", RunTileEntity.StampingMachine.class).setOrePrefix("StampingMachine");
        tieWire = (Mechanics) new Mechanics("tie_wire", RunTileEntity.TieWire.class).setOrePrefix("TieWire");
        manaCoagulation = (Mechanics) new Mechanics("mana_coagulation", RunTileEntity.ManaCoagulation.class).setOrePrefix("ManaCoagulation");
        upMana = (Mechanics) new Mechanics("up_mana", RunTileEntity.UpMana.class).setOrePrefix("UpMana");
        manaPerfusion = (Mechanics) new Mechanics("mana_perfusion", RunTileEntity.ManaPerfusion.class).setOrePrefix("ManaPerfusion");
        highPressureFuse = (Mechanics) new Mechanics("high_pressure_fuse", RunTileEntity.HighPressureFuse.class).setOrePrefix("HighPressureFuse");
        dissolution = (Mechanics) new Mechanics("dissolution", RunTileEntity.Dissolution.class).setOrePrefix("Dissolution");
        freezing = (Mechanics) new Mechanics("freezing", RunTileEntity.Freezing.class).setOrePrefix("Freezing");
        crystallizing = (Mechanics) new Mechanics("crystallizing", RunTileEntity.Crystallizing.class).setOrePrefix("Crystal");
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

    @Deprecated
    public static class TransmissionRepeaterTile extends TileEntity implements ITileEntityType {

        IManaLevel iManaLevel;
        IControl iControl;

        /***
         * y一个防止递归调用的
         */
        boolean preventRecursion;

        @Override
        public Map<Capability<?>, Object> getAllCapabilities(AttachCapabilitiesEvent<TileEntity> event, Map<Capability<?>, Object> map) {
            IManaLevel iManaLevel = new IManaLevel.GetManaLevel(event.getObject());
            IControl iControl = new IControl.Control(event.getObject(), new List<BindType>().add_chainable(new BindType[]{
                    BindType.relayIn
            }), iManaLevel);
            map.put(AllCapability.I_MANA_LEVEL, iManaLevel);
            map.put(AllCapability.I_CONTROL, iControl);
            return map;
        }

        @Nullable
        @Override
        public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
            if (preventRecursion) {
                return null;
            }
            T t = super.getCapability(capability, facing);
            preventRecursion = true;
            if (t == null) {
                List<TileEntity> tileEntitys = iControl.getAllTileEntity(BindType.relayIn);
                for (TileEntity tileEntity : tileEntitys) {
                    T _t = tileEntity.getCapability(capability, facing);
                    if (_t != null) {
                        preventRecursion = false;
                        return _t;
                    }
                }
            }
            return t;
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
            iManaLevel = newManaLevel(event, map);
            iControl = newControl(event, map);
            iClockTime = newClockTime(event, map);
            iHandle = newHandle(event, map);
            map.put(AllCapability.I_CONTROL, iControl);
            map.put(AllCapability.I_HANDEL, iHandle);
            map.put(AllCapability.I_MANA_LEVEL, iManaLevel);
            map.put(AllCapability.I_CLOCK_TIME, iClockTime);
            return map;
        }

        public IManaLevel newManaLevel(AttachCapabilitiesEvent<TileEntity> event, Map<Capability<?>, Object> map) {
            return new IManaLevel.GetManaLevel(event.getObject());
        }

        public IControl newControl(AttachCapabilitiesEvent<TileEntity> event, Map<Capability<?>, Object> map) {
            return new IControl.Control(event.getObject(), getBindType(), iManaLevel);
        }

        public IClockTime newClockTime(AttachCapabilitiesEvent<TileEntity> event, Map<Capability<?>, Object> map) {
            return new IClockTime.ClockTime(iManaLevel);
        }

        public IHandle newHandle(AttachCapabilitiesEvent<TileEntity> event, Map<Capability<?>, Object> map) {
            return new IHandle.Handle(event.getObject(), new List<ShapedType>().add_chainable(getShapedType()), getBindType(), iControl, iManaLevel, iClockTime);
        }

        public abstract ShapedType getShapedType();

        public List<BindType> getBindType() {
            return new List<BindType>().add_chainable(new BindType[]{
                    BindType.itemIn,
                    BindType.itemOut,
                    BindType.manaIn,
                    BindType.manaOut,
                    BindType.fluidIn,
                    BindType.fluidOut,
                    BindType.modelStore,
            });
        }

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

        public static class Assemble extends RunTileEntity {

            @Override
            public ShapedType getShapedType() {
                return ShapedType.assemble;
            }
        }

        public static class StampingMachine extends RunTileEntity {

            @Override
            public ShapedType getShapedType() {
                return ShapedType.stampingMachine;
            }
        }

        public static class TieWire extends RunTileEntity {
            @Override
            public ShapedType getShapedType() {
                return ShapedType.tieWire;
            }
        }

        public static class ManaCoagulation extends RunTileEntity {
            @Override
            public ShapedType getShapedType() {
                return ShapedType.manaCoagulation;
            }

            @Override
            public IClockTime newClockTime(AttachCapabilitiesEvent<TileEntity> event, Map<Capability<?>, Object> map) {
                return new IClockTime.ClockTime(iManaLevel) {
                    @Override
                    public int getCycleTime() {
                        return Shaped.extractMana.surplusTiem() / iManaLevel.getManaLevel().getLevel();
                    }
                };
            }

            @Override
            public IHandle newHandle(AttachCapabilitiesEvent<TileEntity> event, Map<Capability<?>, Object> map) {
                return new IHandle.Handle(event.getObject(), new List<ShapedType>().add_chainable(getShapedType()), getBindType(), iControl, iManaLevel, iClockTime) {
                    @Override
                    public int getParallelHandle() {
                        return 1;
                    }
                };
            }

            @Override
            public List<BindType> getBindType() {
                return new List<BindType>().add_chainable(new BindType[]{
                        BindType.modelStore,
                        BindType.fluidOut,
                        BindType.manaIn,
                });
            }
        }

        public static class UpMana extends RunTileEntity {

            @Override
            public ShapedType getShapedType() {
                return ShapedType.upMana;
            }
        }

        public static class ManaPerfusion extends RunTileEntity {

            @Override
            public ShapedType getShapedType() {
                return ShapedType.manaPerfusion;
            }
        }

        public static class HighPressureFuse extends RunTileEntity {
            @Override
            public ShapedType getShapedType() {
                return ShapedType.highPressureFuse;
            }
        }

        public static class Dissolution extends RunTileEntity {

            @Override
            public ShapedType getShapedType() {
                return ShapedType.dissolution;
            }
        }

        public static class Freezing extends RunTileEntity {

            @Override
            public ShapedType getShapedType() {
                return ShapedType.freezing;
            }
        }

        public static class Crystallizing extends RunTileEntity {

            @Override
            public ShapedType getShapedType() {
                return ShapedType.crystallizing;
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

    public static class WhirlBoostTileEntity extends TileEntity implements ITileEntityType {

        IManaHandle.ManaHandle.WhirlBoostManaHandle iManaHandle;

        @Override
        public Map<Capability<?>, Object> getAllCapabilities(AttachCapabilitiesEvent<TileEntity> event, Map<Capability<?>, Object> map) {
            IManaLevel iManaLevel = new IManaLevel.GetManaLevel(event.getObject());
            IControl iControl = new IControl.Control(event.getObject(), new List<BindType>().add_chainable(BindType.manaIn).add_chainable(BindType.manaOut), iManaLevel);
            iManaHandle = new IManaHandle.ManaHandle.WhirlBoostManaHandle(event.getObject(), iManaLevel, iControl);
            map.put(AllCapability.I_MANA_LEVEL, iManaLevel);
            map.put(AllCapability.I_MANA_HANDEL, iManaHandle);
            map.put(AllCapability.I_CONTROL, iControl);
            return map;
        }
    }

}
