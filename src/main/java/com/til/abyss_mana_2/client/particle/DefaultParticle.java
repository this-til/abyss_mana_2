package com.til.abyss_mana_2.client.particle;

import com.til.abyss_mana_2.client.ClientProxy;
import com.til.abyss_mana_2.util.Pos;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DefaultParticle extends Particle {

    public int particleHalfAge;
    public float particleMaxAlpha;
    public float particleEveryTimeUpAlpha;
    public float particleMaxScale;
    public float particleEveryTimeUpScale;

    private final int framesNumber;
    private final double oneFramesNumber;
    private final ResourceLocation resourceLocation;

    public static final Map<ResourceLocation, List<DefaultParticle>> MAP = new HashMap<>();

    private float f;
    private float f1;
    private float f2;
    private float f3;
    private float f4;
    private float f5;

    public DefaultParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super(worldIn == null ? Minecraft.getMinecraft().player.world : worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.particleMaxAge = getMaxAge();
        this.particleHalfAge = this.particleMaxAge / 2;
        this.particleAlpha = 0;
        this.particleScale = 0;
        this.particleMaxScale = getMaxScale();
        this.particleMaxAlpha = getMaxAlpha();
        this.particleGravity = getGravity();
        Pos posMove = getMove();
        this.motionX = posMove.getX();
        this.motionY = posMove.getY();
        this.motionZ = posMove.getZ();
        this.particleEveryTimeUpScale = this.particleMaxScale / particleHalfAge;
        this.particleEveryTimeUpAlpha = this.particleMaxAlpha / particleHalfAge;
        if (!getChangeAlpa()) this.particleAlpha = getMaxAlpha();
        if (!getChangeScale()) this.particleScale = getMaxScale();
        Pos posRGB = getRBG();
        this.setRBGColorF((float) posRGB.getX(), (float) posRGB.getY(), (float) posRGB.getZ());
        resourceLocation = getResourceLocation();
        this.canCollide = getCanCollide();
        framesNumber = framesNumber();
        oneFramesNumber = 1d / framesNumber;
    }

    @Override
    public void onUpdate() {

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }

        motionY -= 0.04D * (double) this.particleGravity;
        this.move(motionX, motionY, motionZ);

        if (this.particleAge < this.particleHalfAge) {
            if (getChangeScale()) this.particleScale += this.particleEveryTimeUpScale;
            if (getChangeAlpa()) this.particleAlpha += this.particleEveryTimeUpAlpha;
        } else {
            if (getChangeScale()) this.particleScale -= this.particleEveryTimeUpScale;
            if (getChangeAlpa()) this.particleAlpha -= this.particleEveryTimeUpAlpha;
        }
    }

    @Override
    public void renderParticle(BufferBuilder vertexBuffer, Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {

        this.f = f;
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
        this.f4 = f4;
        this.f5 = f5;

        MAP.computeIfAbsent(this.resourceLocation, k -> new ArrayList<>()).add(this);

    }

    public static void dispatchQueuedRenders(Tessellator tessellator) {

        GlStateManager.color(1F, 1.0F, 0.5F, 0.75F);

        for (Map.Entry<ResourceLocation, List<DefaultParticle>> resourceLocationListEntry : MAP.entrySet()) {
            Minecraft.getMinecraft().renderEngine.bindTexture(resourceLocationListEntry.getKey());
            List<DefaultParticle> list = resourceLocationListEntry.getValue();
            tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
            for (DefaultParticle modParticle : list) {
                modParticle.renderQueued(tessellator);
            }
            tessellator.draw();
            list.clear();
        }
    }

    private void renderQueued(Tessellator tessellator) {

        float f10 = 0.5F * particleScale;
        float f11 = (float) (prevPosX + (posX - prevPosX) * f - interpPosX);
        float f12 = (float) (prevPosY + (posY - prevPosY) * f - interpPosY);
        float f13 = (float) (prevPosZ + (posZ - prevPosZ) * f - interpPosZ);

        int combined = 15 << 20 | 15 << 4;
        int k3 = combined >> 16 & 0xFFFF;
        int l3 = combined & 0xFFFF;

        int frames = particleAge % framesNumber;
        double v = oneFramesNumber * frames;
        double v2 = v + oneFramesNumber;

        tessellator.getBuffer().pos(f11 - f1 * f10 - f4 * f10, f12 - f2 * f10, f13 - f3 * f10 - f5 * f10).tex(0, v).lightmap(k3, l3).color(particleRed, particleGreen, particleBlue, 1F).endVertex();
        tessellator.getBuffer().pos(f11 - f1 * f10 + f4 * f10, f12 + f2 * f10, f13 - f3 * f10 + f5 * f10).tex(1, v).lightmap(k3, l3).color(particleRed, particleGreen, particleBlue, 1F).endVertex();
        tessellator.getBuffer().pos(f11 + f1 * f10 + f4 * f10, f12 + f2 * f10, f13 + f3 * f10 + f5 * f10).tex(1, v2).lightmap(k3, l3).color(particleRed, particleGreen, particleBlue, 1F).endVertex();
        tessellator.getBuffer().pos(f11 + f1 * f10 - f4 * f10, f12 - f2 * f10, f13 + f3 * f10 - f5 * f10).tex(0, v2).lightmap(k3, l3).color(particleRed, particleGreen, particleBlue, 1F).endVertex();

    }


    public Pos getMove() {
        return new Pos();
    }

    public Pos getRBG() {
        return new Pos(1, 1, 1);
    }

    public float getMaxScale() {
        return 1;
    }

    public float getMaxAlpha() {
        return 1f;
    }

    public int getMaxAge() {
        return 100;
    }

    public ResourceLocation getResourceLocation() {
        return ClientProxy.getInstance().outsideResource;
    }

    public float getGravity() {
        return 0;
    }

    public boolean getChangeAlpa() {
        return false;
    }

    public boolean getChangeScale() {
        return true;
    }

    public boolean getCanCollide() {
        return false;
    }

    public int framesNumber() {
        return 1;
    }

}
