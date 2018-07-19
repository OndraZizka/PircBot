/**
 * This class is called whenever a user sets the topic, or when
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

package org.jibble.pircbot.handlers;

import org.jibble.pircbot.IrcServerConnection;

import java.sql.Time;

public class OnTopicEventHandler extends AbstractEventHandler {
    String topic;
    String setBy;
    long date;
    boolean changed;

    public OnTopicEventHandler(IrcServerConnection pircBot, String channel, String topic, String setBy, long date, boolean changed) {
       super.pircBot = pircBot;
       super.channel = channel;
       this.topic = topic;
       this.setBy = setBy;
       this.date = date;
       this.changed = changed;
    }

    public OnTopicEventHandler(IrcServerConnection pircBot, String channel,String topic){
        super.pircBot = pircBot;
        super.channel = channel;
        this.topic = topic;
    }
}
