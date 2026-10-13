package shield.modid.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shield.modid.EnchantShieldMod;
import shield.modid.item.ModShields;

/**
 * Mixin para reproducir música mientras se bloquea con el escudo perfecto.
 * La música suena mientras se mantiene el click derecho (acción de uso) y para al soltarlo.
 * Nota: El sonido se reproduce una vez (dura ~3 min). Si el jugador bloquea más tiempo,
 * la música se detendrá hasta que suelte y vuelva a bloquear.
 */
@Mixin(LocalPlayer.class)
public abstract class PerfectShieldMusicMixin {

    private static boolean wasBlocking = false;

    @Inject(method = "tick", at = @At("TAIL"))
    private void enchantshieldmod$tickPerfectShieldMusic(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        ItemStack offhandItem = player.getOffhandItem();
        
        // Verificar si el item en la mano secundaria es el escudo perfecto
        boolean isPerfectShield = offhandItem.getItem() instanceof ShieldItem && 
                                   offhandItem.getItem() == ModShields.PERFECT_WOODEN_SHIELD;
        
        // Verificar si el jugador está bloqueando (usando el item)
        boolean isBlocking = player.isUsingItem() && player.getUseItem() == offhandItem && isPerfectShield;

        if (isBlocking && !wasBlocking) {
            // Empezó a bloquear - iniciar música
            startPerfectShieldMusic(player);
        } else if (!isBlocking && wasBlocking) {
            // Dejó de bloquear - parar música
            stopPerfectShieldMusic(player);
        }

        wasBlocking = isBlocking;
    }

    private void startPerfectShieldMusic(LocalPlayer player) {
        // Obtener el sonido personalizado
        var soundEvent = ModShields.PERFECT_SHIELD_BLOCK_SOUND.value();
        if (soundEvent != null) {
            // Reproducir sonido en la posición del jugador (client-side)
            // Entity.playSound(SoundEvent, float volume, float pitch)
            player.playSound(soundEvent, 1.0F, 1.0F);
        }
    }

    private void stopPerfectShieldMusic(LocalPlayer player) {
        // Parar todos los sonidos usando el SoundManager
        Minecraft minecraft = Minecraft.getInstance();
        SoundManager soundManager = minecraft.getSoundManager();
        soundManager.stop();
    }
}