package io.github.manasmods.tensura_ftb;

import dev.architectury.event.EventResult;
import dev.ftb.mods.ftbchunks.FTBCUtils;
import dev.ftb.mods.ftbchunks.FTBChunksExpected;
import dev.ftb.mods.ftbchunks.FTBChunksWorldConfig;
import dev.ftb.mods.ftbchunks.PlayerNotifier;
import dev.ftb.mods.ftbchunks.api.ClaimedChunk;
import dev.ftb.mods.ftbchunks.data.ClaimedChunkManagerImpl;
import dev.ftb.mods.ftbchunks.data.PvPMode;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.EntityEvents;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import io.github.manasmods.tensura_ftb.registry.FtbConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class FtbHandler {
    public static FtbConfig CONFIG = ConfigRegistry.getConfig(FtbConfig.class);

    public static void init() {
        EntityEvents.LIVING_EFFECT_ADDED.register((entity, source, changeableInstance) -> {
            MobEffectInstance instance = changeableInstance.get();
            if (instance == null) return EventResult.pass();
            if (instance.getEffect().value().isBeneficial()) return EventResult.pass();
            if (CONFIG.harmfulEffect) return EventResult.pass();

            if (!entity.level().isClientSide() && source instanceof LivingEntity attacker) {
                PvPMode mode = FTBChunksWorldConfig.PVP_MODE.get();
                if (mode == PvPMode.ALWAYS) return EventResult.pass();
                if (isPvPProtectedChunk(mode, entity) || isPvPProtectedChunk(mode, attacker)) {
                    if (attacker instanceof Player player)
                        PlayerNotifier.notifyWithCooldown(player, Component.translatable("ftbchunks.message.no_pvp").withStyle(ChatFormatting.GOLD), 3000L);
                    return EventResult.interruptFalse();
                }
            }
            return EventResult.pass();
        });

        TensuraEntityEvents.ENERGY_DRAIN_EVENT.register((target, drainer, drainType, gainType, amount, percentage) -> {
            if (CONFIG.energyDrain) return EventResult.pass();
            if (!target.level().isClientSide() && drainer instanceof LivingEntity attacker) {
                PvPMode mode = FTBChunksWorldConfig.PVP_MODE.get();
                if (mode == PvPMode.ALWAYS) return EventResult.pass();
                if (isPvPProtectedChunk(mode, target) || isPvPProtectedChunk(mode, attacker)) {
                    if (attacker instanceof Player player)
                        PlayerNotifier.notifyWithCooldown(player, Component.translatable("ftbchunks.message.no_pvp").withStyle(ChatFormatting.GOLD), 3000L);
                    return EventResult.interruptFalse();
                }
            }
            return EventResult.pass();
        });

        TensuraEntityEvents.POSSESSION_EVENT.register((target, possessor) -> {
            if (CONFIG.possession) return EventResult.pass();
            if (!target.level().isClientSide() && possessor instanceof LivingEntity attacker) {
                PvPMode mode = FTBChunksWorldConfig.PVP_MODE.get();
                if (mode == PvPMode.ALWAYS) return EventResult.pass();
                if (isPvPProtectedChunk(mode, target) || isPvPProtectedChunk(mode, attacker)) {
                    if (attacker instanceof Player player)
                        PlayerNotifier.notifyWithCooldown(player, Component.translatable("ftbchunks.message.no_pvp").withStyle(ChatFormatting.GOLD), 3000L);
                    return EventResult.interruptFalse();
                }
            }
            return EventResult.pass();
        });

        TensuraEntityEvents.SPIRITUAL_HURT_EVENT.register((target, attacker, originalAmount, resistPercentage, amount, source) -> {
            if (CONFIG.spiritualDamage) return EventResult.pass();
            if (!target.level().isClientSide() && attacker instanceof LivingEntity livingAttacker) {
                PvPMode mode = FTBChunksWorldConfig.PVP_MODE.get();
                if (mode == PvPMode.ALWAYS) return EventResult.pass();
                if (isPvPProtectedChunk(mode, target) || isPvPProtectedChunk(mode, livingAttacker)) {
                    if (livingAttacker instanceof Player player)
                        PlayerNotifier.notifyWithCooldown(player, Component.translatable("ftbchunks.message.no_pvp").withStyle(ChatFormatting.GOLD), 3000L);
                    return EventResult.interruptFalse();
                }
            }
            return EventResult.pass();
        });

        TensuraEntityEvents.INSTANT_TRANSMISSION_EVENT.register((target, teleporter, position, type) -> {
            if (!type.equals(TensuraEntityEvents.TransmissionType.ABILITY) || target == teleporter) return EventResult.pass();
            if (CONFIG.forcedTeleportation) return EventResult.pass();

            if (!target.level().isClientSide() && target instanceof LivingEntity livingTarget && teleporter instanceof LivingEntity livingOwner) {
                PvPMode mode = FTBChunksWorldConfig.PVP_MODE.get();
                if (mode == PvPMode.ALWAYS) return EventResult.pass();
                if (isPvPProtectedChunk(mode, livingTarget) || isPvPProtectedChunk(mode, livingOwner)) {
                    if (livingOwner instanceof Player player)
                        PlayerNotifier.notifyWithCooldown(player, Component.translatable("ftbchunks.message.no_pvp").withStyle(ChatFormatting.GOLD), 3000L);
                    return EventResult.interruptFalse();
                }
            }
            return EventResult.pass();
        });

        TensuraSkillEvents.SKILL_PLUNDER.register((target, owner, steal, skill) -> {
            if (CONFIG.abilityPlundering) return EventResult.pass();
            if (!target.level().isClientSide() && target instanceof LivingEntity livingTarget && owner instanceof LivingEntity livingOwner) {
                PvPMode mode = FTBChunksWorldConfig.PVP_MODE.get();
                if (mode == PvPMode.ALWAYS) return EventResult.pass();
                if (isPvPProtectedChunk(mode, livingTarget) || isPvPProtectedChunk(mode, livingOwner)) {
                    if (livingOwner instanceof Player player)
                        PlayerNotifier.notifyWithCooldown(player, Component.translatable("ftbchunks.message.no_pvp").withStyle(ChatFormatting.GOLD), 3000L);
                    return EventResult.interruptFalse();
                }
            }
            return EventResult.pass();
        });

        TensuraSkillEvents.SKILL_GRIEF_PRE.register((instance, owner, x, y, z) -> {
            if (CONFIG.abilityGrief) return EventResult.pass();
            BlockPos pos = ObjectSelectionHelper.getBlockPos(new Vec3(x, y, z));
            if (ClaimedChunkManagerImpl.getInstance().shouldPreventInteraction(owner, InteractionHand.MAIN_HAND, pos, FTBChunksExpected.getBlockBreakProtection(), null)) {
                if (owner instanceof ServerPlayer sp) FTBCUtils.forceHeldItemSync(sp, InteractionHand.MAIN_HAND);
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
    }

    private static boolean isPvPProtectedChunk(PvPMode mode, LivingEntity entity) {
        if (entity == null) return false;
        if (entity instanceof Player && !CONFIG.protectPlayers) return false;
        if (entity instanceof Mob mob) {
            if (SubordinateHelper.getSubordinateOwnerUUID(mob) != null && !CONFIG.protectSubordinates) return false;
            if (!CONFIG.protectMobs) return false;
        }

        ClaimedChunk cc = ClaimedChunkManagerImpl.getInstance().getChunk(new ChunkDimPos(entity.level(), entity.blockPosition()));
        return cc != null && (mode == PvPMode.NEVER || !cc.getTeamData().allowPVP());
    }
}