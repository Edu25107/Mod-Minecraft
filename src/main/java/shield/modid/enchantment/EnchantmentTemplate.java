package shield.modid.enchantment;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import shield.modid.EnchantShieldMod;

/**
 * Template para encantamientos de escudos.
 * En 1.21.4+ los encantamientos se definen principalmente via JSON en data/enchantment/
 * Esta clase define tags y constantes para usar en los JSON.
 */
public class EnchantmentTemplate {

    // Tag para encantamientos compatibles con escudos (para supported_items en JSON)
    public static final TagKey<Enchantment> SHIELD_ENCHANTMENTS = TagKey.create(
        Registries.ENCHANTMENT,
        Identifier.fromNamespaceAndPath(EnchantShieldMod.MOD_ID, "shield_enchantments")
    );

    // Tag para items que son escudos del mod
    public static final TagKey<Item> SHIELD_ITEMS = TagKey.create(
        Registries.ITEM,
        Identifier.fromNamespaceAndPath(EnchantShieldMod.MOD_ID, "shields")
    );

    /**
     * Los encantamientos personalizados se definen en:
     * src/main/resources/data/enchantshield-mod/enchantment/*.json
     */
}