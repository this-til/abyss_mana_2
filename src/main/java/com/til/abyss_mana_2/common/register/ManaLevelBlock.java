package com.til.abyss_mana_2.common.register;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.common.AllBlock;
import com.til.abyss_mana_2.common.capability.*;
import com.til.abyss_mana_2.common.event.ModEvent;
import com.til.abyss_mana_2.util.data.AllNBT;
import com.til.abyss_mana_2.util.extension.GenericParadigmMap;
import com.til.abyss_mana_2.util.extension.List;
import com.til.abyss_mana_2.util.extension.Map;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ManaLevelBlock extends RegisterBasics<ManaLevelBlock> {

    public static IForgeRegistry<ManaLevelBlock> register = null;

    public ManaLevelBlock(String name, GenericParadigmMap genericParadigmMap) {
        this(new ResourceLocation(AbyssMana2.MODID, name), genericParadigmMap);
    }

    public ManaLevelBlock(ResourceLocation resourceLocation, GenericParadigmMap genericParadigmMap) {
        super(resourceLocation, genericParadigmMap);
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

        public Mechanics(String name, GenericParadigmMap genericParadigmMa) {
            this(new ResourceLocation(AbyssMana2.MODID, name), genericParadigmMa);
        }

        public Mechanics(ResourceLocation resourceLocation, GenericParadigmMap genericParadigmMap) {
            super(resourceLocation, genericParadigmMap);
        }

        @Override
        public Block getBlock(ManaLevel manaLevel) {
            return new AllBlock.MechanicsBlock.ShapedTypeMaterial(Objects.requireNonNull(manaLevel.getRegistryName()).getResourcePath() + "_" + Objects.requireNonNull(getRegistryName()).getResourcePath(), (Class<? extends TileEntity>) getGenericParadigmMap().get(tile));
        }

        @SubscribeEvent
        public void onEvent(ModEvent.ModEventLoad.init event) {
            for (ManaLevel manaLevel : ManaLevel.register) {
                ManaLevel up = manaLevel.getGenericParadigmMap().get(ManaLevel.up).func();
                if (up != null) {

                    Map<String, Integer> inIte = new Map<>();
                    {
                        inIte.put(getOreString(up, this), 1);
                        inIte.put(getOreString(manaLevel, frameBasic), 1);

                        inIte.putAll(getGenericParadigmMap().get(otherIn).func());
                    }

                    Map<String, Integer> inFluid = new Map<>();
                    {
                    }
                    Shaped shaped = new Shaped.ShapedOre(
                            new ResourceLocation(AbyssMana2.MODID, "do_" + getOreString(manaLevel, this)),
                            up,
                            ShapedType.assemble,
                            ShapedDrive.map.get(2),
                            inIte,
                            inFluid,
                            300 * 20 * up.getGenericParadigmMap().get(ManaLevel.level),
                            32L * up.getGenericParadigmMap().get(ManaLevel.level),
                            0,
                            new List<ItemStack>().add_chainable(new ItemStack(manaLevel.itemBlock.get(this))),
                            null
                    );

                    Shaped.register.register(shaped);
                }
            }
        }

        public static final GenericParadigmMap.IKey<GenericParadigmMap.IKey.KeyMapStingInt.Pack> otherIn = new GenericParadigmMap.IKey.KeyMapStingInt();

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

    /***
     * 虚空箱
     * 126000 * l
     */
    public static Mechanics voidCase;

    /***
     * 虚空缸
     * 126000mb * l
     */
    public static Mechanics voidCylinder;

    /***
     * 聚灵
     */
    public static Mechanics gatherMana;

    //_______________


    /***
     * 灵气提提取器
     */
    public static Mechanics extractMana;

    /***
     * 日光晶体
     * 太阳能神教
     */
    public static Mechanics sunlight;

    /***
     * 月光晶体
     */
    public static Mechanics moonlight;

    //_______________

    /***
     * 研磨
     */
    public static Mechanics grind;


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

    /***
     * 雕刻
     */
    public static Mechanics carving;

    /***
     * 高炉
     */
    public static Mechanics blastFurnace;

    /***
     * uu生成
     */
    public static Mechanics uuGenerate;

    /***
     * 质量生成
     */
    public static Mechanics qualityGenerate;

    public static void init() {
        repeater = new ManaLevelBlock("repeater", new GenericParadigmMap()
                .put_genericParadigm(tile, RepeaterTile.class)
                .put_genericParadigm(orePrefix, "Repeater")) {
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

            @SubscribeEvent
            public void onEvent(ModEvent.ModEventLoad.init event) {
                for (ManaLevel manaLevel : ManaLevel.register) {
                    ManaLevel up = manaLevel.getGenericParadigmMap().get(ManaLevel.up).func();
                    if (up != null) {

                        Map<String, Integer> inItem = new Map<>();
                        {
                            for (Ore ore : manaLevel.getGenericParadigmMap().get(ManaLevel.thisNeedItem).func()) {
                                inItem.put(getOreString(OreType.ingot, ore), 2);
                            }
                            inItem.put(getOreString(manaLevel, ManaLevelItem.io), 1);
                            inItem.put(getOreString(manaLevel, ManaLevelItem.rom), 1);
                            inItem.put(getOreString(manaLevel, ManaLevelItem.ram), 1);
                        }

                        Map<String, Integer> inFluid = new Map<>();
                        {
                            for (Ore ore : manaLevel.getGenericParadigmMap().get(ManaLevel.thisNeedFluid).func()) {
                                inFluid.put(ore.fluid.get(OreFluid.solution).getName(), 12 * manaLevel.getGenericParadigmMap().get(ManaLevel.level));
                            }
                        }

                        Shaped shaped = new Shaped.ShapedOre(
                                new ResourceLocation(AbyssMana2.MODID, "do_" + getOreString(manaLevel, this)),
                                up,
                                ShapedType.assemble,
                                ShapedDrive.map.get(3),
                                inItem,
                                inFluid,
                                30 * 20 * up.getGenericParadigmMap().get(ManaLevel.level),
                                16L * up.getGenericParadigmMap().get(ManaLevel.level),
                                0,
                                new List<ItemStack>().add_chainable(new ItemStack(manaLevel.itemBlock.get(this))),
                                null
                        );
                        Shaped.register.register(shaped);
                    }
                }
            }
        };
        frameBasic = new ManaLevelBlock("frame_basic", new GenericParadigmMap()
                .put_genericParadigm(orePrefix, "frameBasic")) {
            @SubscribeEvent
            public void onEvent(ModEvent.ModEventLoad.init event) {
                for (ManaLevel manaLevel : ManaLevel.register) {
                    ManaLevel up = manaLevel.getGenericParadigmMap().get(ManaLevel.up).func();
                    if (up != null) {

                        Map<String, Integer> inItem = new Map<>();
                        {
                            inItem.put(getOreString(manaLevel, ManaLevelBlock.repeater), 1);
                            inItem.put(getOreString(manaLevel, ManaLevelItem.controlCrystal), 1);

                            inItem.putAll(manaLevel.getGenericParadigmMap().get(ManaLevel.bracket_itemIn).func());

                        }

                        Map<String, Integer> inFluid = new Map<>();
                        {
                            inFluid.putAll(manaLevel.getGenericParadigmMap().get(ManaLevel.bracket_fluidIn).func());
                        }

                        Shaped shaped = new Shaped.ShapedOre(
                                new ResourceLocation(AbyssMana2.MODID, "do_" + getOreString(manaLevel, this)),
                                up,
                                ShapedType.assemble,
                                ShapedDrive.map.get(4),
                                inItem,
                                inFluid,
                                60 * 20 * up.getGenericParadigmMap().get(ManaLevel.level),
                                32L * up.getGenericParadigmMap().get(ManaLevel.level),
                                0,
                                new List<ItemStack>().add_chainable(new ItemStack(manaLevel.itemBlock.get(this))),
                                null
                        );
                        Shaped.register.register(shaped);
                    }
                }
            }
        };
        whirlBoost = new Mechanics("whirl_boost", new GenericParadigmMap()
                .put_genericParadigm(tile, WhirlBoostTileEntity.class)
                .put_genericParadigm(orePrefix, "WhirlBoost"));
        gatherMana = new Mechanics("gather_mana", new GenericParadigmMap()
                .put_genericParadigm(tile, GatherManaTileEntity.class)
                .put_genericParadigm(orePrefix, "GatherMana"));
        voidCase = new Mechanics("void_case", new GenericParadigmMap()
                .put_genericParadigm(tile, VoidCase.class)
                .put_genericParadigm(orePrefix, "VoidCase"));
        voidCylinder = new Mechanics("void_cylinder", new GenericParadigmMap()
                .put_genericParadigm(tile, VoidCylinder.class)
                .put_genericParadigm(orePrefix, "VoidCylinder"));

        sunlight = new Mechanics("sunlight", new GenericParadigmMap()
                .put_genericParadigm(tile, SpecialCapacity.Sunlight.class)
                .put_genericParadigm(orePrefix, "Sunlight"));
        moonlight = new Mechanics("moonlight", new GenericParadigmMap()
                .put_genericParadigm(tile, SpecialCapacity.Moonlight.class)
                .put_genericParadigm(orePrefix, "Moonlight"));
        extractMana = new Mechanics("extract_mana", new GenericParadigmMap()
                .put_genericParadigm(tile, RunTileEntity.ExtractMana.class)
                .put_genericParadigm(orePrefix, "ExtractMana"));

        grind = new Mechanics("grind", new GenericParadigmMap()
                .put_genericParadigm(tile, RunTileEntity.GrindTileEntity.class)
                .put_genericParadigm(orePrefix, "Grind"));
        wash = new Mechanics("wash", new GenericParadigmMap()
                .put_genericParadigm(tile, RunTileEntity.Wash.class)
                .put_genericParadigm(orePrefix, "Wash"));
        centrifugal = new Mechanics("centrifugal", new GenericParadigmMap()
                .put_genericParadigm(tile, RunTileEntity.Centrifugal.class)
                .put_genericParadigm(orePrefix, "Centrifugal"));
        pack = new Mechanics("pack", new GenericParadigmMap()
                .put_genericParadigm(tile, RunTileEntity.Pack.class)
                .put_genericParadigm(orePrefix, "Pack"));
        unpack = new Mechanics("unpack", new GenericParadigmMap()
                .put_genericParadigm(tile, RunTileEntity.UnPack.class)
                .put_genericParadigm(orePrefix, "UnPack"));
        assemble = new Mechanics("assemble", new GenericParadigmMap()
                .put_genericParadigm(tile, RunTileEntity.Assemble.class)
                .put_genericParadigm(orePrefix, "Assemble"));
        stampingMachine = new Mechanics("stamping_machine", new GenericParadigmMap()
                .put_genericParadigm(tile, RunTileEntity.StampingMachine.class)
                .put_genericParadigm(orePrefix, "StampingMachine"));
        tieWire = new Mechanics("tie_wire", new GenericParadigmMap()
                .put_genericParadigm(tile, RunTileEntity.TieWire.class)
                .put_genericParadigm(orePrefix, "TieWire"));
        manaCoagulation = new Mechanics("mana_coagulation", new GenericParadigmMap()
                .put_genericParadigm(tile, RunTileEntity.ManaCoagulation.class)
                .put_genericParadigm(orePrefix, "ManaCoagulation"));
        upMana = new Mechanics("up_mana", new GenericParadigmMap()
                .put_genericParadigm(tile, RunTileEntity.UpMana.class)
                .put_genericParadigm(orePrefix, "UpMana"));
        manaPerfusion = new Mechanics("mana_perfusion", new GenericParadigmMap()
                .put_genericParadigm(tile, RunTileEntity.ManaPerfusion.class)
                .put_genericParadigm(orePrefix, "ManaPerfusion"));
        highPressureFuse = new Mechanics("high_pressure_fuse", new GenericParadigmMap()
                .put_genericParadigm(tile, RunTileEntity.HighPressureFuse.class)
                .put_genericParadigm(orePrefix, "HighPressureFuse"));
        dissolution = new Mechanics("dissolution", new GenericParadigmMap()
                .put_genericParadigm(tile, RunTileEntity.Dissolution.class)
                .put_genericParadigm(orePrefix, "Dissolution"));
        freezing = new Mechanics("freezing", new GenericParadigmMap()
                .put_genericParadigm(tile, RunTileEntity.Freezing.class)
                .put_genericParadigm(orePrefix, "Freezing"));
        crystallizing = new Mechanics("crystallizing", new GenericParadigmMap()
                .put_genericParadigm(tile, RunTileEntity.Crystallizing.class)
                .put_genericParadigm(orePrefix, "Crystal"));
        carving = new Mechanics("carving", new GenericParadigmMap()
                .put_genericParadigm(tile, RunTileEntity.Carving.class)
                .put_genericParadigm(orePrefix, "Carving"));
        blastFurnace = new Mechanics("blast_furnace", new GenericParadigmMap()
                .put_genericParadigm(tile, RunTileEntity.BlastFurnace.class)
                .put_genericParadigm(orePrefix, "BlastFurnace"));
        uuGenerate = new Mechanics("uu_generate", new GenericParadigmMap()
                .put_genericParadigm(tile, RunTileEntity.UUGenerate.class)
                .put_genericParadigm(orePrefix, "UUGenerate"));
        qualityGenerate = new Mechanics("quality_generate", new GenericParadigmMap()
                .put_genericParadigm(tile, RunTileEntity.QualityGenerate.class)
                .put_genericParadigm(orePrefix, "QualityGenerate"));
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

    public abstract static class RunTileEntity extends EmptyTile implements ITileEntityType, ITickable {
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
                        return Shaped.extractMana.surplusTiem() / iManaLevel.getManaLevel().getGenericParadigmMap().get(ManaLevel.level);
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

        public static class Carving extends RunTileEntity {

            @Override
            public ShapedType getShapedType() {
                return ShapedType.carving;
            }
        }

        public static class BlastFurnace extends RunTileEntity {

            @Override
            public ShapedType getShapedType() {
                return ShapedType.blastFurnace;
            }
        }

        public static class UUGenerate extends RunTileEntity {

            @Override
            public ShapedType getShapedType() {
                return ShapedType.uuGenerate;
            }
        }

        public static class QualityGenerate extends RunTileEntity {

            @Override
            public ShapedType getShapedType() {
                return ShapedType.qualityGenerate;
            }
        }

    }

    /***
     * 特殊产能
     *      被动产能
     */
    public abstract static class SpecialCapacity extends EmptyTile implements ITileEntityType, ITickable {

        IManaLevel iManaLevel;
        IManaHandle.ManaHandle iManaHandle;

        @Override
        public Map<Capability<?>, Object> getAllCapabilities(AttachCapabilitiesEvent<TileEntity> event, Map<Capability<?>, Object> map) {
            iManaLevel = new IManaLevel.GetManaLevel(event.getObject());
            iManaHandle = new IManaHandle.ManaHandle(event.getObject(), iManaLevel) {

                @Override
                public long getMaxMana() {
                    return 128;
                }

                @Override
                public long getMaxRate() {
                    return getManaLevel().getGenericParadigmMap().get(ManaLevel.level);
                }

            };
            map.put(AllCapability.I_MANA_LEVEL, iManaLevel);
            map.put(AllCapability.I_MANA_HANDEL, iManaHandle);
            return map;
        }

        public static class Sunlight extends SpecialCapacity {
            @Override
            public void update() {
                if (!getWorld().isRemote && world.isDaytime()) {
                    iManaHandle.addMana(iManaLevel.getManaLevel().getGenericParadigmMap().get(ManaLevel.level));
                }
            }
        }

        public static class Moonlight extends SpecialCapacity {
            @Override
            public void update() {
                if (!getWorld().isRemote && !world.isDaytime()) {
                    iManaHandle.addMana(iManaLevel.getManaLevel().getGenericParadigmMap().get(ManaLevel.level));
                }
            }
        }
    }

    public static class GatherManaTileEntity extends EmptyTile implements ITileEntityType {
        @Override
        public Map<Capability<?>, Object> getAllCapabilities(AttachCapabilitiesEvent<TileEntity> event, Map<Capability<?>, Object> map) {
            IManaLevel iManaLevel = new IManaLevel.GetManaLevel(event.getObject());
            IManaHandle iManaHandle = new IManaHandle.ManaHandle(event.getObject(), iManaLevel);
            map.put(AllCapability.I_MANA_LEVEL, iManaLevel);
            map.put(AllCapability.I_MANA_HANDEL, iManaHandle);
            return map;
        }
    }

    public static class WhirlBoostTileEntity extends EmptyTile implements ITileEntityType {

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

    public static class VoidCase extends EmptyTile implements ITileEntityType {

        public IManaLevel iManaLevel;
        public VoidCaseHandler voidCaseHandler;

        @Override
        public Map<Capability<?>, Object> getAllCapabilities(AttachCapabilitiesEvent<TileEntity> event, Map<Capability<?>, Object> map) {
            iManaLevel = new IManaLevel.GetManaLevel(this);
            voidCaseHandler = new VoidCaseHandler(this, iManaLevel);
            map.put(AllCapability.I_MANA_LEVEL, iManaLevel);
            map.put(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, voidCaseHandler);
            return map;
        }


        public static class VoidCaseHandler implements IItemHandler, INBT, IManaLevel {

            ItemStack itemStack = ItemStack.EMPTY;
            int stackSize;

            public final IManaLevel manaLevel;
            public final TileEntity tileEntity;

            public VoidCaseHandler(TileEntity tileEntity, IManaLevel manaLevel) {
                this.manaLevel = manaLevel;
                this.tileEntity = tileEntity;
            }

            @Override
            public int getSlots() {
                return 2;
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return true;
            }

            @NotNull
            @Override
            public ItemStack getStackInSlot(int slot) {
                ItemStack out = itemStack.copy();
                out.setCount(getStackSize());
                return slot == 0 ? out : ItemStack.EMPTY;
            }

            @NotNull
            @Override
            public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                if (itemStack.isEmpty() || itemStack.isItemEqual(stack) && !stack.getItem().equals(Items.AIR)) {
                    ItemStack newItemStack;
                    int s = getStackSize();
                    if (itemStack.isEmpty()) {
                        newItemStack = stack.copy();
                        itemStack.setCount(1);
                    } else {
                        newItemStack = itemStack;
                    }
                    s = Math.min(stack.getCount() + s, getSlotLimit(0));

                    ItemStack out = itemStack = stack.copy();
                    out.setCount(out.getCount() - (s - getStackSize()));
                    if (!simulate) {
                        itemStack = newItemStack;
                        setStackSize(s);
                    }
                    return out;
                }
                return stack;
            }

            @NotNull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (itemStack.isEmpty()) {
                    return ItemStack.EMPTY;
                }

                int r = getStackSize() - Math.max(0, getStackSize() - amount);

                ItemStack out = itemStack.copy();
                out.setCount(r);

                if (!simulate) {
                    setStackSize(getStackSize() - r);
                }
                return out;
            }

            public int getStackSize() {
                return stackSize;
            }

            public void setStackSize(int stackSize) {
                this.stackSize = stackSize;
                if (this.stackSize <= 0) {
                    itemStack = ItemStack.EMPTY;
                }
            }


            @Override
            public TileEntity getThis() {
                return tileEntity;
            }

            @Override
            public ManaLevel getManaLevel() {
                return manaLevel.getManaLevel();
            }

            @Override
            public int getSlotLimit(int slot) {
                return slot == 0 ? 12600 * getManaLevel().getGenericParadigmMap().get(ManaLevel.level) : 0;
            }

            @Override
            public AllNBT.IGS<NBTBase> getNBTBase() {
                return AllNBT.voidCaseHandler;
            }

            @Override
            public NBTTagCompound serializeNBT() {
                NBTTagCompound nbtTagCompound = new NBTTagCompound();
                nbtTagCompound.setTag("stack", itemStack.serializeNBT());
                nbtTagCompound.setInteger("stackSize", getStackSize());
                return nbtTagCompound;
            }

            @Override
            public void deserializeNBT(NBTTagCompound nbt) {
                itemStack = new ItemStack(nbt.getCompoundTag("stack"));
                setStackSize(nbt.getInteger("stackSize"));
            }
        }

    }

    public static class VoidCylinder extends EmptyTile implements ITileEntityType {

        IManaLevel iManaLevel;
        VoidCylinderHandler fluidTank;

        @Override
        public Map<Capability<?>, Object> getAllCapabilities(AttachCapabilitiesEvent<TileEntity> event, Map<Capability<?>, Object> map) {
            iManaLevel = new IManaLevel.GetManaLevel(this);
            fluidTank = new VoidCylinderHandler(this, iManaLevel);
            map.put(AllCapability.I_MANA_LEVEL, iManaLevel);
            map.put(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, fluidTank);
            return map;
        }

        @Override
        public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
            if (capability.equals(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)) {
                return true;
            }
            return super.hasCapability(capability, facing);
        }

        public static class VoidCylinderHandler extends FluidTank implements IManaLevel, INBT, ICapabilityCallback {

            final TileEntity tileEntity;
            final IManaLevel manaLevel;

            public VoidCylinderHandler(TileEntity tileEntity, IManaLevel manaLevel) {
                super(0);
                this.tileEntity = tileEntity;
                this.manaLevel = manaLevel;
            }

            @Override
            public void run() {
                getCapacity();
            }

            @Override
            public int getCapacity() {
                return capacity = 126000 * getManaLevel().getGenericParadigmMap().get(ManaLevel.level);
            }

            @Override
            public ManaLevel getManaLevel() {
                return manaLevel.getManaLevel();
            }

            @Override
            public AllNBT.IGS<NBTBase> getNBTBase() {
                return AllNBT.voidCylinderHandler;
            }

            @Override
            public TileEntity getThis() {
                return tileEntity;
            }

            @Override
            public NBTTagCompound serializeNBT() {
                if (fluid != null) {
                    return fluid.writeToNBT(new NBTTagCompound());
                }
                return new NBTTagCompound();
            }

            @Override
            public void deserializeNBT(NBTTagCompound nbt) {
                fluid = FluidStack.loadFluidStackFromNBT(nbt);
            }
        }

    }

    public static class EmptyTile extends TileEntity {
    }

    public static final GenericParadigmMap.IKey<Class<?>> tile = new GenericParadigmMap.IKey.KeyClass() {
        @Override
        public Class<?> _default() {
            return EmptyTile.class;
        }
    };

}
