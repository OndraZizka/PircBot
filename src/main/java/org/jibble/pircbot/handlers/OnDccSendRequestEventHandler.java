/**
 * This class used to be called when a DCC SEND request was sent to the PircBot.
 * Please use the onIncomingFileTransfer method to receive files, as it
 * has better functionality and supports resuming.
 *
 * @deprecated As of PircBot 1.2.0, use {@link #onIncomingFileTransfer(DccFileTransfer)}
 */

package org.jibble.pircbot.handlers;

import org.jibble.pircbot.DccFileTransfer;
import org.jibble.pircbot.IrcServerConnection;

public class OnDccSendRequestEventHandler extends AbstractEventHandler {

    public OnDccSendRequestEventHandler(IrcServerConnection bot, String channel, String sourceNick, String sourceLogin, String sourceHostname, String param) {
        super(bot, channel, sourceNick, sourceLogin, sourceHostname, param);
    }
}
