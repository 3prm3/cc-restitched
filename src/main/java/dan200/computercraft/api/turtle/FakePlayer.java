/*
 * This file is part of the public ComputerCraft API - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. This API may be redistributed unmodified and in full only.
 * For help using the API, and posting your mods, visit the forums at computercraft.info.
 */
package dan200.computercraft.api.turtle;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.shared.util.FakeNetHandler;
import javax.annotation.Nullable;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.MessageType;
import net.minecraft.network.Packet;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TradeOfferList;
import java.util.Collection;
import java.util.OptionalInt;
import java.util.UUID;

/**
 * A wrapper for {@link ServerPlayerEntity} which denotes a "fake" player.
 *
 * Please note that this does not implement any of the traditional fake player behaviour. It simply exists to prevent me passing in normal players.
 */
public abstract class FakePlayer extends ServerPlayerEntity
{
    public FakePlayer( ServerWorld world, GameProfile gameProfile )
    {
        super( world.getServer(), world, gameProfile );
        networkHandler = new FakeNetHandler( this );
    }

    // region Direct networkHandler access
    @Override
    public void enterCombat()
    {
    }

    @Override
    public void endCombat()
    {
    }

    @Override
    public void tick()
    {
    }

    @Override
    public void playerTick()
    {
    }

    @Override
    public void onDeath( DamageSource damage )
    {
    }

    @Override
    public Entity moveToWorld( ServerWorld destination )
    {
        return this;
    }

    @Override
    public void wakeUp( boolean bl, boolean updateSleepingPlayers )
    {

    }

    @Override
    public boolean startRiding( Entity entity, boolean flag )
    {
        return false;
    }

    @Override
    public void stopRiding()
    {
    }

    @Override
    public void openEditSignScreen( SignBlockEntity tile )
    {
    }

    @Override
    public OptionalInt openHandledScreen( @Nullable NamedScreenHandlerFactory container )
    {
        return OptionalInt.empty();
    }

    @Override
    public void sendTradeOffers( int id, TradeOfferList list, int level, int experience, boolean levelled, boolean refreshable )
    {
    }

    @Override
    public void openHorseInventory( HorseBaseEntity horse, Inventory inventory )
    {
    }

    @Override
    public void useBook( ItemStack stack, Hand hand )
    {
    }

    @Override
    public void openCommandBlockScreen( CommandBlockBlockEntity block )
    {
    }

    //    @Override
    //    public void onSlotUpdate( ScreenHandler container, int slot, ItemStack stack )
    //    {
    //    }
    //
    //    @Override
    //    public void onHandlerRegistered( ScreenHandler container, DefaultedList<ItemStack> defaultedList )
    //    {
    //    }
    //
    //    @Override
    //    public void onPropertyUpdate( ScreenHandler container, int key, int value )
    //    {
    //    }

    @Override
    public void closeHandledScreen()
    {
    }

    //    @Override
    //    public void updateCursorStack()
    //    {
    //    }

    @Override
    public int unlockRecipes( Collection<Recipe<?>> recipes )
    {
        return 0;
    }

    // Indirect
    @Override
    public int lockRecipes( Collection<Recipe<?>> recipes )
    {
        return 0;
    }

    @Override
    public void sendMessage( Text textComponent, boolean status )
    {
    }

    @Override
    protected void consumeItem()
    {
    }

    @Override
    public void lookAt( EntityAnchorArgumentType.EntityAnchor anchor, Vec3d vec3d )
    {
    }

    @Override
    public void lookAtEntity( EntityAnchorArgumentType.EntityAnchor self, Entity entity, EntityAnchorArgumentType.EntityAnchor target )
    {
    }

    @Override
    protected void onStatusEffectApplied( StatusEffectInstance statusEffectInstance, @Nullable Entity source )
    {
    }

    @Override
    protected void onStatusEffectUpgraded( StatusEffectInstance statusEffectInstance, boolean particles, @Nullable Entity source )
    {
    }

    @Override
    protected void onStatusEffectRemoved( StatusEffectInstance statusEffectInstance )
    {
    }

    @Override
    public void requestTeleport( double x, double y, double z )
    {
    }

    //    @Override
    //    public void setGameMode( GameMode gameMode )
    //    {
    //    }

    @Override
    public void sendMessage( Text message, MessageType type, UUID senderUuid )
    {

    }

    @Override
    public String getIp()
    {
        return "[Fake Player]";
    }

    //    @Override
    //    public void sendResourcePackUrl( String url, String hash )
    //    {
    //    }

    //    @Override
    //    public void onStoppedTracking( Entity entity )
    //    {
    //    }

    @Override
    public void setCameraEntity( Entity entity )
    {
    }

    @Override
    public void teleport( ServerWorld serverWorld, double x, double y, double z, float pitch, float yaw )
    {
    }

    @Override
    public void sendInitialChunkPackets( ChunkPos chunkPos, Packet<?> packet )
    {
    }

    @Override
    public void sendUnloadChunkPacket( ChunkPos chunkPos )
    {
    }

    @Override
    public void playSound( SoundEvent soundEvent, SoundCategory soundCategory, float volume, float pitch )
    {
    }
}
