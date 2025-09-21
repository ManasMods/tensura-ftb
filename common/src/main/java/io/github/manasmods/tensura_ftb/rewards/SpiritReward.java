package io.github.manasmods.tensura_ftb.rewards;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.quest.reward.RewardType;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.spirit.ISpiritWielder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class SpiritReward extends Reward {
	public static RewardType SPIRIT;
	private Element element = Element.FLAME;
	private SpiritualMagic.SpiritLevel level = SpiritualMagic.SpiritLevel.LESSER;

	public SpiritReward(long id, Quest quest) {
		super(id, quest);
	}

	@Override
	public RewardType getType() {
		return SPIRIT;
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
	public void claim(ServerPlayer player, boolean notify) {
		ISpiritWielder spirit = TensuraStorages.getSpiritFrom(player);
		spirit.setSpiritLevel(element, level);
		spirit.markDirty();
	}
}
