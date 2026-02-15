# Anchor-Optimizer
download it on modrinth

# License
All rights reserved unless explicitly stated.
No permission is granted to use, copy, modify, or distribute this project or its source code without prior written consent from the author.

# Code Explained


# ClientSideAnchors.java

This is basically the startup one. When you load into the game it loads your config, then it runs the prediction system every tick so it can expire old predictions and keep everything from getting stuck.

You’ll mostly see:
ClientSideAnchorsConfig.load();
ClientTickEvents.END_CLIENT_TICK.register(client -> AnchorPredictionManager.tick());

it’s just keeping the mod alive and updating.

---

# AnchorPredictionManager.java

This is the main part.

It watches for your right-click on a anchor, and if it’s the kind of click that would explode it, it does a quick client-side “pretend it already exploded” so you don’t have to wait for the server to catch up.

First it checks a bunch of stuff so it only triggers in the right scenario:
if (!cfg.enabled) return;
if (!cfg.instantExplosion) return;
if (mc.player.isSneaking()) return;
if (world.getRegistryKey().equals(World.NETHER)) return;

So it won’t run if the mod is off, if instant mode is off, if you’re crouching, or if you’re in the Nether (because anchors don’t explode there).

Then it makes sure you’re actually clicking a charged anchor and not filling it:
if (!(state.getBlock() instanceof RespawnAnchorBlock)) return;
if (charges <= 0) return;
if (stack.isOf(Items.GLOWSTONE)) return;

That glowstone check is important because glowstone is “charge it”, not “detonate it”.

Then the actual instant visual part is literally swapping it to air on your client
BlockState predicted = Blocks.AIR.getDefaultState();
world.setBlockState(key, predicted, 0);

That’s the whole thing. its not touching the server. its just making your screen show what you expect to happen.

The reason it doesn’t flicker back is because the server sometimes sends a temporary “still there” update before it sends the final result.

And it has a hard timeout too (40 ticks) so even if something gets weird or broken, itll stop predicting and go back to what the server last told it.

---

# BlockStateCollisionMixin.java

This part is why you don’t fall through the spot even though it’s air.

It checks: “is this block air right now, and is it one of our predicted positions?” If yes, it borrows the old anchor’s collision shape.

So visually it’s gone, but your movement still treats it like there’s something solid there for that short prediction window, this is good for not giving an advtantage.

---

# ClientPlayerInteractionManagerMixin.java

This is how it tells you clicked.

It hooks into the client’s interactBlock call and immediately runs:
AnchorPredictionManager.handleInteract(world, hand, hitResult);

So the prediction happens right as you click, not later like normally.

---

# ClientPlayNetworkHandlerMixin.java

this one listens to the server.

When the server sends block update packets, this mixin forwards them into:
AnchorPredictionManager.onServerBlockUpdate(world, pos, state);

So when the server finally confirms that its gone (AIR), the prediction ends normally.

---

# ClientWorldSetBlockStateMixin.java

This is the anti-flash protection thingy.

While a position is being predicted, if something tries to set it back to a real block, it gets ignored. But if its air, that’s allowed (because air is the server confirming the explosion).

So it’s basically “don’t let the anchor come back into view while we’re waiting for the server.”

---

# Why it’s not a cheat

Because the server is still the boss ceo type thingy. This mod can’t force an explosion, cant add damage, can’t change knockback, cant make placements actually happen faster server-side.

All it does is change what YOUR client shows for a second while the server packets are catching up.

