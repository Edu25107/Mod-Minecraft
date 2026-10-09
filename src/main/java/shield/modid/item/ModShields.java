package shield.modid.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import shield.modid.EnchantShieldMod;

/**
 * Clase para registrar todos los escudos personalizados del mod.
 * Usa ShieldTemplate para crear escudos encantables en la mesa de encantamientos.
 */
public class ModShields {

    // Tags de reparación para cada escudo (se definen en data/tags/items/)
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

    // Tag general para todos los escudos del mod
    public static final TagKey<Item> SHIELDS = TagKey.create(
        Registries.ITEM,
        Identifier.fromNamespaceAndPath(EnchantShieldMod.MOD_ID, "shields")
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

        // Crear y registrar cada escudo usando ShieldTemplate (que extiende ShieldItem)
        ENCHANTABLE_SHIELD = registerShield(
            "enchantable_shield",
            ShieldTemplate.createShieldProperties(336, EnchantShieldMod.id("enchantable_shield")),
            15  // encantabilidad
        );
        
        REINFORCED_SHIELD = registerShield(
            "reinforced_shield",
            ShieldTemplate.createShieldProperties(672, EnchantShieldMod.id("reinforced_shield")),
            10
        );
        
        MYSTICAL_SHIELD = registerShield(
            "mystical_shield",
            ShieldTemplate.createShieldProperties(252, EnchantShieldMod.id("mystical_shield")),
            25
        );
        
        WOODEN_SHIELD = registerShield(
            "wooden_shield",
            ShieldTemplate.createShieldProperties(112, EnchantShieldMod.id("wooden_shield")),
            15
        );

        EnchantShieldMod.LOGGER.info("Registered: enchantable_shield");
        EnchantShieldMod.LOGGER.info("Registered: reinforced_shield");
        EnchantShieldMod.LOGGER.info("Registered: mystical_shield");
        EnchantShieldMod.LOGGER.info("Registered: wooden_shield");
    }

    /**
     * Registra un escudo con encantabilidad personalizada.
     * Usa ShieldTemplate para asegurar que el componente BLOCKS_ATTACKS se aplique correctamente.
     */
    private static ShieldItem registerShield(String name, Properties properties, int enchantability) {
        // IMPORTANTE: Usar ShieldTemplate en lugar de ShieldItem para que se use nuestra clase personalizada
        ShieldItem shield = new ShieldTemplate(properties);
        
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, EnchantShieldMod.id(name));
        Registry.register(BuiltInRegistries.ITEM, key, shield);
        
        return shield;
    }

    /**
     * Ejemplo de cómo crear stacks pre-encantados para loot tables o crafting
     */
    public static ItemStack createEnchantedShieldExample() {
        ItemStack stack = new ItemStack(ENCHANTABLE_SHIELD);
        // Los encantamientos se añaden via EnchantmentHelper en runtime
        return stack;
    }
}