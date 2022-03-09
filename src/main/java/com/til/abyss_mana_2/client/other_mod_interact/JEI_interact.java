package com.til.abyss_mana_2.client.other_mod_interact;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.client.util.Lang;
import com.til.abyss_mana_2.common.other_mod_interact.EventInteractClassTag;
import com.til.abyss_mana_2.common.register.ManaLevel;
import com.til.abyss_mana_2.common.register.Shaped;
import com.til.abyss_mana_2.common.register.ShapedDrive;
import com.til.abyss_mana_2.common.register.ShapedType;
import com.til.abyss_mana_2.util.extension.Map;
import mezz.jei.Internal;
import mezz.jei.api.*;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.gui.GuiHelper;
import mezz.jei.runtime.JeiHelpers;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EventInteractClassTag(modID = "jei", value = Side.CLIENT)
@JEIPlugin
public class JEI_interact implements IModPlugin {

    @Override
    public void register(IModRegistry registry) {

        Map<String, List<ShapedRecipeWrapper>> map = new Map<>();
        Shaped.register.forEach(s -> map.get(Objects.requireNonNull(s.getShapedType().getRegistryName()).toString(), ArrayList::new).add(new ShapedRecipeWrapper(s)));
        map.forEach((k, v) -> {
            registry.addRecipes(v, k);
        });
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registry) {
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        JeiHelpers jeiHelpers = Internal.getHelpers();
        GuiHelper guiHelper = jeiHelpers.getGuiHelper();
        for (ShapedType shapedType : ShapedType.register) {
            registry.addRecipeCategories(new CurrencyCategory(guiHelper, shapedType));
        }
    }


    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
    }

    public static class CurrencyCategory implements IRecipeCategory<ShapedRecipeWrapper> {

        public static final int width = 144;
        public static final int height = 54;

        public final ShapedType shapedType;
        public final IDrawable background;
        public final IDrawable icon;

        public CurrencyCategory(IGuiHelper guiHelper, ShapedType shapedType) {
            this.shapedType = shapedType;
            background = guiHelper.createDrawable(new ResourceLocation(AbyssMana2.MODID, "textures/gui/currency_category.png"), 0, 0, width, height);
            icon = guiHelper.createDrawableIngredient(new ItemStack(ManaLevel.T10.block.get(shapedType.getJEIBlock())));
        }

        @Override
        public String getModName() {
            return AbyssMana2.MODID;
        }

        @Override
        public String getUid() {
            return Objects.requireNonNull(shapedType.getRegistryName()).toString();
        }

        @Override
        public String getTitle() {
            return Lang.getLang(shapedType);
        }

        @Override
        public IDrawable getBackground() {
            return background;
        }

        @Nullable
        @Override
        public IDrawable getIcon() {
            return icon;
        }

        @Override
        public void setRecipe(IRecipeLayout recipeLayout, ShapedRecipeWrapper recipeWrapper, IIngredients ingredients) {
            List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
            List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);
            List<List<FluidStack>> inputFluidStacks = ingredients.getInputs(VanillaTypes.FLUID);
            List<List<FluidStack>> outputFluidStacks = ingredients.getOutputs(VanillaTypes.FLUID);

            IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
            IGuiFluidStackGroup iGuiFluidStackGroup = recipeLayout.getFluidStacks();

            int sID = 0;
            for (int y = 0; y < 3; ++y) {
                for (int x = 0; x < 3; ++x) {
                    if (sID < inputs.size()) {
                        guiItemStacks.init(sID, true, x * 18, y * 18);
                    } else {
                        if (sID - inputs.size() < inputFluidStacks.size()) {
                            iGuiFluidStackGroup.init(sID, true, x * 18, y * 18);
                        }
                    }
                    sID++;
                }
            }

            sID = 0;
            for (int y = 0; y < 3; ++y) {
                for (int x = 0; x < 3; ++x) {
                    if (sID < outputs.size()) {
                        guiItemStacks.init(9 + sID, false, 90 + x * 18, y * 18);
                    } else {
                        if (sID - outputs.size() < outputFluidStacks.size()) {
                            iGuiFluidStackGroup.init(9 + sID, false, 90 + x * 18, y * 18);
                        }
                    }
                    sID++;
                }
            }

            guiItemStacks.addTooltipCallback(((slotIndex, input, itemStack, tooltip) -> {
                if (itemStack.hasTagCompound()) {
                    NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
                    assert nbtTagCompound != null;
                    if (nbtTagCompound.hasKey("probability")) {
                        DecimalFormat df = new DecimalFormat("0.00%");
                        tooltip.add(Lang.getLang("probability") + df.format(nbtTagCompound.getFloat("probability")));
                    }
                }
            }));
            iGuiFluidStackGroup.addTooltipCallback(((slotIndex, input, fluidStack, tooltip) -> {
                if (fluidStack.tag != null) {
                    if (fluidStack.tag.hasKey("mb")) {
                        tooltip.add(fluidStack.tag.getInteger("mb") + "mb");
                    }
                    if (fluidStack.tag.hasKey("probability")) {
                        DecimalFormat df = new DecimalFormat("0.00%");
                        tooltip.add(Lang.getLang("probability") + df.format(fluidStack.tag.getFloat("probability")));
                    }
                }
            }));

            guiItemStacks.set(ingredients);
            iGuiFluidStackGroup.set(ingredients);
        }
    }

    public static class ShapedRecipeWrapper implements IRecipeWrapper {

        public final HoverChecker buttonHoverChecker;
        public final Map<String, HoverChecker> hoverCheckerMap = new Map<>();
        public final Shaped shaped;

        public ShapedRecipeWrapper(Shaped shaped) {
            GuiButton guiButton = new GuiButton(0, 61, 19, 22, 15, "");
            buttonHoverChecker = new HoverChecker(guiButton, -1);
            this.shaped = shaped;
        }

        @Override
        public void getIngredients(IIngredients ingredients) {
            hoverCheckerMap.clear();
            Shaped.IJEIShaped ijeiShaped = shaped.getIJEIShaped();
            List<List<ItemStack>> itemIn = ijeiShaped.getItemIn();
            if (itemIn != null) {
                ingredients.setInputLists(VanillaTypes.ITEM, itemIn);
            }
            List<List<FluidStack>> fluidIn = ijeiShaped.getFluidIn();
            if (fluidIn != null) {
                ingredients.setInputLists(VanillaTypes.FLUID, fluidIn);
            }
            List<List<FluidStack>> fluidOut = ijeiShaped.getFluidOut();
            if (fluidOut != null) {
                ingredients.setOutputLists(VanillaTypes.FLUID, fluidOut);
            }
            List<List<ItemStack>> itemOut = ijeiShaped.getItemOut();
            if (itemOut != null) {
                ingredients.setOutputLists(VanillaTypes.ITEM, itemOut);
            }
        }

        @Override
        public List<String> getTooltipStrings(int mouseX, int mouseY) {
            List<String> tooltipStrings = new ArrayList<>();
            if (buttonHoverChecker.checkHover(mouseX, mouseY)) {
                tooltipStrings.add(Lang.getLang("message"));

                tooltipStrings.add(Lang.getLang("use.mana.level") + Lang.getLang(shaped.getManaLevel()));
                tooltipStrings.add(Lang.getLang("use.shaped.drive") + ShapedDrive.map.getKey(shaped.getShapedDrive()));
                if (shaped.consumeMana() > 0) {
                    tooltipStrings.add(Lang.getLang("consume.mana") + shaped.consumeMana());
                }
                if (shaped.surplusTiem() > 0) {
                    tooltipStrings.add(Lang.getLang("consume.time") + shaped.surplusTiem());
                }
                if (shaped.outMana() > 0) {
                    tooltipStrings.add(Lang.getLang("out.mana") + shaped.outMana());
                }
            }
            return tooltipStrings;
        }
    }

}
