package io.github.manasmods.tensura_ftb;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.reward.RewardTypes;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura_ftb.registry.FtbConfig;
import io.github.manasmods.tensura_ftb.rewards.*;
import io.github.manasmods.tensura_ftb.tasks.*;
import net.minecraft.resources.ResourceLocation;

public final class TensuraFtb {
    public static final String MOD_ID = "tensura_ftb";
    public static FtbConfig CONFIG = ConfigRegistry.getConfig(FtbConfig.class);

    public static void init() {
        ConfigRegistry.registerConfig(new FtbConfig());
        FtbHandler.init();
        registerQuests();
    }

    public static void registerQuests() {
        AbilityTask.ABILITY = TaskTypes.register(ResourceLocation.fromNamespaceAndPath(TensuraFtb.MOD_ID, "ability"),
                AbilityTask::new, () -> getFtbIcon(TensuraMaterialItems.BATTLEWILL_MANUAL.getId()));
        AwakeningTask.AWAKENING = TaskTypes.register(ResourceLocation.fromNamespaceAndPath(TensuraFtb.MOD_ID, "awakening"),
                AwakeningTask::new, () -> getFtbIcon(TensuraMobDropItems.DEMON_ESSENCE.getId()));
        ExistenceTask.EXISTENCE = TaskTypes.register(ResourceLocation.fromNamespaceAndPath(TensuraFtb.MOD_ID, "existence_value"),
                ExistenceTask::new, () -> getFtbIcon(TensuraMobDropItems.DRAGON_ESSENCE.getId()));
        SpiritTask.SPIRIT = TaskTypes.register(ResourceLocation.fromNamespaceAndPath(TensuraFtb.MOD_ID, "spirit"),
                SpiritTask::new, () -> getFtbIcon(TensuraMaterialItems.ELEMENT_CORE_EMPTY.getId()));
        RaceTask.RACE = TaskTypes.register(ResourceLocation.fromNamespaceAndPath(TensuraFtb.MOD_ID, "race"),
                RaceTask::new, () -> getFtbIcon(TensuraMobDropItems.ROYAL_BLOOD.getId()));

        AbilityReward.ABILITY = RewardTypes.register(ResourceLocation.fromNamespaceAndPath(TensuraFtb.MOD_ID, "ability"),
                AbilityReward::new, () -> getFtbIcon(TensuraMaterialItems.BATTLEWILL_MANUAL.getId()));
        RandomAbilityReward.RANDOM_ABILITY = RewardTypes.register(ResourceLocation.fromNamespaceAndPath(TensuraFtb.MOD_ID, "random_ability"),
                RandomAbilityReward::new, () -> getFtbIcon(TensuraMobDropItems.ELEMENTAL_ESSENCE.getId()));
        AwakeningReward.AWAKENING = RewardTypes.register(ResourceLocation.fromNamespaceAndPath(TensuraFtb.MOD_ID, "awakening"),
                AwakeningReward::new, () -> getFtbIcon(TensuraMobDropItems.DEMON_ESSENCE.getId()));
        ExistenceReward.EXISTENCE = RewardTypes.register(ResourceLocation.fromNamespaceAndPath(TensuraFtb.MOD_ID, "existence_value"),
                ExistenceReward::new, () -> getFtbIcon(TensuraMobDropItems.DRAGON_ESSENCE.getId()));
        SpiritReward.SPIRIT = RewardTypes.register(ResourceLocation.fromNamespaceAndPath(TensuraFtb.MOD_ID, "spirit"),
                SpiritReward::new, () -> getFtbIcon(TensuraMaterialItems.ELEMENT_CORE_EMPTY.getId()));
        RaceReward.RACE = RewardTypes.register(ResourceLocation.fromNamespaceAndPath(TensuraFtb.MOD_ID, "race"),
                RaceReward::new, () -> getFtbIcon(TensuraMobDropItems.ROYAL_BLOOD.getId()));
    }

    private static Icon getFtbIcon(ResourceLocation id) {
        return Icon.getIcon(id.getNamespace() + ":item/" + id.getPath());
    }
}
