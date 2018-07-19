package org.jibble.pircbot.api;

import org.jibble.pircbot.DccChat;
import org.jibble.pircbot.DccFileTransfer;
import org.jibble.pircbot.beans.User;

/**
 *  Interface for IRC event handler implementations.
 * 
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
public interface IIrcEventHandler {


    /**
     * This method is called once the PircBot has successfully connected to
     * the IRC server.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @since PircBot 0.9.6
     */
    void onConnect();


    /**
     * This method carries out the actions to be performed when the PircBot
     * gets disconnected.  This may happen if the PircBot quits from the
     * server, or if the connection is unexpectedly lost.
     *  <p>
     * Disconnection from the IRC server is detected immediately if either
     * we or the server close the connection normally. If the connection to
     * the server is lost, but neither we nor the server have explicitly closed
     * the connection, then it may take a few minutes to detect (this is
     * commonly referred to as a "ping timeout").
     *  <p>
     * If you wish to get your IRC pircBot to automatically rejoin a server after
     * the connection has been lost, then this is probably the ideal method to
     * override to implement such functionality.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     */
    void onDisconnect();






    /**
     * This method is called when we receive a numeric response from the
     * IRC server.
     *  <p>
     * Numerics in the range from 001 to 099 are used for client-server
     * connections only and should never travel between servers.  Replies
     * generated in response to commands are found in the range from 200
     * to 399.  Error replies are found in the range from 400 to 599.
     *  <p>
     * For example, we can use this method to discover the topic of a
     * channel when we join it.  If we join the channel #test which
     * has a topic of &quot;I am King of Test&quot; then the response
     * will be &quot;<code>PircBot #test :I Am King of Test</code>&quot;
     * with a code of 332 to signify that this is a topic.
     * (This is just an example - note that overriding the
     * <code>onTopic</code> method is an easier way of finding the
     * topic for a channel). Check the IRC RFC for the full list of other
     * command response codes.
     *  <p>
     * PircBot implements the interface ReplyConstants, which contains
     * contstants that you may find useful here.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @param code The three-digit numerical code for the response.
     * @param response The full response from the IRC server.
     *
     * @see ReplyConstants
     */
    void onServerResponse(int code, String response);


    /**
     * This method is called when we receive a user list from the server
     * after joining a channel.
     *  <p>
     * Shortly after joining a channel, the IRC server sends a list of all
     * users in that channel. The PircBot collects this information and
     * calls this method as soon as it has the full list.
     *  <p>
     * To obtain the nick of each user in the channel, call the getNick()
     * method on each User object in the array.
     *  <p>
     * At a later time, you may call the getUsers method to obtain an
     * up to date list of the users in the channel.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @since PircBot 1.0.0
     *
     * @param channel The name of the channel.
     * @param users An array of User objects belonging to this channel.
     *
     * @see User
     */
    void onUserList(String channel, User[] users);


    /**
     * This method is called whenever a message is sent to a channel.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @param channel The channel to which the message was sent.
     * @param sender The nick of the person who sent the message.
     * @param login The login of the person who sent the message.
     * @param hostname The hostname of the person who sent the message.
     * @param message The actual message sent to the channel.
     */
    void onMessage(String channel, String sender, String login, String hostname, String message);


    /**
     * This method is called whenever a private message is sent to the PircBot.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @param sender The nick of the person who sent the private message.
     * @param login The login of the person who sent the private message.
     * @param hostname The hostname of the person who sent the private message.
     * @param message The actual message.
     */
    void onPrivateMessage(String sender, String login, String hostname, String message);


    /**
     * This method is called whenever an ACTION is sent from a user.  E.g.
     * such events generated by typing "/me goes shopping" in most IRC clients.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @param sender The nick of the user that sent the action.
     * @param login The login of the user that sent the action.
     * @param hostname The hostname of the user that sent the action.
     * @param target The target of the action, be it a channel or our nick.
     * @param action The action carried out by the user.
     */
    void onAction(String sender, String login, String hostname, String target, String action);


    /**
     * This method is called whenever we receive a notice.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @param sourceNick The nick of the user that sent the notice.
     * @param sourceLogin The login of the user that sent the notice.
     * @param sourceHostname The hostname of the user that sent the notice.
     * @param target The target of the notice, be it our nick or a channel name.
     * @param notice The notice message.
     */
    void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice);


    /**
     * This method is called whenever someone (possibly us) joins a channel
     * which we are on.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @param channel The channel which somebody joined.
     * @param sender The nick of the user who joined the channel.
     * @param login The login of the user who joined the channel.
     * @param hostname The hostname of the user who joined the channel.
     */
    void onJoin(String channel, String sender, String login, String hostname);


    /**
     * This method is called whenever someone (possibly us) parts a channel
     * which we are on.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @param channel The channel which somebody parted from.
     * @param sender The nick of the user who parted from the channel.
     * @param login The login of the user who parted from the channel.
     * @param hostname The hostname of the user who parted from the channel.
     */
    void onPart(String channel, String sender, String login, String hostname);


    /**
     * This method is called whenever someone (possibly us) changes nick on any
     * of the channels that we are on.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @param oldNick The old nick.
     * @param login The login of the user.
     * @param hostname The hostname of the user.
     * @param newNick The new nick.
     */
    void onNickChange(String oldNick, String login, String hostname, String newNick);


    /**
     * This method is called whenever someone (possibly us) is kicked from
     * any of the channels that we are in.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @param channel The channel from which the recipient was kicked.
     * @param kickerNick The nick of the user who performed the kick.
     * @param kickerLogin The login of the user who performed the kick.
     * @param kickerHostname The hostname of the user who performed the kick.
     * @param recipientNick The unfortunate recipient of the kick.
     * @param reason The reason given by the user who performed the kick.
     */
    void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason);


    /**
     * This method is called whenever someone (possibly us) quits from the
     * server.  We will only observe this if the user was in one of the
     * channels to which we are connected.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @param sourceNick The nick of the user that quit from the server.
     * @param sourceLogin The login of the user that quit from the server.
     * @param sourceHostname The hostname of the user that quit from the server.
     * @param reason The reason given for quitting the server.
     */
    void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason);


    /**
     * This method is called whenever a user sets the topic, or when
     * PircBot joins a new channel and discovers its topic.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @param channel The channel that the topic belongs to.
     * @param topic The topic for the channel.
     *
     * @deprecated As of 1.2.0, replaced by {@link #onTopic(String,String,String,long,boolean)}
     */
    void onTopic(String channel, String topic);


    /**
     * This method is called whenever a user sets the topic, or when
     * PircBot joins a new channel and discovers its topic.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @param channel The channel that the topic belongs to.
     * @param topic The topic for the channel.
     * @param setBy The nick of the user that set the topic.
     * @param date When the topic was set (milliseconds since the epoch).
     * @param changed True if the topic has just been changed, false if
     *                the topic was already there.
     *
     */
    void onTopic(String channel, String topic, String setBy, long date, boolean changed);


    /**
     * After calling the listChannels() method in PircBot, the server
     * will start to send us information about each channel on the
     * server.  You may override this method in order to receive the
     * information about each channel as soon as it is received.
     *  <p>
     * Note that certain channels, such as those marked as hidden,
     * may not appear in channel listings.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @param channel The name of the channel.
     * @param userCount The number of users visible in this channel.
     * @param topic The topic for this channel.
     *
     * @see #listChannels() listChannels
     */
    void onChannelInfo(String channel, int userCount, String topic);

    
    
    
    
    
    
    /**
     * Called when a user (possibly us) gets voice status granted in a channel.
     *  <p>
     * This is a type of mode change and is also passed to the onMode
     * method in the PircBot class.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @since PircBot 0.9.5
     *
     * @param channel The channel in which the mode change took place.
     * @param sourceNick The nick of the user that performed the mode change.
     * @param sourceLogin The login of the user that performed the mode change.
     * @param sourceHostname The hostname of the user that performed the mode change.
     * @param recipient The nick of the user that got 'voiced'.
     */
    void onVoice( String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient );
    
    /**
     * Called when a user (possibly us) gets voice status removed.
     *  <p>
     * This is a type of mode change and is also passed to the onMode
     * method in the PircBot class.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @since PircBot 0.9.5
     *
     * @param channel The channel in which the mode change took place.
     * @param sourceNick The nick of the user that performed the mode change.
     * @param sourceLogin The login of the user that performed the mode change.
     * @param sourceHostname The hostname of the user that performed the mode change.
     * @param recipient The nick of the user that got 'devoiced'.
     */
    void onDeVoice( String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient );


    
    
    
    /**
     * Called when a user (possibly us) gets granted operator status for a channel.
     *  <p>
     * This is a type of mode change and is also passed to the onMode
     * method in the PircBot class.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @since PircBot 0.9.5
     *
     * @param channel The channel in which the mode change took place.
     * @param sourceNick The nick of the user that performed the mode change.
     * @param sourceLogin The login of the user that performed the mode change.
     * @param sourceHostname The hostname of the user that performed the mode change.
     * @param recipient The nick of the user that got 'opped'.
     */
    void onOp( String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient );

    /**
     * Called when a user (possibly us) gets operator status taken away.
     *  <p>
     * This is a type of mode change and is also passed to the onMode
     * method in the PircBot class.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @since PircBot 0.9.5
     *
     * @param channel The channel in which the mode change took place.
     * @param sourceNick The nick of the user that performed the mode change.
     * @param sourceLogin The login of the user that performed the mode change.
     * @param sourceHostname The hostname of the user that performed the mode change.
     * @param recipient The nick of the user that got 'deopped'.
     */
    void onDeop( String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient );

    
    

    /**
     * Called when a channel key is set.  When the channel key has been set,
     * other users may only join that channel if they know the key.  Channel keys
     * are sometimes referred to as passwords.
     *  <p>
     * This is a type of mode change and is also passed to the onMode
     * method in the PircBot class.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @since PircBot 0.9.5
     *
     * @param channel The channel in which the mode change took place.
     * @param sourceNick The nick of the user that performed the mode change.
     * @param sourceLogin The login of the user that performed the mode change.
     * @param sourceHostname The hostname of the user that performed the mode change.
     * @param key The new key for the channel.
     */
    public void onSetChannelKey(String channel, String sourceNick, String sourceLogin, String sourceHostname, String key);


    /**
     * Called when a channel key is removed.
     *  <p>
     * This is a type of mode change and is also passed to the onMode
     * method in the PircBot class.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @since PircBot 0.9.5
     *
     * @param channel The channel in which the mode change took place.
     * @param sourceNick The nick of the user that performed the mode change.
     * @param sourceLogin The login of the user that performed the mode change.
     * @param sourceHostname The hostname of the user that performed the mode change.
     * @param key The key that was in use before the channel key was removed.
     */
    public void onRemoveChannelKey(String channel, String sourceNick, String sourceLogin, String sourceHostname, String key);


    /**
     * Called when a user limit is set for a channel.  The number of users in
     * the channel cannot exceed this limit.
     *  <p>
     * This is a type of mode change and is also passed to the onMode
     * method in the PircBot class.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @since PircBot 0.9.5
     *
     * @param channel The channel in which the mode change took place.
     * @param sourceNick The nick of the user that performed the mode change.
     * @param sourceLogin The login of the user that performed the mode change.
     * @param sourceHostname The hostname of the user that performed the mode change.
     * @param limit The maximum number of users that may be in this channel at the same time.
     */
    public void onSetChannelLimit(String channel, String sourceNick, String sourceLogin, String sourceHostname, int limit);


    /**
     * Called when the user limit is removed for a channel.
     *  <p>
     * This is a type of mode change and is also passed to the onMode
     * method in the PircBot class.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @since PircBot 0.9.5
     *
     * @param channel The channel in which the mode change took place.
     * @param sourceNick The nick of the user that performed the mode change.
     * @param sourceLogin The login of the user that performed the mode change.
     * @param sourceHostname The hostname of the user that performed the mode change.
     */
    public void onRemoveChannelLimit(String channel, String sourceNick, String sourceLogin, String sourceHostname);


    /**
     * Called when a user (possibly us) gets banned from a channel.  Being
     * banned from a channel prevents any user with a matching hostmask from
     * joining the channel.  For this reason, most bans are usually directly
     * followed by the user being kicked :-)
     *  <p>
     * This is a type of mode change and is also passed to the onMode
     * method in the PircBot class.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @since PircBot 0.9.5
     *
     * @param channel The channel in which the mode change took place.
     * @param sourceNick The nick of the user that performed the mode change.
     * @param sourceLogin The login of the user that performed the mode change.
     * @param sourceHostname The hostname of the user that performed the mode change.
     * @param hostmask The hostmask of the user that has been banned.
     */
    public void onSetChannelBan(String channel, String sourceNick, String sourceLogin, String sourceHostname, String hostmask);


    /**
     * Called when a hostmask ban is removed from a channel.
     *  <p>
     * This is a type of mode change and is also passed to the onMode
     * method in the PircBot class.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @since PircBot 0.9.5
     *
     * @param channel The channel in which the mode change took place.
     * @param sourceNick The nick of the user that performed the mode change.
     * @param sourceLogin The login of the user that performed the mode change.
     * @param sourceHostname The hostname of the user that performed the mode change.
     * @param hostmask
     */
    public void onRemoveChannelBan(String channel, String sourceNick, String sourceLogin, String sourceHostname, String hostmask);


    /**
     * Called when topic protection is enabled for a channel.  Topic protection
     * means that only operators in a channel may change the topic.
     *  <p>
     * This is a type of mode change and is also passed to the onMode
     * method in the PircBot class.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @since PircBot 0.9.5
     *
     * @param channel The channel in which the mode change took place.
     * @param sourceNick The nick of the user that performed the mode change.
     * @param sourceLogin The login of the user that performed the mode change.
     * @param sourceHostname The hostname of the user that performed the mode change.
     */
    public void onSetTopicProtection(String channel, String sourceNick, String sourceLogin, String sourceHostname);


    /**
     * Called when topic protection is removed for a channel.
     *  <p>
     * This is a type of mode change and is also passed to the onMode
     * method in the PircBot class.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @since PircBot 0.9.5
     *
     * @param channel The channel in which the mode change took place.
     * @param sourceNick The nick of the user that performed the mode change.
     * @param sourceLogin The login of the user that performed the mode change.
     * @param sourceHostname The hostname of the user that performed the mode change.
     */
    public void onRemoveTopicProtection(String channel, String sourceNick, String sourceLogin, String sourceHostname);


    /**
     * Called when a channel is set to only allow messages from users that
     * are in the channel.
     *  <p>
     * This is a type of mode change and is also passed to the onMode
     * method in the PircBot class.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @since PircBot 0.9.5
     *
     * @param channel The channel in which the mode change took place.
     * @param sourceNick The nick of the user that performed the mode change.
     * @param sourceLogin The login of the user that performed the mode change.
     * @param sourceHostname The hostname of the user that performed the mode change.
     */
    public void onSetNoExternalMessages(String channel, String sourceNick, String sourceLogin, String sourceHostname);


    /**
     * Called when a channel is set to allow messages from any user, even
     * if they are not actually in the channel.
     *  <p>
     * This is a type of mode change and is also passed to the onMode
     * method in the PircBot class.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @since PircBot 0.9.5
     *
     * @param channel The channel in which the mode change took place.
     * @param sourceNick The nick of the user that performed the mode change.
     * @param sourceLogin The login of the user that performed the mode change.
     * @param sourceHostname The hostname of the user that performed the mode change.
     */
    public void onRemoveNoExternalMessages(String channel, String sourceNick, String sourceLogin, String sourceHostname);


    /**
     * Called when a channel is set to 'invite only' mode.  A user may only
     * join the channel if they are invited by someone who is already in the
     * channel.
     *  <p>
     * This is a type of mode change and is also passed to the onMode
     * method in the PircBot class.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @since PircBot 0.9.5
     *
     * @param channel The channel in which the mode change took place.
     * @param sourceNick The nick of the user that performed the mode change.
     * @param sourceLogin The login of the user that performed the mode change.
     * @param sourceHostname The hostname of the user that performed the mode change.
     */
    public void onSetInviteOnly(String channel, String sourceNick, String sourceLogin, String sourceHostname);


    /**
     * Called when a channel has 'invite only' removed.
     *  <p>
     * This is a type of mode change and is also passed to the onMode
     * method in the PircBot class.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @since PircBot 0.9.5
     *
     * @param channel The channel in which the mode change took place.
     * @param sourceNick The nick of the user that performed the mode change.
     * @param sourceLogin The login of the user that performed the mode change.
     * @param sourceHostname The hostname of the user that performed the mode change.
     */
    public void onRemoveInviteOnly(String channel, String sourceNick, String sourceLogin, String sourceHostname);


    /**
     * Called when a channel is set to 'moderated' mode.  If a channel is
     * moderated, then only users who have been 'voiced' or 'opped' may speak
     * or change their nicks.
     *  <p>
     * This is a type of mode change and is also passed to the onMode
     * method in the PircBot class.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @since PircBot 0.9.5
     *
     * @param channel The channel in which the mode change took place.
     * @param sourceNick The nick of the user that performed the mode change.
     * @param sourceLogin The login of the user that performed the mode change.
     * @param sourceHostname The hostname of the user that performed the mode change.
     */
    public void onSetModerated(String channel, String sourceNick, String sourceLogin, String sourceHostname);


    /**
     * Called when a channel is set to 'moderated' mode.  If a channel is
     * moderated, then only users who have been 'voiced' or 'opped' may speak
     * or change their nicks.
     *  <p>
     * This is a type of mode change and is also passed to the onMode
     * method in the PircBot class.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @since PircBot 0.9.5
     *
     * @param channel The channel in which the mode change took place.
     * @param sourceNick The nick of the user that performed the mode change.
     * @param sourceLogin The login of the user that performed the mode change.
     * @param sourceHostname The hostname of the user that performed the mode change.
     */
    public void onRemoveModerated(String channel, String sourceNick, String sourceLogin, String sourceHostname);


    /**
     * Called when a channel is marked as being in private mode.
     *  <p>
     * This is a type of mode change and is also passed to the onMode
     * method in the PircBot class.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @since PircBot 0.9.5
     *
     * @param channel The channel in which the mode change took place.
     * @param sourceNick The nick of the user that performed the mode change.
     * @param sourceLogin The login of the user that performed the mode change.
     * @param sourceHostname The hostname of the user that performed the mode change.
     */
    public void onSetPrivate(String channel, String sourceNick, String sourceLogin, String sourceHostname);


    /**
     * Called when a channel is marked as not being in private mode.
     *  <p>
     * This is a type of mode change and is also passed to the onMode
     * method in the PircBot class.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @since PircBot 0.9.5
     *
     * @param channel The channel in which the mode change took place.
     * @param sourceNick The nick of the user that performed the mode change.
     * @param sourceLogin The login of the user that performed the mode change.
     * @param sourceHostname The hostname of the user that performed the mode change.
     */
    public void onRemovePrivate(String channel, String sourceNick, String sourceLogin, String sourceHostname);


    /**
     * Called when a channel is set to be in 'secret' mode.  Such channels
     * typically do not appear on a server's channel listing.
     *  <p>
     * This is a type of mode change and is also passed to the onMode
     * method in the PircBot class.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @since PircBot 0.9.5
     *
     * @param channel The channel in which the mode change took place.
     * @param sourceNick The nick of the user that performed the mode change.
     * @param sourceLogin The login of the user that performed the mode change.
     * @param sourceHostname The hostname of the user that performed the mode change.
     */
    public void onSetSecret(String channel, String sourceNick, String sourceLogin, String sourceHostname);


    /**
     * Called when a channel has 'secret' mode removed.
     *  <p>
     * This is a type of mode change and is also passed to the onMode
     * method in the PircBot class.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @since PircBot 0.9.5
     *
     * @param channel The channel in which the mode change took place.
     * @param sourceNick The nick of the user that performed the mode change.
     * @param sourceLogin The login of the user that performed the mode change.
     * @param sourceHostname The hostname of the user that performed the mode change.
     */
    public void onRemoveSecret(String channel, String sourceNick, String sourceLogin, String sourceHostname);


    /**
     * Called when we are invited to a channel by a user.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @since PircBot 0.9.5
     *
     * @param targetNick The nick of the user being invited - should be us!
     * @param sourceNick The nick of the user that sent the invitation.
     * @param sourceLogin The login of the user that sent the invitation.
     * @param sourceHostname The hostname of the user that sent the invitation.
     * @param channel The channel that we're being invited to.
     */
    public void onInvite(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String channel) ;


    /**
     * This method used to be called when a DCC SEND request was sent to the PircBot.
     * Please use the onIncomingFileTransfer method to receive files, as it
     * has better functionality and supports resuming.
     *
     * @deprecated As of PircBot 1.2.0, use {@link #onIncomingFileTransfer(DccFileTransfer)}
     */
    public void onDccSendRequest(String sourceNick, String sourceLogin, String sourceHostname, String filename, long address, int port, int size);


    /**
     * This method used to be called when a DCC CHAT request was sent to the PircBot.
     * Please use the onIncomingChatRequest method to accept chats, as it
     * has better functionality.
     *
     * @deprecated As of PircBot 1.2.0, use {@link #onIncomingChatRequest(DccChat)}
     */
    public void onDccChatRequest(String sourceNick, String sourceLogin, String sourceHostname, long address, int port);

    
    
    
    
    
    /**
     * This method is called whenever a DCC SEND request is sent to the PircBot.
     * This means that a client has requested to send a file to us.
     * This abstract implementation performs no action, which means that all
     * DCC SEND requests will be ignored by default. If you wish to receive
     * the file, then you may override this method and call the receive method
     * on the DccFileTransfer object, which connects to the sender and downloads
     * the file.
     *  <p>
     * Example:
     * <pre> public void onIncomingFileTransfer(DccFileTransfer transfer) {
     *     // Use the suggested file name.
     *     File file = transfer.getFile();
     *     // Receive the transfer and save it to the file, allowing resuming.
     *     transfer.receive(file, true);
     * }</pre>
     *  <p>
     * <b>Warning:</b> Receiving an incoming file transfer will cause a file
     * to be written to disk. Please ensure that you make adequate security
     * checks so that this file does not overwrite anything important!
     *  <p>
     * Each time a file is received, it happens within a new Thread
     * in order to allow multiple files to be downloaded by the PircBot
     * at the same time.
     *  <p>
     * If you allow resuming and the file already partly exists, it will
     * be appended to instead of overwritten.  If resuming is not enabled,
     * the file will be overwritten if it already exists.
     *  <p>
     * You can throttle the speed of the transfer by calling the setPacketDelay
     * method on the DccFileTransfer object, either before you receive the
     * file or at any moment during the transfer.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @since PircBot 1.2.0
     *
     * @param transfer The DcccFileTransfer that you may accept.
     *
     * @see DccFileTransfer
     *
     */
    public void onIncomingFileTransfer(DccFileTransfer transfer);


    /**
     * This method gets called when a DccFileTransfer has finished.
     * If there was a problem, the Exception will say what went wrong.
     * If the file was sent successfully, the Exception will be null.
     *  <p>
     * Both incoming and outgoing file transfers are passed to this method.
     * You can determine the type by calling the isIncoming or isOutgoing
     * methods on the DccFileTransfer object.
     *
     * @since PircBot 1.2.0
     *
     * @param transfer The DccFileTransfer that has finished.
     * @param e null if the file was transfered successfully, otherwise this
     *          will report what went wrong.
     *
     * @see DccFileTransfer
     *
     */
    public void onFileTransferFinished(DccFileTransfer transfer, Exception e);


    /**
     * This method will be called whenever a DCC Chat request is received.
     * This means that a client has requested to chat to us directly rather
     * than via the IRC server. This is useful for sending many lines of text
     * to and from the pircBot without having to worry about flooding the server
     * or any operators of the server being able to "spy" on what is being
     * said. This abstract implementation performs no action, which means
     * that all DCC CHAT requests will be ignored by default.
     *  <p>
     * If you wish to accept the connection, then you may override this
     * method and call the accept() method on the DccChat object, which
     * connects to the sender of the chat request and allows lines to be
     * sent to and from the pircBot.
     *  <p>
     * Your pircBot must be able to connect directly to the user that sent the
     * request.
     *  <p>
     * Example:
     * <pre> public void onIncomingChatRequest(DccChat chat) {
     *     try {
     *         // Accept all chat, whoever it's from.
     *         chat.accept();
     *         chat.sendLine("Hello");
     *         String response = chat.readLine();
     *         chat.close();
     *     }
     *     catch (IOException e);
     * }</pre>
     *
     * Each time this method is called, it is called from within a new Thread
     * so that multiple DCC CHAT sessions can run concurrently.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @since PircBot 1.2.0
     *
     * @param chat A DccChat object that represents the incoming chat request.
     *
     * @see DccChat
     *
     */
    public void onIncomingChatRequest(DccChat chat);

    
    


    /**
     * This method is called whenever we receive a FINGER request.
     *  <p>
     * This abstract implementation responds correctly, so if you override this
     * method, be sure to either mimic its functionality or to call
     * super.onFinger(...);
     *
     * @param sourceNick The nick of the user that sent the FINGER request.
     * @param sourceLogin The login of the user that sent the FINGER request.
     * @param sourceHostname The hostname of the user that sent the FINGER request.
     * @param target The target of the FINGER request, be it our nick or a channel name.
     */
    void onFinger( String sourceNick, String sourceLogin, String sourceHostname, String target );



    /**
     * Called when the mode of a channel is set.
     *  <p>
     * You may find it more convenient to decode the meaning of the mode
     * string by overriding the onOp, onDeOp, onVoice, onDeVoice,
     * onChannelKey, onDeChannelKey, onChannelLimit, onDeChannelLimit,
     * onChannelBan or onDeChannelBan methods as appropriate.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @param channel The channel that the mode operation applies to.
     * @param sourceNick The nick of the user that set the mode.
     * @param sourceLogin The login of the user that set the mode.
     * @param sourceHostname The hostname of the user that set the mode.
     * @param mode The mode that has been set.
     *
     */
    void onMode( String channel, String sourceNick, String sourceLogin, String sourceHostname, String mode );



    /**
     * This method is called whenever we receive a PING request from another
     * user.
     *  <p>
     * This abstract implementation responds correctly, so if you override this
     * method, be sure to either mimic its functionality or to call
     * super.onPing(...);
     *
     * @param sourceNick The nick of the user that sent the PING request.
     * @param sourceLogin The login of the user that sent the PING request.
     * @param sourceHostname The hostname of the user that sent the PING request.
     * @param target The target of the PING request, be it our nick or a channel name.
     * @param pingValue The value that was supplied as an argument to the PING command.
     */
    void onPing( String sourceNick, String sourceLogin, String sourceHostname, String target, String pingValue );




    /**
     * The actions to perform when a PING request comes from the server.
     *  <p>
     * This sends back a correct response, so if you override this method,
     * be sure to either mimic its functionality or to call
     * super.onServerPing(response);
     *
     * @param response The response that should be given back in your PONG.
     */
    void onServerPing( String response );




    /**
     * This method is called whenever we receive a TIME request.
     *  <p>
     * This abstract implementation responds correctly, so if you override this
     * method, be sure to either mimic its functionality or to call
     * super.onTime(...);
     *
     * @param sourceNick The nick of the user that sent the TIME request.
     * @param sourceLogin The login of the user that sent the TIME request.
     * @param sourceHostname The hostname of the user that sent the TIME request.
     * @param target The target of the TIME request, be it our nick or a channel name.
     */
    void onTime( String sourceNick, String sourceLogin, String sourceHostname, String target );


    /**
     * This method is called whenever we receive a line from the server that
     * the PircBot has not been programmed to recognise.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @param line The raw line that was received from the server.
     */
    void onUnknown( String line );


    /**
     * Called when the mode of a user is set.
     *  <p>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @since PircBot 1.2.0
     *
     * @param targetNick The nick that the mode operation applies to.
     * @param sourceNick The nick of the user that set the mode.
     * @param sourceLogin The login of the user that set the mode.
     * @param sourceHostname The hostname of the user that set the mode.
     * @param mode The mode that has been set.
     *
     */
    void onUserMode( String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String mode );


    /**
     * This method is called whenever we receive a VERSION request.
     * This abstract implementation responds with the PircBot's _version string,
     * so if you override this method, be sure to either mimic its functionality
     * or to call super.onVersion(...);
     *
     * @param sourceNick The nick of the user that sent the VERSION request.
     * @param sourceLogin The login of the user that sent the VERSION request.
     * @param sourceHostname The hostname of the user that sent the VERSION request.
     * @param target The target of the VERSION request, be it our nick or a channel name.
     */
    void onVersion( String sourceNick, String sourceLogin, String sourceHostname, String target );


}// interface
