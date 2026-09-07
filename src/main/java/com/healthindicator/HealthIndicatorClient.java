package com.healthindicator;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityWorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class HealthIndicatorClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        EntityWorldRenderEvents.AFTER_ENTITIES.register(context -> {

            MinecraftClient client = MinecraftClient.getInstance();

            if (client.world == null || client.player == null) {
                return;
            }

            MatrixStack matrices = context.matrixStack();
            VertexConsumerProvider consumers = context.consumers();

            for (LivingEntity entity : client.world.getEntitiesByClass(
                    LivingEntity.class,
                    client.player.getBoundingBox().expand(32),
                    e -> e != client.player && e.isAlive()
            )) {

                renderHealth(client, matrices, consumers, entity);
            }
        });
    }

    private static void renderHealth(
            MinecraftClient client,
            MatrixStack matrices,
            VertexConsumerProvider consumers,
            LivingEntity entity
    ) {

        float health = entity.getHealth();
        float maxHealth = entity.getMaxHealth();

        if (maxHealth <= 0) {
            return;
        }

        float percentage = health / maxHealth;

        String color;

        if (percentage > 0.75f) {
            color = "§a";
        } else if (percentage > 0.50f) {
            color = "§e";
        } else if (percentage > 0.25f) {
            color = "§6";
        } else {
            color = "§c";
        }

        int hearts = Math.max(1, Math.round(health / 2.0f));

        Text text = Text.literal(
                color + "❤ " + hearts
        );

        TextRenderer renderer = client.textRenderer;

        Vec3d camera = client.gameRenderer
                .getCamera()
                .getPos();

        double x = entity.getX() - camera.x;
        double y = entity.getY() + entity.getHeight() + 0.5 - camera.y;
        double z = entity.getZ() - camera.z;

        matrices.push();

        matrices.translate(x, y, z);

        matrices.multiply(
                client.gameRenderer.getCamera().getRotation()
        );

        float scale = 0.025f;

        matrices.scale(-scale, -scale, scale);

        float width = renderer.getWidth(text);

        renderer.draw(
                text,
                -width / 2.0f,
                0,
                0xFFFFFFFF,
                true,
                matrices.peek().getPositionMatrix(),
                consumers,
                TextRenderer.TextLayerType.SEE_THROUGH,
                0,
                15728880
        );

        matrices.pop();
    }
                }
