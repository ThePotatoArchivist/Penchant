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

        var tablePack = fabricDataGenerator.createBuiltinResourcePack(Penchant.TABLE_REWORK);
        tablePack.addProvider(TableBlockTagGenerator::new);
        tablePack.addProvider(FlagTagGenerator.generator(
                PenchantFlag.REWORKED_TABLE_MENU,
                PenchantFlag.LENIENT_BOOKSHELF_PLACEMENT,
                PenchantFlag.NO_ANVIL_BOOKS
        ));
        tablePack.addProvider(PackMetaGen.pack(Penchant.TABLE_REWORK));
	}
}
