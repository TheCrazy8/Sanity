# Sanity

A Minecraft 1.21.1 mod that adds a sanity system affected by light level, hunger, thirst, and temperature - designed for liminal spaces modpacks.

## Features

### Core Sanity System
- **Sanity Bar**: Visual HUD overlay showing current sanity level (0-100%)
- **Persistent Data**: Player sanity is tracked and saved between sessions
- **Dynamic Updates**: Sanity changes based on multiple environmental factors

### Sanity Factors
1. **Light Level**: 
   - Darkness (< 7 light level): Decreases sanity
   - Bright light (>= 12 light level): Slowly recovers sanity
   
2. **Hunger**: 
   - Low hunger (≤ 6 food level): Decreases sanity faster
   
3. **Thirst Integration** (ThirstWasTaken mod):
   - Ready for integration when ThirstWasTaken mod is present
   - Will affect sanity when thirst is low
   
4. **Temperature Integration** (Cold Sweat mod):
   - Ready for integration when Cold Sweat mod is present
   - Extreme temperatures will affect sanity

### Hallucinations & Visual Effects (Liminal Spaces Theme)

#### Visual Impairments (Sanity < 80%)
- **Vignette Effect**: Screen edges darken progressively
- **Screen Distortion**: Wavy lines and artifacts appear
- **Color Shift**: Desaturated/reddish color overlay
- **Random Flashes**: Quick white/red screen flashes

#### Hallucinations (Sanity < 30%)
- **Visual Hallucinations**:
  - Shadowy shapes appearing in corners
  - Unsettling text messages ("You are not alone", "Behind you", etc.)
  - Screen shake indicators
  
- **Entity Hallucinations** (Sanity < 30%):
  - Temporary phantom entities that appear and fade
  - Completely harmless - cannot attack or damage player
  - Include zombies, skeletons, endermen, creepers, phantoms
  - Entities are ethereal (no AI, silent, invulnerable, no gravity)
  - Auto-despawn after a few seconds
  
- **Sound Hallucinations** (Sanity < 50%):
  - Random eerie sounds from around the player
  - Cave ambience, enderman sounds, ghast cries, warden ambience, etc.

### Sanity Effects
Based on sanity level, players experience increasing debuffs:
- **20% or below**: Nausea II, Darkness, Weakness
- **20-40%**: Nausea I, Darkness
- **40-60%**: Darkness
- **60-80%**: Minor visual effects only

## Building

### Requirements
- Java 21
- Gradle 8.8+
- Internet access to NeoForge Maven repository

### Build Commands
```bash
# Build the mod
./gradlew build

# The mod jar will be in build/libs/
```

## GitHub Actions
The repository includes a GitHub Actions workflow (`.github/workflows/build.yml`) that automatically:
- Builds the mod on push to main/master/develop branches
- Uploads the mod jar as an artifact
- Attaches the jar to GitHub releases when a release is created

## Installation
1. Install Minecraft 1.21.1
2. Install NeoForge 21.1.121 or later
3. Place the mod jar in the `mods` folder
4. (Optional) Install ThirstWasTaken and/or Cold Sweat mods for additional sanity factors

## Configuration
Currently, sanity parameters are hardcoded in the source. Future versions may include a configuration file.

## Technical Details

### Mod Structure
- **Main Class**: `com.thecrazy8.sanity.Sanity`
- **Sanity Data**: Stored as player attachment using NeoForge's attachment system
- **Event Handlers**: Server-side logic for sanity updates and hallucinations
- **Client Overlays**: Visual effects and HUD rendering

### Key Classes
- `SanityData`: Stores player sanity value and last update timestamp
- `SanityAttachments`: Registers the sanity data attachment type
- `SanityEventHandler`: Handles sanity calculations and effects
- `SanityHallucinationHandler`: Manages entity and sound hallucinations
- `HallucinationCleanupHandler`: Removes expired hallucination entities
- `HallucinationDamagePreventionHandler`: Ensures hallucinations cannot harm players
- `SanityHudOverlay`: Renders the sanity bar on screen
- `SanityVisualEffects`: Renders visual impairments and hallucination overlays

## License
MIT

## Credits
Created for liminal spaces modpacks by TheCrazy8

