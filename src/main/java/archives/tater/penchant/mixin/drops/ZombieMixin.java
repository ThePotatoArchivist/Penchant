package archives.tater.penchant.mixin.drops;

import archives.tater.penchant.registry.PenchantFlag;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Mixin(Zombie.class)
public class ZombieMixin {
    @ModifyArg(
            method = "populateDefaultEquipmentSlots",
            at = @At(value = "INVOKE:LAST", target = "Lnet/minecraft/world/entity/monster/Zombie;setItemSlot(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;)V"),
            index = 1
    )
    private ItemStack spawnPickaxe(ItemStack original, @Local(argsOnly = true) RandomSource random) {
        return PenchantFlag.ZOMBIE_SPAWN_PICKAXE.isEnabled() && random.nextInt(4) == 0 ? new ItemStack(Items.IRON_PICKAXE) : original;
    }
}
