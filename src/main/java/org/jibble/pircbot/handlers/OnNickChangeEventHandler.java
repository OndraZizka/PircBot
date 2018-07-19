/**
 * This class is called whenever someone (possibly us) changes nick on any
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

package org.jibble.pircbot.handlers;

import org.jibble.pircbot.IrcServerConnection;

public class OnNickChangeEventHandler extends AbstractEventHandler {
    public OnNickChangeEventHandler(IrcServerConnection pircBot, String channel, String sourceNick, String sourceLogin, String sourceHostname, String param) {
        super(pircBot, channel, sourceNick, sourceLogin, sourceHostname, param);
    }
}
