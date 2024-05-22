package net.rogues.item;

import net.minecraft.util.Identifier;
import net.rogues.RoguesMod;
import net.spell_engine.api.item.trinket.SpellBooks;

import java.util.List;

public class Books {
    public static void register() {
        var books = List.of("rogue", "warrior");
        for (var name: books) {
            SpellBooks.createAndRegister(new Identifier(RoguesMod.NAMESPACE, name), Group.KEY);
        }
    }
}
