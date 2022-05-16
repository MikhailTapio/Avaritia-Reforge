package nova.committee.avaritia.common.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.registries.ForgeRegistryEntry;
import nova.committee.avaritia.init.registry.ModRecipe;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/2 17:40
 * Version: 1.0
 */
public class CompressorRecipe implements ISpecialRecipe, ICompressorRecipe {
    private final ResourceLocation recipeId;
    private final NonNullList<Ingredient> inputs;
    private final ItemStack output;
    private final int inputCount;


    public CompressorRecipe(ResourceLocation recipeId, Ingredient input, ItemStack output, int inputCount) {
        this.recipeId = recipeId;
        this.inputs = NonNullList.of(Ingredient.EMPTY, input);
        this.output = output;
        this.inputCount = inputCount;

    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return this.output;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.inputs;
    }

    @Override
    public ResourceLocation getId() {
        return this.recipeId;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipe.COMPRESSOR_RECIPE;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipe.RecipeTypes.COMPRESSOR;
    }

    @Override
    public ItemStack assemble(IItemHandler inventory) {
        return this.output.copy();
    }

    @Override
    public ItemStack assemble(Container inv) {
        return this.output.copy();
    }

    @Override
    public boolean matches(IItemHandler inventory) {
        var input = inventory.getStackInSlot(0);

        return this.inputs.get(0).test(input);
    }

    @Override
    public boolean matches(Container inv, Level level) {
        return this.matches(new InvWrapper(inv));
    }

    @Override
    public int getInputCount() {
        return this.inputCount;
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<CompressorRecipe> {
        @Override
        public CompressorRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            var input = Ingredient.fromJson(json.getAsJsonObject("ingredient"));
            var output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            int inputCount = GsonHelper.getAsInt(json, "inputCount", 10000);

            return new CompressorRecipe(recipeId, input, output, inputCount);
        }

        @Override
        public CompressorRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            var input = Ingredient.fromNetwork(buffer);
            var output = buffer.readItem();
            int inputCount = buffer.readInt();

            return new CompressorRecipe(recipeId, input, output, inputCount);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CompressorRecipe recipe) {
            recipe.inputs.get(0).toNetwork(buffer);
            buffer.writeItem(recipe.output);
            buffer.writeInt(recipe.inputCount);

        }
    }
}