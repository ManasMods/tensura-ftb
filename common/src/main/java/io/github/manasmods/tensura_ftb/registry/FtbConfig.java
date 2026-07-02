package io.github.manasmods.tensura_ftb.registry;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;

import java.util.List;

public class FtbConfig extends ManasConfig {
    @Comment("Should FTB teams members to be counted as Ally for Tensura's abilities.")
    public boolean ftbAllyTensura = true;
    @Comment("Protect players from tensura abilities in non-pvp claims.")
    public boolean protectPlayers = true;
    @Comment("Protect subordinates/pets from tensura abilities in non-pvp claims.")
    public boolean protectSubordinates = true;
    @Comment("Protect every mob (including non-subordinate/pet) from tensura abilities in non-pvp claims.")
    public boolean protectMobs = false;
    @Comment("List of entities that are automatically protected from tensura abilities in non-pvp claims regardless of their entity type.")
    public List<String> protectedEntities = List.of("minecraft:villager", "tensura:dwarf");

    @Comment("Allow non-ability damage (normal weapon swing, arrow, etc) from others to damage non-player protected mobs in non-pvp claims.")
    public boolean mobDamage = false;
    @Comment("Allow ability damage from others to damage non-player protected mobs in non-pvp claims.")
    public boolean mobDamageAbility = false;

    @Comment("Allow harmful effect from others to be inflicted in non-pvp claims.")
    public boolean harmfulEffect = false;
    @Comment("Allow energy draining in non-pvp claims.")
    public boolean energyDrain = false;
    @Comment("Allow mind controlling in non-pvp claims.")
    public boolean mindControl = false;
    @Comment("Allow possession in non-pvp claims.")
    public boolean possession = false;
    @Comment("Allow spiritual damage in non-pvp claims.")
    public boolean spiritualDamage = false;
    @Comment("Allow movement forced by others' abilities in non-pvp claims.")
    public boolean forcedMovement = false;
    @Comment("Allow teleportation forced by others' abilities in non-pvp claims.")
    public boolean forcedTeleportation = false;
    @Comment("Allow ability plundering in non-pvp claims.")
    public boolean abilityPlundering = false;
    @Comment("Allow ability griefing in claimed chunks.")
    public boolean abilityGrief = false;

    public String getFileName() {
        return "tensura/ftb_config";
    }
}
