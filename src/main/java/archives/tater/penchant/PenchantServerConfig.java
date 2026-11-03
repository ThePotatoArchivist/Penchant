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

    @Comment("Used to generate the fallback values for Penchant's components")
    @Comment("Will not apply to any components that are set directly")
    @Comment("Requires server restart/world relog")
    public FallbackParameters fallbackParameters = new FallbackParameters();
    public static class FallbackParameters implements Section {

        @Comment("Multiplied by enchantment.anvil_cost to calculate experience_cost")
        public float xpCostMultiplier = 1;

        @Comment("Multiplied by enchantment.min_cost(1) to calculate book_requirement")
        public float bookRequirementMultiplier = 2;

        @Comment("Constant offset for book_requirement")
        public int bookRequirementConstant = -5;

        @Comment("Multiplied by enchantment.max_cost.per_level_above_first to calculate penchantibility.per_level_above_first")
        public float enchantmentPenchantibilityMultiplier = 1;

        @Comment("Multiplied by enchantment.max_cost.base to calculate penchantibility.base")
        public int enchantmentPenchantibilityConstant = 0;

        @Comment("Multiplied by item.max_damage to calculate penchantibility")
        @Comment("Items in #penchant:half_default_penchantibility will use half this this value")
        public float itemPenchantibilityMultiplier = 0.01f;

        @Comment("Minimum value for item penchantibility")
        public int minItemPenchantibility = 1;

        @Comment("Maximum value for item penchantibility")
        @Comment("0 means no maximum")
        public int maxItemPenchantibility = 0;

    }

}
