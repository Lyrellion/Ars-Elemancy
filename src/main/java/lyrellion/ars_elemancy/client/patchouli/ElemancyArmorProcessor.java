package lyrellion.ars_elemancy.client.patchouli;


import com.hollingsworth.arsnouveau.common.items.data.ArmorPerkHolder;
import lyrellion.ars_elemancy.recipe.ElemancyArmorRecipe;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ElemancyArmorProcessor implements IComponentProcessor {

    private RecipeHolder<? extends ElemancyArmorRecipe> holder;

    @Override
    public void setup(Level level, IVariableProvider variables) {
        RecipeManager manager = level.getRecipeManager();
        String recipeID = variables.get("recipe", level.registryAccess()).asString();
        try {
            holder = (RecipeHolder<? extends ElemancyArmorRecipe>) manager.byKey(ResourceLocation.tryParse(recipeID)).orElse(null);
        } catch (Exception ignored) {
        }
    }

    @Override
    public @NotNull IVariable process(Level level, String key) {
        if (holder == null) return IVariable.empty();

        var recipe = holder.value();
        return switch (key) {
            case "reagent" -> IVariable.wrapList(Arrays.stream(recipe.reagent().getItems()).map(i -> IVariable.from(i, level.registryAccess())).collect(Collectors.toList()), level.registryAccess());
            case "recipe" -> IVariable.wrap(holder.id().toString(), level.registryAccess());
            case "tier" -> IVariable.wrap(recipe.getOutputComponent().getString(), level.registryAccess());
            case "output" -> {
                var result = recipe.result().copy();
                result.set(DataComponentRegistry.ARMOR_PERKS, new ArmorPerkHolder().setTier(3));
                yield IVariable.from(result, level.registryAccess());
            }
            case "footer" -> IVariable.wrap(recipe.result().getItem().getDescriptionId(), level.registryAccess());
            default -> IVariable.empty();
        };
    }

}
