package com.peak.florality.phys.entity.core;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.peak.florality.phys.Path;
import org.joml.Vector3d;

public class RigidBodyEntity extends Entity {
    protected Vec3d velocity = Vec3d.ZERO;
    protected Vec3d angularVelocity = Vec3d.ZERO;
    protected double mass = 1.0;
    protected double drag = 0.02;
    protected double angularDrag = 0.02;
    protected double restitution = 0.5;

    private static final TrackedData<Float> YAW_TRACKER =
            DataTracker.registerData(RigidBodyEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> PITCH_TRACKER =
            DataTracker.registerData(RigidBodyEntity.class, TrackedDataHandlerRegistry.FLOAT);

    private Path followPath = null;
    private int pathIndex = 0;
    private double pathSpeed = 0.1;

    public RigidBodyEntity(EntityType<? extends RigidBodyEntity> type, World world) {
        super(type, world);
    }

    public void follow(Path path, double speed) {
        this.followPath = path;
        this.pathSpeed = speed;
        this.pathIndex = 0;
    }

    @Override
    public void tick() {
        super.tick();

//        if (followPath != null && pathIndex < followPath.getPoints().size()) {
//            Vector3d target = followPath.getPoints().get(pathIndex)(;
//            Vector3d pos = new Vector3d(this.getX(), this.getY(), this.getZ());
//            Vector3d dir = new Vector3d(target).sub(pos);
//            double distance = dir.length();
//            if (distance < 0.01) pathIndex++;
//            else {
//                dir.normalize().mul(pathSpeed);
//                velocity = new Vec3d(dir.x, dir.y, dir.z);
//            }
//        }

        if (!this.isOnGround()) velocity = velocity.add(0, -0.08, 0);
        velocity = velocity.multiply(1 - drag);

        Vec3d pos = this.getEntityPos();
        Vec3d move = velocity;

        pos = resolveCollision(pos, move.x, 0, 0);
        pos = resolveCollision(pos, 0, move.y, 0);
        pos = resolveCollision(pos, 0, 0, move.z);

        this.setPos(pos.x, pos.y, pos.z);

        angularVelocity = angularVelocity.multiply(1 - angularDrag);
        this.setYaw(this.getYaw() + (float) angularVelocity.y);
        this.setPitch(this.getPitch() + (float) angularVelocity.x);

        this.dataTracker.set(YAW_TRACKER, this.getYaw());
        this.dataTracker.set(PITCH_TRACKER, this.getPitch());
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    private Vec3d resolveCollision(Vec3d pos, double dx, double dy, double dz) {
        if (dx == 0 && dy == 0 && dz == 0) return pos;

        Box box = this.getBoundingBox().offset(dx, dy, dz);

        for (BlockPos blockPos : BlockPos.iterate(
                BlockPos.ofFloored(box.minX, box.minY, box.minZ),
                BlockPos.ofFloored(box.maxX, box.maxY, box.maxZ))) {

            if (!this.getEntityWorld().isAir(blockPos)) {
                Box blockBox = this.getEntityWorld().getBlockState(blockPos)
                        .getOutlineShape(this.getEntityWorld(), blockPos)
                        .getBoundingBox()
                        .offset(blockPos.getX(), blockPos.getY(), blockPos.getZ());

                if (box.intersects(blockBox)) {
                    if (dx != 0) velocity = new Vec3d(-velocity.x * restitution, velocity.y, velocity.z);
                    if (dy != 0) velocity = new Vec3d(velocity.x, -velocity.y * restitution, velocity.z);
                    if (dz != 0) velocity = new Vec3d(velocity.x, velocity.y, -velocity.z * restitution);

                    if (dx != 0) dx = 0;
                    if (dy != 0) dy = 0;
                    if (dz != 0) dz = 0;

                    box = this.getBoundingBox().offset(dx, dy, dz);
                }
            }
        }

        return pos.add(dx, dy, dz);
    }

    public void applyForce(Vec3d force) {
        velocity = velocity.add(force.multiply(1.0 / mass));
    }

    public void applyTorque(Vec3d torque) {
        angularVelocity = angularVelocity.add(torque);
    }

    public Vec3d getVelocity() { return velocity; }
    public Vec3d getAngularVelocity() { return angularVelocity; }
    public void setVelocity(Vec3d v) { velocity = v; }
    public void setAngularVelocity(Vec3d v) { angularVelocity = v; }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        this.dataTracker.set(YAW_TRACKER, this.getYaw());
        this.dataTracker.set(PITCH_TRACKER, this.getPitch());
    }

    @Override
    protected void readCustomData(ReadView view) {
        velocity = new Vec3d(
                view.getDouble("VelX", 0.0),
                view.getDouble("VelY", 0.0),
                view.getDouble("VelZ", 0.0)
        );

        angularVelocity = new Vec3d(
                view.getDouble("AngVelX", 0.0),
                view.getDouble("AngVelY", 0.0),
                view.getDouble("AngVelZ", 0.0)
        );

        mass = view.getDouble("Mass", 1.0);
        drag = view.getDouble("Drag", 0.02);
        angularDrag = view.getDouble("AngularDrag", 0.02);
        restitution = view.getDouble("Restitution", 0.5);
    }

    @Override
    protected void writeCustomData(WriteView view) {
        view.putDouble("VelX", velocity.x);
        view.putDouble("VelY", velocity.y);
        view.putDouble("VelZ", velocity.z);
        view.putDouble("AngVelX", angularVelocity.x);
        view.putDouble("AngVelY", angularVelocity.y);
        view.putDouble("AngVelZ", angularVelocity.z);
        view.putDouble("Mass", mass);
        view.putDouble("Drag", drag);
        view.putDouble("AngularDrag", angularDrag);
        view.putDouble("Restitution", restitution);
    }
}