package com.example.input;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CloudInputController {
    private final Random random = new Random();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private void setKeyHumanized(KeyBinding key, boolean pressed) {
        // Humans have 10-40ms of hardware/nerve latency variation
        int delay = random.nextInt(30) + 10;
        scheduler.schedule(() -> {
            key.setPressed(pressed);
        }, delay, TimeUnit.MILLISECONDS);
    }

    public void setForward(MinecraftClient client, boolean pressed) { setKeyHumanized(client.options.forwardKey, pressed); }
    public void setBack(MinecraftClient client, boolean pressed) { setKeyHumanized(client.options.backKey, pressed); }
    public void setLeft(MinecraftClient client, boolean pressed) { setKeyHumanized(client.options.leftKey, pressed); }
    public void setRight(MinecraftClient client, boolean pressed) { setKeyHumanized(client.options.rightKey, pressed); }
    public void setJump(MinecraftClient client, boolean pressed) { setKeyHumanized(client.options.jumpKey, pressed); }
    public void setSneak(MinecraftClient client, boolean pressed) { setKeyHumanized(client.options.sneakKey, pressed); }
    public void setSprint(MinecraftClient client, boolean pressed) { setKeyHumanized(client.options.sprintKey, pressed); }

    public void releaseMovementKeys(MinecraftClient client) {
        // Staggered release prevents the "All Keys Up" packet flag on 2b2t
        scheduler.schedule(() -> client.options.forwardKey.setPressed(false), random.nextInt(20), TimeUnit.MILLISECONDS);
        scheduler.schedule(() -> client.options.backKey.setPressed(false), random.nextInt(25), TimeUnit.MILLISECONDS);
        client.options.leftKey.setPressed(false);
        client.options.rightKey.setPressed(false);
        client.options.jumpKey.setPressed(false);
    }
}