package com.til.abyss_mana_2.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.*;

import java.util.Random;

public class Pos {

    public double x;
    public double y;
    public double z;

    public Pos() {
    }

    public Pos(Vec3i vec3i) {
        this(vec3i.getX() + 0.5, vec3i.getY() + 0.5, vec3i.getZ() + 0.5);
    }

    public Pos(double x, double y, double z) {
        setPos(x, y, z);
    }

    public Pos(Entity entity) {
        this.x = entity.posX;
        this.y = entity.posY + entity.height / 2;
        this.z = entity.posZ;
    }

    public Pos(Pos pos) {
        setPos(pos.x, pos.y, pos.z);
    }

    public Pos(TileEntity tileEntity) {
        this(tileEntity.getPos());
    }

    public Pos(double rotationYaw, double rotationPitch) {
        double fYawDtoR = (rotationYaw / 180d) * Math.PI;
        double fPitDtoR = (rotationPitch / 180d) * Math.PI;
        setPos(-Math.sin(fYawDtoR) * Math.cos(fPitDtoR), -Math.sin(fPitDtoR), Math.cos(fYawDtoR) * Math.cos(fPitDtoR));
    }

    public Pos(ByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
    }

    public Pos(NBTTagCompound nbtTagCompound) {
        x = nbtTagCompound.getDouble("x");
        y = nbtTagCompound.getDouble("y");
        z = nbtTagCompound.getDouble("z");
    }

    public ByteBuf writePos(ByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        return buf;
    }

    public NBTTagCompound getNBT() {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        nbtTagCompound.setDouble("x", x);
        nbtTagCompound.setDouble("y", y);
        nbtTagCompound.setDouble("z", z);
        return nbtTagCompound;
    }

    public NBTTagCompound writeNBT(NBTTagCompound nbtTagCompound) {
        nbtTagCompound.setDouble("x", x);
        nbtTagCompound.setDouble("y", y);
        nbtTagCompound.setDouble("z", z);
        return nbtTagCompound;
    }

    public double getAngle(Pos vec) {
        return Math.acos(normalize().dotProduct(vec.normalize()));
    }

    public Pos getRotate(double angle, Pos axis) {
        return Quat.aroundAxis(axis.normalize(), angle).rotate(this);
    }

    public double dotProduct(Pos vec) {
        double d = vec.x * x + vec.y * y + vec.z * z;

        if (d > 1 && d < 1.00001)
            d = 1;
        else if (d < -1 && d > -1.00001)
            d = -1;
        return d;
    }

    public Pos setPos(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Pos setPos(Pos pos) {
        return setPos(pos.x, pos.y, pos.z);
    }

    public Pos addX(double x) {
        this.x += x;
        return this;
    }

    public Pos addY(double y) {
        this.y += y;
        return this;
    }

    public Pos addZ(double z) {
        this.z += z;
        return this;
    }

    public AxisAlignedBB toAxisAlignedBB(double dAmbit) {
        return new AxisAlignedBB(x - dAmbit, y - dAmbit, z - dAmbit, x + dAmbit,
                y + dAmbit, z + dAmbit);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getDistance(Pos pos) {
        double f = this.x - pos.x;
        double f1 = this.y - pos.y;
        double f2 = this.z - pos.z;
        return MathHelper.sqrt(f * f + f1 * f1 + f2 * f2);
    }

    public Pos perpendicular() {
        if (z == 0)
            return zCrossProduct();
        return xCrossProduct();
    }

    public Pos xCrossProduct() {
        double d = z;
        double d1 = -y;
        return new Pos(0, d, d1);
    }

    public Pos zCrossProduct() {
        double d = y;
        double d1 = -x;
        return new Pos(d, d1, 0);
    }

    public Pos normalize() {
        double d = getMag();
        if (d != 0)
            return getMultiply(1 / d);

        return this;
    }

    public Pos getMultiply(double d) {
        return getMultiply(d, d, d);
    }

    public Pos getMultiply(double fx, double fy, double fz) {
        return new Pos(x * fx, y * fy, z * fz);
    }

    public Pos toMultiply(double fx, double fy, double fz) {
        x *= fx;
        y *= fy;
        z *= fz;
        return this;
    }

    public Pos toMultiply(double d) {
        return toMultiply(d, d, d);
    }

    public double getMag() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public float getDistance(Entity entity) {
        double f = this.x - entity.posX;
        double f1 = this.y - entity.posY + entity.height / 2;
        double f2 = this.z - entity.posZ;
        return MathHelper.sqrt(f * f + f1 * f1 + f2 * f2);
    }

    public BlockPos toBlockPos() {
        return new BlockPos(x, y, z);
    }

    public Entity moveEntity(Entity entity) {
        entity.setPositionAndUpdate(x, y, z);
        return entity;
    }

    public Pos toMove(Pos pos) {
        x += pos.x;
        y += pos.y;
        z += pos.z;
        return this;
    }

    public Pos getMove(Pos pos) {
        return new Pos(x + pos.x, y + pos.y, z + pos.z);
    }

    public Vec3d getVec3d() {
        return new Vec3d(x, y, z);
    }

    public static final Random rand = new Random();
    public static final Pos POS0 = new Pos();

    public static Pos getRandomPos() {
        return new Pos(rand.nextBoolean() ? rand.nextDouble() : -rand.nextDouble(),
                rand.nextBoolean() ? rand.nextDouble() : -rand.nextDouble(),
                rand.nextBoolean() ? rand.nextDouble() : -rand.nextDouble());
    }

    public static Pos getRandomPos(double x, double y, double z) {
        return new Pos(rand.nextBoolean() ? rand.nextDouble() * x : -(rand.nextDouble() * x),
                rand.nextBoolean() ? rand.nextDouble() * y : -(rand.nextDouble() * y),
                rand.nextBoolean() ? rand.nextDouble() * z : -(rand.nextDouble() * z));
    }

    public static Pos getRandomPos(double x, double y, double z, double xMin, double yMin, double zMin) {
        return new Pos((rand.nextBoolean() ? rand.nextDouble() * x : -(rand.nextDouble() * x)) + xMin,
                (rand.nextBoolean() ? rand.nextDouble() * y : -(rand.nextDouble() * y)) + yMin,
                (rand.nextBoolean() ? rand.nextDouble() * z : -(rand.nextDouble() * z)) + zMin);
    }

    public static Pos getCoordinateDifference(Pos pos1, Pos pos2) {
        return new Pos(pos2.x - pos1.x, pos2.y - pos1.y, pos2.z - pos1.z);
    }

    public static Pos getMovePos(Pos pos1, Pos pos2, double tick) {
        if (tick <= 0) return POS0;
        Pos pos = getCoordinateDifference(pos1, pos2);
        return new Pos(pos.x / tick, pos.y / tick, pos.z / tick);
    }

    public Pos getCoordinateDifference(Pos pos) {
        return new Pos(pos.x - this.x, pos.y - this.y, pos.z - this.z);
    }

    public Pos getContraryCoordinateDifference(Pos pos) {
        return new Pos(this.x - pos.x, this.y - pos.y, this.z - pos.z);
    }

    public Pos getMovePos(Pos pos, double tick) {
        if (tick <= 0) return POS0;
        Pos p = getCoordinateDifference(pos);
        return new Pos(p.x / tick, p.y / tick, p.z / tick);
    }

    public Pos toMovePos(Pos pos, double tick) {
        if (tick <= 0) return POS0;
        Pos p = getCoordinateDifference(pos);
        x = p.x / tick;
        y = p.y / tick;
        z = p.z / tick;
        return this;
    }

    public Pos toLimtx(double d) {
        d = Math.abs(d);
        if (Math.abs(x) > d) {
            if (x > 0) {
                x = d;
            } else {
                x = -d;
            }
        }
        return this;
    }

    public Pos toLimty(double d) {
        d = Math.abs(d);
        if (Math.abs(y) > d) {
            if (y > 0) {
                y = d;
            } else {
                y = -d;
            }
        }
        return this;
    }

    public Pos toLimtz(double d) {
        d = Math.abs(d);
        if (Math.abs(z) > d) {
            if (z > 0) {
                z = d;
            } else {
                z = -d;
            }
        }
        return this;
    }

    public Pos toLimit(double d) {
        d = Math.abs(d);
        if (Math.abs(x) > d) {
            if (x > 0) {
                x = d;
            } else {
                x = -d;
            }
        }

        if (Math.abs(y) > d) {
            if (y > 0) {
                y = d;
            } else {
                y = -d;
            }
        }

        if (Math.abs(z) > d) {
            if (z > 0) {
                z = d;
            } else {
                z = -d;
            }
        }

        return this;
    }

    public Pos getCrossProduct(Pos vec) {
        double d = y * vec.z - z * vec.y;
        double d1 = z * vec.x - x * vec.z;
        double d2 = x * vec.y - y * vec.x;
        return new Pos(d, d1, d2);
    }

    public Pos toNegative() {
        x = -x;
        y = -y;
        z = -z;
        return this;
    }

    public Pos toNegativeX() {
        x = -x;
        return this;
    }

    public Pos toNegativeY() {
        y = -y;
        return this;
    }

    public Pos toNegativeZ() {
        z = -z;
        return this;
    }

    public Pos getNegative() {
        return new Pos(this).toNegative();
    }

    public Pos getNegativeX() {
        return new Pos(this).toNegativeZ();
    }

    public Pos getNegativeY() {
        return new Pos(this).toNegativeY();
    }

    public Pos getNegativeZ() {
        return new Pos(this).toNegativeZ();
    }

    /***
     * 返回相量
     */
    public Pos getPhasor() {
        return new Pos(Math.atan2(this.x, this.y) * 180 / Math.PI, (Math.atan2(this.y, getSqrt()) * 180.0D / Math.PI), 0);
    }

    public Pos toPhasor() {
        x = Math.atan2(this.x, this.y) * 180 / Math.PI;
        y = Math.atan2(this.y, getSqrt()) * 180.0D / Math.PI;
        z = 0;
        return this;
    }

    public double getSqrt() {
        return Math.sqrt(this.x * this.x + this.z * this.z);
    }

    /***
     * 返回向量
     */
    public static Pos getVector(double rotationYaw, double rotationPitch) {
        double fYawDtoR = (rotationYaw / 180d) * Math.PI;
        double fPitDtoR = (rotationPitch / 180d) * Math.PI;
        return new Pos(-Math.sin(fYawDtoR) * Math.cos(fPitDtoR), -Math.sin(fPitDtoR), Math.cos(fYawDtoR) * Math.cos(fPitDtoR));
    }

    /***
     * 返回向量
     */
    public Pos getVector() {
        return getVector(x, y);
    }

    public Pos toVector() {
        setPos(getVector(x, y));
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof Pos) {
            Pos pos = ((Pos) obj);
            return pos.x == x && pos.y == y && pos.z == z;
        }

        return false;
    }

    public JsonObject getJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("x", new JsonPrimitive(x));
        jsonObject.add("y", new JsonPrimitive(y));
        jsonObject.add("z", new JsonPrimitive(z));
        return jsonObject;
    }

    public Pos(JsonObject jsonObject) {
        if (jsonObject.has("x")) {
            x = jsonObject.get("x").getAsDouble();
        }
        if (jsonObject.has("y")) {
            y = jsonObject.get("y").getAsDouble();
        }
        if (jsonObject.has("z")) {
            z = jsonObject.get("z").getAsDouble();
        }
    }

    @Override
    public String toString() {
        return "[" + x + "," + y + "," + z + "]";
    }

}
