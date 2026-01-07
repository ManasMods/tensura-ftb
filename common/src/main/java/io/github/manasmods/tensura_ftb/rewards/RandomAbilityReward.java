package io.github.manasmods.tensura_ftb.rewards;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.quest.reward.RewardType;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkillInstance;
import io.github.manasmods.tensura.ability.battlewill.Battlewill;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura_ftb.tasks.AbilityTask;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class RandomAbilityReward extends Reward {
	public static RewardType RANDOM_ABILITY;
	private RandomType random = RandomType.ALL;
	private AbilityTask.AcquisitionStatus status = AbilityTask.AcquisitionStatus.LEARNT;
	private int removeTime = -1;
	private boolean checkForNewAbility = true;

	public RandomAbilityReward(long id, Quest quest) {
		super(id, quest);
	}

	@Override
	public RewardType getType() {
		return RANDOM_ABILITY;
	}

	@Override
	public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
		super.writeData(nbt, provider);
		nbt.putString("random", random.toString());
		nbt.putString("status", status.toString());
		nbt.putInt("removeTime", removeTime);
		nbt.putBoolean("checkForNewAbility", checkForNewAbility);
	}

	@Override
	public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
		super.readData(nbt, provider);
		random = RandomType.valueOf(nbt.getString("random"));
		status = AbilityTask.AcquisitionStatus.valueOf(nbt.getString("status"));
		removeTime = nbt.getInt("removeTime");
		checkForNewAbility = nbt.getBoolean("checkForNewAbility");
	}

	@Override
	public void writeNetData(RegistryFriendlyByteBuf buffer) {
		super.writeNetData(buffer);
		buffer.writeEnum(random);
		buffer.writeEnum(status);
		buffer.writeInt(removeTime);
		buffer.writeBoolean(checkForNewAbility);
	}

	@Override
	public void readNetData(RegistryFriendlyByteBuf buffer) {
		super.readNetData(buffer);
		random = buffer.readEnum(RandomType.class);
		status = buffer.readEnum(AbilityTask.AcquisitionStatus.class);
		removeTime = buffer.readInt();
		checkForNewAbility = buffer.readBoolean();
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void fillConfigGroup(ConfigGroup config) {
		super.fillConfigGroup(config);
		config.addString("random", random.toString(), v -> random = RandomType.valueOf(v), "ALL").setNameKey("tensura_ftb.reward.ability.random");
		config.addString("status", status.toString(), v -> status = AbilityTask.AcquisitionStatus.valueOf(v), "LEARNT").setNameKey("tensura_ftb.task.ability.status");
		config.addInt("removeTime", removeTime, v -> removeTime = v, -1, -2, Integer.MAX_VALUE).setNameKey("tensura_ftb.task.ability.remove_time");
		config.addBool("checkForNewAbility", checkForNewAbility, v -> checkForNewAbility = v, Boolean.TRUE).setNameKey("tensura_ftb.reward.ability.new_ability");
	}

	@Override
	public void claim(ServerPlayer player, boolean notify) {
        if (player == null) return;
		Skills skills = SkillAPI.getSkillsFrom(player);
		List<ManasSkill> list = SkillAPI.getSkillRegistry().entrySet().stream().map(Map.Entry::getValue)
				.filter(manasSkill -> this.checkNewAbility(skills, manasSkill)).toList();
		if (list.isEmpty()) return;

		ManasSkill manasSkill = list.get(player.level().getRandom().nextInt(list.size()));
		if (manasSkill == null) return;
		SkillHelper.learnSkill(player, new TensuraSkillInstance(manasSkill), removeTime);
	}

	private boolean checkNewAbility(Skills skills, ManasSkill skill) {
		if (!this.random.predicate.test(skill)) return false;
		if (this.checkForNewAbility) {
			Optional<ManasSkillInstance> optional = skills.getSkill(skill);
			if (optional.isEmpty()) return true;
			if (this.status.equals(AbilityTask.AcquisitionStatus.LEARNT)) return optional.get().getMastery() < 0;
			if (this.status.equals(AbilityTask.AcquisitionStatus.MASTERED)) return optional.get().getMastery() < optional.get().getMaxMastery();
		}
		return true;
	}

	public enum RandomType {
		ALL(skill -> true),
		SKILL(skill -> skill instanceof Skill),
		RESISTANCE(skill -> skill instanceof Skill pSkill && pSkill.getType().equals(Skill.SkillType.RESISTANCE)),
		COMMON(skill -> skill instanceof Skill pSkill && pSkill.getType().equals(Skill.SkillType.COMMON)),
		INTRINSIC(skill -> skill instanceof Skill pSkill && pSkill.getType().equals(Skill.SkillType.INTRINSIC)),
		EXTRA(skill -> skill instanceof Skill pSkill && pSkill.getType().equals(Skill.SkillType.EXTRA)),
		UNIQUE(skill -> skill instanceof Skill pSkill && pSkill.getType().equals(Skill.SkillType.UNIQUE)),
		ULTIMATE(skill -> skill instanceof Skill pSkill && pSkill.getType().equals(Skill.SkillType.ULTIMATE)),
		MAGIC(skill -> skill instanceof Magic),
		ELEMENTAL(skill -> skill instanceof Magic magic && magic.getType().equals(Magic.MagicType.ASPECTUAL)),
		SPIRITUAL(skill -> skill instanceof Magic magic && magic.getType().equals(Magic.MagicType.SPIRITUAL)),
		SUMMONING(skill -> skill instanceof Magic magic && magic.getType().equals(Magic.MagicType.SUMMONING)),
		MISC(skill -> skill instanceof Magic magic && magic.getType().equals(Magic.MagicType.MISC)),
		BATTLEWILL(skill -> skill instanceof Battlewill);

		public final Predicate<ManasSkill> predicate;
		RandomType(Predicate<ManasSkill> predicate) {
			this.predicate = predicate;
		}
	}
}
