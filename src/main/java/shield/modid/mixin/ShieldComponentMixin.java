package shield.modid.mixin;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.component.BlocksAttacks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.core.Registry")
public class ShieldComponentMixin {
    
    @Inject(
        method = "register(Lnet/minecraft/core/Holder$Reference;Ljava/lang/Object;)Lnet/minecraft/core/Holder$Reference;",
        at = @At("HEAD")
    )
    private static void onRegister(Registry<?> registry, ResourceKey<?> key, Object value, CallbackInfoReturnable<?> cir) {
        // Solo procesar items de nuestro mod que sean escudos
        if (registry.key().equals(net.minecraft.core.registries.Registries.ITEM) && value instanceof net.minecraft.world.item.ShieldItem) {
            net.minecraft.resources.Identifier id = key.identifier();
            if (id.getNamespace().equals("enchantshield-mod")) {
                // El escudo ya debería tener el componente BLOCKS_ATTACKS del registro
            }
        }
    }
}