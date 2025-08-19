package io.github.manasmods.tensura_ftb.tasks;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.ISingleLongValueTask;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura_ftb.registry.TensuraTaskTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ExistenceTask extends Task implements ISingleLongValueTask {
	private long value = 1L;
	private ExistenceType type = ExistenceType.MAX_EP;
	public ExistenceTask(long id, Quest quest) {
		super(id, quest);
	}

	@Override
	public TaskType getType() {
		return TensuraTaskTypes.EXISTENCE;
	}

	@Override
	public long getMaxProgress() {
		return value;
	}

	@Override
	public String formatMaxProgress() {
		return Long.toUnsignedString(value);
	}

	@Override
	public String formatProgress(TeamData teamData, long progress) {
		return Long.toUnsignedString(progress);
	}

	@Override
	public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
		super.writeData(nbt, provider);
		nbt.putLong("value", value);
		nbt.putString("type", type.toString());
	}

	@Override
	public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
		super.readData(nbt, provider);
		value = nbt.getLong("value");
		type = ExistenceType.valueOf(nbt.getString("type"));
	}

	@Override
	public void writeNetData(RegistryFriendlyByteBuf buffer) {
		super.writeNetData(buffer);
		buffer.writeVarLong(value);
		buffer.writeEnum(type);
	}

	@Override
	public void readNetData(RegistryFriendlyByteBuf buffer) {
		super.readNetData(buffer);
		value = buffer.readVarLong();
		type = buffer.readEnum(ExistenceType.class);
	}

	@Override
	public void setValue(long v) {
		value = v;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void fillConfigGroup(ConfigGroup config) {
		super.fillConfigGroup(config);
		config.addLong("value", value, v -> value = v, 1L, 1L, Long.MAX_VALUE).setNameKey("tensura_ftb.task.existence_value.value");
		config.addString("type", type.toString(), v -> type = ExistenceType.valueOf(v), "MAX_EP").setNameKey("tensura_ftb.task.existence_value.type");
	}

	@Override
	public int autoSubmitOnPlayerTick() {
		return 20;
	}

	public double getExistenceValue(ServerPlayer player) {
		return switch (type) {
			case EP -> TensuraStorages.getExistenceFrom(player).getEP();
			case MAGICULE -> TensuraStorages.getExistenceFrom(player).getMagicule();
			case AURA -> TensuraStorages.getExistenceFrom(player).getAura();
			case MAX_EP -> EnergyHelper.getMaxEP(player);
			case MAX_MAGICULE -> EnergyHelper.getMaxMagicule(player);
			case MAX_AURA -> EnergyHelper.getMaxAura(player);
			case SOUL -> TensuraStorages.getExistenceFrom(player).getSoulPoints() / 10000.0;
			case HUMAN_KILL -> TensuraStorages.getExistenceFrom(player).getHumanKill();
			case RESET_COUNTER -> TensuraStorages.getPlayerDataFrom(player).getResetCounter();
		};
	}

	public void submitTask(TeamData teamData, ServerPlayer player, ItemStack craftedItem) {
		if (!checkTaskSequence(teamData)) return;
		teamData.setProgress(this, (long) this.getExistenceValue(player));
	}

	public enum ExistenceType {
		EP,
		MAX_EP,
		AURA,
		MAX_AURA,
		MAGICULE,
		MAX_MAGICULE,
		SOUL,
		HUMAN_KILL,
		RESET_COUNTER
	}
}
