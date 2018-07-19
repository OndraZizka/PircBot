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

package org.jibble.pircbot.handlers;

import org.jibble.pircbot.IrcServerConnection;

public class OnModeEventHandler extends AbstractEventHandler {

    public OnModeEventHandler(IrcServerConnection pircBot, String channel, String sourceNick, String sourceLogin, String sourceHostname, String param) {
        super(pircBot, channel, sourceNick, sourceLogin, sourceHostname, param);
    }
}
