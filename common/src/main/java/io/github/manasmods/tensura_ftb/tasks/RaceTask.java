package io.github.manasmods.tensura_ftb.tasks;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.AbstractBooleanTask;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura_ftb.registry.TensuraTaskTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class RaceTask extends AbstractBooleanTask {
	private ResourceLocation race = TensuraRaces.HUMAN.getId();

	public RaceTask(long id, Quest quest) {
		super(id, quest);
	}

	@Override
	public TaskType getType() {
		return TensuraTaskTypes.RACE;
	}

	@Override
	public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
		super.writeData(nbt, provider);
		nbt.putString("race", race.toString());
	}

	@Override
	public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
		super.readData(nbt, provider);
		race = ResourceLocation.tryParse(nbt.getString("race"));
	}

	@Override
	public void writeNetData(RegistryFriendlyByteBuf buffer) {
		super.writeNetData(buffer);
		buffer.writeResourceLocation(race);
	}

	@Override
	public void readNetData(RegistryFriendlyByteBuf buffer) {
		super.readNetData(buffer);
		race = buffer.readResourceLocation();
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void fillConfigGroup(ConfigGroup config) {
		super.fillConfigGroup(config);
		config.addString("race", race.toString(), v -> race = ResourceLocation.tryParse(v), TensuraRaces.HUMAN.getId().toString());
	}

	@Override
	public boolean canSubmit(TeamData teamData, ServerPlayer player) {
		ManasRace manasRace = RaceAPI.getRaceRegistry().get(race);
		if (manasRace == null) return false;
		Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(player).getRace();
        return optional.map(instance -> instance.getRace().equals(manasRace)).orElse(false);
    }
}
