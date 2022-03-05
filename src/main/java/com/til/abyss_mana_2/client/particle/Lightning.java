package com.til.abyss_mana_2.client.particle;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.client.ClientProxy;
import com.til.abyss_mana_2.util.Pos;
import gnu.trove.map.hash.TIntIntHashMap;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.*;

public abstract class Lightning extends Particle {

    private static final int fadetime = 20;
    private final TIntIntHashMap splitParents = new TIntIntHashMap();
    private final double length;
    private final Random rand = new Random();

    private List<LightningSegment> segments = new ArrayList<>();
    private int segmentCount = 1;
    private int splitCount;
    private float speed = 1.5F;

    public Lightning(World world, Pos sourcevec, Pos targetvec, float ticksPerMeter) {
        super(world == null ? Minecraft.getMinecraft().world : world, sourcevec.getX(), sourcevec.getY(), sourcevec.getZ());
        speed = ticksPerMeter;
        length = targetvec.getContraryCoordinateDifference(sourcevec).getMag();
        particleMaxAge = fadetime + rand.nextInt(fadetime) - fadetime / 2;
        particleAge = -(int) (length * speed);

        segments.add(new LightningSegment(sourcevec, targetvec));

        fractal(2, length / 1.5, 0.7F, 0.7F, 45);
        fractal(2, length / 4, 0.5F, 0.8F, 50);
        fractal(2, length / 15, 0.5F, 0.9F, 55);
        fractal(2, length / 30, 0.5F, 1.0F, 60);
        fractal(2, length / 60, 0, 0, 0);
        fractal(2, length / 100, 0, 0, 0);
        fractal(2, length / 400, 0, 0, 0);

        calculateCollisionAndDiffs();

        segments.sort((o1, o2) -> Float.compare(o2.light, o1.light));
    }

    private float rayTraceResistance(Pos start, Pos end, float prevresistance) {
        RayTraceResult mop = world.rayTraceBlocks(start.getVec3d(), end.getVec3d());

        if (mop == null)
            return prevresistance;

        if (mop.typeOfHit == RayTraceResult.Type.BLOCK) {
            Block block = world.getBlockState(mop.getBlockPos()).getBlock();

            if (world.isAirBlock(mop.getBlockPos()))
                return prevresistance;

            return prevresistance + block.getExplosionResistance(null) + 0.3F;
        } else return prevresistance;
    }

    private void fractal(int splits, double amount, double splitChance, double splitLength, double splitAngle) {
        List<LightningSegment> oldSegments = segments;
        segments = new ArrayList<>();

        LightningSegment prev;

        for (LightningSegment segment : oldSegments) {
            prev = segment.prev;

            Pos subsegment = segment.diff.getMultiply(1F / splits);

            LightningPos[] newpoints = new LightningPos[splits + 1];

            Pos startpoint = segment.startPoint.point;
            newpoints[0] = segment.startPoint;
            newpoints[splits] = segment.endPoint;

            for (int i = 1; i < splits; i++) {
                Pos randoff = segment.diff.perpendicular().normalize().getRotate(rand.nextFloat() * 360, segment.diff);
                randoff = randoff.getMultiply((rand.nextFloat() - 0.5F) * amount * 2);

                Pos basepoint = new Pos(startpoint).toMove(subsegment.getMultiply(i));

                newpoints[i] = new LightningPos(basepoint, randoff);
            }

            for (int i = 0; i < splits; i++) {
                LightningSegment next = new LightningSegment(newpoints[i], newpoints[i + 1], segment.light, segment.segmentNo * splits + i, segment.splitNo);
                next.prev = prev;
                if (prev != null)
                    prev.next = next;

                if (i != 0 && rand.nextFloat() < splitChance) {
                    Pos splitrot = next.diff.xCrossProduct().getRotate(rand.nextFloat() * 360, next.diff);
                    Pos diff = next.diff.getRotate((rand.nextFloat() * 0.66F + 0.33F) * splitAngle, splitrot).getMultiply(splitLength);

                    splitCount++;
                    splitParents.put(splitCount, next.splitNo);

                    LightningSegment split = new LightningSegment(newpoints[i], new LightningPos(newpoints[i + 1].basepoint, new Pos(newpoints[i + 1].offsetvec).toMove(diff)), segment.light / 2F, next.segmentNo, splitCount);
                    split.prev = prev;

                    segments.add(split);
                }

                prev = next;
                segments.add(next);
            }

            if (segment.next != null)
                segment.next.prev = prev;
        }

        segmentCount *= splits;
    }

    private void calculateCollisionAndDiffs() {
        TIntIntHashMap lastactivesegment = new TIntIntHashMap();

        segments.sort((o1, o2) -> {
            int comp = Integer.compare(o1.splitNo, o2.splitNo);
            if (comp == 0)
                return Integer.compare(o1.segmentNo, o2.segmentNo);
            else return comp;
        });

        int lastSplitCalc = 0;
        int lastActiveSegment = 0;// unterminated
        float splitResistance = 0;

        for (LightningSegment segment : segments) {
            if (segment.splitNo > lastSplitCalc) {
                lastactivesegment.put(lastSplitCalc, lastActiveSegment);
                lastSplitCalc = segment.splitNo;
                lastActiveSegment = lastactivesegment.get(splitParents.get(segment.splitNo));
                splitResistance = lastActiveSegment < segment.segmentNo ? 50 : 0;
            }

            if (splitResistance >= 40 * segment.light)
                continue;

            splitResistance = rayTraceResistance(segment.startPoint.point, segment.endPoint.point, splitResistance);
            lastActiveSegment = segment.segmentNo;
        }
        lastactivesegment.put(lastSplitCalc, lastActiveSegment);

        lastSplitCalc = 0;
        lastActiveSegment = lastactivesegment.get(0);
        for (Iterator<LightningSegment> iterator = segments.iterator(); iterator.hasNext(); ) {
            LightningSegment segment = iterator.next();
            if (lastSplitCalc != segment.splitNo) {
                lastSplitCalc = segment.splitNo;
                lastActiveSegment = lastactivesegment.get(segment.splitNo);
            }

            if (segment.segmentNo > lastActiveSegment)
                iterator.remove();
            segment.calcEndDiffs();
        }
    }

    public void renderBolt(int pass, boolean inner) {
        BufferBuilder wr = Tessellator.getInstance().getBuffer();

        float boltAge = particleAge < 0 ? 0 : (float) particleAge / (float) particleMaxAge;
        float mainAlpha;
        if (pass == 0)
            mainAlpha = (1 - boltAge) * 0.4F;
        else mainAlpha = 1 - boltAge * 0.5F;

        int expandTime = (int) (length * speed);

        int renderstart = (int) ((expandTime / 2 - particleMaxAge + particleAge) / (float) (expandTime / 2) * segmentCount);
        int renderend = (int) ((particleAge + expandTime) / (float) expandTime * segmentCount);

        for (LightningSegment rendersegment : segments) {
            if (rendersegment.segmentNo < renderstart || rendersegment.segmentNo > renderend)
                continue;

            Pos playerVec = getRelativeViewVector(rendersegment.startPoint.point).getMultiply(-1);

            double width = 0.025F * (playerVec.getMag() / 5 + 1) * (1 + rendersegment.light) * 0.5F;

            Pos diff1 = playerVec.getCrossProduct(rendersegment.prevDiff).normalize().getMultiply(width / rendersegment.sinPrev);
            Pos diff2 = playerVec.getCrossProduct(rendersegment.nextDiff).normalize().getMultiply(width / rendersegment.sinNext);

            Pos startvec = rendersegment.startPoint.point;
            Pos endvec = rendersegment.endPoint.point;

            Pos rgb = inner ? getColorInner() : getColorOuter();
            float r = ((float) rgb.getX());
            float g = ((float) rgb.getY());
            float b = ((float) rgb.getZ());
            float a = mainAlpha * rendersegment.light;

            wr.pos(endvec.getX() - diff2.getX(), endvec.getY() - diff2.getY(), endvec.getZ() - diff2.getZ()).tex(0.5, 0).lightmap(0xF0, 0xF0).color(r, g, b, a).endVertex();
            wr.pos(startvec.getX() - diff1.getX(), startvec.getY() - diff1.getY(), startvec.getZ() - diff1.getZ()).tex(0.5, 0).lightmap(0xF0, 0xF0).color(r, g, b, a).endVertex();
            wr.pos(startvec.getX() + diff1.getX(), startvec.getY() + diff1.getY(), startvec.getZ() + diff1.getZ()).tex(0.5, 1).lightmap(0xF0, 0xF0).color(r, g, b, a).endVertex();
            wr.pos(endvec.getX() + diff2.getX(), endvec.getY() + diff2.getY(), endvec.getZ() + diff2.getZ()).tex(0.5, 1).lightmap(0xF0, 0xF0).color(r, g, b, a).endVertex();

            if (rendersegment.next == null) {
                Pos roundend = new Pos(rendersegment.endPoint.point).toMove(rendersegment.diff.normalize().getMultiply(width));

                wr.pos(roundend.getX() - diff2.getX(), roundend.getY() - diff2.getY(), roundend.getZ() - diff2.getZ()).tex(0, 0).lightmap(0xF0, 0xF0).color(r, g, b, a).endVertex();
                wr.pos(endvec.getX() - diff2.getX(), endvec.getY() - diff2.getY(), endvec.getZ() - diff2.getZ()).tex(0.5, 0).lightmap(0xF0, 0xF0).color(r, g, b, a).endVertex();
                wr.pos(endvec.getX() + diff2.getX(), endvec.getY() - diff2.getY(), endvec.getZ() + diff2.getZ()).tex(0.5, 1).lightmap(0xF0, 0xF0).color(r, g, b, a).endVertex();
                wr.pos(roundend.getX() + diff2.getX(), roundend.getY() + diff2.getY(), roundend.getZ() + diff2.getZ()).tex(0, 1).lightmap(0xF0, 0xF0).color(r, g, b, a).endVertex();
            }

            if (rendersegment.prev == null) {
                Pos roundend = rendersegment.startPoint.point.getContraryCoordinateDifference(rendersegment.diff.normalize().getMultiply(width));

                wr.pos(startvec.getX() - diff1.getX(), startvec.getY() - diff1.getY(), startvec.getZ() - diff1.getZ()).tex(0.5, 0).lightmap(0xF0, 0xF0).color(r, g, b, a).endVertex();
                wr.pos(roundend.getX() - diff1.getX(), roundend.getY() - diff1.getY(), roundend.getZ() - diff1.getZ()).tex(0, 0).lightmap(0xF0, 0xF0).color(r, g, b, a).endVertex();
                wr.pos(roundend.getX() + diff1.getX(), roundend.getY() + diff1.getY(), roundend.getZ() + diff1.getZ()).tex(0, 1).lightmap(0xF0, 0xF0).color(r, g, b, a).endVertex();
                wr.pos(startvec.getX() + diff1.getX(), startvec.getY() + diff1.getY(), startvec.getZ() + diff1.getZ()).tex(0.5, 1).lightmap(0xF0, 0xF0).color(r, g, b, a).endVertex();
            }
        }
    }

    public abstract Pos getColorInner();

    public abstract Pos getColorOuter();

    private static Pos getRelativeViewVector(Pos pos) {
        Entity renderEntity = Minecraft.getMinecraft().getRenderViewEntity();
        if (renderEntity != null) {
            return new Pos((float) renderEntity.posX - pos.getX(), (float) renderEntity.posY + renderEntity.getEyeHeight() - pos.getY(), (float) renderEntity.posZ - pos.getZ());
        }
        return new Pos();
    }

    @Override
    public void renderParticle(BufferBuilder wr, Entity entity, float partialTicks, float rotX, float rotZ, float rotYZ, float rotXY, float rotXZ) {
        ((ClientProxy) AbyssMana2.proxy).offerLightning(this);
    }

    public static class LightningSegment {

        public final LightningPos startPoint;
        public final LightningPos endPoint;

        public Pos diff;

        public LightningSegment prev;
        public LightningSegment next;

        public Pos nextDiff;
        public Pos prevDiff;

        public float sinPrev;
        public float sinNext;
        public final float light;

        public final int segmentNo;
        public final int splitNo;

        public LightningSegment(LightningPos start, LightningPos end, float light, int segmentnumber, int splitnumber) {
            startPoint = start;
            endPoint = end;
            this.light = light;
            segmentNo = segmentnumber;
            splitNo = splitnumber;

            calcDiff();
        }

        public LightningSegment(Pos start, Pos end) {
            this(new LightningPos(start, new Pos(0, 0, 0)), new LightningPos(end, new Pos(0, 0, 0)), 1, 0, 0);
        }

        public void calcDiff() {
            diff = endPoint.point.getContraryCoordinateDifference(startPoint.point);
        }

        public void calcEndDiffs() {
            if (prev != null) {
                Pos prevdiffnorm = prev.diff.normalize();
                Pos thisdiffnorm = diff.normalize();

                prevDiff = new Pos(thisdiffnorm).toMove(prevdiffnorm).normalize();
                sinPrev = (float) Math.sin(thisdiffnorm.getAngle(prevdiffnorm.getMultiply(-1)) / 2);
            } else {
                prevDiff = diff.normalize();
                sinPrev = 1;
            }

            if (next != null) {
                Pos nextdiffnorm = next.diff.normalize();
                Pos thisdiffnorm = diff.normalize();

                nextDiff = new Pos(thisdiffnorm).toMove(nextdiffnorm).normalize();
                sinNext = (float) Math.sin(thisdiffnorm.getAngle(nextdiffnorm.getMultiply(-1)) / 2);
            } else {
                nextDiff = diff.normalize();
                sinNext = 1;
            }
        }

        @Override
        public String toString() {
            return startPoint.point.toString() + " " + endPoint.point.toString();
        }

    }

    public static class LightningPos {

        public final Pos point;
        public final Pos basepoint;
        public final Pos offsetvec;

        public LightningPos(Pos basepoint, Pos offsetvec) {
            this.basepoint = basepoint;
            this.offsetvec = offsetvec;
            point = new Pos(basepoint).toMove(offsetvec);
        }

    }
}
