package shield.modid.client;

import java.util.Optional;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.Registry;
import shield.modid.EnchantShieldMod;
import shield.modid.client.render.CustomShieldSpecialRenderer;

public class EnchantShieldModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Registrar el tipo de modelo especial para los escudos personalizados,
		// de modo que cada escudo use su propia textura (entity/shield/*_base.png).
		SpecialModelRenderers.ID_MAPPER.put(
			Identifier.fromNamespaceAndPath(EnchantShieldMod.MOD_ID, "custom_shield"),
			CustomShieldSpecialRenderer.Unbaked.MAP_CODEC
		);

		// Registrar el evento de sonido del escudo perfecto en el cliente
		registerPerfectShieldSound();
	}

	private void registerPerfectShieldSound() {
		Identifier soundId = Identifier.fromNamespaceAndPath(EnchantShieldMod.MOD_ID, "perfect_shield_block");
		ResourceKey<SoundEvent> soundKey = ResourceKey.create(Registries.SOUND_EVENT, soundId);
		
		// Solo registrar si no existe ya
		if (!BuiltInRegistries.SOUND_EVENT.containsKey(soundKey)) {
			SoundEvent soundEvent = new SoundEvent(soundId, Optional.empty());
			Registry.register(BuiltInRegistries.SOUND_EVENT, soundKey, soundEvent);
		}
	}
}
