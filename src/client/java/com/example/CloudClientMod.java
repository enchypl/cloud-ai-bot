package com.example;

import com.example.command.CloudCommandHandler;
import com.example.input.CloudInputController;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Random;

public class CloudClientMod implements ClientModInitializer {
    public static CloudClientMod INSTANCE;
    private static final Logger LOGGER = LoggerFactory.getLogger("cloud-client-mod");

    private final CloudCommandHandler commandHandler = new CloudCommandHandler();
    private final CloudInputController inputController = new CloudInputController();
    private final Random random = new Random();

    private float targetYaw, targetPitch;
    private boolean isRotating = false;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        ClientTickEvents.END_CLIENT_TICK.register(this::onEndClientTick);
    }

    private void onEndClientTick(MinecraftClient client) {
        if (client.player == null || client.world == null) return;

        handleSmoothRotation(client);
        commandHandler.processNextCommand(client, inputController);

        if (commandHandler.getQueueSize() < 5) {
            commandHandler.fetchCommandsAsync().exceptionally(t -> {
                LOGGER.warn("API Error: {}", t.getMessage());
                return null;
            });
        }
    }

    private void handleSmoothRotation(MinecraftClient client) {
        if (!isRotating) return;

        float yawDiff = targetYaw - client.player.getYaw();
        float pitchDiff = targetPitch - client.player.getPitch();

        // Moves 15-25% of the distance per tick for a "smooth glide"
        float speed = 0.15f + (random.nextFloat() * 0.1f);
        float jitter = (random.nextFloat() - 0.5f) * 0.08f; // Biological tremor

        if (Math.abs(yawDiff) > 0.1 || Math.abs(pitchDiff) > 0.1) {
            client.player.setYaw(client.player.getYaw() + (yawDiff * speed) + jitter);
            client.player.setPitch(client.player.getPitch() + (pitchDiff * speed) + jitter);
        } else {
            isRotating = false;
        }
    }

    public void setTargetLook(float yaw, float pitch) {
        this.targetYaw = yaw;
        this.targetPitch = pitch;
        this.isRotating = true;
    }
}