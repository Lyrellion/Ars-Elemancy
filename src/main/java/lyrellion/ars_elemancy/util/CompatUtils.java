package lyrellion.ars_elemancy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.function.Predicate;

public class CompatUtils {

    public static SlotResult getCurio(LivingEntity player, Predicate<ItemStack> predicate) {
        var lazy = CuriosApi.getCuriosInventory(player);
        SlotResult noResult = new SlotResult(null, ItemStack.EMPTY);
        if (lazy.isPresent()) {
            var curioInv = lazy.get();
                return curioInv.findFirstCurio(predicate).orElse(noResult);
        }
        return noResult;
    }


}
