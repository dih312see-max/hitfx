# HitFX (Fabric, Minecraft 26.2, client-side)

Press **H** in game to open the menu (rebindable in Controls). Settings save to `config/hitfx.json`.

## Features
- Hit particles (9 styles, adjustable amount) and hit sounds (5, pitch rises with combo)
- Kill effects: totem, firework, hearts, soul
- Hit marker, combo counter, red edge flash when hurt
- Walking trail
- HUD: FPS, coordinates, facing, session hits/kills/best combo, keystrokes with CPS, armor durability

Effects are only visible and audible to you. Vanilla hit particles still appear.
Kill effects need the game to show the target dying; some servers remove entities instantly.
"Kills" counts targets that die within 3 seconds of your last hit, even if something else finished them.

## Build (needs JDK 25 and internet)
    ./gradlew build          (Windows: gradlew.bat build)
The mod is `build/libs/hitfx-1.0.0.jar`.

No JDK? Upload this folder to a GitHub repo (keep the .github folder), open the Actions tab,
run "build", and download the `hitfx-jar` artifact (unzip it to get the jar).

## Install
Install Fabric Loader for 26.2, then put Fabric API and the jar in your `mods` folder.

## Server rules
No automation, no changes to reach, aim, movement or what you can see through walls.
Rules differ by server, and some restrict even HUD items such as keystrokes, coordinates
or armor status. Check the rules; every HUD item can be turned off.
