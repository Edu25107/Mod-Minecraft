package shield.modid.item;

import java.util.List;
import java.util.Optional;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

/**
 * Template para crear escudos personalizados encantables en la mesa de encantamientos.
 * Compatible con Minecraft 1.21.4+ (26.3).
 */
public class ShieldTemplate extends ShieldItem {

    public ShieldTemplate(Properties properties) {
        super(properties);
    }

    /**
     * Crea las propiedades base de un escudo, replicando el comportamiento del escudo
     * vanilla (bloqueo, sonidos, reparación) pero con durabilidad, encantabilidad y
     * material de reparación personalizados.
     *
     * @param maxDamage      durabilidad del escudo
     * @param id             id del item (namespace:path)
     * @param enchantability encantabilidad (valor usado por la mesa de encantamientos)
     * @param repairItem     tag de items que reparan el escudo
     */
    public static Properties createShieldProperties(int maxDamage, Identifier id, int enchantability, TagKey<Item> repairItem) {
        return createShieldProperties(maxDamage, id, enchantability, repairItem, 90.0F, null);
    }

    /**
     * Crea las propiedades de un escudo con ángulo de bloqueo personalizado.
     *
     * @param maxDamage            durabilidad del escudo
     * @param id                   id del item (namespace:path)
     * @param enchantability       encantabilidad (valor usado por la mesa de encantamientos)
     * @param repairItem           tag de items que reparan el escudo
     * @param horizontalBlockingAngle ángulo de bloqueo horizontal en grados (90 = frontal 180°, 180 = 360° completo)
     */
    public static Properties createShieldProperties(int maxDamage, Identifier id, int enchantability, TagKey<Item> repairItem, float horizontalBlockingAngle) {
        return createShieldProperties(maxDamage, id, enchantability, repairItem, horizontalBlockingAngle, null);
    }

    /**
     * Crea las propiedades de un escudo con ángulo de bloqueo y sonido de bloqueo personalizado.
     *
     * @param maxDamage            durabilidad del escudo
     * @param id                   id del item (namespace:path)
     * @param enchantability       encantabilidad (valor usado por la mesa de encantamientos)
     * @param repairItem           tag de items que reparan el escudo
     * @param horizontalBlockingAngle ángulo de bloqueo horizontal en grados (90 = frontal 180°, 180 = 360° completo)
     * @param customBlockSound     sonido personalizado al bloquear (null para usar vanilla SHIELD_BLOCK)
     */
    public static Properties createShieldProperties(int maxDamage, Identifier id, int enchantability, TagKey<Item> repairItem, float horizontalBlockingAngle, Holder<SoundEvent> customBlockSound) {
        Holder<SoundEvent> blockSound = customBlockSound != null ? customBlockSound : SoundEvents.SHIELD_BLOCK;
        
        return new Properties()
                .durability(maxDamage)
                .stacksTo(1)
                .setId(ResourceKey.create(Registries.ITEM, id))
                .enchantable(enchantability)
                // Componentes vanilla del escudo
                .component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)
                .repairable(repairItem)
                .equippableUnswappable(EquipmentSlot.OFFHAND)
                .delayedComponent(
                        DataComponents.BLOCKS_ATTACKS,
                        context -> new BlocksAttacks(
                                0.25F,                          // blockDelaySeconds (5 ticks)
                                1.0F,                           // disableCooldownScale
                                List.of(new BlocksAttacks.DamageReduction(
                                        horizontalBlockingAngle, // ángulo de bloqueo horizontal (grados)
                                        Optional.empty(),       // aplica a todos los tipos de daño
                                        0.0F,                   // reducción base
                                        1.0F                    // factor (bloquea todo el daño restante)
                                )),
                                new BlocksAttacks.ItemDamageFunction(3.0F, 1.0F, 1.0F),
                                Optional.of(context.getOrThrow(DamageTypeTags.BYPASSES_SHIELD)),
                                Optional.of(blockSound),
                                Optional.of(SoundEvents.SHIELD_BREAK)
                        )
                )
                .component(DataComponents.BREAK_SOUND, SoundEvents.SHIELD_BREAK);
    }
}
