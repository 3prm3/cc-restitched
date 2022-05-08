/*
 * This file is part of the public ComputerCraft API - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. This API may be redistributed unmodified and in full only.
 * For help using the API, and posting your mods, visit the forums at computercraft.info.
 */
package dan200.computercraft.api.pocket;

import dan200.computercraft.shared.util.NonNullSupplier;
import javax.annotation.Nonnull;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import java.util.function.Supplier;

/**
 * A base class for {@link IPocketUpgrade}s.
 *
 * One does not have to use this, but it does provide a convenient template.
 */
public abstract class AbstractPocketUpgrade implements IPocketUpgrade
{
    private final Identifier id;
    private final String adjective;
    private final NonNullSupplier<ItemStack> stack;

    protected AbstractPocketUpgrade( Identifier id, String adjective, NonNullSupplier<ItemStack> stack )
    {
        this.id = id;
        this.adjective = adjective;
        this.stack = stack;
    }

    protected AbstractPocketUpgrade( Identifier id, NonNullSupplier<ItemStack> item )
    {
        this( id, Util.createTranslationKey( "upgrade", id ) + ".adjective", item );
    }

    protected AbstractPocketUpgrade( Identifier id, String adjective, ItemStack stack )
    {
        this( id, adjective, () -> stack );
    }

    protected AbstractPocketUpgrade( Identifier id, ItemStack stack )
    {
        this( id, () -> stack );
    }

    protected AbstractPocketUpgrade( Identifier id, String adjective, ItemConvertible item )
    {
        this( id, adjective, new CachedStack( () -> item ) );
    }

    protected AbstractPocketUpgrade( Identifier id, ItemConvertible item )
    {
        this( id, new CachedStack( () -> item ) );
    }

    protected AbstractPocketUpgrade( Identifier id, String adjective, Supplier<? extends ItemConvertible> item )
    {
        this( id, adjective, new CachedStack( item ) );
    }

    protected AbstractPocketUpgrade( Identifier id, Supplier<? extends ItemConvertible> item )
    {
        this( id, new CachedStack( item ) );
    }

    @Nonnull
    @Override
    public final Identifier getUpgradeID()
    {
        return id;
    }

    @Nonnull
    @Override
    public final String getUnlocalisedAdjective()
    {
        return adjective;
    }

    @Nonnull
    @Override
    public final ItemStack getCraftingItem()
    {
        return stack.get();
    }

    /**
     * Caches the construction of an item stack.
     *
     * @see dan200.computercraft.api.turtle.AbstractTurtleUpgrade For explanation of this class.
     */
    private static final class CachedStack implements NonNullSupplier<ItemStack>
    {
        private final Supplier<? extends ItemConvertible> provider;
        private Item item;
        private ItemStack stack;

        CachedStack( Supplier<? extends ItemConvertible> provider )
        {
            this.provider = provider;
        }

        @Nonnull
        @Override
        public ItemStack get()
        {
            Item item = provider.get().asItem();
            if( item == this.item && stack != null ) return stack;
            return stack = new ItemStack( this.item = item );
        }
    }
}
