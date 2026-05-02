package io.github.manasmods.tensura_ftb;

import dev.architectury.event.EventResult;
import dev.architectury.hooks.level.entity.PlayerHooks;
import dev.ftb.mods.ftbchunks.FTBCUtils;
import dev.ftb.mods.ftbchunks.FTBChunksExpected;
import dev.ftb.mods.ftbchunks.FTBChunksWorldConfig;
import dev.ftb.mods.ftbchunks.PlayerNotifier;
import dev.ftb.mods.ftbchunks.api.ClaimedChunk;
import dev.ftb.mods.ftbchunks.api.Protection;
import dev.ftb.mods.ftbchunks.api.ProtectionPolicy;
import dev.ftb.mods.ftbchunks.data.ClaimedChunkImpl;
import dev.ftb.mods.ftbchunks.data.ClaimedChunkManagerImpl;
import dev.ftb.mods.ftbchunks.data.PvPMode;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.EntityEvents;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.storage.player.WarpPoint;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import io.github.manasmods.tensura_ftb.registry.FtbConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class FtbHandler {
    public static void init() {
        EntityEvents.LIVING_EFFECT_ADDED.register((entity, source, changeableInstance) -> {
            MobEffectInstance instance = changeableInstance.get();
            if (instance == null) return EventResult.pass();
            if (instance.getEffect().value().isBeneficial()) return EventResult.pass();
            if (source == entity || Objects.equals(changeableInstance.get().tensura$getSource(), entity.getUUID())) return EventResult.pass();
            if (ConfigRegistry.getConfig(FtbConfig.class).harmfulEffect) return EventResult.pass();

            if (!entity.level().isClientSide() && source instanceof LivingEntity attacker) {
                PvPMode mode = FTBChunksWorldConfig.PVP_MODE.get();
                if (mode == PvPMode.ALWAYS) return EventResult.pass();
                if (isPvPProtectedChunk(mode, entity, attacker)) {
                    if (attacker instanceof Player player)
                        PlayerNotifier.notifyWithCooldown(player, Component.translatable("ftbchunks.message.no_pvp").withStyle(ChatFormatting.GOLD), 3000L);
                    return EventResult.interruptFalse();
                }
            }
            return EventResult.pass();
        });

        TensuraEntityEvents.ENERGY_DRAIN_EVENT.register((target, drainer, drainType, gainType, amount, percentage) -> {
            if (ConfigRegistry.getConfig(FtbConfig.class).energyDrain) return EventResult.pass();
            if (!target.level().isClientSide() && drainer instanceof LivingEntity attacker) {
                PvPMode mode = FTBChunksWorldConfig.PVP_MODE.get();
                if (mode == PvPMode.ALWAYS) return EventResult.pass();
                if (isPvPProtectedChunk(mode, target, attacker)) {
                    if (attacker instanceof Player player)
                        PlayerNotifier.notifyWithCooldown(player, Component.translatable("ftbchunks.message.no_pvp").withStyle(ChatFormatting.GOLD), 3000L);
                    return EventResult.interruptFalse();
                }
            }
            return EventResult.pass();
        });

        TensuraEntityEvents.FORCE_TAME_EVENT.register((target, possessor, temporary) -> {
            if (ConfigRegistry.getConfig(FtbConfig.class).mindControl) return EventResult.pass();
            if (!target.level().isClientSide() && possessor instanceof LivingEntity attacker) {
                PvPMode mode = FTBChunksWorldConfig.PVP_MODE.get();
                if (mode == PvPMode.ALWAYS) return EventResult.pass();
                if (isPvPProtectedChunk(mode, target, attacker)) {
                    if (attacker instanceof Player player)
                        PlayerNotifier.notifyWithCooldown(player, Component.translatable("ftbchunks.message.no_pvp").withStyle(ChatFormatting.GOLD), 3000L);
                    return EventResult.interruptFalse();
                }
            }
            return EventResult.pass();
        });

        TensuraEntityEvents.POSSESSION_EVENT.register((target, possessor) -> {
            if (ConfigRegistry.getConfig(FtbConfig.class).possession) return EventResult.pass();
            if (!target.level().isClientSide() && possessor instanceof LivingEntity attacker) {
                PvPMode mode = FTBChunksWorldConfig.PVP_MODE.get();
                if (mode == PvPMode.ALWAYS) return EventResult.pass();
                if (isPvPProtectedChunk(mode, target, attacker)) {
                    if (attacker instanceof Player player)
                        PlayerNotifier.notifyWithCooldown(player, Component.translatable("ftbchunks.message.no_pvp").withStyle(ChatFormatting.GOLD), 3000L);
                    return EventResult.interruptFalse();
                }
            }
            return EventResult.pass();
        });

        TensuraEntityEvents.SPIRITUAL_HURT_EVENT.register((target, attacker, originalAmount, resistPercentage, amount, source) -> {
            if (ConfigRegistry.getConfig(FtbConfig.class).spiritualDamage) return EventResult.pass();
            if (!target.level().isClientSide() && attacker instanceof LivingEntity livingAttacker) {
                PvPMode mode = FTBChunksWorldConfig.PVP_MODE.get();
                if (mode == PvPMode.ALWAYS) return EventResult.pass();
                if (isPvPProtectedChunk(mode, target, livingAttacker)) {
                    if (livingAttacker instanceof Player player)
                        PlayerNotifier.notifyWithCooldown(player, Component.translatable("ftbchunks.message.no_pvp").withStyle(ChatFormatting.GOLD), 3000L);
                    return EventResult.interruptFalse();
                }
            }
            return EventResult.pass();
        });

        TensuraEntityEvents.FORCE_MOVEMENT_EVENT.register((target, mover, skill, ve3) -> {
            if (ConfigRegistry.getConfig(FtbConfig.class).forcedMovement) return EventResult.pass();
            if (!target.level().isClientSide() && mover instanceof LivingEntity attacker) {
                PvPMode mode = FTBChunksWorldConfig.PVP_MODE.get();
                if (mode == PvPMode.ALWAYS) return EventResult.pass();
                if (isPvPProtectedChunk(mode, target, attacker)) {
                    if (attacker instanceof Player player)
                        PlayerNotifier.notifyWithCooldown(player, Component.translatable("ftbchunks.message.no_pvp").withStyle(ChatFormatting.GOLD), 3000L);
                    return EventResult.interruptFalse();
                }
            }
            return EventResult.pass();
        });

        TensuraEntityEvents.INSTANT_TRANSMISSION_EVENT.register((target, teleporter, position, type) -> {
            if (!type.equals(WarpPoint.TransmissionType.ABILITY) || target == teleporter) return EventResult.pass();
            if (ConfigRegistry.getConfig(FtbConfig.class).forcedTeleportation) return EventResult.pass();

            if (!target.level().isClientSide() && target instanceof LivingEntity livingTarget && teleporter instanceof LivingEntity livingOwner) {
                PvPMode mode = FTBChunksWorldConfig.PVP_MODE.get();
                if (mode == PvPMode.ALWAYS) return EventResult.pass();
                if (isPvPProtectedChunk(mode, livingTarget, livingOwner)) {
                    if (livingOwner instanceof Player player)
                        PlayerNotifier.notifyWithCooldown(player, Component.translatable("ftbchunks.message.no_pvp").withStyle(ChatFormatting.GOLD), 3000L);
                    return EventResult.interruptFalse();
                }
            }
            return EventResult.pass();
        });

        TensuraSkillEvents.SKILL_PLUNDER.register((target, owner, steal, skill) -> {
            if (ConfigRegistry.getConfig(FtbConfig.class).abilityPlundering) return EventResult.pass();
            if (target != null && !target.level().isClientSide() && target instanceof LivingEntity livingTarget && owner instanceof LivingEntity livingOwner) {
                PvPMode mode = FTBChunksWorldConfig.PVP_MODE.get();
                if (mode == PvPMode.ALWAYS) return EventResult.pass();
                if (isPvPProtectedChunk(mode, livingTarget, livingOwner)) {
                    if (livingOwner instanceof Player player)
                        PlayerNotifier.notifyWithCooldown(player, Component.translatable("ftbchunks.message.no_pvp").withStyle(ChatFormatting.GOLD), 3000L);
                    return EventResult.interruptFalse();
                }
            }
            return EventResult.pass();
        });

        TensuraSkillEvents.SKILL_GRIEF_PRE.register((instance, level, owner, x, y, z) -> {
            if (ConfigRegistry.getConfig(FtbConfig.class).abilityGrief) return EventResult.pass();
            BlockPos pos = ObjectSelectionHelper.getBlockPos(new Vec3(x, y, z));
            if (shouldPreventInteraction(ClaimedChunkManagerImpl.getInstance(), level, owner, InteractionHand.MAIN_HAND, pos, FTBChunksExpected.getBlockBreakProtection(), null)) {
                if (owner instanceof ServerPlayer sp) FTBCUtils.forceHeldItemSync(sp, InteractionHand.MAIN_HAND);
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
    }

    private static boolean isPvPProtectedChunk(PvPMode mode, Entity entity, LivingEntity attacker) {
        if (entity == null) return false;
        ClaimedChunkManagerImpl manager = ClaimedChunkManagerImpl.getInstance();
        if (manager.getBypassProtection(attacker.getUUID())) return false;

        FtbConfig CONFIG = ConfigRegistry.getConfig(FtbConfig.class);
        if (CONFIG == null) return false;
        if (CONFIG.protectedEntities.contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString())) return canPVP(mode, entity, manager);

        if (CONFIG.protectPlayers && entity instanceof Player player) {
            if (attacker.getType().equals(EntityType.PLAYER)) return canPVP(mode, player, manager);
            if (attacker instanceof Mob mob && SubordinateHelper.getSubordinateOwner(mob) instanceof Player) return canPVP(mode, player, manager);
        }

        if (entity instanceof Mob mob) {
            if (CONFIG.protectSubordinates && SubordinateHelper.getSubordinateOwner(mob) instanceof Player player && !player.equals(attacker)) {
                if (attacker.getType().equals(EntityType.PLAYER)) return canPVP(mode, mob, manager);
                if (attacker instanceof Mob sub && SubordinateHelper.getSubordinateOwner(sub) instanceof Player) return canPVP(mode, mob, manager);
            }

            if (CONFIG.protectMobs) {
                if (attacker.getType().equals(EntityType.PLAYER)) return canPVP(mode, mob, manager);
                if (attacker instanceof Mob sub && SubordinateHelper.getSubordinateOwner(sub) instanceof Player) return canPVP(mode, mob, manager);
            }
        } else canPVP(mode, entity, manager);
        return false;
    }

    private static boolean canPVP(PvPMode mode, Entity entity, ClaimedChunkManagerImpl manager) {
        ClaimedChunk cc = manager.getChunk(new ChunkDimPos(entity.level(), entity.blockPosition()));
        return cc != null && (mode == PvPMode.NEVER || !cc.getTeamData().allowPVP());
    }

    public static boolean shouldPreventInteraction(ClaimedChunkManagerImpl manager, Level level, @Nullable Entity actor, InteractionHand hand, BlockPos pos, Protection protection, @Nullable Entity targetEntity) {
        if (FTBChunksWorldConfig.DISABLE_PROTECTION.get()) return false;
        boolean isFake = actor instanceof ServerPlayer player && PlayerHooks.isFake(player);
        if (isFake && FTBChunksWorldConfig.ALLOW_FAKE_PLAYERS.get().isOverride()) {
            return FTBChunksWorldConfig.ALLOW_FAKE_PLAYERS.get().shouldPreventInteraction();
        }

        ClaimedChunkImpl chunk = manager.getChunk(new ChunkDimPos(level, pos));
        if (chunk != null) {
            if (actor == null) return true;

            ProtectionPolicy policy = actor instanceof ServerPlayer player ? protection.getProtectionPolicy(player, pos, hand, chunk, targetEntity) : null;
            boolean prevented = policy != null && policy.isOverride() ? policy.shouldPreventInteraction() : isFake || !manager.getBypassProtection(actor.getUUID());
            if (prevented && actor instanceof ServerPlayer player) {
                PlayerNotifier.notifyWithCooldown(player, Component.translatable("ftbchunks.action_prevented").withStyle(ChatFormatting.GOLD), 2000);
                if (isFake) chunk.getTeamData().logPreventedAccess(player, System.currentTimeMillis());
            }
            return prevented;

        } else if (actor instanceof ServerPlayer player && FTBChunksWorldConfig.noWilderness(player)) {
            ProtectionPolicy override = protection.getProtectionPolicy(player, pos, hand, null, targetEntity);
            if (override.isOverride()) return override.shouldPreventInteraction();
            else if (!isFake && (manager.getBypassProtection(player.getUUID()) || player.isSpectator())) return false;
            PlayerNotifier.notifyWithCooldown(player, Component.translatable("ftbchunks.need_to_claim_chunk").withStyle(ChatFormatting.GOLD), 2000);
            return true;
        }
        return false;
    }
}