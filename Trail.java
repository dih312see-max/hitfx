package dev.hitfx;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;

public enum Trail {
	OFF(null),
	FLAME(ParticleTypes.FLAME),
	SOUL(ParticleTypes.SOUL_FIRE_FLAME),
	END_ROD(ParticleTypes.END_ROD),
	HEARTS(ParticleTypes.HEART),
	NOTES(ParticleTypes.NOTE),
	SPARKS(ParticleTypes.ELECTRIC_SPARK);

	public final SimpleParticleType type;

	Trail(SimpleParticleType type) {
		this.type = type;
	}
}
