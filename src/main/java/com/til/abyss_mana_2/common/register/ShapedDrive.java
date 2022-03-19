package com.til.abyss_mana_2.common.register;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.common.AllBlock;
import com.til.abyss_mana_2.common.ModTab;
import com.til.abyss_mana_2.common.capability.AllCapability;
import com.til.abyss_mana_2.common.capability.IShapedDrive;
import com.til.abyss_mana_2.common.capability.ITileEntityType;
import com.til.abyss_mana_2.util.extension.GenericParadigmMap;
import com.til.abyss_mana_2.util.extension.List;
import com.til.abyss_mana_2.util.extension.Map;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;

public class ShapedDrive extends RegisterBasics<ShapedDrive> {
    public static IForgeRegistry<ShapedDrive> register = null;
    public static Map<Integer, ShapedDrive> map = new Map<>();

    public Block block;
    public ItemBlock itemBlock;

    public ShapedDrive(String name) {
        this(new ResourceLocation(AbyssMana2.MODID, "shaped_drive_" + name), new GenericParadigmMap());
    }

    public ShapedDrive(ResourceLocation resourceLocation, GenericParadigmMap genericParadigmMap) {
        super(resourceLocation, genericParadigmMap);
    }

    public int getLayer() {
        return 0;
    }

    @SubscribeEvent
    public void register(RegistryEvent.Register<ShapedDrive> event) {
        event.getRegistry().register(this);
        block = new ShapedDriveBlock(this);
        itemBlock = (ItemBlock) new ItemBlock(block)
                .setRegistryName(Objects.requireNonNull(block.getRegistryName()))
                .setUnlocalizedName(AbyssMana2.MODID + "." + block.getRegistryName().getResourcePath());
        GameData.register_impl(block);
        GameData.register_impl(itemBlock);
    }

    public static void init() {
        for (int i = 0; i < 16; i++) {
            map.put(i, new ShapedDrive(Integer.toString(i)));
        }
        GameRegistry.registerTileEntity(ShapedDriveTileEntity.class, new ResourceLocation(AbyssMana2.MODID, ShapedDriveTileEntity.class.getName()));
    }

    public static void initDictionary() {
        for (ShapedDrive shapedDrive : register) {
            OreDictionary.registerOre("shaped_drive", shapedDrive.itemBlock);
        }
    }

    public static class ShapedDriveBlock extends AllBlock.MechanicsBlock {

        public ShapedDrive shapedDrive;

        public ShapedDriveBlock(ShapedDrive shapedDrive) {
            super(Material.GLASS, MapColor.GRASS, SoundType.GLASS, ModTab.TAB, shapedDrive.getRegistryName(), "pickaxe", 2, 2.25f, 12);
            this.shapedDrive = shapedDrive;
        }

        @Override
        public TileEntity createNewTileEntity(World worldIn, int meta) {
            return new ShapedDriveTileEntity();
        }
    }

    public static class ShapedDriveTileEntity extends ManaLevelBlock.EmptyTile implements ITileEntityType {

        public ShapedDriveTileEntity() {

        }

        @Override
        public Map<Capability<?>, Object> getAllCapabilities(AttachCapabilitiesEvent<TileEntity> event, Map<Capability<?>, Object> map) {
            return map.put_chainable(AllCapability.I_SHAPED_DRIVE, (IShapedDrive) () -> {
                List<ShapedDrive> list = new List<>();
                Block block = event.getObject().getBlockType();
                if (block instanceof ShapedDriveBlock) {
                    list.add(((ShapedDriveBlock) block).shapedDrive);
                }
                return list;
            });
        }
    }

}
