package com.til.abyss_mana_2.common.key;


import com.til.abyss_mana_2.common.AllItem;
import com.til.abyss_mana_2.common.capability.AllCapability;
import com.til.abyss_mana_2.common.capability.IControl;
import com.til.abyss_mana_2.common.register.BindType;
import com.til.abyss_mana_2.util.data.AllNBT;
import com.til.abyss_mana_2.util.data.message.key_message.KeyMessage;
import com.til.abyss_mana_2.util.data.message.player_message.PlayerMessage;
import com.til.abyss_mana_2.util.extension.Extension;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.awt.*;
import java.util.Objects;

public enum KeyRun implements Extension.Action_2V<KeyMessage.Message, MessageContext> {

    AIR {
        @Override
        public void action(KeyMessage.Message message, MessageContext messageContext) {

        }
    },

    key_G {
        @Override
        public void action(KeyMessage.Message message, MessageContext messageContext) {
            EntityPlayerMP entityPlayerMP = messageContext.getServerHandler().player;
            ItemStack itemStack = entityPlayerMP.getHeldItem(EnumHand.MAIN_HAND);
            if (itemStack.getItem().equals(AllItem.bindStaff)) {
                if (entityPlayerMP.isSneaking()) {
                    AllNBT.playerMessage.upDataToPlayerCLIENT(new PlayerMessage.MessageData(true, "已经清除坐标数据.name"), entityPlayerMP);
                    NBTTagCompound nbtTagCompound = new NBTTagCompound();
                    nbtTagCompound.setInteger("color", new Color(entityPlayerMP.getRNG().nextInt(255), entityPlayerMP.getRNG().nextInt(255), entityPlayerMP.getRNG().nextInt(255)).getRGB());
                    itemStack.setTagCompound(nbtTagCompound);
                } else {
                    NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
                    if (nbtTagCompound == null) {
                        nbtTagCompound = new NBTTagCompound();
                        itemStack.setTagCompound(nbtTagCompound);
                    }

                    BlockPos blockPos = new BlockPos(nbtTagCompound.getInteger("x"), nbtTagCompound.getInteger("y"), nbtTagCompound.getInteger("z"));
                    TileEntity _tile = entityPlayerMP.world.getTileEntity(blockPos);
                    if (_tile != null) {
                        IControl iControl = _tile.getCapability(AllCapability.I_CONTROL, null);
                        if (iControl != null) {
                            BindType bindType = BindType.register.getValue(new ResourceLocation(nbtTagCompound.getString("type")));
                            com.til.abyss_mana_2.util.extension.List<BindType> list = iControl.getCanBindType();
                            bindType = list.get(list.getAngleMark(bindType) + 1);
                            nbtTagCompound.setString("type", Objects.requireNonNull(bindType.getRegistryName()).toString());
                            AllNBT.playerMessage.upDataToPlayerCLIENT(
                                    new PlayerMessage.MessageData(true, "已经绑定类型切换至——{0}.name", bindType.getRegistryName().toString()), entityPlayerMP);
                            return;
                        }
                    }
                    AllNBT.playerMessage.upDataToPlayerCLIENT(new PlayerMessage.MessageData(true, "请先绑定主绑定方块.name"), entityPlayerMP);
                }
            }
        }
    }
}


