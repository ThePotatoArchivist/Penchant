package archives.tater.penchant;

import archives.tater.penchant.datagen.DurabilityEnchantmentGenerator;
import archives.tater.penchant.datagen.DurabilityEnchantmentTagGenerator;
import archives.tater.penchant.datagen.TableBlockTagGenerator;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class PenchantDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var durabilityPack = fabricDataGenerator.createBuiltinResourcePack(Penchant.DURABILITY_REWORK);
        durabilityPack.addProvider(DurabilityEnchantmentGenerator::new);
        durabilityPack.addProvider(DurabilityEnchantmentTagGenerator::new);

        var tablePack = fabricDataGenerator.createBuiltinResourcePack(Penchant.TABLE_REWORK);
        tablePack.addProvider(TableBlockTagGenerator::new);
	}
}
