package dev.hitfx;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayDeque;
import java.util.Locale;

public final class Hud {
	private static final int WHITE = 0xFFFFFFFF;
	private static final int BACKGROUND = 0x80000000;
	private static final EquipmentSlot[] ARMOR = {
			EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
	};

	private static final ArrayDeque<Long> frames = new ArrayDeque<>();
	private static final ArrayDeque<Long> leftClicks = new ArrayDeque<>();
	private static final ArrayDeque<Long> rightClicks = new ArrayDeque<>();
	private static boolean leftWasDown;
	private static boolean rightWasDown;

	private Hud() {
	}

	public static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null) return;

		Config config = HitFxClient.config;
		long now = System.currentTimeMillis();
		frames.addLast(now);
		prune(frames, now);
		trackClicks(client, now);

		if (config.hurtFlash && HitTracker.flashTicks > 0) hurtFlash(graphics);
		if (config.hitMarker && HitTracker.markerTicks > 0) hitMarker(graphics);
		if (config.comboCounter && HitTracker.combo > 1) combo(graphics, client);
		if (config.infoHud) info(graphics, client);
		if (config.keystrokes) keystrokes(graphics, client);
		if (config.armorHud) armor(graphics, client);
	}

	private static void hurtFlash(GuiGraphicsExtractor graphics) {
		int width = graphics.guiWidth();
		int height = graphics.guiHeight();
		int alpha = HitTracker.flashTicks * 12;
		int red = (alpha << 24) | 0xFF0000;

		graphics.fillGradient(0, 0, width, height / 4, red, 0x00FF0000);
		graphics.fillGradient(0, height - height / 4, width, height, 0x00FF0000, red);
	}

	private static void hitMarker(GuiGraphicsExtractor graphics) {
		int x = graphics.guiWidth() / 2;
		int y = graphics.guiHeight() / 2;

		// Four short diagonals around the crosshair.
		for (int i = 4; i < 8; i++) {
			graphics.fill(x - i - 1, y - i - 1, x - i, y - i, WHITE);
			graphics.fill(x + i, y - i - 1, x + i + 1, y - i, WHITE);
			graphics.fill(x - i - 1, y + i, x - i, y + i + 1, WHITE);
			graphics.fill(x + i, y + i, x + i + 1, y + i + 1, WHITE);
		}
	}

	private static void combo(GuiGraphicsExtractor graphics, Minecraft client) {
		int color = HitTracker.combo >= 10 ? 0xFFFF5555 : HitTracker.combo >= 5 ? 0xFFFFAA00 : 0xFFFFFF55;
		graphics.centeredText(client.font, HitTracker.combo + "x COMBO",
				graphics.guiWidth() / 2, graphics.guiHeight() / 2 + 16, color);
	}

	private static void info(GuiGraphicsExtractor graphics, Minecraft client) {
		String[] lines = {
				"FPS: " + frames.size(),
				String.format(Locale.ROOT, "XYZ: %.1f / %.1f / %.1f",
						client.player.getX(), client.player.getY(), client.player.getZ()),
				"Facing: " + client.player.getDirection().getName(),
				"Hits: " + HitTracker.hits + "  Kills: " + HitTracker.kills + "  Best combo: " + HitTracker.bestCombo
		};

		for (int i = 0; i < lines.length; i++) {
			graphics.text(client.font, lines[i], 4, 4 + i * 10, WHITE, true);
		}
	}

	private static void keystrokes(GuiGraphicsExtractor graphics, Minecraft client) {
		int x = 4;
		int y = 50;
		key(graphics, client, client.options.keyUp, "W", x + 22, y, 20);
		key(graphics, client, client.options.keyLeft, "A", x, y + 22, 20);
		key(graphics, client, client.options.keyDown, "S", x + 22, y + 22, 20);
		key(graphics, client, client.options.keyRight, "D", x + 44, y + 22, 20);
		key(graphics, client, client.options.keyAttack, "L " + leftClicks.size(), x, y + 44, 31);
		key(graphics, client, client.options.keyUse, "R " + rightClicks.size(), x + 33, y + 44, 31);
		key(graphics, client, client.options.keyJump, "SPACE", x, y + 66, 64);
	}

	private static void key(GuiGraphicsExtractor graphics, Minecraft client, KeyMapping mapping,
			String label, int x, int y, int width) {
		boolean down = mapping.isDown();
		graphics.fill(x, y, x + width, y + 20, down ? 0xC0FFFFFF : BACKGROUND);
		graphics.centeredText(client.font, label, x + width / 2, y + 6, down ? 0xFF000000 : WHITE);
	}

	private static void armor(GuiGraphicsExtractor graphics, Minecraft client) {
		int y = graphics.guiHeight() - 60;

		for (int i = ARMOR.length - 1; i >= 0; i--) {
			ItemStack stack = client.player.getItemBySlot(ARMOR[i]);
			if (stack.isEmpty() || !stack.isDamageableItem()) continue;

			int left = stack.getMaxDamage() - stack.getDamageValue();
			float fraction = (float) left / stack.getMaxDamage();
			int color = fraction > 0.5F ? 0xFF55FF55 : fraction > 0.2F ? 0xFFFFFF55 : 0xFFFF5555;
			String label = stack.getHoverName().getString() + " " + left;

			graphics.text(client.font, label,
					graphics.guiWidth() - client.font.width(label) - 4, y, color, true);
			y -= 10;
		}
	}

	/** Sampled every frame. Display only: it never presses anything. */
	private static void trackClicks(Minecraft client, long now) {
		boolean left = client.options.keyAttack.isDown();
		boolean right = client.options.keyUse.isDown();
		if (left && !leftWasDown) leftClicks.addLast(now);
		if (right && !rightWasDown) rightClicks.addLast(now);
		leftWasDown = left;
		rightWasDown = right;
		prune(leftClicks, now);
		prune(rightClicks, now);
	}

	private static void prune(ArrayDeque<Long> times, long now) {
		while (!times.isEmpty() && now - times.peekFirst() >= 1000) {
			times.removeFirst();
		}
	}
}
