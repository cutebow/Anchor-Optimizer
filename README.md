# Anchor-Optimizer
download it on modrinth

# License
All rights reserved unless explicitly stated.
No permission is granted to use, copy, modify, or distribute this project or its source code without prior written consent from the author.

# Code Explained 

# ClientSideAnchors.java

It boots the mod on the client, loads the config, and runs the mod’s ticking logic every tick.
It doesn’t touch gameplay, it just makes sure the prediction manager and intro message logic get time to run.

# AnchorPredictionManager.java

This is the core logic.
It watches for you right clicking a respawn anchor, and if it’s charged and you aren’t holding glowstone, it does a temporary client-only visual prediction by setting the block to air on your screen.
It also tracks the positions it predicted so it can hold that visual state briefly and then stop once the server confirms the real result.

# ClientSideAnchorsIntro.java

This just shows a one-time chat message in-game the first time the mod runs, then marks introShown in the config so it won’t spam again.
No gameplay logic, just a one-time message.

# ClientSideAnchorsConfig.java

This loads and saves the config file in your .minecraft/config folder.
It stores toggles like enabled, instantExplosion visuals, hideGlowstoneRing, swingHandOnUse, and hurtCamFromAnchor.

# ClientSideAnchorsConfigScreen.java

This builds the YACL config screen.
It’s just the UI that lets you toggle the settings in-game, nothing related to anchors themselves.

# ClientSideAnchorsModMenuIntegration.java

This connects Mod Menu to the YACL config screen so when you click the mod in Mod Menu, it opens the config UI.
mod menu integration.

# ClientPlayerInteractionManagerMixin.java

This listens for the client “interactBlock” call when you right click a block.
It grabs the clicked block position and calls the prediction manager so the mod can do its client-side visual prediction right when you click.

# ClientPlayNetworkHandlerMixin.java

This listens for server block update packets like BlockUpdateS2CPacket and ChunkDeltaUpdateS2CPacket.
Then it uses those updates to decide “ok the server confirmed what actually happened, so I can stop my temporary client-side prediction and clear my tracking.”

# ClientWorldSetBlockStateMixin.java

This is the anti-flicker/compatibility piece.
It prevents the client from temporarily showing certain unwanted block states while a prediction is active, so you don’t see the anchor pop back for a split second.
It also ignores a specific “fake anchor” block id from Hero’s Anchor Optimizer so it doesn’t visually conflict so you can have both installed while still keeping the visuals working, anchors still handled like normal.
