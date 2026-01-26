package archives.tater.penchant;

import archives.tater.penchant.datagen.*;
import archives.tater.penchant.registry.PenchantFlag;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class PenchantDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();
        pack.addProvider(FlagTagGenerator.generator());
        pack.addProvider(EnchantmentTagGenerator::new);
        pack.addProvider(BlockTagGenerator::new);

        var durabilityPack = fabricDataGenerator.createBuiltinResourcePack(Penchant.DURABILITY_REWORK);
        durabilityPack.addProvider(DurabilityEnchantmentGenerator::new);
        durabilityPack.addProvider(DurabilityEnchantmentTagGenerator::new);
        durabilityPack.addProvider(PackMetaGen.pack(Penchant.DURABILITY_REWORK));

        var bookshelfPack = fabricDataGenerator.createBuiltinResourcePack(Penchant.BOOKSHELF_PLACEMENT);
        bookshelfPack.addProvider(FlagTagGenerator.generator(PenchantFlag.LENIENT_BOOKSHELF_PLACEMENT));
        bookshelfPack.addProvider(BookshelfBlockTagGenerator::new);
        bookshelfPack.addProvider(PackMetaGen.pack(Penchant.BOOKSHELF_PLACEMENT));

        var anvilPack = fabricDataGenerator.createBuiltinResourcePack(Penchant.NO_ANVIL_BOOKS);
        anvilPack.addProvider(FlagTagGenerator.generator(PenchantFlag.NO_ANVIL_BOOKS));
        anvilPack.addProvider(PackMetaGen.pack(Penchant.NO_ANVIL_BOOKS));

        var tablePack = fabricDataGenerator.createBuiltinResourcePack(Penchant.TABLE_REWORK);
        tablePack.addProvider(FlagTagGenerator.generator(PenchantFlag.REWORKED_TABLE_MENU));
        tablePack.addProvider(TableAdvancementGenerator::new);
        tablePack.addProvider(PackMetaGen.pack(Penchant.TABLE_REWORK));

        var lootPack = fabricDataGenerator.createBuiltinResourcePack(Penchant.LOOT_REWORK);
        lootPack.addProvider(LootEnchantmentTagGenerator::new);
        lootPack.addProvider(LootAdvancementGenerator::new);
        lootPack.addProvider(PackMetaGen.pack(Penchant.LOOT_REWORK));
	}
}
