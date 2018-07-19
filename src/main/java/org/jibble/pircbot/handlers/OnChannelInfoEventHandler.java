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

package org.jibble.pircbot.handlers;

import org.jibble.pircbot.IrcServerConnection;

public class OnChannelInfoEventHandler extends AbstractEventHandler{
    private int userCount;
    private String topic;

    public OnChannelInfoEventHandler(IrcServerConnection bot, String channel, int userCount, String topic) {
        super.pircBot = bot;
        super.channel = channel;
        this.userCount = userCount;
        this.topic = topic;
    }
}
