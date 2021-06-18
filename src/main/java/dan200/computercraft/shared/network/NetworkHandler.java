/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2021. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.shared.network;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.shared.network.client.*;
import dan200.computercraft.shared.network.server.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.function.Function;
import java.util.function.Supplier;

public final class NetworkHandler
{
    public static SimpleChannel network;

    private NetworkHandler()
    {
    }

    public static void setup()
    {
        String version = ComputerCraftAPI.getInstalledVersion();
        network = NetworkRegistry.ChannelBuilder.named( new ResourceLocation( ComputerCraft.MOD_ID, "network" ) )
            .networkProtocolVersion( () -> version )
            .clientAcceptedVersions( version::equals ).serverAcceptedVersions( version::equals )
            .simpleChannel();

        // Server messages
        registerMainThread( 0, NetworkDirection.PLAY_TO_SERVER, ComputerActionServerMessage::new );
        registerMainThread( 1, NetworkDirection.PLAY_TO_SERVER, QueueEventServerMessage::new );
        registerMainThread( 2, NetworkDirection.PLAY_TO_SERVER, RequestComputerMessage::new );
        registerMainThread( 3, NetworkDirection.PLAY_TO_SERVER, KeyEventServerMessage::new );
        registerMainThread( 4, NetworkDirection.PLAY_TO_SERVER, MouseEventServerMessage::new );

        // Client messages
        registerMainThread( 10, NetworkDirection.PLAY_TO_CLIENT, ChatTableClientMessage::new );
        registerMainThread( 11, NetworkDirection.PLAY_TO_CLIENT, ComputerDataClientMessage::new );
        registerMainThread( 12, NetworkDirection.PLAY_TO_CLIENT, ComputerDeletedClientMessage::new );
        registerMainThread( 13, NetworkDirection.PLAY_TO_CLIENT, ComputerTerminalClientMessage::new );
        registerMainThread( 14, NetworkDirection.PLAY_TO_CLIENT, PlayRecordClientMessage.class, PlayRecordClientMessage::new );
        registerMainThread( 15, NetworkDirection.PLAY_TO_CLIENT, MonitorClientMessage.class, MonitorClientMessage::new );
        registerMainThread( 16, NetworkDirection.PLAY_TO_CLIENT, SpeakerPlayClientMessage.class, SpeakerPlayClientMessage::new );
        registerMainThread( 17, NetworkDirection.PLAY_TO_CLIENT, SpeakerStopClientMessage.class, SpeakerStopClientMessage::new );
        registerMainThread( 18, NetworkDirection.PLAY_TO_CLIENT, SpeakerMoveClientMessage.class, SpeakerMoveClientMessage::new );
    }

    public static void sendToPlayer( PlayerEntity player, NetworkMessage packet )
    {
        network.sendTo( packet, ((ServerPlayerEntity) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT );
    }

    public static void sendToAllPlayers( NetworkMessage packet )
    {
        for( ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers() )
        {
            sendToPlayer( player, packet );
        }
    }

    public static void sendToServer( NetworkMessage packet )
    {
        network.sendToServer( packet );
    }

    public static void sendToAllAround( NetworkMessage packet, World world, Vec3d pos, double range )
    {
        PacketDistributor.TargetPoint target = new PacketDistributor.TargetPoint( pos.x, pos.y, pos.z, range, world.getDimension().getType() );
        network.send( PacketDistributor.NEAR.with( () -> target ), packet );
    }

    public static void sendToAllTracking( NetworkMessage packet, Chunk chunk )
    {
        network.send( PacketDistributor.TRACKING_CHUNK.with( () -> chunk ), packet );
    }

    /**
     * /**
     * Register packet, and a thread-unsafe handler for it.
     *
     * @param <T>       The type of the packet to send.
     * @param id        The identifier for this packet type.
     * @param direction A network direction which will be asserted before any processing of this message occurs.
     * @param factory   The factory for this type of packet.
     */
    private static <T extends NetworkMessage> void registerMainThread( int id, NetworkDirection direction, Supplier<T> factory )
    {
        registerMainThread( id, direction, getType( factory ), buf -> {
            T instance = factory.get();
            instance.fromBytes( buf );
            return instance;
        } );
    }

    /**
     * /**
     * Register packet, and a thread-unsafe handler for it.
     *
     * @param <T>       The type of the packet to send.
     * @param type      The class of the type of packet to send.
     * @param id        The identifier for this packet type.
     * @param direction A network direction which will be asserted before any processing of this message occurs
     * @param decoder   The factory for this type of packet.
     */
    private static <T extends NetworkMessage> void registerMainThread( int id, NetworkDirection direction, Class<T> type, Function<PacketBuffer, T> decoder )
    {
        network.messageBuilder( type, id, direction )
            .encoder( NetworkMessage::toBytes )
            .decoder( decoder )
            .consumer( ( packet, contextSup ) -> {
                NetworkEvent.Context context = contextSup.get();
                context.enqueueWork( () -> packet.handle( context ) );
                context.setPacketHandled( true );
            } )
            .add();
    }

    @SuppressWarnings( "unchecked" )
    private static <T> Class<T> getType( Supplier<T> supplier )
    {
        return (Class<T>) supplier.get().getClass();
    }
}
