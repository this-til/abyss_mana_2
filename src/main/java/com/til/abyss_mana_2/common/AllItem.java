package com.til.abyss_mana_2.common;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.common.capability.AllCapability;
import com.til.abyss_mana_2.common.capability.IControl;
import com.til.abyss_mana_2.common.register.BindType;
import com.til.abyss_mana_2.util.data.AllNBT;
import com.til.abyss_mana_2.util.data.message.player_message.PlayerMessage;
import com.til.abyss_mana_2.util.extension.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.Objects;

public class AllItem {

    public static final List<Item> independentItem = new List<>();

    public static class ModItem extends Item {

        public ModItem(String name) {
            setRegistryName(AbyssMana2.MODID, name);
            setUnlocalizedName(AbyssMana2.MODID + "." + name);
            setCreativeTab(ModTab.TAB);
            MinecraftForge.EVENT_BUS.register(this);
            independentItem.add(this);
        }

        public ModItem(String modID, String name) {
            setRegistryName(modID, name);
            setUnlocalizedName(modID + "." + name);
            setCreativeTab(ModTab.TAB);
            MinecraftForge.EVENT_BUS.register(this);
            independentItem.add(this);
        }

        @SubscribeEvent
        public void onEvent(RegistryEvent.Register<Item> event) {
            event.getRegistry().register(this);
        }

    }

    public static Item bindStaff;

    public static void init() {
        bindStaff = new ModItem("bind_staff") {

            @Override
            public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
                if (!worldIn.isRemote) {
                    NBTTagCompound nbtTagCompound = stack.getTagCompound();
                    if (nbtTagCompound == null) {
                        nbtTagCompound = new NBTTagCompound();
                        stack.setTagCompound(nbtTagCompound);
                    }
                    nbtTagCompound.setInteger("color", new Color(entityLiving.getRNG().nextInt(255), entityLiving.getRNG().nextInt(255), entityLiving.getRNG().nextInt(255)).getRGB());
                }
                return super.onItemUseFinish(stack, worldIn, entityLiving);
            }

            @Override
            public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
                EnumActionResult enumActionResult = EnumActionResult.FAIL;
                if (!worldIn.isRemote && hand.equals(EnumHand.MAIN_HAND)) {
                    ItemStack itemStack = player.getHeldItem(hand);
                    NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
                    if (nbtTagCompound == null) {
                        nbtTagCompound = new NBTTagCompound();
                        itemStack.setTagCompound(nbtTagCompound);
                    }
                    TileEntity tileEntity = worldIn.getTileEntity(pos);
                    BindType bindType = BindType.register.getValue(new ResourceLocation(nbtTagCompound.getString("type")));
                    if (bindType == null) {
                        bindType = BindType.itemIn;
                    }
                    if (tileEntity == null) {
                        AllNBT.playerMessage.upDataToPlayerCLIENT(
                                new PlayerMessage.MessageData(true, "错误，目标方块没有方块实体.name"), (EntityPlayerMP) player);
                        return enumActionResult;
                    }
                    BlockPos blockPos = new BlockPos(nbtTagCompound.getInteger("x"), nbtTagCompound.getInteger("y"), nbtTagCompound.getInteger("z"));
                    TileEntity _tile = worldIn.getTileEntity(blockPos);
                    IControl _iControl = null;
                    if (_tile != null) {
                        _iControl = _tile.getCapability(AllCapability.I_CONTROL, null);
                    }
                    if (_iControl == null) {
                        IControl iControl = tileEntity.getCapability(AllCapability.I_CONTROL, null);
                        if (iControl == null) {
                            AllNBT.playerMessage.upDataToPlayerCLIENT(
                                    new PlayerMessage.MessageData(true, "错误，目标方块没有控制器，无法设置为主绑定对象.name"), (EntityPlayerMP) player);
                            return enumActionResult;
                        }
                        nbtTagCompound.setInteger("x", pos.getX());
                        nbtTagCompound.setInteger("y", pos.getY());
                        nbtTagCompound.setInteger("z", pos.getZ());
                        nbtTagCompound.setString("type", Objects.requireNonNull(iControl.getCanBindType().get(0)).toString());
                        AllNBT.playerMessage.upDataToPlayerCLIENT(
                                new PlayerMessage.MessageData(true, "已将目标方块设定为主绑定对象.name"), (EntityPlayerMP) player);
                    } else {
                        if (_tile.equals(tileEntity)) {
                            AllNBT.playerMessage.upDataToPlayerCLIENT(
                                    new PlayerMessage.MessageData(true, "错误，主绑定对象和被绑定对象是同一个方块.name"), (EntityPlayerMP) player);
                            return enumActionResult;
                        }
                        if (_iControl.hasBundling(tileEntity, bindType)) {
                            AllNBT.playerMessage.upDataToPlayerCLIENT(_iControl.unBindling(tileEntity, bindType), (EntityPlayerMP) player);
                        } else {
                            AllNBT.playerMessage.upDataToPlayerCLIENT(_iControl.binding(tileEntity, bindType), (EntityPlayerMP) player);
                        }
                    }
                }

               /* if (!worldIn.isRemote && hand.equals(EnumHand.MAIN_HAND)) {
                    ItemStack itemStack = player.getHeldItem(hand);
                    NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
                    if (nbtTagCompound == null) {
                        nbtTagCompound = new NBTTagCompound();
                        itemStack.setTagCompound(nbtTagCompound);
                    }
                    TileEntity tileEntity = worldIn.getTileEntity(pos);
                    BindType bindType = BindType.register.getValue(new ResourceLocation(nbtTagCompound.getString("type")));
                    if (bindType == null) {
                        bindType = BindType.itemIn;
                    }

                    if (tileEntity == null) {
                        AllNBT.playerMessage.upDataToPlayerCLIENT(
                                new PlayerMessage.MessageData(true, "错误，目标方块没有方块实体.name"), (EntityPlayerMP) player);
                        return enumActionResult;
                    }

                    if (!player.isSneaking()) {
                        IControl iControl = tileEntity.getCapability(AllCapability.I_CONTROL, facing);
                        if (iControl == null) {
                            AllNBT.playerMessage.upDataToPlayerCLIENT(
                                    new PlayerMessage.MessageData(true, "错误，目标方块没有控制器，无法设置为主绑定对象.name"), (EntityPlayerMP) player);
                            return enumActionResult;
                        }

                        AllNBT.playerMessage.upDataToPlayerCLIENT(
                                new PlayerMessage.MessageData(true, "已将目标方块设定为主绑定对象.name"), (EntityPlayerMP) player);
                        nbtTagCompound.setBoolean("bool", true);
                        nbtTagCompound.setInteger("x", pos.getX());
                        nbtTagCompound.setInteger("y", pos.getY());
                        nbtTagCompound.setInteger("z", pos.getZ());
                    } else {

                        if (!nbtTagCompound.getBoolean("bool")) {
                            AllNBT.playerMessage.upDataToPlayerCLIENT(
                                    new PlayerMessage.MessageData(true, "错误，没有主绑定对象，请先设置（右键可作为主绑定方块的方块）.name"), (EntityPlayerMP) player);
                            return enumActionResult;
                        }

                        BlockPos blockPos = new BlockPos(nbtTagCompound.getInteger("x"), nbtTagCompound.getInteger("y"), nbtTagCompound.getInteger("z"));
                        TileEntity _tile = worldIn.getTileEntity(blockPos);
                        if (_tile == null) {
                            AllNBT.playerMessage.upDataToPlayerCLIENT(
                                    new PlayerMessage.MessageData(true, "错误，主绑定对象已失效.name"), (EntityPlayerMP) player);
                            nbtTagCompound.setBoolean("bool", false);
                            return enumActionResult;
                        }

                        IControl iControl = _tile.getCapability(AllCapability.I_CONTROL, facing);
                        if (iControl == null) {
                            AllNBT.playerMessage.upDataToPlayerCLIENT(
                                    new PlayerMessage.MessageData(true, "错误，主绑定对象已失效.name"), (EntityPlayerMP) player);
                            nbtTagCompound.setBoolean("bool", false);
                            return enumActionResult;
                        }
                        if (_tile .equals(tileEntity)) {
                            AllNBT.playerMessage.upDataToPlayerCLIENT(
                                    new PlayerMessage.MessageData(true, "错误，主绑定对象和被绑定对象是同一个方块.name"), (EntityPlayerMP) player);
                            return enumActionResult;
                        }
                        if (iControl.hasBundling(tileEntity, bindType)) {
                            AllNBT.playerMessage.upDataToPlayerCLIENT(iControl.unBindling(tileEntity, bindType), (EntityPlayerMP) player);
                        } else {
                            AllNBT.playerMessage.upDataToPlayerCLIENT(iControl.binding(tileEntity, bindType), (EntityPlayerMP) player);
                        }
                    }
                }*/
                return enumActionResult;
            }


        }.setMaxStackSize(1);
    }
}
