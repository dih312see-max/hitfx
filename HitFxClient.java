package dev.hitfx;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;

public final class HitFxClient implements ClientModInitializer {
	public static Config config;

	@Override
	public void onInitializeClient() {
		config = Config.load();

		KeyMapping.Category category = KeyMapping.Category.register(
				Identifier.fromNamespaceAndPath("hitfx", "main"));
		KeyMapping menuKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.hitfx.menu", InputConstants.Type.KEYSYM, InputConstants.KEY_H, category));

		// Only observes the attack. PASS leaves vanilla behaviour untouched.
		AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
			if (level.isClientSide() && !player.isSpectator() && entity instanceof LivingEntity living) {
				HitTracker.onAttack(living);
			}

			return InteractionResult.PASS;
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (menuKey.consumeClick()) {
				client.gui.setScreen(new MenuScreen());
			}

			if (!client.isPaused()) {
				HitTracker.tick(client);
				Effects.trail(client);
			}
		});

		ClientPlayConnectionEvents.DISCONNECT.register((listener, client) -> HitTracker.reset());

		HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT,
				Identifier.fromNamespaceAndPath("hitfx", "hud"), Hud::render);
	}
}
