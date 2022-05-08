/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.shared.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import javax.annotation.Nonnull;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public final class ImpostorShapelessRecipe extends ShapelessRecipe
{
    private final String group;

    private ImpostorShapelessRecipe( @Nonnull Identifier id, @Nonnull String group, @Nonnull ItemStack result, DefaultedList<Ingredient> ingredients )
    {
        super( id, group, result, ingredients );
        this.group = group;
    }

    @Nonnull
    @Override
    public String getGroup()
    {
        return group;
    }

    @Override
    public boolean matches( @Nonnull CraftingInventory inv, @Nonnull World world )
    {
        return false;
    }

    @Nonnull
    @Override
    public ItemStack craft( @Nonnull CraftingInventory inventory )
    {
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return SERIALIZER;
    }

    public static final RecipeSerializer<ImpostorShapelessRecipe> SERIALIZER = new RecipeSerializer<ImpostorShapelessRecipe>()
    {
        @Nonnull
        @Override
        public ImpostorShapelessRecipe read( @Nonnull Identifier id, @Nonnull JsonObject json )
        {
            String s = JsonHelper.getString( json, "group", "" );
            DefaultedList<Ingredient> ingredients = readIngredients( JsonHelper.getArray( json, "ingredients" ) );

            if( ingredients.isEmpty() ) throw new JsonParseException( "No ingredients for shapeless recipe" );
            if( ingredients.size() > 9 )
            {
                throw new JsonParseException( "Too many ingredients for shapeless recipe the max is 9" );
            }
            JsonObject resultObject = JsonHelper.getObject( json, "result" );
            ItemStack itemStack = ShapedRecipe.outputFromJson( resultObject );
            RecipeUtil.setNbt( itemStack, resultObject );
            return new ImpostorShapelessRecipe( id, s, itemStack, ingredients );
        }

        private DefaultedList<Ingredient> readIngredients( JsonArray arrays )
        {
            DefaultedList<Ingredient> items = DefaultedList.of();
            for( int i = 0; i < arrays.size(); ++i )
            {
                Ingredient ingredient = Ingredient.fromJson( arrays.get( i ) );
                if( !ingredient.isEmpty() ) items.add( ingredient );
            }

            return items;
        }

        @Override
        public ImpostorShapelessRecipe read( @Nonnull Identifier id, PacketByteBuf buffer )
        {
            String s = buffer.readString( 32767 );
            int i = buffer.readVarInt();
            DefaultedList<Ingredient> items = DefaultedList.ofSize( i, Ingredient.EMPTY );

            for( int j = 0; j < items.size(); j++ ) items.set( j, Ingredient.fromPacket( buffer ) );
            ItemStack result = buffer.readItemStack();

            return new ImpostorShapelessRecipe( id, s, result, items );
        }

        @Override
        public void toNetwork( @Nonnull PacketByteBuf buffer, @Nonnull ImpostorShapelessRecipe recipe )
        {
            buffer.writeString( recipe.getGroup() );
            buffer.writeVarInt( recipe.getIngredients().size() );

            for( Ingredient ingredient : recipe.getIngredients() ) ingredient.write( buffer );
            buffer.writeItemStack( recipe.getOutput() );
        }
    };
}
