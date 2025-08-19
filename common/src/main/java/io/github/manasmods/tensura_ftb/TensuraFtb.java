package io.github.manasmods.tensura_ftb;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.tensura_ftb.registry.FtbConfig;

public final class TensuraFtb {
    public static final String MOD_ID = "tensura_ftb";

    public static void init() {
        ConfigRegistry.registerConfig(new FtbConfig());
        FtbHandler.init();
    }
}
