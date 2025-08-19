package io.github.manasmods.tensura_ftb.mixin;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura_ftb.TensuraFtb;
import io.github.manasmods.tensura_ftb.registry.TensuraTaskTypes;
import io.github.manasmods.tensura_ftb.tasks.*;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(TaskTypes.class)
public interface MixinTaskTypes {
    @Shadow
    static TaskType register(ResourceLocation name, TaskType.Provider provider, Supplier<Icon> iconSupplier) {
        return null;
    }

    @Inject(method = "init", at = @At(value = "TAIL"), remap = false)
    private static void init(CallbackInfo ci) {
        TensuraTaskTypes.ABILITY = register(ResourceLocation.fromNamespaceAndPath(TensuraFtb.MOD_ID, "ability"),
                AbilityTask::new, () -> tensura_ftb$getIcon(TensuraMaterialItems.BATTLEWILL_MANUAL.getId()));
        TensuraTaskTypes.AWAKENING = register(ResourceLocation.fromNamespaceAndPath(TensuraFtb.MOD_ID, "awakening"),
                AwakeningTask::new, () -> tensura_ftb$getIcon(TensuraMobDropItems.DEMON_ESSENCE.getId()));
        TensuraTaskTypes.EXISTENCE = register(ResourceLocation.fromNamespaceAndPath(TensuraFtb.MOD_ID, "existence_value"),
                ExistenceTask::new, () -> tensura_ftb$getIcon(TensuraMobDropItems.DRAGON_ESSENCE.getId()));
        TensuraTaskTypes.SPIRIT = register(ResourceLocation.fromNamespaceAndPath(TensuraFtb.MOD_ID, "spirit"),
                SpiritTask::new, () -> tensura_ftb$getIcon(TensuraMaterialItems.ELEMENT_CORE_EMPTY.getId()));
        TensuraTaskTypes.RACE = register(ResourceLocation.fromNamespaceAndPath(TensuraFtb.MOD_ID, "race"),
                RaceTask::new, () -> tensura_ftb$getIcon(TensuraMobDropItems.ROYAL_BLOOD.getId()));
    }

    @Unique
    private static Icon tensura_ftb$getIcon(ResourceLocation id) {
        return Icon.getIcon(id.getNamespace() + ":item/" + id.getPath());
    }
}
