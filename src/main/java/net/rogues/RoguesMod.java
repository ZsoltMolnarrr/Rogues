package net.rogues;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.rogues.config.Default;
import net.rogues.item.Group;
import net.rogues.item.Weapons;
import net.rogues.item.armor.Armors;
import net.spell_engine.api.item.ItemConfig;
import net.tinyconfig.ConfigManager;

public class RoguesMod implements ModInitializer {

    public static final String NAMESPACE = "rogues";

    public static ConfigManager<ItemConfig> itemConfig = new ConfigManager<>
            ("items", Default.itemConfig)
            .builder()
            .setDirectory(NAMESPACE)
            .sanitize(true)
            .build();


    @Override
    public void onInitialize() {
        itemConfig.refresh();
        Group.ROGUES = FabricItemGroup.builder()
                .icon(() -> new ItemStack(Armors.RogueArmorSet_t2.head))
                .displayName(Text.translatable("itemGroup." + NAMESPACE + ".general"))
                .build();
        Registry.register(Registries.ITEM_GROUP, Group.KEY, Group.ROGUES);
        Weapons.register(itemConfig.value.weapons);
        Armors.register(itemConfig.value.armor_sets);
        itemConfig.save();
    }
}
