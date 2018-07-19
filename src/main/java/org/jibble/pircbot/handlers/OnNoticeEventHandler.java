/**
 * This class is called whenever we receive a notice.
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

package org.jibble.pircbot.handlers;

import org.jibble.pircbot.IrcServerConnection;

public class OnNoticeEventHandler extends AbstractEventHandler {
    public OnNoticeEventHandler(IrcServerConnection pircBot, String channel, String sourceNick, String sourceLogin, String sourceHostname, String param) {
        super(pircBot, channel, sourceNick, sourceLogin, sourceHostname, param);
    }
}
