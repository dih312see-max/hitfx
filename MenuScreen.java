package dev.hitfx;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public final class MenuScreen extends Screen {
	private int row;

	public MenuScreen() {
		super(Component.literal("HitFX"));
	}

	@Override
	protected void init() {
		Config c = HitFxClient.config;
		row = 0;

		option(() -> "Hit particles: " + name(c.hitParticle), () -> c.hitParticle = next(c.hitParticle));
		option(() -> "Hit sound: " + name(c.hitSound), () -> c.hitSound = next(c.hitSound));
		option(() -> "Kill effect: " + name(c.killEffect), () -> c.killEffect = next(c.killEffect));
		option(() -> "Walk trail: " + name(c.trail), () -> c.trail = next(c.trail));
		option(() -> "Particle amount: " + c.particleAmount,
				() -> c.particleAmount = c.particleAmount >= 40 ? 5 : c.particleAmount + 5);
		option(() -> "Hit marker: " + onOff(c.hitMarker), () -> c.hitMarker = !c.hitMarker);
		option(() -> "Combo counter: " + onOff(c.comboCounter), () -> c.comboCounter = !c.comboCounter);
		option(() -> "Hurt flash: " + onOff(c.hurtFlash), () -> c.hurtFlash = !c.hurtFlash);
		option(() -> "Info HUD: " + onOff(c.infoHud), () -> c.infoHud = !c.infoHud);
		option(() -> "Keystrokes: " + onOff(c.keystrokes), () -> c.keystrokes = !c.keystrokes);
		option(() -> "Armor HUD: " + onOff(c.armorHud), () -> c.armorHud = !c.armorHud);

		addRenderableWidget(Button.builder(Component.literal("Done"), button -> onClose())
				.bounds(width / 2 - 75, 40 + 6 * 22 + 4, 150, 20).build());
	}

	private void option(Supplier<String> label, Runnable change) {
		int x = width / 2 + (row % 2 == 0 ? -154 : 4);
		int y = 40 + (row / 2) * 22;
		row++;

		addRenderableWidget(Button.builder(Component.literal(label.get()), button -> {
			change.run();
			button.setMessage(Component.literal(label.get()));
		}).bounds(x, y, 150, 20).build());
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		super.extractRenderState(graphics, mouseX, mouseY, delta);
		graphics.centeredText(font, title, width / 2, 20, 0xFFFFFFFF);
	}

	@Override
	public void onClose() {
		HitFxClient.config.save();
		super.onClose();
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private static <E extends Enum<E>> E next(E value) {
		E[] values = value.getDeclaringClass().getEnumConstants();
		return values[(value.ordinal() + 1) % values.length];
	}

	private static String name(Enum<?> value) {
		return value.name().replace('_', ' ');
	}

	private static String onOff(boolean value) {
		return value ? "ON" : "OFF";
	}
}
