package io.github.manasmods.tensura_ftb.neoforge;

import io.github.manasmods.tensura_ftb.TensuraFtb;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(TensuraFtb.MOD_ID)
public final class TensuraFtbNeoForge {
    public TensuraFtbNeoForge(IEventBus bus) {
        TensuraFtb.init();
    }
}
