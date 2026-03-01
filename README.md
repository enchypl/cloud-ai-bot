Cloud Client Mod: Humanized Anarchy Bot
This Fabric mod is designed to bypass advanced anticheats like Grim and Vulcan on anarchy servers like 2b2t. It replaces traditional "robotic" bot behavior with a Ghost Controller architecture that uses a Cloud AI "Brain" and humanized input smoothing.

### Core Features
Saccadic Rotation Engine: Bypasses rotation checks by moving the camera along Cubic Bézier curves with natural overshoot and corrective jitter.

Variable Latency Input: Simulates human nerve/hardware delay (10-40ms) and staggered key releases to break "perfect packet" patterns.

Cloud AI Batching: Offloads decision-making to a Gemini-powered Python bridge, reducing laptop resource usage and introducing human-like reaction times.

Biological Noise: Injects micro-tremors into movement and non-uniform distributions into click speeds.

### Project Structure
The mod is organized into a clean, client-side Fabric architecture:

com.example.CloudClientMod: The main entry point handling the smooth rotation tick loop.

com.example.input.CloudInputController: Manages humanized keyboard/mouse states with randomized delays.

com.example.command.CloudCommandHandler: Fetches and parses batch instructions from the local Python API.

com.example.mixin.MinecraftClientAccessor: Unlocks private Minecraft methods for automated attacking and item use.

### Installation & Setup
#### 1. Prerequisites
Java 21 & Fabric Loader (1.21.1).

Python 3.10+ (for the AI Bridge).

Gemini API Key (Free from Google AI Studio).

#### 2. Fabric Setup
Place the provided .java files into their respective packages in IntelliJ.

Ensure fabric.mod.json includes the entrypoints and mixins block.

Create src/main/resources/cloud-client-mod.mixins.json to enable the Accessor.

#### 3. Python Bridge Setup
Install dependencies: pip install fastapi uvicorn google-generativeai.

Run bridge.py before launching Minecraft to establish the connection.
