/**
 * This method used to be called when a DCC CHAT request was sent to the PircBot.
 * Please use the onIncomingChatRequest method to accept chats, as it
 * has better functionality.
 *
 * @deprecated As of PircBot 1.2.0, use {@link #onIncomingChatRequest(DccChat)}
 */
package org.jibble.pircbot.handlers;

import org.jibble.pircbot.DccChat;
import org.jibble.pircbot.IrcServerConnection;

public class OnDccChatRequestEventHandler extends AbstractEventHandler{

    public OnDccChatRequestEventHandler(IrcServerConnection bot, String channel, String sourceNick, String sourceLogin, String sourceHostname, String param) {
        super(bot, channel, sourceNick, sourceLogin, sourceHostname, param);
    }
}
