/**
 * This class is called whenever a message is sent to a channel.
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

package org.jibble.pircbot.handlers;

import org.jibble.pircbot.IrcServerConnection;
import org.jibble.pircbot.beans.User;

public class OnUserListEventHandler extends  AbstractEventHandler{
    private User[] users ;

    public OnUserListEventHandler(IrcServerConnection pircBot, String channel, User[] users){
        super.pircBot = pircBot ;
        super.channel = channel ;
        this.users = users ;
    }
}
