package org.jibble.pircbot.handlers;

import org.jibble.pircbot.IrcServerConnection;

public class OnMessageEventHandler extends AbstractEventHandler {
    public OnMessageEventHandler(IrcServerConnection pircBot, String channel, String sourceNick, String sourceLogin, String sourceHostname, String param) {
        super(pircBot, channel, sourceNick, sourceLogin, sourceHostname, param);
    }
}
