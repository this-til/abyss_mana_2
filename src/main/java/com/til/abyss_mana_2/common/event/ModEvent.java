package com.til.abyss_mana_2.common.event;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.common.CommonProxy;
import com.til.abyss_mana_2.common.capability.IControl;
import com.til.abyss_mana_2.common.capability.IHandle;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ModEvent extends Event {

    public final AbyssMana2 abyssMana2;

    public ModEvent() {
        super();
        abyssMana2 = AbyssMana2.instance;
    }

    public static class ModEventLoad extends ModEvent {

        public final CommonProxy commonProxy;

        public ModEventLoad() {
            super();
            commonProxy = CommonProxy.commonProxy;
        }

        public static class preInit extends ModEventLoad {
        }

        public static class init extends ModEventLoad {
        }

        public static class postInit extends ModEventLoad {
        }

    }

    public static class EventControl extends ModEvent {

        public final IControl control;

        public EventControl(IControl iControl) {
            super();
            this.control = iControl;
        }

        public static class EventHandle extends EventControl{

            public final IHandle iHandle;

            public EventHandle(IHandle iControl) {
                super(iControl);
                this.iHandle = iControl;
            }

            /***
             * 开启新配方时
             */
            public static class addShapedHandle extends EventHandle {

                public final IHandle.ShapedHandle shapedHandle;

                public addShapedHandle(IHandle iControl, IHandle.ShapedHandle shapedHandle) {
                    super(iControl);
                    this.shapedHandle = shapedHandle;
                }
            }

        }
    }
}
