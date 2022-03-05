package com.til.abyss_mana_2.client.other_mod_interact;

import com.til.abyss_mana_2.client.util.Lang;
import com.til.abyss_mana_2.common.AllBlock;
import com.til.abyss_mana_2.common.capability.IHandle;
import com.til.abyss_mana_2.common.event.ModEvent;
import com.til.abyss_mana_2.common.other_mod_interact.EventInteractClassTag;
import com.til.abyss_mana_2.common.register.BindType;
import com.til.abyss_mana_2.common.register.ManaLevel;
import com.til.abyss_mana_2.util.data.AllNBT;
import com.til.abyss_mana_2.util.extension.Map;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EventInteractClassTag(modID = "waila", value = Side.CLIENT)
public class Hwyla_interact {

    @SubscribeEvent
    public static void onEvent(ModEvent.ModEventLoad.postInit event) {

        ModuleRegistrar moduleRegistrar = ModuleRegistrar.instance();
        MechanicsWailaDataProvider mechanicsWailaDataProvider = new MechanicsWailaDataProvider();
        moduleRegistrar.registerHeadProvider(mechanicsWailaDataProvider, AllBlock.MechanicsBlock.class);
        moduleRegistrar.registerBodyProvider(mechanicsWailaDataProvider, AllBlock.MechanicsBlock.class);
        moduleRegistrar.registerTailProvider(mechanicsWailaDataProvider, AllBlock.MechanicsBlock.class);

    }

    public static class MechanicsWailaDataProvider implements IWailaDataProvider {

        @NotNull
        @Override
        public List<String> getWailaBody(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor, IWailaConfigHandler config) {

            NBTTagCompound nbtTagCompound = accessor.getNBTData();

            ManaLevel manaLevel;

            if (nbtTagCompound.hasKey("iManaLevel")) {
                manaLevel = ManaLevel.register.getValue(new ResourceLocation(nbtTagCompound.getString("iManaLevel")));
                if (manaLevel == null) {
                    manaLevel = ManaLevel.T1;
                }
                tooltip.add(Lang.getLang("mana.level") + Objects.requireNonNull(manaLevel.getRegistryName()).getResourcePath());
            }

            if (nbtTagCompound.hasKey("iHandle")) {
                NBTTagCompound iHandle = nbtTagCompound.getCompoundTag("iHandle");
                tooltip.add(Lang.getLang("handle"));
                {
                    tooltip.add("   " + Lang.getLang("shaped.drive.use") + iHandle.getTagList("shapedDrive", 3));
                }
                {
                    List<String> stringList = new ArrayList<>();
                    iHandle.getTagList("shapedTypes", 8).forEach(nbtBase -> {
                        if (nbtBase instanceof NBTTagString) {
                            stringList.add(Lang.getLang((NBTTagString) nbtBase));
                        }
                    });
                    tooltip.add("   " + Lang.getLang("use.shaped.type") + stringList);
                }
                tooltip.add("   " + Lang.getLang("max.parallel") + iHandle.getInteger("maxParallel"));
                tooltip.add("   " + Lang.getLang("clock.time") + MessageFormat.format("{0}/{1}", iHandle.getInteger("clockTime"), iHandle.getInteger("maxClockTime")));
                int i = 1;
                for (NBTBase shapedHandles : iHandle.getTagList("shapedHandles", 10)) {
                    if (shapedHandles instanceof NBTTagCompound) {
                        NBTTagCompound s = (NBTTagCompound) shapedHandles;
                        IHandle.ShapedHandle shapedHandle = new IHandle.ShapedHandle(s);
                        tooltip.add("   " + Lang.getLang("shaped.handle") + i);
                        tooltip.add("       " + Lang.getLang("state") + Lang.getLang(shapedHandle.process.toString()));
                        if (shapedHandle._surplusTiem != 0) {
                            tooltip.add("       " + Lang.getLang("surplus.tiem") + shapedHandle._surplusTiem);
                        }
                        if (shapedHandle.consumeMana != 0) {
                            tooltip.add("       " + Lang.getLang("consume.mana") + shapedHandle.consumeMana);
                        }
                        if (shapedHandle.outMana != 0) {
                            tooltip.add("       " + Lang.getLang("out.mana") + shapedHandle.outMana);
                        }
                        if (shapedHandle.outItem != null) {
                            tooltip.add("           " + Lang.getLang(BindType.itemOut));
                            shapedHandle.outItem.forEach(stack -> tooltip.add("             " + stack.getDisplayName() + "   x" + stack.getCount()));
                        }
                        if (shapedHandle.outFuid != null) {
                            tooltip.add("           " + Lang.getLang(BindType.fluidOut));
                            shapedHandle.outFuid.forEach(stack -> tooltip.add("             " + I18n.format(stack.getUnlocalizedName()).trim() + "   x" + stack.amount + "mb"));
                        }
                        i++;
                    }
                }
            }

            if (nbtTagCompound.hasKey("iControl")) {
                NBTTagCompound iControl = nbtTagCompound.getCompoundTag("iControl");
                int maxBind = iControl.getInteger("maxBind");
                tooltip.add(Lang.getLang("control"));
                tooltip.add(Lang.getLang("max.range") + iControl.getInteger("maxRange"));
                Map<BindType, com.til.abyss_mana_2.util.extension.List<BlockPos>> map = AllNBT.controlBindBlock.get(iControl);
                map.forEach((k, v) -> {
                    if (v.isEmpty()) {
                        return;
                    }
                    tooltip.add("  " + Lang.getLang(k) + "(" + v.size() + "/" + maxBind + ")");
                    v.forEach(p -> tooltip.add("        " + MessageFormat.format("[x:{0},y:{1},z:{2}]", p.getX(), p.getY(), p.getZ())));
                });
            }

            if (nbtTagCompound.hasKey("iManaHandle")) {
                NBTTagCompound iManaHandle = nbtTagCompound.getCompoundTag("iManaHandle");
                tooltip.add(Lang.getLang("mana.handel"));
                tooltip.add("   " + MessageFormat.format(Lang.getLang("now.mana.handel"), iManaHandle.getInteger("now"), iManaHandle.getInteger("max")));
                tooltip.add("   " + Lang.getLang("rate.mana.handel") + iManaHandle.getInteger("rate"));
            }

            if (nbtTagCompound.hasKey("iShapedDrive")) {
                NBTTagList iManaHandleNBT = nbtTagCompound.getTagList("iShapedDrive", 3);
                if (iManaHandleNBT.tagCount() > 0) {
                   /* List<Integer> integerList = new ArrayList<>();
                    for (NBTBase nbtBase : iManaHandleNBT) {
                        if (nbtBase instanceof NBTTagInt) {
                            integerList.add(((NBTTagInt) nbtBase).getInt());
                        }
                    }*/
                    tooltip.add(Lang.getLang("shaped.drive.provide") + iManaHandleNBT);
                }
            }

            /*            if (nbtTagCompound.hasKey("I_CONTROL")) {
                NBTTagCompound iControlNBT = nbtTagCompound.getCompoundTag("I_CONTROL");

                if (iControlNBT.hasKey("useShapedType")) {
                    List<String> list = new ArrayList<>();
                    NBTTagList useShapedType = iControlNBT.getTagList("useShapedType", 8);
                    for (NBTBase nbtBase : useShapedType) {
                        if (nbtBase instanceof NBTTagString) {
                            list.add(Lang.getLang((NBTTagString) nbtBase));
                        }
                    }
                    tooltip.add(Lang.getLang("use.shaped.type") + list);
                }

            }

            if (nbtTagCompound.hasKey("I_MANA_HANDEL")) {
                NBTTagCompound iManaHandleNBT = nbtTagCompound.getCompoundTag("I_MANA_HANDEL");
                tooltip.add(Lang.getLang("mana.handel"));
                tooltip.add("   " + MessageFormat.format(Lang.getLang("now.mana.handel"), iManaHandleNBT.getInteger("now"), iManaHandleNBT.getInteger("max")));
                tooltip.add("   " + Lang.getLang("rate.mana.handel") + iManaHandleNBT.getInteger("rate"));
            }

            if (nbtTagCompound.hasKey("I_SHAPED_DRIVE")) {
                NBTTagList iManaHandleNBT = nbtTagCompound.getTagList("I_SHAPED_DRIVE", 3);
                if (iManaHandleNBT.tagCount() > 0) {
                    List<Integer> list = new ArrayList<>();
                    tooltip.add(Lang.getLang("shaped.drive"));
                    for (NBTBase nbtBase : iManaHandleNBT) {
                        if (nbtBase instanceof NBTTagInt) {
                            list.add(((NBTTagInt) nbtBase).getInt());
                        }
                    }
                    tooltip.add(Lang.getLang("shaped.drive") + list);
                }
            }*/

            return tooltip;
        }

    }

}
