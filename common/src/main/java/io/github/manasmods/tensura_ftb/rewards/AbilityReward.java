package io.github.manasmods.tensura_ftb.rewards;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.quest.reward.RewardType;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.TensuraSkillInstance;
import io.github.manasmods.tensura.ability.skill.resist.ResistSkill;
import io.github.manasmods.tensura.registry.skill.CommonSkills;
import io.github.manasmods.tensura_ftb.tasks.AbilityTask;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class AbilityReward extends Reward {
	public static RewardType ABILITY;
	private ResourceLocation skill = CommonSkills.SELF_REGENERATION.getId();
	private AbilityTask.AcquisitionStatus status = AbilityTask.AcquisitionStatus.LEARNT;
	private int removeTime = -1;

	public AbilityReward(long id, Quest quest) {
		super(id, quest);
	}

	@Override
	public RewardType getType() {
		return ABILITY;
	}

	@Override
	public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
		super.writeData(nbt, provider);
		nbt.putString("ability", skill.toString());
		nbt.putString("status", status.toString());
		nbt.putInt("removeTime", removeTime);
	}

	@Override
	public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
		super.readData(nbt, provider);
		skill = ResourceLocation.tryParse(nbt.getString("ability"));
		status = AbilityTask.AcquisitionStatus.valueOf(nbt.getString("status"));
		removeTime = nbt.getInt("removeTime");
	}

	@Override
	public void writeNetData(RegistryFriendlyByteBuf buffer) {
		super.writeNetData(buffer);
		buffer.writeResourceLocation(skill);
		buffer.writeEnum(status);
		buffer.writeInt(removeTime);
	}

	@Override
	public void readNetData(RegistryFriendlyByteBuf buffer) {
		super.readNetData(buffer);
		skill = buffer.readResourceLocation();
		status = buffer.readEnum(AbilityTask.AcquisitionStatus.class);
		removeTime = buffer.readInt();
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void fillConfigGroup(ConfigGroup config) {
		super.fillConfigGroup(config);
		config.addString("ability", skill.toString(), v -> skill = ResourceLocation.tryParse(v), CommonSkills.SELF_REGENERATION.getId().toString()).setNameKey("tensura_ftb.task.ability.ability");
		config.addString("status", status.toString(), v -> status = AbilityTask.AcquisitionStatus.valueOf(v), "LEARNT").setNameKey("tensura_ftb.task.ability.status");
		config.addInt("removeTime", removeTime, v -> removeTime = v, -1, -2, Integer.MAX_VALUE).setNameKey("tensura_ftb.task.ability.remove_time");
	}

	@Override
	public void claim(ServerPlayer player, boolean notify) {
		ManasSkill manasSkill = SkillAPI.getSkillRegistry().get(skill);
		if (manasSkill == null) return;
		TensuraSkillInstance instance = new TensuraSkillInstance(manasSkill);
		switch (status) {
			case AVAILABLE -> {
				if (manasSkill instanceof ResistSkill resist) instance.setMastery(resist.getLearningPointRequirement() * -1);
				else if (manasSkill instanceof TensuraSkill tensuraSkill) instance.setMastery(tensuraSkill.getAcquirementMastery(player));
				else instance.setMastery(TensuraSkill.BASE_CONFIG.Learning.learningPointRequirement * -1);
			}
			case LEARNT -> instance.setMastery(0);
			case MASTERED -> instance.setMastery(instance.getMaxMastery());
		}
		SkillHelper.learnSkill(player, instance, removeTime);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Icon getAltIcon() {
		ManasSkill manasSkill = SkillAPI.getSkillRegistry().get(skill);
		if (manasSkill != null) return Icon.getIcon(manasSkill.getSkillIcon());
		return super.getAltIcon();
	}
}
