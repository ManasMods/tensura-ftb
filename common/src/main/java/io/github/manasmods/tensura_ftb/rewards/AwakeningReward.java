package io.github.manasmods.tensura_ftb.rewards;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.quest.reward.RewardType;
import io.github.manasmods.tensura.race.RaceHelper;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class AwakeningReward extends Reward {
	public static RewardType AWAKENING;
	private AwakeningStatus status = AwakeningStatus.RACE_EVOLVE;

	public AwakeningReward(long id, Quest quest) {
		super(id, quest);
	}

	@Override
	public RewardType getType() {
		return AWAKENING;
	}

	@Override
	public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
		super.writeData(nbt, provider);
		nbt.putString("status", status.toString());
	}

	@Override
	public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
		super.readData(nbt, provider);
		status = AwakeningStatus.valueOf(nbt.getString("status"));
	}

	@Override
	public void writeNetData(RegistryFriendlyByteBuf buffer) {
		super.writeNetData(buffer);
		buffer.writeEnum(status);
	}

	@Override
	public void readNetData(RegistryFriendlyByteBuf buffer) {
		super.readNetData(buffer);
		status = buffer.readEnum(AwakeningStatus.class);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void fillConfigGroup(ConfigGroup config) {
		super.fillConfigGroup(config);
		config.addString("status", status.toString(), v -> status = AwakeningStatus.valueOf(v), "RACE_EVOLVE").setNameKey("tensura_ftb.task.ability.status");
	}

	@Override
	public void claim(ServerPlayer player, boolean notify) {
		if (status == AwakeningStatus.RACE_EVOLVE) {
			RaceHelper.evolveRace(player, true);
			return;
		}

		IExistence existence = TensuraStorages.getExistenceFrom(player);
		switch (status) {
			case DEMON_LORD -> {
				existence.setTrueDemonLord(true);
				RaceHelper.awakening(player, false);
			}
			case DEMON_LORD_SEED -> existence.setDemonLordSeed(true);
			case MAJIN -> {
				if (existence.getAlignment().equals(Alignment.DEFAULT))
					existence.setAlignment(Alignment.MAJIN);
			}
			case CHAOS -> existence.setAlignment(Alignment.CHAOS);
			case HERO -> {
				existence.setTrueHero(true);
				RaceHelper.awakening(player, true);
			}
			case HERO_EGG -> existence.setHeroEgg(true);
			case BLESSED -> existence.setBlessed(true);
		}
		existence.markDirty();
	}

	public enum AwakeningStatus {
		RACE_EVOLVE,
		DEMON_LORD,
		DEMON_LORD_SEED,
		MAJIN,
		CHAOS,
		HERO,
		HERO_EGG,
		BLESSED
	}
}
