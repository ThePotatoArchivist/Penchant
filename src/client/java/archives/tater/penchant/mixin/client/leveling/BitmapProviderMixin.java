package archives.tater.penchant.mixin.client.leveling;

import archives.tater.penchant.client.FontUtils;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.client.gui.font.providers.BitmapProvider;
import net.minecraft.resources.ResourceLocation;

// This was quite a hack I'm sorry
@Mixin(BitmapProvider.Definition.class)
public class BitmapProviderMixin {
    @Shadow
    @Final
    private ResourceLocation file;

    @ModifyArg(
            method = "load",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/font/providers/BitmapProvider$Glyph;<init>(FLcom/mojang/blaze3d/platform/NativeImage;IIIIII)V"),
            index = 6
    )
    private int disableSpacing(int width) {
        return file.equals(FontUtils.FONT_TEXTURE) ? width - 1 : width;
    }
}
