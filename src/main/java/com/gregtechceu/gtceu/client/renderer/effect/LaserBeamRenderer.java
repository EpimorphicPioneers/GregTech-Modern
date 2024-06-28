package com.gregtechceu.gtceu.client.renderer.effect;

import com.gregtechceu.gtceu.client.util.ClientUtil;
import com.lowdragmc.lowdraglib.utils.Vector3fHelper;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class LaserBeamRenderer {

    /**
     * Render the Laser Beam.
     *
     * @param texture         body texture id.
     * @param headTexture     head texture id. wont render the head texture if -1.
     * @param direction       direction and length vector of laser beam.
     * @param cameraDirection Vector from the eye to the origin position of the laser.
     *                        <p>
     *                        if NULL, a 3D laser rendering will be simulated by two quads (perpendicular to each other)
     *                        </p>
     *                        <p>
     *                        else render normal vertical quad.
     *                        </p>
     * @param beamHeight      beam width.
     * @param headWidth       head width.
     * @param alpha           alpha.
     * @param offset          offset of the UV texture.
     */
    public static void renderRawBeam(Matrix4f mat, @NotNull ResourceLocation texture, @Nullable ResourceLocation headTexture, Vector3f direction, Vector3f cameraDirection,
                                     float beamHeight, float headWidth, int alpha, int light, float offset) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        buffer.defaultColor(255, 255, 255, alpha);
        ClientUtil.bindTexture(texture);

        // TODO trick here. should be more strict in the future.
        if (direction.x == direction.z && direction.x == 0) {
            direction.add(0.001F, 0F, 0.001F);
        }
        float distance = direction.length();
        float start = Math.min(headWidth, distance * headWidth);
        distance -= start;

        // linear algebra drives me crazy :(
        var quaternion = new Quaternionf().rotationTo(new Vector3f(1, 0, 0), direction);
        mat.rotate(quaternion);

        if (cameraDirection != null) {
            Vector3f v1 = cameraDirection.sub(Vector3fHelper.project(new Vector3f(cameraDirection), direction));
            Vector3f v2 = new Vector3f(0, 0, 1).rotate(quaternion);
            mat.rotate(new Quaternionf().rotationTo(v2, v1));

            buffer.vertex(mat, distance, -beamHeight, 0).uv(offset + distance, 0).uv2(light)
                    .endVertex();
            buffer.vertex(mat, start, -beamHeight, 0).uv(offset, 0).uv2(light).endVertex();
            buffer.vertex(mat, start, beamHeight, 0).uv(offset, 1).uv2(light).endVertex();
            buffer.vertex(mat, distance, beamHeight, 0).uv(offset + distance, 1).uv2(light)
                    .endVertex();
            tesselator.end();
            if (headTexture != null) { // head
                buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
                ClientUtil.bindTexture(headTexture);

                buffer.vertex(mat, start, -beamHeight, 0).uv(1, 0).uv2(light).endVertex();
                buffer.vertex(mat, 0, -beamHeight, 0).uv(0, 0).uv2(light).endVertex();
                buffer.vertex(mat, 0, beamHeight, 0).uv(0, 1).uv2(light).endVertex();
                buffer.vertex(mat, start, beamHeight, 0).uv(1, 1).uv2(light).endVertex();

                buffer.vertex(mat, distance + start, -beamHeight, 0).uv(0, 0).uv2(light)
                        .endVertex();
                buffer.vertex(mat, distance, -beamHeight, 0).uv(1, 0).uv2(light).endVertex();
                buffer.vertex(mat, distance, beamHeight, 0).uv(1, 1).uv2(light).endVertex();
                buffer.vertex(mat, distance + start, beamHeight, 0).uv(0, 1).uv2(light).endVertex();

                tesselator.end();
            }
        } else {
            for (int i = 0; i < 2; ++i) {
                mat.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                buffer.vertex(mat, distance, -beamHeight, 0).uv(offset + distance, 0).uv2(light)
                        .endVertex();
                buffer.vertex(mat, start, -beamHeight, 0).uv(offset, 0).uv2(light).endVertex();
                buffer.vertex(mat, start, beamHeight, 0).uv(offset, 1).uv2(light).endVertex();
                buffer.vertex(mat, distance, beamHeight, 0).uv(offset + distance, 1).uv2(light)
                        .endVertex();
            }
            tesselator.end();

            if (headTexture != null) { // head
                buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
                ClientUtil.bindTexture(headTexture);

                for (int i = 0; i < 2; ++i) {
                    mat.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                    buffer.vertex(mat, start, -beamHeight, 0).uv(1, 0).uv2(light).endVertex();
                    buffer.vertex(mat, 0, -beamHeight, 0).uv(0, 0).uv2(light).endVertex();
                    buffer.vertex(mat, 0, beamHeight, 0).uv(0, 1).uv2(light).endVertex();
                    buffer.vertex(mat, start, beamHeight, 0).uv(1, 1).uv2(light).endVertex();
                }
                for (int i = 0; i < 2; ++i) { // tail
                    mat.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                    buffer.vertex(mat, distance + start, -beamHeight, 0).uv(0, 0).uv2(light)
                            .endVertex();
                    buffer.vertex(mat, distance, -beamHeight, 0).uv(1, 0).uv2(light).endVertex();
                    buffer.vertex(mat, distance, beamHeight, 0).uv(1, 1).uv2(light).endVertex();
                    buffer.vertex(mat, distance + start, beamHeight, 0).uv(0, 1).uv2(light)
                            .endVertex();
                }

                tesselator.end();
            }
        }
        buffer.unsetDefaultColor();
    }
}
