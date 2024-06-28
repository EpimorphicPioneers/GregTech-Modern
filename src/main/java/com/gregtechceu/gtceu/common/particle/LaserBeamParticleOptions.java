package com.gregtechceu.gtceu.common.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import org.joml.Vector3f;

import java.util.Locale;

import static com.gregtechceu.gtceu.common.data.GTParticleTypes.LASER_BEAM_PARTICLE;

@AllArgsConstructor
@Accessors(chain = true, fluent = true)
public class LaserBeamParticleOptions implements ParticleOptions {

    public static final Codec<LaserBeamParticleOptions> CODEC = RecordCodecBuilder.create((i) -> i.group(
                    Codec.FLOAT.fieldOf("endX").forGetter(p -> p.endX),
                    Codec.FLOAT.fieldOf("endY").forGetter(p -> p.endY),
                    Codec.FLOAT.fieldOf("endZ").forGetter(p -> p.endZ),
                    Codec.FLOAT.fieldOf("beamHeight").forGetter(p -> p.beamHeight),
                    Codec.FLOAT.fieldOf("headWidth").forGetter(p -> p.headWidth),
                    Codec.FLOAT.fieldOf("alpha").forGetter(p -> p.alpha),
                    Codec.FLOAT.fieldOf("emit").forGetter(p -> p.emit),
                    Codec.BOOL.fieldOf("doubleVertical").forGetter(p -> p.doubleVertical))
            .apply(i, LaserBeamParticleOptions::new));

    public final float endX;
    public final float endY;
    public final float endZ;

    @Setter
    public float beamHeight = 0.075f;
    @Setter
    public float headWidth;
    @Setter
    public float alpha = 1;
    @Setter
    public float emit;
    @Setter
    public boolean doubleVertical;

    public LaserBeamParticleOptions(float endX, float endY, float endZ) {
        this.endX = endX;
        this.endY = endY;
        this.endZ = endZ;
    }

    public LaserBeamParticleOptions(Vector3f endPos) {
        this.endX = endPos.x;
        this.endY = endPos.y;
        this.endZ = endPos.z;
    }

    @Override
    public ParticleType<?> getType() {
        return LASER_BEAM_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeFloat(endX);
        buffer.writeFloat(endY);
        buffer.writeFloat(endZ);

        buffer.writeFloat(beamHeight);
        buffer.writeFloat(headWidth);
        buffer.writeFloat(alpha);
        buffer.writeFloat(emit);
        buffer.writeBoolean(doubleVertical);
    }

    @Override
    public String writeToString() {
        return String.format(
                Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %.2f %.2f %s", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), endX, endY, endZ, beamHeight, headWidth, alpha, emit, doubleVertical);
    }

    public static final Deserializer<LaserBeamParticleOptions> DESERIALIZER = new Deserializer<>() {
        @Override
        public LaserBeamParticleOptions fromCommand(
                ParticleType<LaserBeamParticleOptions> particleTypeIn, StringReader reader)
                throws CommandSyntaxException {
            reader.expect(' ');
            float endX = reader.readFloat();
            reader.expect(' ');
            float endY = reader.readFloat();
            reader.expect(' ');
            float endZ = reader.readFloat();
            reader.expect(' ');
            float beamHeight = reader.readFloat();
            reader.expect(' ');
            float headWidth = reader.readFloat();
            reader.expect(' ');
            float alpha = reader.readFloat();
            reader.expect(' ');
            float emit = reader.readFloat();
            reader.expect(' ');
            boolean doubleVertical = reader.readBoolean();
            return new LaserBeamParticleOptions(endX, endY, endZ)
                    .beamHeight(beamHeight)
                    .headWidth(headWidth)
                    .alpha(alpha)
                    .emit(emit)
                    .doubleVertical(doubleVertical);
        }

        @Override
        public LaserBeamParticleOptions fromNetwork(
                ParticleType<LaserBeamParticleOptions> particleTypeIn, FriendlyByteBuf buffer) {
            float endX = buffer.readFloat();
            float endY = buffer.readFloat();
            float endZ = buffer.readFloat();
            float beamHeight = buffer.readFloat();
            float headWidth = buffer.readFloat();
            float alpha = buffer.readFloat();
            float emit = buffer.readFloat();
            boolean doubleVertical = buffer.readBoolean();
            return new LaserBeamParticleOptions(endX, endY, endZ)
                    .beamHeight(beamHeight)
                    .headWidth(headWidth)
                    .alpha(alpha)
                    .emit(emit)
                    .doubleVertical(doubleVertical);
        }
    };
}
