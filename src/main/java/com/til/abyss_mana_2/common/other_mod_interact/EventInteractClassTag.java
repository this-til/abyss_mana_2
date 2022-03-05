package com.til.abyss_mana_2.common.other_mod_interact;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.annotation.*;

@Target(value={ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EventInteractClassTag {
    String modID();
    Side value();
}
