package archives.tater.penchant;

import archives.tater.penchant.datagen.*;
import archives.tater.penchant.registry.PenchantFlag;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PenchantDataGenerator implements DataGeneratorEntrypoint {
    private static final Logger log = LoggerFactory.getLogger(PenchantDataGenerator.class);

    private static FabricDataGenerator.Pack createPack(FabricDataGenerator fabricDataGenerator, Identifier id) {
        var pack = fabricDataGenerator.createBuiltinResourcePack(id);
        pack.addProvider(PackMetaGen.pack(id));
        return pack;
    }

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();
        pack.addProvider(FlagTagGenerator.generator());
        pack.addProvider(EnchantmentTagGenerator::new);
        pack.addProvider(BlockTagGenerator::new);

        var durabilityPack = createPack(fabricDataGenerator, Penchant.DURABILITY_REWORK);
        durabilityPack.addProvider(DurabilityEnchantmentGenerator::new);
        durabilityPack.addProvider(DurabilityEnchantmentTagGenerator::new);

        var bookshelfPack = createPack(fabricDataGenerator, Penchant.BOOKSHELF_PLACEMENT);
        bookshelfPack.addProvider(FlagTagGenerator.generator(PenchantFlag.LENIENT_BOOKSHELF_PLACEMENT));
        bookshelfPack.addProvider(BookshelfBlockTagGenerator::new);

        var anvilPack = createPack(fabricDataGenerator, Penchant.NO_ANVIL_BOOKS);
        anvilPack.addProvider(FlagTagGenerator.generator(PenchantFlag.NO_ANVIL_BOOKS));

        var tablePack = createPack(fabricDataGenerator, Penchant.TABLE_REWORK);
        tablePack.addProvider(FlagTagGenerator.generator(PenchantFlag.REWORKED_TABLE_MENU));
        tablePack.addProvider(TableAdvancementGenerator::new);

        var lootPack = createPack(fabricDataGenerator, Penchant.LOOT_REWORK);
        lootPack.addProvider(FlagTagGenerator.generator(PenchantFlag.ZOMBIE_SPAWN_PICKAXE));
        lootPack.addProvider(LootModificationGenerator::new);
        lootPack.addProvider(LootEnchantmentTagGenerator::new);
        lootPack.addProvider(LootAdvancementGenerator::new);
        lootPack.addProvider(LootEnchantmentProviderGenerator::new);

        var dropPack = createPack(fabricDataGenerator, Penchant.GUARANTEED_DROPS);
        dropPack.addProvider(FlagTagGenerator.generator(PenchantFlag.GUARANTEED_ENCHANTED_DROP, PenchantFlag.GUARANTEED_TRIDENT_DROP));

        var noCursePack = createPack(fabricDataGenerator, Penchant.REDUCED_CURSES);
        noCursePack.addProvider(CurseEnchantmentTagGenerator::new);
	}
}
