package com.til.abyss_mana_2.client;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.client.key.AllKey;
import com.til.abyss_mana_2.client.particle.DefaultParticle;
import com.til.abyss_mana_2.client.particle.Lightning;
import com.til.abyss_mana_2.common.AllItem;
import com.til.abyss_mana_2.common.CommonProxy;
import com.til.abyss_mana_2.common.other_mod_interact.EventInteractClassTag;
import com.til.abyss_mana_2.common.register.*;
import com.til.abyss_mana_2.util.ClassUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Objects;


@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    public static ClientProxy getInstance() {
        return clientProxy;
    }

    protected static ClientProxy clientProxy;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        clientProxy = this;
        super.preInit(event);

        for (Class<?> aClass : ClassUtil.getClasses("com.til.abyss_mana_2.client.other_mod_interact")) {
            EventInteractClassTag subAnnotation = aClass.getAnnotation(EventInteractClassTag.class);
            if (subAnnotation != null) {
                if (Loader.isModLoaded(subAnnotation.modID()) && subAnnotation.value().equals(Side.CLIENT)) {
                    MinecraftForge.EVENT_BUS.register(aClass);
                }
            }
        }

        registerCustomModelLoader();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        registerColor();
        registerKey();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }

    @SubscribeEvent
    public void registerMadel(ModelRegistryEvent event) {
        for (Ore ore : Ore.register) {
            ore.item.forEach((oreType, item) -> ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Objects.requireNonNull(oreType.getRegistryName()), "inventory")));
            ore.itemBlock.forEach((oreBlock, itemBlock) -> ModelLoader.setCustomModelResourceLocation(itemBlock, 0, new ModelResourceLocation(Objects.requireNonNull(oreBlock.getRegistryName()), "inventory")));
            ore.fluidItem.forEach((oreFluid, item) -> ModelBakery.registerItemVariants(item));
            ore.fluidItem.forEach((oreFluid, item) -> {
                ModelLoader.setCustomMeshDefinition(item, stack -> {
                    ResourceLocation fileType = Objects.requireNonNull(oreFluid.getRegistryName());
                    return new ModelResourceLocation(fileType, fileType.getResourcePath());
                });
            });
            ore.fluidBlock.forEach((oreFluid, blockFluidClassic) -> {
                ResourceLocation fluidName = Objects.requireNonNull(blockFluidClassic.getRegistryName());
                ModelLoader.setCustomStateMapper(blockFluidClassic, new StateMapperBase() {
                    @Override
                    protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                        ResourceLocation fileType = Objects.requireNonNull(oreFluid.getRegistryName());
                        return new ModelResourceLocation(fileType, fileType.getResourcePath());
                    }
                });
            });
        }
        for (ManaLevel manaLevel : ManaLevel.register) {
            manaLevel.item.forEach((manaLevelItem, item) -> ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Objects.requireNonNull(manaLevelItem.getRegistryName()), "inventory")));
            manaLevel.itemBlock.forEach((oreBlock, itemBlock) -> ModelLoader.setCustomModelResourceLocation(itemBlock, 0, new ModelResourceLocation(Objects.requireNonNull(oreBlock.getRegistryName()), "inventory")));
        }
        ShapedDrive.register.forEach(d -> ModelLoader.setCustomModelResourceLocation(d.itemBlock, 0, new ModelResourceLocation(AbyssMana2.MODID + ":" + "shaped_drive", "inventory")));
        AllItem.independentItem.forEach(i -> ModelLoader.setCustomModelResourceLocation(i, 0, new ModelResourceLocation(Objects.requireNonNull(i.getRegistryName()), "inventory")));
    }

    public void registerColor() {
        for (Ore ore : Ore.register) {
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
                OreType oreType = ore.item.getKey(stack.getItem());
                return oreType != null ? oreType.getLayer() == tintIndex ? ore.getGenericParadigmMap().get(Ore.color).getRGB() : -1 : -1;
            }, ore.item.values().toArray(new Item[0]));
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
                OreBlock oreType = ore.itemBlock.getKey(stack.getItem());
                return oreType != null ? oreType.getLayer() == tintIndex ? ore.getGenericParadigmMap().get(Ore.color).getRGB() : -1 : -1;
            }, ore.itemBlock.values().toArray(new Item[0]));
            Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((state, worldIn, pos, tintIndex) -> {
                OreBlock oreType = ore.block.getKey(state.getBlock());
                return oreType != null ? oreType.getLayer() == tintIndex ? ore.getGenericParadigmMap().get(Ore.color).getRGB() : -1 : -1;
            }, ore.block.values().toArray(new Block[0]));
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
                OreFluid oreType = ore.fluidItem.getKey(stack.getItem());
                return oreType != null ? oreType.getLayer() == tintIndex ? ore.getGenericParadigmMap().get(Ore.color).getRGB() : -1 : -1;
            }, ore.fluidItem.values().toArray(new Item[0]));
            Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((state, worldIn, pos, tintIndex) -> {
                OreFluid oreType = ore.fluidBlock.getKey(state.getBlock());
                return oreType != null ? oreType.getLayer() == tintIndex ? ore.getGenericParadigmMap().get(Ore.color).getRGB() : -1 : -1;
            }, ore.fluidBlock.values().toArray(new Block[0]));

           /* Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
                OreFluid oreType = ore.fluidItem.getKey(stack.getItem());
                return oreType != null ? oreType.getLayer() == tintIndex ? new Color(255, 0, 0, 125).getRGB() : -1 : -1;
            }, ore.fluidItem.get(OreFluid.chargingRedstoneSolution));
            Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((state, worldIn, pos, tintIndex) -> {
                OreFluid oreType = ore.fluidBlock.getKey(state.getBlock());
                return oreType != null ? oreType.getLayer() == tintIndex ? new Color(255, 0, 0, 125).getRGB() : -1 : -1;
            }, ore.fluidBlock.get(OreFluid.chargingRedstoneSolution));

            Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
                OreFluid oreType = ore.fluidItem.getKey(stack.getItem());
                return oreType != null ? oreType.getLayer() == tintIndex ? new Color(4, 43, 173, 125).getRGB() : -1 : -1;
            }, ore.fluidItem.get(OreFluid.manaSolution));
            Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((state, worldIn, pos, tintIndex) -> {
                OreFluid oreType = ore.fluidBlock.getKey(state.getBlock());
                return oreType != null ? oreType.getLayer() == tintIndex ? new Color(4, 43, 173, 125).getRGB() : -1 : -1;
            }, ore.fluidBlock.get(OreFluid.manaSolution));*/

        }

        for (ManaLevel manaLevel : ManaLevel.register) {
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
                ManaLevelItem oreType = manaLevel.item.getKey(stack.getItem());
                return oreType != null ? oreType.getLayer() == tintIndex ? manaLevel.getGenericParadigmMap().get(ManaLevel.color).getRGB() : -1 : -1;
            }, manaLevel.item.values().toArray(new Item[0]));
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
                ManaLevelBlock oreType = manaLevel.itemBlock.getKey(stack.getItem());
                return oreType != null ? oreType.getLayer() == tintIndex ? manaLevel.getGenericParadigmMap().get(ManaLevel.color).getRGB() : -1 : -1;
            }, manaLevel.itemBlock.values().toArray(new Item[0]));
            Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((state, worldIn, pos, tintIndex) -> {
                ManaLevelBlock oreType = manaLevel.block.getKey(state.getBlock());
                return oreType != null ? oreType.getLayer() == tintIndex ? manaLevel.getGenericParadigmMap().get(ManaLevel.color).getRGB() : -1 : -1;
            }, manaLevel.block.values().toArray(new Block[0]));

            {
                Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
                    ManaLevelItem oreType = manaLevel.item.getKey(stack.getItem());
                    if (tintIndex == 1) {
                        return new Color(34, 43, 197).getRGB();
                    }
                    return oreType != null ? oreType.getLayer() == tintIndex ? manaLevel.getGenericParadigmMap().get(ManaLevel.color).getRGB() : -1 : -1;
                }, manaLevel.itemBlock.get(ManaLevelBlock.moonlight));
                Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((state, worldIn, pos, tintIndex) -> {
                    ManaLevelBlock oreType = manaLevel.block.getKey(state.getBlock());
                    if (tintIndex == 1) {
                        return new Color(34, 43, 197).getRGB();
                    }
                    return oreType != null ? oreType.getLayer() == tintIndex ? manaLevel.getGenericParadigmMap().get(ManaLevel.color).getRGB() : -1 : -1;
                }, manaLevel.block.get(ManaLevelBlock.moonlight));
            }
            {
                Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
                    ManaLevelItem oreType = manaLevel.item.getKey(stack.getItem());
                    if (tintIndex == 1) {
                        return new Color(255, 245, 46).getRGB();
                    }
                    return oreType != null ? oreType.getLayer() == tintIndex ? manaLevel.getGenericParadigmMap().get(ManaLevel.color).getRGB() : -1 : -1;
                }, manaLevel.itemBlock.get(ManaLevelBlock.sunlight));
                Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((state, worldIn, pos, tintIndex) -> {
                    ManaLevelBlock oreType = manaLevel.block.getKey(state.getBlock());
                    if (tintIndex == 1) {
                        return new Color(255, 245, 46).getRGB();
                    }
                    return oreType != null ? oreType.getLayer() == tintIndex ? manaLevel.getGenericParadigmMap().get(ManaLevel.color).getRGB() : -1 : -1;
                }, manaLevel.block.get(ManaLevelBlock.sunlight));
            }

        }

        for (ShapedDrive shapedDrive : ShapedDrive.register) {
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
                int i = ShapedDrive.map.getKey(shapedDrive);
                return shapedDrive.getLayer() == tintIndex ? new Color(125, 255 - 16 * i, 75).getRGB() : -1;
            }, shapedDrive.itemBlock);
            Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((state, worldIn, pos, tintIndex) -> {
                int i = ShapedDrive.map.getKey(shapedDrive);
                return shapedDrive.getLayer() == tintIndex ? new Color(125, 255 - 16 * i, 75).getRGB() : -1;
            }, shapedDrive.block);
        }

        Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
            NBTTagCompound nbtTagCompound = stack.getTagCompound();
            return nbtTagCompound != null ? nbtTagCompound.getInteger("color") : -1;
        }, AllItem.bindStaff);

    }

    public void registerCustomModelLoader() {
        ModelLoaderRegistry.registerLoader(new ICustomModelLoader() {

            @Override
            public void onResourceManagerReload(IResourceManager resourceManager) {

            }

            @Override
            public boolean accepts(ResourceLocation modelLocation) {
                for (Ore ore : Ore.register) {
                    for (Map.Entry<OreBlock, Block> oreBlockItemBlockEntry : ore.block.entrySet()) {
                        if (Objects.equals(oreBlockItemBlockEntry.getValue().getRegistryName(), modelLocation)) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public IModel loadModel(ResourceLocation modelLocation) throws Exception {
                for (Ore ore : Ore.register) {
                    for (Map.Entry<OreBlock, Block> oreBlockItemBlockEntry : ore.block.entrySet()) {
                        if (Objects.equals(oreBlockItemBlockEntry.getValue().getRegistryName(), modelLocation)) {
                            ResourceLocation name = Objects.requireNonNull(oreBlockItemBlockEntry.getKey().getRegistryName());
                            return ModelLoaderRegistry.getModel(new ResourceLocation(name.getResourceDomain(), "block/" + name.getResourcePath()));
                        }
                    }
                }
                return ModelLoaderRegistry.getMissingModel();
            }
        });
        ModelLoaderRegistry.registerLoader(new ICustomModelLoader() {
            @Override
            public void onResourceManagerReload(IResourceManager resourceManager) {

            }

            @Override
            public boolean accepts(ResourceLocation modelLocation) {
                for (ManaLevel manaLevel : ManaLevel.register) {
                    for (Map.Entry<ManaLevelBlock, Block> entry : manaLevel.block.entrySet()) {
                        if (Objects.equals(entry.getValue().getRegistryName(), modelLocation)) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public IModel loadModel(ResourceLocation modelLocation) throws Exception {
                for (ManaLevel manaLevel : ManaLevel.register) {
                    for (Map.Entry<ManaLevelBlock, Block> entry : manaLevel.block.entrySet()) {
                        if (Objects.equals(entry.getValue().getRegistryName(), modelLocation)) {
                            ResourceLocation name = Objects.requireNonNull(entry.getKey().getRegistryName());
                            if (modelLocation instanceof ModelResourceLocation) {
                                ModelResourceLocation modelResourceLocation = (ModelResourceLocation) modelLocation;
                                if (!modelResourceLocation.getVariant().equals("normal")) {
                                    return ModelLoaderRegistry.getModel(new ModelResourceLocation(new ResourceLocation(name.getResourceDomain(), name.getResourcePath()), modelResourceLocation.getVariant()));
                                }
                            }
                            return ModelLoaderRegistry.getModel(new ResourceLocation(name.getResourceDomain(), "block/" + name.getResourcePath()));
                        }
                    }
                }
                return ModelLoaderRegistry.getMissingModel();
            }
        });
        ModelLoaderRegistry.registerLoader(new ICustomModelLoader() {
            @Override
            public void onResourceManagerReload(IResourceManager resourceManager) {

            }

            @Override
            public boolean accepts(ResourceLocation modelLocation) {
                for (ShapedDrive shapedDrive : ShapedDrive.register) {
                    if (Objects.equals(shapedDrive.getRegistryName(), modelLocation)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public IModel loadModel(ResourceLocation modelLocation) throws Exception {
                for (ShapedDrive shapedDrive : ShapedDrive.register) {
                    if (Objects.equals(shapedDrive.getRegistryName(), modelLocation)) {
                        return ModelLoaderRegistry.getModel(new ResourceLocation(AbyssMana2.MODID, "block/shaped_drive"));
                    }
                }
                return ModelLoaderRegistry.getMissingModel();
            }
        });
    }

    public void registerKey() {
        AllKey.init();
    }

    public final Deque<Lightning> queuedLightningBolts = new ArrayDeque<>();
    public final ResourceLocation outsideResource = new ResourceLocation(AbyssMana2.MODID, "textures/particle/modparticle.png");
    public final ResourceLocation insideResource = new ResourceLocation(AbyssMana2.MODID, "textures/particle/small.png");
    public final int BATCH_THRESHOLD = 200;

    public void offerLightning(Lightning lightning) {
        queuedLightningBolts.offer(lightning);
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event) {

    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        Profiler profiler = Minecraft.getMinecraft().mcProfiler;
        Tessellator tessellator = Tessellator.getInstance();
        profiler.endStartSection("lightning");
        {
            float frame = event.getPartialTicks();
            Entity entity = Minecraft.getMinecraft().player;
            TextureManager render = Minecraft.getMinecraft().renderEngine;

            double interpPosX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * frame;
            double interpPosY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * frame;
            double interpPosZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * frame;

            GlStateManager.pushMatrix();
            GlStateManager.translate(-interpPosX, -interpPosY, -interpPosZ);

            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            render.bindTexture(outsideResource);
            int counter = 0;

            tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
            for (Lightning bolt : queuedLightningBolts) {
                bolt.renderBolt(0, false);
                if (counter % BATCH_THRESHOLD == BATCH_THRESHOLD - 1) {
                    tessellator.draw();
                    tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
                }
                counter++;
            }
            tessellator.draw();

            render.bindTexture(insideResource);
            counter = 0;

            tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
            for (Lightning bolt : queuedLightningBolts) {
                bolt.renderBolt(1, true);
                if (counter % BATCH_THRESHOLD == BATCH_THRESHOLD - 1) {
                    tessellator.draw();
                    tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
                }
                counter++;
            }
            tessellator.draw();

            queuedLightningBolts.clear();

            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);

            GlStateManager.translate(interpPosX, interpPosY, interpPosZ);
            GlStateManager.popMatrix();

            profiler.endSection();
            profiler.endSection();
        }
        profiler.endStartSection("defaultParticle");
        {
            GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);
            GlStateManager.disableLighting();

            profiler.endStartSection("defaultParticle");
            DefaultParticle.dispatchQueuedRenders(tessellator);
            profiler.endSection();

            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            GL11.glPopAttrib();
        }
    }

    @SubscribeEvent
    public void doWorldTickEventOFClientProxy(TickEvent.WorldTickEvent event) {

    }

}
