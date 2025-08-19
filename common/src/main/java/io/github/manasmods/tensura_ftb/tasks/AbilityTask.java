package io.github.manasmods.tensura_ftb.tasks;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.AbstractBooleanTask;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura_ftb.registry.TensuraTaskTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class AbilityTask extends AbstractBooleanTask {
	private ResourceLocation skill = UniqueSkills.PREDATOR.getId();
	private AcquisitionStatus status = AcquisitionStatus.LEARNT;

	public AbilityTask(long id, Quest quest) {
		super(id, quest);
	}

	@Override
	public TaskType getType() {
		return TensuraTaskTypes.ABILITY;
	}

	@Override
	public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
		super.writeData(nbt, provider);
		nbt.putString("ability", skill.toString());
		nbt.putString("status", status.toString());
	}

	@Override
	public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
		super.readData(nbt, provider);
		skill = ResourceLocation.tryParse(nbt.getString("ability"));
		status = AcquisitionStatus.valueOf(nbt.getString("status"));
	}

	@Override
	public void writeNetData(RegistryFriendlyByteBuf buffer) {
		super.writeNetData(buffer);
		buffer.writeResourceLocation(skill);
		buffer.writeEnum(status);
	}

	@Override
	public void readNetData(RegistryFriendlyByteBuf buffer) {
		super.readNetData(buffer);
		skill = buffer.readResourceLocation();
		status = buffer.readEnum(AcquisitionStatus.class);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void fillConfigGroup(ConfigGroup config) {
		super.fillConfigGroup(config);
		config.addString("ability", skill.toString(), v -> skill = ResourceLocation.tryParse(v), UniqueSkills.PREDATOR.getId().toString()).setNameKey("tensura_ftb.task.ability.ability");
		config.addString("status", status.toString(), v -> status = AcquisitionStatus.valueOf(v), "LEARNT").setNameKey("tensura_ftb.task.ability.status");
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Icon getAltIcon() {
		ManasSkill manasSkill = SkillAPI.getSkillRegistry().get(skill);
		if (manasSkill != null) return Icon.getIcon(manasSkill.getSkillIcon());
		return super.getAltIcon();
	}

	@Override
	public boolean canSubmit(TeamData teamData, ServerPlayer player) {
		Skills skills = SkillAPI.getSkillsFrom(player);
		Optional<ManasSkillInstance> optional = skills.getSkill(skill);
		return switch (status) {
			case AVAILABLE -> optional.isPresent();
			case LEARNT -> optional.isPresent() && optional.get().getMastery() >= 0;
			case MASTERED -> optional.isPresent() && optional.get().isMastered(player);
		};
	}

	public enum AcquisitionStatus {
		AVAILABLE,
		LEARNT,
		MASTERED
	}
}
