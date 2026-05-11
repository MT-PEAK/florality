package com.peak.florality.init;

import com.peak.florality.Florality;
import com.peak.florality.phys.entity.InWorldItemEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class Entities {
    public static final EntityType<InWorldItemEntity> IN_WORLD_ITEM_ENTITY = register(
            "in_world_item",
            EntityType.Builder.<InWorldItemEntity>create(InWorldItemEntity::new, SpawnGroup.MISC)
                    .dimensions(0.75f, 1.75f)
    );

    private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
        RegistryKey<EntityType<?>> key = RegistryKey.of(Registries.ENTITY_TYPE.getKey(), Identifier.of(Florality.MODID, name));
        return Registry.register(Registries.ENTITY_TYPE, key, builder.build(key));
    }

    public static void init() {}
}
