package com.gregtechceu.gtceu.common.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.common.particle.HazardParticleOptions;

import com.gregtechceu.gtceu.common.particle.LaserBeamParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import com.mojang.serialization.Codec;

public class GTParticleTypes {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister
            .create(Registries.PARTICLE_TYPE, GTCEu.MOD_ID);

    public static final RegistryObject<ParticleType<HazardParticleOptions>> HAZARD_PARTICLE = PARTICLE_TYPES
            .register("hazard", () -> new ParticleType<>(false, HazardParticleOptions.DESERIALIZER) {

                @Override
                public Codec<HazardParticleOptions> codec() {
                    return HazardParticleOptions.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<LaserBeamParticleOptions>> LASER_BEAM_PARTICLE = PARTICLE_TYPES
            .register("laser_beam", () -> new ParticleType<>(false, LaserBeamParticleOptions.DESERIALIZER) {

                @Override
                public Codec<LaserBeamParticleOptions> codec() {
                    return LaserBeamParticleOptions.CODEC;
                }
            });

    public static void init(IEventBus modBus) {
        PARTICLE_TYPES.register(modBus);
    }
}
