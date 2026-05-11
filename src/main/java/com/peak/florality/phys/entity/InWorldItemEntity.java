package com.peak.florality.phys.entity;

import com.peak.florality.init.Entities;
import com.peak.florality.phys.entity.core.RigidBodyEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;

public class InWorldItemEntity extends RigidBodyEntity {

    private static final TrackedData<ItemStack> STACK =
            DataTracker.registerData(InWorldItemEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

    public InWorldItemEntity(EntityType<InWorldItemEntity> type, World world, ItemStack stack) {
        super(type, world);
        setStack(stack);
    }

    public InWorldItemEntity(EntityType<InWorldItemEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        this.dataTracker.set(STACK, ItemStack.EMPTY);
    }

    public ItemStack getStack() {
        return dataTracker.get(STACK);
    }

    public void setStack(ItemStack stack) {
        dataTracker.set(STACK, stack.copy());
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
    }

    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
    }

    @Override
    public boolean damage(ServerWorld world, net.minecraft.entity.damage.DamageSource source, float amount) {
        return false;
    }
}