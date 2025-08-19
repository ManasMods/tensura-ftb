package io.github.manasmods.tensura_ftb.mixin;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.reward.RewardType;
import dev.ftb.mods.ftbquests.quest.reward.RewardTypes;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura_ftb.TensuraFtb;
import io.github.manasmods.tensura_ftb.registry.TensuraRewardTypes;
import io.github.manasmods.tensura_ftb.rewards.*;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(RewardTypes.class)
public interface MixinRewardTypes {
    @Shadow
    static RewardType register(ResourceLocation name, RewardType.Provider p, Supplier<Icon> i) {
        return null;
    }

    @Inject(method = "init", at = @At(value = "TAIL"), remap = false)
    private static void init(CallbackInfo ci) {
        TensuraRewardTypes.ABILITY = register(ResourceLocation.fromNamespaceAndPath(TensuraFtb.MOD_ID, "ability"),
                AbilityReward::new, () -> tensura_ftb$getIcon(TensuraMaterialItems.BATTLEWILL_MANUAL.getId()));
        TensuraRewardTypes.AWAKENING = register(ResourceLocation.fromNamespaceAndPath(TensuraFtb.MOD_ID, "awakening"),
                AwakeningReward::new, () -> tensura_ftb$getIcon(TensuraMobDropItems.DEMON_ESSENCE.getId()));
        TensuraRewardTypes.EXISTENCE = register(ResourceLocation.fromNamespaceAndPath(TensuraFtb.MOD_ID, "existence_value"),
                ExistenceReward::new, () -> tensura_ftb$getIcon(TensuraMobDropItems.DRAGON_ESSENCE.getId()));
        TensuraRewardTypes.SPIRIT = register(ResourceLocation.fromNamespaceAndPath(TensuraFtb.MOD_ID, "spirit"),
                SpiritReward::new, () -> tensura_ftb$getIcon(TensuraMaterialItems.ELEMENT_CORE_EMPTY.getId()));
        TensuraRewardTypes.RACE = register(ResourceLocation.fromNamespaceAndPath(TensuraFtb.MOD_ID, "race"),
                RaceReward::new, () -> tensura_ftb$getIcon(TensuraMobDropItems.ROYAL_BLOOD.getId()));
    }

    @Unique
    private static Icon tensura_ftb$getIcon(ResourceLocation id) {
        return Icon.getIcon(id.getNamespace() + ":item/" + id.getPath());
    }
}
