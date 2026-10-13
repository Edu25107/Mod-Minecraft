package shield.modid.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.resources.Identifier;
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
	}
}
