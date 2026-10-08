package shield.modid.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.component.BlocksAttacks;
import shield.modid.EnchantShieldMod;

/**
 * Template para crear escudos personalizados encantables en la mesa de encantamientos.
 * Compatible con Minecraft 1.21.4+ (26.3).
 */
public class ShieldTemplate extends ShieldItem {

    /**
     * Crea un nuevo escudo template.
     * 
     * @param properties Propiedades del item CON ID ya establecido
     */
    public ShieldTemplate(Properties properties) {
        super(properties);
    }

    /**
     * Crea las propiedades base para un escudo con ID y componente de bloqueo.
     * En 1.21.4+ se requiere el componente minecraft:blocks_attacks para que funcione el bloqueo.
     */
    public static Properties createShieldProperties(int maxDamage, Identifier id) {
        // Componente de bloqueo estilo vanilla (usar sonidos por defecto)
        BlocksAttacks shieldComponent = new BlocksAttacks(
            0.25f,                    // blockDelaySeconds (5 ticks)
            0.15f,                    // disableCooldownScale
            java.util.List.of(),      // damageReductions (vacío = usa defaults)
            BlocksAttacks.ItemDamageFunction.DEFAULT, // itemDamage
            java.util.Optional.empty(), // bypassedBy
            java.util.Optional.empty(), // blockSound (default)
            java.util.Optional.empty()  // disableSound (default)
        );
        
        return new Properties()
                .durability(maxDamage)
                .stacksTo(1)
                .setId(ResourceKey.create(Registries.ITEM, id))
                .component(DataComponents.BLOCKS_ATTACKS, shieldComponent);
    }
}