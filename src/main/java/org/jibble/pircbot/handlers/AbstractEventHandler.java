package org.jibble.pircbot.handlers;

import org.jibble.pircbot.IrcServerConnection;
import org.jibble.pircbot.api.IIrcEventHandlerBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractEventHandler implements IIrcEventHandlerBase{
  private final Logger logger = LoggerFactory.getLogger(getClass());

  protected IrcServerConnection pircBot;
  protected String channel;
  protected String sourceNick;
  protected String sourceLogin;
  protected String sourceHostname;
  protected String param;
  protected String substring;
  protected String target;

  public AbstractEventHandler(IrcServerConnection pircBot, String channel, String sourceNick, String sourceLogin, String sourceHostname, String param){
      this.pircBot = pircBot;
      this.channel = channel ;
      this.sourceNick = sourceNick ;
      this.sourceLogin = sourceLogin ;
      this.sourceHostname = sourceHostname ;
      this.param = param ;
  }


    public AbstractEventHandler() {
    }

    @Override
  public void execute() {
      logger.warn("calling execute but non implemented");
  }
}
