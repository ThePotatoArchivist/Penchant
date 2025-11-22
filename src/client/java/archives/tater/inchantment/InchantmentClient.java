package archives.tater.inchantment;

import net.fabricmc.api.ClientModInitializer;

public class InchantmentClient implements ClientModInitializer {
//    private static KeyMapping keybind(Identifier id, int key, KeyMapping.Category category) {
//        return KeyBindingHelper.registerKeyBinding(new KeyMapping(makeDescriptionId("key", id), Type.KEYSYM, key, category));
//    }
//
//    private static final KeyMapping.Category INCHANTMENT_CATEGORY = KeyMapping.Category.register(Inchantment.id(Inchantment.MOD_ID));
//    public static final KeyMapping SHOW_PROGRESS_KEYBIND = keybind(
//            Inchantment.id("show_progress"),
//            InputConstants.KEY_LSHIFT,
//            INCHANTMENT_CATEGORY
//    );

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
	}
}