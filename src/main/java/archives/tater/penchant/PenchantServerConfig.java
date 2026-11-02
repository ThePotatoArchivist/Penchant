package archives.tater.penchant;

import folk.sisby.kaleido.api.WrappedConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.SerializedNameConvention;
import folk.sisby.kaleido.lib.quiltconfig.api.metadata.NamingSchemes;

@SerializedNameConvention(NamingSchemes.SNAKE_CASE)
public class PenchantServerConfig extends WrappedConfig {

    @Comment("Which modules should be enabled by default for new worlds.")
    @Comment("Note this can still be overridden per-world and will not affect existing worlds.")
    @Comment("This config is mainly intended for modpack developers. If you are an end user, prefer adjusting modules per-world using the vanilla datapack menu.")
    public ModuleDefaults moduleDefaults = new ModuleDefaults();
    public static class ModuleDefaults implements Section {

        @Comment("Remove mending & alter unbreaking")
        public boolean durabilityRework = true;

        @Comment("Larger bookshelf radius & allow chiseled bookshelves")
        public boolean bookshelfPlacement = true;

        @Comment("Enchanting via chiseled bookshelves & remove table RNG")
        public boolean tableRework = true;

        @Comment("Prevent applying enchanted books to equipment in anvil")
        public boolean noAnvilBooks = true;

        @Comment("Reorganize where enchantments are unlocked & found")
        public boolean lootRework = true;

        @Comment("Mobs always drop enchanted equipment")
        public boolean guaranteedDrops = false;

        @Comment("Librarians sell a different book each time")
        public boolean randomizedLibrarians = false;

    }

    public FallbackParameters fallbackParameters = new FallbackParameters();
    public static class FallbackParameters implements Section, PenchantFallbackConfig {
        public float xpCostMultiplier = 1;
        public float bookRequirementMultiplier = 2;
        public int bookRequirementConstant = -5;
        public float enchantmentPenchantibilityMultiplier = 1;
        public int enchantmentPenchantibilityConstant = 0;
        public float itemPenchantibilityMultiplier = 0.01f;
        public int minItemPenchantibility = 1;
        public int maxItemPenchantibility = 0;

        @Override
        public float xpCostMultiplier() {
            return xpCostMultiplier;
        }

        @Override
        public float bookRequirementMultiplier() {
            return bookRequirementMultiplier;
        }

        @Override
        public int bookRequirementConstant() {
            return bookRequirementConstant;
        }

        @Override
        public float enchantmentPenchantibilityMultiplier() {
            return enchantmentPenchantibilityMultiplier;
        }

        @Override
        public int enchantmentPenchantibilityConstant() {
            return enchantmentPenchantibilityConstant;
        }

        @Override
        public float itemPenchantibilityMultiplier() {
            return itemPenchantibilityMultiplier;
        }

        @Override
        public int minItemPenchantibility() {
            return minItemPenchantibility;
        }

        @Override
        public int maxItemPenchantibility() {
            return maxItemPenchantibility;
        }
    }

}
