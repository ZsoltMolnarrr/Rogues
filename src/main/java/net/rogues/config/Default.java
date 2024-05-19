package net.rogues.config;

import net.fabric_extras.structure_pool.api.StructurePoolConfig;
import net.spell_engine.api.item.ItemConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Default {
    public static final ItemConfig itemConfig;
    public static final StructurePoolConfig villages;

    static {
        itemConfig = new ItemConfig();
        villages = new StructurePoolConfig();

        var smallWeight = 25;
        var limit = 1;
        villages.entries = new ArrayList<>(List.of(
//                new StructurePoolConfig.Entry("minecraft:village/desert/houses", new ArrayList<>(Arrays.asList(
//                        new StructurePoolConfig.Entry.Structure("rogues:village/desert/barracks", smallWeight, limit))
//                )),
//                new StructurePoolConfig.Entry("minecraft:village/savanna/houses", new ArrayList<>(Arrays.asList(
//                        new StructurePoolConfig.Entry.Structure("rogues:village/savanna/barracks", smallWeight, limit))
//                )),
                new StructurePoolConfig.Entry("minecraft:village/plains/houses", new ArrayList<>(Arrays.asList(
                        new StructurePoolConfig.Entry.Structure("rogues:village/plains/barracks", smallWeight, limit))
                )),
                new StructurePoolConfig.Entry("minecraft:village/taiga/houses", new ArrayList<>(Arrays.asList(
                        new StructurePoolConfig.Entry.Structure("rogues:village/taiga/barracks", smallWeight, limit))
                ))
//                new StructurePoolConfig.Entry("minecraft:village/snowy/houses", new ArrayList<>(Arrays.asList(
//                        new StructurePoolConfig.Entry.Structure("rogues:village/snowy/barracks", smallWeight, limit))
//                ))
        ));

    }
}