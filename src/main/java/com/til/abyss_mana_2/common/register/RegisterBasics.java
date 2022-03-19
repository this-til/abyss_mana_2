package com.til.abyss_mana_2.common.register;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.util.extension.GenericParadigmMap;
import com.til.abyss_mana_2.util.extension.List;
import com.til.abyss_mana_2.util.extension.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RegisterBasics<T extends IForgeRegistryEntry<T>> extends IForgeRegistryEntry.Impl<T> {

    public final GenericParadigmMap genericParadigmMap;

    public RegisterBasics(String name, GenericParadigmMap genericParadigmMap) {
        setRegistryName(new ResourceLocation(AbyssMana2.MODID, name));
        MinecraftForge.EVENT_BUS.register(this);
        this.genericParadigmMap = genericParadigmMap;
    }

    public RegisterBasics(ResourceLocation resourceLocation, GenericParadigmMap genericParadigmMap) {
        setRegistryName(resourceLocation);
        MinecraftForge.EVENT_BUS.register(this);
        this.genericParadigmMap = genericParadigmMap;
    }

    public String getOrePrefix() {

        if (getGenericParadigmMap().containsKey(orePrefix)) {
            return getGenericParadigmMap().get(orePrefix);
        } else {
            ResourceLocation resourceLocation = getRegistryName();
            if (resourceLocation != null) {
                String ore = resourceLocation.getResourcePath();
                getGenericParadigmMap().put_genericParadigm(orePrefix, ore);
                return ore;
            }
        }
        return "";
    }

    public static String getOreString(RegisterBasics<?> r1, RegisterBasics<?> r2) {
        return r1.getOrePrefix() + r2.getOrePrefix();
    }

    public static void registerOreRecipe(ResourceLocation name, ItemStack itemStack, Object... objects) {
        ShapedOreRecipe shapedOreRecipe = new ShapedOreRecipe(name, itemStack, objects);
        if (shapedOreRecipe.getRegistryName() == null) {
            shapedOreRecipe.setRegistryName(name);
        }
        ForgeRegistries.RECIPES.register(shapedOreRecipe);
    }

    public static ResourceLocation getRecipeNameOfAToB(RegisterBasics<?> at, RegisterBasics<?> a, RegisterBasics<?> bt, RegisterBasics<?> b) {
        return new ResourceLocation(AbyssMana2.MODID, "shaped_ore_recipe_" + getOreString(at, a) + "_to_" + getOreString(bt, b));
    }

    public static ResourceLocation getRecipeNameOfAListToBString(Map<RegisterBasics<?>, RegisterBasics<?>> map, String out) {
        StringBuilder p = new StringBuilder();
        int i = 0;
        for (java.util.Map.Entry<RegisterBasics<?>, RegisterBasics<?>> registerBasicsRegisterBasicsEntry : map.entrySet()) {
            p.append(getOreString(registerBasicsRegisterBasicsEntry.getKey(), registerBasicsRegisterBasicsEntry.getValue()));
            if (i < map.size() - 1) {
                p.append("_and_");
            }
            i++;
        }
        return new ResourceLocation("shaped_ore_recipe_" + p + "_to_" + out);
    }

    public static ResourceLocation getRecipeNameOfAToB(RegisterBasics<?> at, RegisterBasics<?> a, RegisterBasics<?> bt, RegisterBasics<?> b, String s) {
        return new ResourceLocation(AbyssMana2.MODID, "shaped_ore_recipe_" + getOreString(at, a) + "_to_" + getOreString(bt, b) +"_"+ s);
    }

    public static ResourceLocation getRecipeNameOfAListToBString(List<String> strings, String out) {
        StringBuilder p = new StringBuilder();
        int i = 0;
        for (String s : strings) {
            p.append(strings);
            if (i < strings.size() - 1) {
                p.append("_and_");
            }
            i++;
        }
        return new ResourceLocation("shaped_ore_recipe_" + p + "_to_" + out);
    }

    public static ResourceLocation getName(Map<String, Integer> map, String out) {
        StringBuilder p = new StringBuilder();
        int i = 0;
        for (java.util.Map.Entry<String, Integer> stringIntegerEntry : map.entrySet()) {
            p.append(stringIntegerEntry.getKey());
            if (i < map.size() - 1) {
                p.append("_and_");
            }
            i++;
        }
        return new ResourceLocation("shaped_ore_recipe_" + p + "_to_" + out);
    }

    public GenericParadigmMap getGenericParadigmMap() {
        return genericParadigmMap;
    }

    public static class NoValue extends RuntimeException {
        /**
         * Constructs a new runtime exception with {@code null} as its
         * detail message.  The cause is not initialized, and may subsequently be
         * initialized by a call to {@link #initCause}.
         */
        public NoValue() {
            super();
        }

        /**
         * Constructs a new runtime exception with the specified detail message.
         * The cause is not initialized, and may subsequently be initialized by a
         * call to {@link #initCause}.
         *
         * @param message the detail message. The detail message is saved for
         *                later retrieval by the {@link #getMessage()} method.
         */
        public NoValue(String message) {
            super(message);
        }

        /**
         * Constructs a new runtime exception with the specified detail message and
         * cause.  <p>Note that the detail message associated with
         * {@code cause} is <i>not</i> automatically incorporated in
         * this runtime exception's detail message.
         *
         * @param message the detail message (which is saved for later retrieval
         *                by the {@link #getMessage()} method).
         * @param cause   the cause (which is saved for later retrieval by the
         *                {@link #getCause()} method).  (A <tt>null</tt> value is
         *                permitted, and indicates that the cause is nonexistent or
         *                unknown.)
         * @since 1.4
         */
        public NoValue(String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * Constructs a new runtime exception with the specified cause and a
         * detail message of <tt>(cause==null ? null : cause.toString())</tt>
         * (which typically contains the class and detail message of
         * <tt>cause</tt>).  This constructor is useful for runtime exceptions
         * that are little more than wrappers for other throwables.
         *
         * @param cause the cause (which is saved for later retrieval by the
         *              {@link #getCause()} method).  (A <tt>null</tt> value is
         *              permitted, and indicates that the cause is nonexistent or
         *              unknown.)
         * @since 1.4
         */
        public NoValue(Throwable cause) {
            super(cause);
        }

        /**
         * Constructs a new runtime exception with the specified detail
         * message, cause, suppression enabled or disabled, and writable
         * stack trace enabled or disabled.
         *
         * @param message            the detail message.
         * @param cause              the cause.  (A {@code null} value is permitted,
         *                           and indicates that the cause is nonexistent or unknown.)
         * @param enableSuppression  whether or not suppression is enabled
         *                           or disabled
         * @param writableStackTrace whether or not the stack trace should
         *                           be writable
         * @since 1.7
         */
        protected NoValue(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

    public static final GenericParadigmMap.IKey<String> orePrefix = new GenericParadigmMap.IKey.KeyString();

}
