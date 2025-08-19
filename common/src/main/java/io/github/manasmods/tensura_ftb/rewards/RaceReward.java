package io.github.manasmods.tensura_ftb.rewards;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.quest.reward.RewardType;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.race.api.Races;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura_ftb.registry.TensuraRewardTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class RaceReward extends Reward {
	private ResourceLocation race = TensuraRaces.HUMAN.getId();
	public RaceReward(long id, Quest quest) {
		super(id, quest);
	}

	@Override
	public RewardType getType() {
		return TensuraRewardTypes.RACE;
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
		config.addString("race", race.toString(), v -> race = ResourceLocation.tryParse(v), TensuraRaces.HUMAN.getId().toString()).setNameKey("tensura_ftb.task.race.race");
	}

	@Override
	public void claim(ServerPlayer player, boolean notify) {
		ManasRace manasRace = RaceAPI.getRaceRegistry().get(race);
		if (manasRace == null) return;
		Races races = RaceAPI.getRaceFrom(player);
		races.setRace(manasRace, false);
		races.markDirty();
	}
}
