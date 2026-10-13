package shield.modid.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShieldItem;
import shield.modid.EnchantShieldMod;

/**
 * Clase para registrar todos los escudos personalizados del mod.
 * Usa ShieldTemplate para crear escudos encantables en la mesa de encantamientos.
 */
public class ModShields {

    // Tags de reparación para cada escudo (se definen en data/enchantshield-mod/tags/item/)
    public static final TagKey<Item> ENCHANTABLE_SHIELD_REPAIR = TagKey.create(
        Registries.ITEM,
        Identifier.fromNamespaceAndPath(EnchantShieldMod.MOD_ID, "enchantable_shield_repair")
    );
    public static final TagKey<Item> REINFORCED_SHIELD_REPAIR = TagKey.create(
        Registries.ITEM,
        Identifier.fromNamespaceAndPath(EnchantShieldMod.MOD_ID, "reinforced_shield_repair")
    );
    public static final TagKey<Item> MYSTICAL_SHIELD_REPAIR = TagKey.create(
        Registries.ITEM,
        Identifier.fromNamespaceAndPath(EnchantShieldMod.MOD_ID, "mystical_shield_repair")
    );
    public static final TagKey<Item> WOODEN_SHIELD_REPAIR = TagKey.create(
        Registries.ITEM,
        Identifier.fromNamespaceAndPath(EnchantShieldMod.MOD_ID, "wooden_shield_repair")
    );

    // Instancias de los escudos (se inicializan en registerShields)
    public static ShieldItem ENCHANTABLE_SHIELD;
    public static ShieldItem REINFORCED_SHIELD;
    public static ShieldItem MYSTICAL_SHIELD;
    public static ShieldItem WOODEN_SHIELD;

    /**
     * Registra todos los escudos del mod.
     * Se llama desde EnchantShieldMod.onInitialize()
     */
    public static void registerShields() {
        EnchantShieldMod.LOGGER.info("Registering custom shields...");

        ENCHANTABLE_SHIELD = registerShield("enchantable_shield", 336, 15, ENCHANTABLE_SHIELD_REPAIR);
        REINFORCED_SHIELD  = registerShield("reinforced_shield", 672, 10, REINFORCED_SHIELD_REPAIR);
        MYSTICAL_SHIELD    = registerShield("mystical_shield", 252, 25, MYSTICAL_SHIELD_REPAIR);
        WOODEN_SHIELD      = registerShield("wooden_shield", 112, 15, WOODEN_SHIELD_REPAIR);

        EnchantShieldMod.LOGGER.info("Registered 4 custom shields: enchantable, reinforced, mystical, wooden");
    }

    /**
     * Crea y registra un escudo con durabilidad, encantabilidad y material de reparación.
     */
    private static ShieldItem registerShield(String name, int maxDamage, int enchantability, TagKey<Item> repairItem) {
        Identifier id = EnchantShieldMod.id(name);
        ShieldItem shield = new ShieldTemplate(
            ShieldTemplate.createShieldProperties(maxDamage, id, enchantability, repairItem)
        );

        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        Registry.register(BuiltInRegistries.ITEM, key, shield);

        return shield;
    }
}
