package archives.tater.penchant.datagen;

import archives.tater.hcapi.api.provider.DataHolderComponentProvider;
import archives.tater.penchant.registry.PenchantComponents;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.concurrent.CompletableFuture;

public class EnchantmentComponentGenerator extends DataHolderComponentProvider {
    public EnchantmentComponentGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void generate(HolderLookup.Provider registries) {
        builder(Enchantments.AQUA_AFFINITY).newPatch()
                .set(PenchantComponents.EXPERIENCE_COST, 4)
                .set(PenchantComponents.BOOK_REQUIREMENT, 8)
                .set(PenchantComponents.PENCHANTIBILITY, Enchantment.constantCost(20));
    }

    @Override
    public String getName() {
        return "Enchantment Components";
    }
}
