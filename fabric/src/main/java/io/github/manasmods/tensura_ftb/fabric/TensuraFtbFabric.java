package io.github.manasmods.tensura_ftb.fabric;

import io.github.manasmods.tensura_ftb.TensuraFtb;
import net.fabricmc.api.ModInitializer;

public final class TensuraFtbFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        TensuraFtb.init();
    }
}
