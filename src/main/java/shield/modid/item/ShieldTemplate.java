package shield.modid.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.sounds.SoundEvents;
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
     * Configuración exacta como vanilla (componente directo).
     */
    public static Properties createShieldProperties(int maxDamage, Identifier id) {
        // Componente de bloqueo con 100% reducción de daño (multiplicador 0.0 = daño × 0)
        BlocksAttacks shieldComponent = new BlocksAttacks(
            0.25f,                           // blockDelaySeconds (5 ticks)
            0.15f,                           // disableCooldownScale
            java.util.List.of(
                new BlocksAttacks.DamageReduction(
                    0.0f,                    // 0.0f = 100% reducción (multiplicador de daño = 0)
                    java.util.Optional.<net.minecraft.core.HolderSet<net.minecraft.world.damagesource.DamageType>>empty(),
                    0.0f, 0.0f               // min/max reduction = 0 (daño × 0)
                )
            ),
            BlocksAttacks.ItemDamageFunction.DEFAULT,
            java.util.Optional.empty(),
            java.util.Optional.empty(),
            java.util.Optional.empty()
        );
        
        return new Properties()
                .durability(maxDamage)
                .stacksTo(1)
                .setId(ResourceKey.create(Registries.ITEM, id))
                // Componentes vanilla del escudo (orden exacto)
                .component(DataComponents.BANNER_PATTERNS, net.minecraft.world.level.block.entity.BannerPatternLayers.EMPTY)
                .repairable(ItemTags.WOODEN_TOOL_MATERIALS)
                .equippableUnswappable(EquipmentSlot.OFFHAND)
                .component(DataComponents.BLOCKS_ATTACKS, shieldComponent)
                .component(DataComponents.BREAK_SOUND, SoundEvents.SHIELD_BREAK);
    }
}