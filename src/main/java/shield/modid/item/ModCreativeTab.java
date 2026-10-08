package shield.modid.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import shield.modid.EnchantShieldMod;

/**
 * Registra los escudos en la pestaña creativa de Combate (COMBAT).
 * Usa Fabric CreativeModeTabEvents (fabric-creative-tab-api-v1).
 */
public class ModCreativeTab {

    public static void register() {
        // Añadir a la pestaña COMBAT (vanilla)
        ResourceKey<CreativeModeTab> combatTab = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            Identifier.fromNamespaceAndPath("minecraft", "combat")
        );

        CreativeModeTabEvents.modifyOutputEvent(combatTab).register(entries -> {
            Item enchantable = ModShields.ENCHANTABLE_SHIELD;
            Item reinforced = ModShields.REINFORCED_SHIELD;
            Item mystical = ModShields.MYSTICAL_SHIELD;
            Item wooden = ModShields.WOODEN_SHIELD;

            if (enchantable != null) entries.accept(new ItemStack(enchantable));
            if (reinforced != null) entries.accept(new ItemStack(reinforced));
            if (mystical != null) entries.accept(new ItemStack(mystical));
            if (wooden != null) entries.accept(new ItemStack(wooden));
        });
    }
}