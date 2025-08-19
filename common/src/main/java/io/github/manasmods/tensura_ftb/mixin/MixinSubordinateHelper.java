package io.github.manasmods.tensura_ftb.mixin;

import dev.architectury.event.EventResult;
import dev.ftb.mods.ftbchunks.data.ClaimedChunkManagerImpl;
import io.github.manasmods.tensura.util.SubordinateHelper;
import io.github.manasmods.tensura_ftb.FtbHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SubordinateHelper.class)
public class MixinSubordinateHelper {
    @Inject(method = "isAlly", at = @At(value = "RETURN"), remap = false, cancellable = true)
    private static void isAlly(LivingEntity entity, LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;
        if (!FtbHandler.CONFIG.ftbAllyTensura) return;
        if (!(entity instanceof ServerPlayer owner) || !(target instanceof ServerPlayer player)) return;
        if (ClaimedChunkManagerImpl.getInstance().getOrCreateData(owner).isAlly(player.getUUID())) cir.setReturnValue(true);
        else if (ClaimedChunkManagerImpl.getInstance().getOrCreateData(player).isAlly(owner.getUUID())) cir.setReturnValue(true);
    }
}
