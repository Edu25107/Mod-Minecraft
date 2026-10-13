package shield.modid.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Hace que Respiración acuática (Respiration) funcione sobre un escudo mientras el
 * jugador está bloqueando con él.
 *
 * <p>En 26.3 Respiración es data-driven: su JSON aplica el atributo
 * {@code minecraft:oxygen_bonus} solo cuando el objeto encantado está en una de sus
 * {@code slots} (la cabeza). Un escudo bloqueado no ocupa ninguna ranura, así que el
 * atributo no se aplica. Este mixin replica la misma fórmula de
 * {@code LivingEntity.decreaseAirSupply(int)} leyendo el nivel de Respiración del escudo
 * con el que se está bloqueando ({@link LivingEntity#getItemBlockingWith()}); si el
 * encantamiento no está en el escudo, no hace nada.
 */
@Mixin(LivingEntity.class)
public abstract class ShieldRespirationBlockingMixin {

	@Inject(method = "decreaseAirSupply", at = @At("HEAD"), cancellable = true)
	private void enchantshieldmod$respirationWhileBlocking(int currentSupply, CallbackInfoReturnable<Integer> cir) {
		LivingEntity self = (LivingEntity) (Object) this;
		ItemStack blocking = self.getItemBlockingWith();
		if (blocking == null || blocking.isEmpty()) {
			return;
		}

		Holder<Enchantment> respiration = self.registryAccess()
			.lookupOrThrow(Registries.ENCHANTMENT)
			.getOrThrow(Enchantments.RESPIRATION);
		int level = EnchantmentHelper.getItemEnchantmentLevel(respiration, blocking);
		if (level <= 0) {
			return;
		}

		// Misma fórmula que vanilla: el atributo oxygen_bonus vale 1 por nivel y la
		// probabilidad de no consumir aire es bonus / (bonus + 1).
		AttributeInstance attr = self.getAttribute(Attributes.OXYGEN_BONUS);
		double bonus = Math.max(attr != null ? attr.getValue() : 0.0, level);
		int result = bonus > 0.0 && self.getRandom().nextDouble() >= 1.0 / (bonus + 1.0)
			? currentSupply
			: currentSupply - 1;
		cir.setReturnValue(result);
	}
}
