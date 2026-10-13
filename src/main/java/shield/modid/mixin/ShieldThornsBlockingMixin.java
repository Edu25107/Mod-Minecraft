package shield.modid.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Hace que Espinas (Thorns) se dispare cuando un ataque queda totalmente bloqueado
 * por un escudo encantado.
 *
 * <p>En vanilla, el bloqueo del escudo reduce el daño a 0 y {@code LivingEntity.hurtServer}
 * devuelve {@code false}; como el atacante solo invoca
 * {@link EnchantmentHelper#doPostAttackEffects(ServerLevel, net.minecraft.world.entity.Entity, DamageSource)}
 * cuando el daño "entró" ({@code hurtServer == true}), los efectos {@code post_attack}
 * (entre ellos Espinas) nunca se procesan en el escudo.
 *
 * <p>Este mixin reutiliza La misma pipeline data-driven de vanilla: si el golpe fue
 * bloqueado por completo (daño restante <= 0), se ejecutan los efectos {@code post_attack}
 * sobre el bloqueado. Así Espinas (con su JSON en {@code data/minecraft/enchantment/thorns.json})
 * hace daño al atacante y desgasta el escudo 2 puntos por activación, con su probabilidad
 * habitual (15 % por nivel).
 */
@Mixin(LivingEntity.class)
public abstract class ShieldThornsBlockingMixin {

	@Inject(
		method = "applyItemBlocking(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)F",
		at = @At("RETURN")
	)
	private void enchantshieldmod$runPostAttackOnFullBlock(
		ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Float> cir
	) {
		float damageBlocked = cir.getReturnValue();
		// Solo cuando el escudo bloqueó TODO el daño: si quedara daño restante, el flujo
		// normal de vanilla ya invocaria doPostAttackEffects y evitaríamos hacerlo dos veces.
		if (damageBlocked > 0.0F && damage <= damageBlocked) {
			EnchantmentHelper.doPostAttackEffects(level, (LivingEntity) (Object) this, source);
		}
	}
}