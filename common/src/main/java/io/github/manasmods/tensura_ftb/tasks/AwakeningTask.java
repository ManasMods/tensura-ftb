package io.github.manasmods.tensura_ftb.tasks;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.AbstractBooleanTask;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.storage.TensuraStorages;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class AwakeningTask extends AbstractBooleanTask {
	public static TaskType AWAKENING;
	private AwakeningStatus status = AwakeningStatus.ANY;

	public AwakeningTask(long id, Quest quest) {
		super(id, quest);
	}

	@Override
	public TaskType getType() {
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
		config.addString("status", status.toString(), v -> status = AwakeningStatus.valueOf(v), "ANY");
	}

	@Override
	public boolean canSubmit(TeamData teamData, ServerPlayer player) {
		return switch (status) {
			case ANY -> TensuraStorages.getExistenceFrom(player).isTrueDemonLord() || TensuraStorages.getExistenceFrom(player).isTrueHero();
			case DEMON_LORD -> TensuraStorages.getExistenceFrom(player).isTrueDemonLord();
			case DEMON_LORD_SEED -> TensuraStorages.getExistenceFrom(player).isDemonLordSeed();
			case MAJIN -> TensuraStorages.getExistenceFrom(player).getAlignment().equals(Alignment.MAJIN);
			case CHAOS -> TensuraStorages.getExistenceFrom(player).getAlignment().equals(Alignment.CHAOS);
			case HERO -> TensuraStorages.getExistenceFrom(player).isTrueHero();
			case HERO_EGG -> TensuraStorages.getExistenceFrom(player).isHeroEgg();
			case BLESSED -> TensuraStorages.getExistenceFrom(player).isBlessed();
			case FINAL_RACE -> {
				Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(player).getRace();
				if (optional.isEmpty()) yield false;
				yield optional.get().getRace().getNextEvolutions(optional.get(), player).isEmpty();
			}
		};
	}

	public enum AwakeningStatus {
		ANY,
		DEMON_LORD,
		DEMON_LORD_SEED,
		MAJIN,
		CHAOS,
		HERO,
		HERO_EGG,
		BLESSED,
		FINAL_RACE
	}
}
