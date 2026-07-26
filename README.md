# Linux T Prefix on Opening Chat
![GitHub License](https://img.shields.io/github/license/RecursiveG/Mc122477Fix)
[![Mojira issue MC-122477](https://img.shields.io/jira/issue/MC-122477?baseUrl=https%3A%2F%2Fbugs.mojang.com)](https://bugs.mojang.com/browse/MC-122477)

Fixes the extra `t` or `/` issue on some Linux desktop environments when opening the chat window.

This is the NeoForge port of [RecursiveG's MC-122477 Fix](https://github.com/RecursiveG/Mc122477Fix). The original project and this port are licensed under the MIT License.

## NeoForge 1.21.1 port

This branch ports the client-side fix to Minecraft 1.21.1 using NeoForge 21.1 and Java 21.
Build it with `JAVA_HOME=/path/to/java-21 ./gradlew build`; the distributable JAR is written to `build/libs`.

## Downloads
- [NeoForge port releases](https://github.com/bomxacalaka/Mc122477Fix/releases)
- [Original project on CurseForge](https://www.curseforge.com/minecraft/mc-mods/mc122477fix)
- [Original project on Modrinth](https://modrinth.com/mod/mc122477fix)

## What is this bug?
Thank you to `__null` on Mojira for their help in discovering the source of the bug. MC-122477 arises from an issue originating in GLFW (see [GLFW/glfw#1794](https://github.com/glfw/GLFW/issues/1794)). The Minecraft client polls for GLFW events twice per frame. A key press event is an event created when the player presses a key in the game. It is used to process game input like WASD. A char type event is also an event created when the player presses a key and is used to process text input. Key press events are always processed before char type events. In a normal environment, a key press and char type event would be polled at the same time. However, on some Linux desktop environments, the key press event and char type event are received on *separate* polls, allowing for the possibility of a game tick to occur between receiving the key press and char type events. Because of this possible extra game tick in between the two events, the key press event can be processed to open the chat on the game tick, and then the char type event is processed after the chat has already opened, causing an extra character to be typed. This doesn't happen on Windows, Mac OS, and the remaining Linux desktop environments because it is not possible for the chat to already be open when the char type event for opening chat (e.g. the character `t`) is processed.

## How does this mod work?

### Fabric
The Fabric version of the mod injects Mixin callbacks into `Keyboard#onKey` and `Keyboard#onChar` to listen for key press and char type events from [GLFW](https://github.com/glfw/GLFW). A Mixin is also injected into `MinecraftClient#tick` on 1.14 and `RenderSystem#flipFrame` on 1.15+ to listen for GLFW event polls. The mod then keeps track of how many polls have been processed since the game started in a poll counter. The mod stores this poll count separately when a key press event is detected for the chat open key or command key. The first char type event that is received within 5 polls after the poll of the original key press is then canceled, and the stored field is reset for the next time.  This fixes the bug because it stops the char type event from ever being able to be processed and is only executed when the chat is first opened.

### Forge / NeoForge
The Forge-family version uses built-in client events to detect the chat or creative inventory screen opening. Key and character events arriving during the first two rendered frames are canceled, preventing the delayed opening character from reaching the newly opened text field.
