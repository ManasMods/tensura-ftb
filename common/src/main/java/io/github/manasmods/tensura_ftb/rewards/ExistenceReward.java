package io.github.manasmods.tensura_ftb.rewards;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.quest.reward.RewardType;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura_ftb.registry.TensuraRewardTypes;
import io.github.manasmods.tensura_ftb.tasks.ExistenceTask;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;

public class ExistenceReward extends Reward {
	private int value;
	private ExistenceTask.ExistenceType type = ExistenceTask.ExistenceType.MAX_EP;
	public ExistenceReward(long id, Quest quest, int xp) {
		super(id, quest);
		this.value = xp;
	}

	public ExistenceReward(long id, Quest quest) {
		this(id, quest, 1);
	}

	@Override
	public RewardType getType() {
		return TensuraRewardTypes.EXISTENCE;
	}

	@Override
	public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
		super.writeData(nbt, provider);
		nbt.putInt("existence", value);
		nbt.putString("type", type.toString());
	}

	@Override
	public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
		super.readData(nbt, provider);
		value = nbt.getInt("existence");
		type = ExistenceTask.ExistenceType.valueOf(nbt.getString("type"));
	}

	@Override
	public void writeNetData(RegistryFriendlyByteBuf buffer) {
		super.writeNetData(buffer);
		buffer.writeVarInt(value);
		buffer.writeEnum(type);
	}

	@Override
	public void readNetData(RegistryFriendlyByteBuf buffer) {
		super.readNetData(buffer);
		value = buffer.readVarInt();
		type = buffer.readEnum(ExistenceTask.ExistenceType.class);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void fillConfigGroup(ConfigGroup config) {
		super.fillConfigGroup(config);
		config.addInt("value", value, v -> value = v, 1, 1, Integer.MAX_VALUE).setNameKey("tensura_ftb.task.existence_value.value");
		config.addString("type", type.toString(), v -> type = ExistenceTask.ExistenceType.valueOf(v), "MAX_EP").setNameKey("tensura_ftb.task.existence_value.type");
	}

	@Override
	public void claim(ServerPlayer player, boolean notify) {
		switch (type) {
			case EP -> {
				EnergyHelper.gainMagicule(player, value / 2D, EnergyHelper.GainType.NORMAL);
				EnergyHelper.gainAura(player, value / 2D, EnergyHelper.GainType.NORMAL);
			}
			case MAGICULE -> EnergyHelper.gainMagicule(player, value, EnergyHelper.GainType.NORMAL);
			case AURA -> EnergyHelper.gainAura(player, value, EnergyHelper.GainType.NORMAL);
			case MAX_EP -> {
				EnergyHelper.gainMagicule(player, value / 2D, EnergyHelper.GainType.MAX);
				EnergyHelper.gainAura(player, value / 2D, EnergyHelper.GainType.MAX);
			}
			case MAX_MAGICULE -> EnergyHelper.gainMagicule(player, value, EnergyHelper.GainType.MAX);
			case MAX_AURA -> EnergyHelper.gainAura(player, value, EnergyHelper.GainType.MAX);
			case SOUL -> {
				IExistence existence = TensuraStorages.getExistenceFrom(player);
				existence.setSoulPoints(existence.getSoulPoints() + value * 1000);
				existence.markDirty();
			}
			case HUMAN_KILL -> {
				IExistence existence = TensuraStorages.getExistenceFrom(player);
				existence.setHumanKill(existence.getHumanKill() + value);
				existence.markDirty();
			}
			case RESET_COUNTER -> {
				ITensuraPlayer playerData = TensuraStorages.getPlayerDataFrom(player);
				playerData.setResetCounter(playerData.getResetCounter() + value);
				playerData.markDirty();
			}
		}
		if (notify) player.playNotifySound(TensuraSoundEvents.ENERGY_DRAIN.get(), SoundSource.PLAYERS, 1, 1);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public String getButtonText() {
		return "+" + value;
	}
}
