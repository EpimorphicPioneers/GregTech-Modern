package com.gregtechceu.gtceu.client.particle;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.client.renderer.effect.LaserBeamRenderer;
import com.gregtechceu.gtceu.common.particle.LaserBeamParticleOptions;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Accessors(chain = true, fluent = true)
public class LaserBeamParticle extends Particle {
    private static final ResourceLocation BODY = GTCEu.id("textures/fx/laser/laser.png");
    private static final ResourceLocation HEAD = GTCEu.id("textures/fx/laser/laser_start.png");
    private Vector3f direction;
    @Setter
    private float beamHeight;
    @Setter
    private float headWidth;
    @Setter
    private float alpha;
    @Setter
    private float emit;
    @Setter
    private boolean doubleVertical;

    protected LaserBeamParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
    }

    public LaserBeamParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    public LaserBeamParticle setEndPos(@NotNull Vector3f endPos) {
        this.direction = endPos.sub((float) x, (float) y, (float) z);
        return this;
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        Vec3 cameraPos = renderInfo.getPosition();
        float offX = (float) (Mth.lerp(partialTicks, xo, x) - cameraPos.x());
        float offY = (float) (Mth.lerp(partialTicks, yo, y) - cameraPos.y());
        float offZ = (float) (Mth.lerp(partialTicks, zo, z) - cameraPos.z());

        float offset = -emit * (age + partialTicks);
        LaserBeamRenderer.renderRawBeam(new Matrix4f().translate(offX, offY, offZ), BODY, HEAD,
                direction, renderInfo.getLookVector(), beamHeight, headWidth, (int) (alpha * 255), 255, offset);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return new ParticleRenderType() {

            @Override
            public void begin(BufferBuilder builder, TextureManager textureManager) {
                RenderSystem.depthMask(false);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.disableCull();
            }

            @Override
            public void end(Tesselator tesselator) {
                RenderSystem.enableCull();
            }
        };
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<LaserBeamParticleOptions> {
        @Nullable
        @Override
        public Particle createParticle(
                LaserBeamParticleOptions type,
                ClientLevel level,
                double x,
                double y,
                double z,
                double xSpeed,
                double ySpeed,
                double zSpeed) {
            return new LaserBeamParticle(level, x, y, z)
                    .setEndPos(new Vector3f(type.endX, type.endY, type.endZ))
                    .beamHeight(type.beamHeight)
                    .headWidth(type.headWidth)
                    .alpha(type.alpha)
                    .emit(type.emit)
                    .doubleVertical(type.doubleVertical);
        }
    }
}
