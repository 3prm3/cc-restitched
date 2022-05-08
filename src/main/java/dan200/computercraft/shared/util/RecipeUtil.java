/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.shared.util;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import java.util.Map;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;

// TODO: Replace some things with Forge??

public final class RecipeUtil
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private RecipeUtil() {}

    public static class ShapedTemplate
    {
        public final int width;
        public final int height;
        public final DefaultedList<Ingredient> ingredients;

        public ShapedTemplate( int width, int height, DefaultedList<Ingredient> ingredients )
        {
            this.width = width;
            this.height = height;
            this.ingredients = ingredients;
        }
    }

    public static ShapedTemplate getTemplate( JsonObject json )
    {
        Map<Character, Ingredient> ingMap = Maps.newHashMap();
        for( Map.Entry<String, JsonElement> entry : JsonHelper.getObject( json, "key" ).entrySet() )
        {
            if( entry.getKey().length() != 1 )
            {
                throw new JsonSyntaxException( "Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only)." );
            }
            if( " ".equals( entry.getKey() ) )
            {
                throw new JsonSyntaxException( "Invalid key entry: ' ' is a reserved symbol." );
            }

            ingMap.put( entry.getKey().charAt( 0 ), Ingredient.fromJson( entry.getValue() ) );
        }

        ingMap.put( ' ', Ingredient.EMPTY );

        JsonArray patternJ = JsonHelper.getArray( json, "pattern" );

        if( patternJ.size() == 0 )
        {
            throw new JsonSyntaxException( "Invalid pattern: empty pattern not allowed" );
        }

        String[] pattern = new String[patternJ.size()];
        for( int x = 0; x < pattern.length; x++ )
        {
            String line = JsonHelper.asString( patternJ.get( x ), "pattern[" + x + "]" );
            if( x > 0 && pattern[0].length() != line.length() )
            {
                throw new JsonSyntaxException( "Invalid pattern: each row must  be the same width" );
            }
            pattern[x] = line;
        }

        int width = pattern[0].length();
        int height = pattern.length;
        DefaultedList<Ingredient> ingredients = DefaultedList.ofSize( width * height, Ingredient.EMPTY );

        Set<Character> missingKeys = Sets.newHashSet( ingMap.keySet() );
        missingKeys.remove( ' ' );

        int i = 0;
        for( String line : pattern )
        {
            for( char chr : line.toCharArray() )
            {
                Ingredient ing = ingMap.get( chr );
                if( ing == null )
                {
                    throw new JsonSyntaxException( "Pattern references symbol '" + chr + "' but it's not defined in the key" );
                }
                ingredients.set( i++, ing );
                missingKeys.remove( chr );
            }
        }

        if( !missingKeys.isEmpty() )
        {
            throw new JsonSyntaxException( "Key defines symbols that aren't used in pattern: " + missingKeys );
        }

        return new ShapedTemplate( width, height, ingredients );
    }

    public static ComputerFamily getFamily( JsonObject json, String name )
    {
        String familyName = JsonHelper.getString( json, name );
        for( ComputerFamily family : ComputerFamily.values() )
        {
            if( family.name().equalsIgnoreCase( familyName ) ) return family;
        }

        throw new JsonSyntaxException( "Unknown computer family '" + familyName + "' for field " + name );
    }

    public static void setNbt( ItemStack itemStack, JsonObject result )
    {
        JsonElement nbtElement = result.get( "nbt" );
        if( nbtElement != null )
        {
            try
            {
                itemStack.setNbt( StringNbtReader.parse( nbtElement.isJsonObject() ? GSON.toJson( nbtElement ) : JsonHelper.asString( nbtElement, "nbt" ) ) );
            }
            catch( CommandSyntaxException e )
            {
                throw new JsonSyntaxException( "Invalid NBT entry: " + e.getMessage() );
            }
        }
    }
}
