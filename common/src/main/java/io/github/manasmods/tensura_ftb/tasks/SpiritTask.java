package io.github.manasmods.tensura_ftb.tasks;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.AbstractBooleanTask;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.spirit.ISpiritWielder;
import io.github.manasmods.tensura_ftb.registry.TensuraTaskTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class SpiritTask extends AbstractBooleanTask {
	private Element element = Element.UNIDENTIFIED;
	private SpiritualMagic.SpiritLevel level = SpiritualMagic.SpiritLevel.LESSER;

	public SpiritTask(long id, Quest quest) {
		super(id, quest);
	}

	@Override
	public TaskType getType() {
		return TensuraTaskTypes.SPIRIT;
	}

	@Override
	public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
		super.writeData(nbt, provider);
		nbt.putString("element", element.toString());
		nbt.putString("level", level.toString());
	}

	@Override
	public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
		super.readData(nbt, provider);
		element = Element.valueOf(nbt.getString("element"));
		level = SpiritualMagic.SpiritLevel.valueOf(nbt.getString("level"));
	}

	@Override
	public void writeNetData(RegistryFriendlyByteBuf buffer) {
		super.writeNetData(buffer);
		buffer.writeEnum(element);
		buffer.writeEnum(level);
	}

	@Override
	public void readNetData(RegistryFriendlyByteBuf buffer) {
		super.readNetData(buffer);
		element = buffer.readEnum(Element.class);
		level = buffer.readEnum(SpiritualMagic.SpiritLevel.class);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void fillConfigGroup(ConfigGroup config) {
		super.fillConfigGroup(config);
		config.addString("element", element.toString(), v -> element = Element.valueOf(v), "UNIDENTIFIED").setNameKey("tensura_ftb.task.spirit.element");
		config.addString("level", level.toString(), v -> level = SpiritualMagic.SpiritLevel.valueOf(v), "LESSER").setNameKey("tensura_ftb.task.spirit.level");
	}

	@Override
	public boolean canSubmit(TeamData teamData, ServerPlayer player) {
		ISpiritWielder spirit = TensuraStorages.getSpiritFrom(player);
		if (element == Element.UNIDENTIFIED) {
			for (Element each : Element.values()) {
				if (spirit.getSpiritLevelId(each) >= level.getId()) return true;
			}
			return false;
		}
		return spirit.getSpiritLevelId(element) >= level.getId();
	}
}
