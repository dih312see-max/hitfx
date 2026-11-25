package dev.hitfx;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;

public enum HitParticle {
	OFF(null),
	CRIT(ParticleTypes.CRIT),
	MAGIC(ParticleTypes.ENCHANTED_HIT),
	HEARTS(ParticleTypes.HEART),
	FLAME(ParticleTypes.FLAME),
	SOUL(ParticleTypes.SOUL_FIRE_FLAME),
	SPARKS(ParticleTypes.ELECTRIC_SPARK),
	SNOW(ParticleTypes.SNOWFLAKE),
	NOTES(ParticleTypes.NOTE),
	END_ROD(ParticleTypes.END_ROD);

	public final SimpleParticleType type;

	HitParticle(SimpleParticleType type) {
		this.type = type;
	}
}
