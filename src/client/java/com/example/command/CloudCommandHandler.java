package com.example.command;

import com.example.CloudClientMod;
import com.example.input.CloudInputController;
import com.example.mixin.MinecraftClientAccessor;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class CloudCommandHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("cloud-command-handler");
    private static final URI ENDPOINT = URI.create("http://localhost:8000/get_batch");

    private final Queue<String> instructionQueue = new ConcurrentLinkedQueue<>();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final AtomicBoolean fetchInFlight = new AtomicBoolean(false);

    public int getQueueSize() { return instructionQueue.size(); }

    public void processNextCommand(MinecraftClient client, CloudInputController inputController) {
        String command = instructionQueue.poll();
        if (command != null) executeCommand(client, inputController, command);
    }

    public CompletableFuture<Void> fetchCommandsAsync() {
        if (!fetchInFlight.compareAndSet(false, true)) return CompletableFuture.completedFuture(null);

        String body = "{\"want\":10}";
        HttpRequest req = HttpRequest.newBuilder(ENDPOINT).POST(HttpRequest.BodyPublishers.ofString(body)).build();

        return httpClient.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(this::enqueue)
                .whenComplete((r, t) -> fetchInFlight.set(false));
    }

    private void enqueue(String json) {
        try {
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            JsonArray cmds = obj.getAsJsonArray("commands");
            for (JsonElement e : cmds) instructionQueue.offer(e.getAsString().toUpperCase());
        } catch (Exception e) { LOGGER.error("JSON Error"); }
    }

    private void executeCommand(MinecraftClient client, CloudInputController inputController, String cmd) {
        if (cmd.startsWith("LOOK_")) {
            float yaw = switch(cmd) {
                case "LOOK_NORTH" -> 180f; case "LOOK_SOUTH" -> 0f;
                case "LOOK_EAST" -> 270f; case "LOOK_WEST" -> 90f;
                default -> client.player.getYaw();
            };
            CloudClientMod.INSTANCE.setTargetLook(yaw, 0);
            return;
        }

        switch (cmd) {
            case "FORWARD" -> inputController.setForward(client, true);
            case "STOP" -> inputController.releaseMovementKeys(client);
            case "JUMP" -> inputController.setJump(client, true);
            case "ATTACK" -> { if (client instanceof MinecraftClientAccessor a) a.invokeDoAttack(); }
        }
    }
}