package io.github.manasmods.tensura_ftb.mixin;

import dev.architectury.event.EventResult;
import dev.ftb.mods.ftbquests.FTBQuestsEventHandler;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.task.KillTask;
import io.github.manasmods.tensura.util.SubordinateHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(FTBQuestsEventHandler.class)
public abstract class MixinFTBQuestsEventHandler {
    @Shadow private List<KillTask> killTasks;
    @Inject(method = "playerKill", at = @At(value = "RETURN"), remap = false)
    private void playerKill(LivingEntity entity, DamageSource source, CallbackInfoReturnable<EventResult> cir) {
        if (source.getEntity() instanceof Mob mob) {
            LivingEntity subOwner = SubordinateHelper.getSubordinateOwner(mob);
            if (subOwner instanceof ServerPlayer player) {

                if (killTasks == null) killTasks = ServerQuestFile.INSTANCE.collect(KillTask.class);
                if (killTasks.isEmpty()) return;
                ServerQuestFile.INSTANCE.getTeamData(player).ifPresent(data -> {
                    for (KillTask task : killTasks) {
                        if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.getQuest())) task.kill(data, entity);
                    }
                });
            }
        }
    }
}
