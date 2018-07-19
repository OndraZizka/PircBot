/*
    Copyright Paul James Mutton, 2001-2009, http://www.jibble.org/
    This software is dual-licensed, allowing you to choose between the GNU
    General Public License (GPL) and the www.jibble.org Commercial License.
    Since the GPL may be too restrictive for use in a proprietary application,
    a commercial license is also provided. Full license information can be
    found at http://www.jibble.org/licenses/
    Modifications from PircBot 1.5 by David Lazar and Ondrej Zizka.
*/
package org.jibble.pircbot;

import org.jibble.pircbot.api.IIrcEventHandlerBase;
import org.jibble.pircbot.beans.ReplyConstants;
import org.jibble.pircbot.beans.ConnectionSettings;
import org.jibble.pircbot.beans.User;
import org.jibble.pircbot.ex.IrcException;
import org.jibble.pircbot.ex.NickAlreadyInUseException;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.net.*;
import javax.net.ssl.*;

import org.jibble.pircbot.handlers.*;
import org.jibble.pircbot.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PircBot is a Java framework for writing IRC bots quickly and easily.
 *  <p>
 * It provides an event-driven architecture to handle common IRC
 * events, flood protection, DCC support, ident support, and more.
 * The comprehensive logfile format is suitable for use with pisg to generate
 * channel statistics.
 *  <p>
 * Methods of the PircBot class can be called to send events to the IRC server
 * that it connects to.  For example, calling the sendMessage method will
 * send a message to a channel or user on the IRC server.  Multiple servers
 * can be supported using multiple instances of PircBot.
 *  <p>
 * To perform an action when the PircBot receives a normal message from the IRC
 * server, you would override the onMessage method defined in the PircBot
 * class.  All on<i>XYZ</i> methods in the PircBot class are automatically called
 * when the event <i>XYZ</i> happens, so you would override these if you wish
 * to do something when it does happen.
 *  <p>
 * Some event methods, such as onPing, should only really perform a specific
 * function (i.e. respond to a PING from the server).  For your convenience, such
 * methods are already correctly implemented in the PircBot and should not
 * normally need to be overridden.  Please read the full documentation for each
 * method to see which ones are already implemented by the PircBot class.
 *  <p>
 * Please visit the PircBot homepage at
 * <a href="http://www.jibble.org/pircbot.php">http://www.jibble.org/pircbot.php</a>
 * for full revision history, a beginners guide to creating your first PircBot
 * and a list of some existing Java IRC bots and clients that use the PircBot
 * framework.
 *
 * @author  Paul James Mutton, <a href="http://www.jibble.org/">http://www.jibble.org/</a>
 * @author  Ondrej Zizka, <a href="http://www.pohlidame.cz/">http://www.pohlidame.cz/</a>
 */
public class IrcServerConnection implements ReplyConstants {
    private static final Logger log = LoggerFactory.getLogger(IrcServerConnection.class);

    /**
     * The definitive version number of this release of PircBot.
     */
    public static final String VERSION = "1.7.0";


    private static final int OP_ADD = 1;
    private static final int OP_REMOVE = 2;
    private static final int VOICE_ADD = 3;
    private static final int VOICE_REMOVE = 4;
    

    // IRC event handler.
    private IIrcEventHandlerBase eventHandler;

    public IIrcEventHandlerBase getEventHandler() { return eventHandler; }
    public void setEventHandler( IIrcEventHandlerBase eventHandler ) { this.eventHandler = eventHandler; }


       /**
     * Attempt to connect to the specified IRC server.
     * The onConnect method is called upon success.
     *
     * @param hostname The hostname of the server to connect to.
     *
     * @throws IOException if it was not possible to connect to the server.
     * @throws IrcException if the server would not let us join it.
     * @throws NickAlreadyInUseException if our nick is already in use on the server.
     */
    public final synchronized void connect(String hostname) throws IOException, IrcException, NickAlreadyInUseException {
        ConnectionSettings cs = new ConnectionSettings(hostname);
        this.connect(cs);
    }


    /**
     * Attempt to connect to the specified IRC server and port number.
     * The onConnect method is called upon success.
     *
     * @param hostname The hostname of the server to connect to.
     * @param port The port number to connect to on the server.
     *
     * @throws IOException if it was not possible to connect to the server.
     * @throws IrcException if the server would not let us join it.
     * @throws NickAlreadyInUseException if our nick is already in use on the server.
     */
    public final synchronized void connect(String hostname, int port) throws IOException, IrcException, NickAlreadyInUseException {
        ConnectionSettings cs = new ConnectionSettings(hostname);
        cs.port = port;
        this.connect(cs);
    }


    /**
     * Attempt to connect to the specified IRC server using the supplied
     * password.
     * The onConnect method is called upon success.
     *
     * @param hostname The hostname of the server to connect to.
     * @param port The port number to connect to on the server.
     * @param password The password to use to join the server.
     *
     * @throws IOException if it was not possible to connect to the server.
     * @throws IrcException if the server would not let us join it.
     * @throws NickAlreadyInUseException if our nick is already in use on the server.
     */
    public final synchronized void connect(String hostname, int port, String password) throws IOException, IrcException, NickAlreadyInUseException {
        ConnectionSettings cs = new ConnectionSettings(hostname);
        cs.port = port;
        cs.password = password;
        this.connect(cs);
    }


    /**
     * Attempt to connect to an IRC server using the supplied
     * connection settings.
     * The onConnect method is called upon success.
     *
     * @param cs The connection settings to use.
     *
     * @throws IOException if it was not possible to connect to the server.
     * @throws IrcException if the server would not let us join it.
     * @throws NickAlreadyInUseException if our nick is already in use on the server.
     */
    public final synchronized void connect(ConnectionSettings cs) throws IOException, IrcException, NickAlreadyInUseException {

        ConnectionSettings _cs = cs.clone();
        _connectionSettings = _cs;

        if (isConnected()) {
            throw new IOException("The PircBot is already connected to an IRC server.  Disconnect first.");
        }

        // Don't clear the outqueue - there might be something important in it!

        // Clear everything we may have know about channels.
        this.removeAllChannels();

        // Connect to the server.
        Socket socket;
        if (_cs.useSSL) {
            try {
                SocketFactory factory;
                if (_cs.verifySSL) {
                    factory = SSLSocketFactory.getDefault();
                } else {
                    SSLContext sc = UnverifiedSSL.getUnverifiedSSLContext();
                    factory = sc.getSocketFactory();
                }
                socket = factory.createSocket(_cs.server, _cs.port);
            } catch (Exception e) {
                throw new IOException("SSL failure");
            }
        } else {
            socket = new Socket(_cs.server, _cs.port);
        }

        log.debug("*** Connected to server.");

        _inetAddress = socket.getLocalAddress();

        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;
        if (getEncoding() != null) {
            // Assume the specified encoding is valid for this JVM.
            inputStreamReader = new InputStreamReader(socket.getInputStream(), getEncoding());
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream(), getEncoding());
        }
        else {
            // Otherwise, just use the JVM's default encoding.
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
        }

        BufferedReader breader = new BufferedReader(inputStreamReader);
        BufferedWriter bwriter = new BufferedWriter(outputStreamWriter);

        // Attempt to join the server.
        if (_cs.password != null && !_cs.password.equals("")) {
            OutputThread.sendRawLine(this, bwriter, "PASS " + _cs.password);
        }
        String nick = this.getName();
        OutputThread.sendRawLine(this, bwriter, "NICK " + nick);
        // http://tools.ietf.org/html/rfc2812#section-3.1.3
        OutputThread.sendRawLine(this, bwriter, "USER " + this.getUserName() + " 8 * :" + this.getRealName());

        _inputThread = new InputThread(this, socket, breader, bwriter);

        // Read stuff back from the server to see if we connected.
        String line = null;
        int tries = 1;
        while ((line = breader.readLine()) != null) {

            this.handleLine(line);

            int firstSpace = line.indexOf(" ");
            int secondSpace = line.indexOf(" ", firstSpace + 1);
            if (secondSpace >= 0) {
                String code = line.substring(firstSpace + 1, secondSpace);

                if (code.equals("004")) {
                    // We're connected to the server.
                    break;
                }
                else if (code.equals("433")) {
                    if (_autoNickChange) {
                        tries++;
                        nick = getName() + tries;
                        OutputThread.sendRawLine(this, bwriter, "NICK " + nick);
                    }
                    else {
                        socket.close();
                        _inputThread = null;
                        throw new NickAlreadyInUseException(line);
                    }
                }
                else if (code.equals("439")) {
                    // No action required.
                }
                else if (code.startsWith("5") || code.startsWith("4")) {
                    socket.close();
                    _inputThread = null;
                    throw new IrcException("Could not log into the IRC server: " + line);
                }
            }
            this.setNick(nick);

        }

        log.debug("*** Logged onto server.");

        // This makes the socket timeout on read operations after 5 minutes.
        // Maybe in some future version I will let the user change this at runtime.
        socket.setSoTimeout(5 * 60 * 1000);

        // Now start the InputThread to read all other lines from the server.
        _inputThread.start();

        // Now start the outputThread that will be used to send all messages.
        if (_outputThread == null) {
            _outputThread = new OutputThread(this, _outQueue);
            _outputThread.start();
        }

        new OnConnectEventHandler(this).execute();
    }


    /**
     * Reconnects to the IRC server that we were previously connected to.
     * If necessary, the appropriate port number and password will be used.
     * This method will throw an IrcException if we have never connected
     * to an IRC server previously.
     *
     * @since PircBot 0.9.9
     *
     * @throws IOException if it was not possible to connect to the server.
     * @throws IrcException if the server would not let us join it.
     * @throws NickAlreadyInUseException if our nick is already in use on the server.
     */
    public final synchronized void reconnect() throws IOException, IrcException, NickAlreadyInUseException{
        if (getServer() == null) {
            throw new IrcException("Cannot reconnect to an IRC server because we were never connected to one previously!");
        }
        connect(_connectionSettings);
    }


    /**
     * This method disconnects from the server cleanly by calling the
     * quitServer() method.  Providing the PircBot was connected to an
     * IRC server, the onDisconnect() will be called as soon as the
     * disconnection is made by the server.
     *
     * @see #quitServer() quitServer
     * @see #quitServer(String) quitServer
     */
    public final synchronized void disconnect() {
        this.quitServer();
    }


    /**
     * When you connect to a server and your nick is already in use and
     * this is set to true, a new nick will be automatically chosen.
     * This is done by adding numbers to the end of the nick until an
     * available nick is found.
     *
     * @param autoNickChange Set to true if you want automatic nick changes
     *                       during connection.
     */
    public void setAutoNickChange(boolean autoNickChange) {
        _autoNickChange = autoNickChange;
    }


    /**
     * Starts an ident server (Identification Protocol Server, RFC 1413).
     *  <p>
     * Most IRC servers attempt to contact the ident server on connecting
     * hosts in order to determine the user's identity.  A few IRC servers
     * will not allow you to connect unless this information is provided.
     *  <p>
     * So when a PircBot is run on a machine that does not run an ident server,
     * it may be necessary to call this method to start one up.
     *  <p>
     * Calling this method starts up an ident server which will respond with
     * the login provided by calling getLogin() and then shut down immediately.
     * It will also be shut down if it has not been contacted within 60 seconds
     * of creation.
     *  <p>
     * If you require an ident response, then the correct procedure is to start
     * the ident server and then connect to the IRC server.  The IRC server may
     * then contact the ident server to get the information it needs.
     *  <p>
     * The ident server will fail to start if there is already an ident server
     * running on port 113, or if you are running as an unprivileged user who
     * is unable to create a server socket on that port number.
     *  <p>
     * If it is essential for you to use an ident server when connecting to an
     * IRC server, then make sure that port 113 on your machine is visible to
     * the IRC server so that it may contact the ident server.
     *
     * @since PircBot 0.9c
     */
    public final void startIdentServer() {
        new IdentServer(this, getLogin());
    }


    /**
     * Joins a channel.
     *
     * @param channel The name of the channel to join (eg "#cs").
     */
    public final void joinChannel(String channel) {
        this.sendRawLine("JOIN " + channel);
    }


    /**
     * Joins a channel with a key.
     *
     * @param channel The name of the channel to join (eg "#cs").
     * @param key The key that will be used to join the channel.
     */
    public final void joinChannel(String channel, String key) {
        this.joinChannel(channel + " " + key);
    }


    /**
     * Parts a channel.
     *
     * @param channel The name of the channel to leave.
     */
    public final void partChannel(String channel) {
        this.sendRawLine("PART " + channel);
    }


    /**
     * Parts a channel, giving a reason.
     *
     * @param channel The name of the channel to leave.
     * @param reason  The reason for parting the channel.
     */
    public final void partChannel(String channel, String reason) {
        this.sendRawLine("PART " + channel + " :" + reason);
    }


    /**
     * Quits from the IRC server.
     * Providing we are actually connected to an IRC server, the
     * onDisconnect() method will be called as soon as the IRC server
     * disconnects us.
     */
    public final void quitServer() {
        this.quitServer("");
    }


    /**
     * Quits from the IRC server with a reason.
     * Providing we are actually connected to an IRC server, the
     * onDisconnect() method will be called as soon as the IRC server
     * disconnects us.
     *
     * @param reason The reason for quitting the server.
     */
    public final void quitServer(String reason) {
        this.sendRawLine("QUIT :" + reason);
    }


    /**
     * Sends a raw line to the IRC server as soon as possible, bypassing the
     * outgoing message queue.
     *
     * @param line The raw line to send to the IRC server.
     */
    public final synchronized void sendRawLine(String line) {
        if (isConnected()) {
            _inputThread.sendRawLine(line);
        }
    }

    /**
     * Sends a raw line through the outgoing message queue.
     *
     * @param line The raw line to send to the IRC server.
     */
    public final synchronized void sendRawLineViaQueue(String line) {
        if (line == null) {
            throw new IllegalArgumentException("Cannot send null messages to server");
        }
        if (isConnected()) {
            _outQueue.add(line);
        }
    }


    /**
     * Sends a message to a channel or a private message to a user.  These
     * messages are added to the outgoing message queue and sent at the
     * earliest possible opportunity.
     *  <p>
     * Some examples: -
     *  <pre>    // Send the message "Hello!" to the channel #cs.
     *    sendMessage("#cs", "Hello!");
     *
     *    // Send a private message to Paul that says "Hi".
     *    sendMessage("Paul", "Hi");</pre>
     *
     * You may optionally apply colors, boldness, underlining, etc. to
     * the message by using the <code>Colors</code> class.
     *
     * @param target The name of the channel or user nick to send to.
     * @param message The message to send.
     *
     * @see Colors
     */
    public final void sendMessage(String target, String message) {
        _outQueue.add("PRIVMSG " + target + " :" + message);
    }
    
    public final void sendMessage(String user, String channel, String message) {
        if (channel == null) {
            if (user == null) {
                throw new IllegalArgumentException("Neither user nor channel set, can't send the message: " + message);
            } else {
                this.sendMessage(user, message);
            }
        } else {
            String whoFor = (user == null) ? "" : (user + ": ");
            this.sendMessage(channel, whoFor + message);
        }
    }
    


    /**
     * Sends an action to the channel or to a user.
     *
     * @param target The name of the channel or user nick to send to.
     * @param action The action to send.
     *
     * @see Colors
     */
    public final void sendAction(String target, String action) {
        sendCTCPCommand(target, "ACTION " + action);
    }


    /**
     * Sends a notice to the channel or to a user.
     *
     * @param target The name of the channel or user nick to send to.
     * @param notice The notice to send.
     */
    public final void sendNotice(String target, String notice) {
        _outQueue.add("NOTICE " + target + " :" + notice);
    }


    /**
     * Sends a CTCP command to a channel or user.  (Client to client protocol).
     * Examples of such commands are "PING <number>", "FINGER", "VERSION", etc.
     * For example, if you wish to request the version of a user called "Dave",
     * then you would call <code>sendCTCPCommand("Dave", "VERSION");</code>.
     * The type of response to such commands is largely dependant on the target
     * client software.
     *
     * @since PircBot 0.9.5
     *
     * @param target The name of the channel or user to send the CTCP message to.
     * @param command The CTCP command to send.
     */
    public final void sendCTCPCommand(String target, String command) {
        _outQueue.add("PRIVMSG " + target + " :\u0001" + command + "\u0001");
    }


    /**
     * Attempt to change the current nick (nickname) of the pircBot when it
     * is connected to an IRC server.
     * After confirmation of a successful nick change, the getNick method
     * will return the new nick.
     *
     * @param newNick The new nick to use.
     */
    public final void changeNick(String newNick) {
        this.sendRawLine("NICK " + newNick);
    }


    /**
     * Identify the pircBot with NickServ, supplying the appropriate password.
     * Some IRC Networks (such as freenode) require users to <i>register</i> and
     * <i>identify</i> with NickServ before they are able to send private messages
     * to other users, thus reducing the amount of spam.  If you are using
     * an IRC network where this kind of policy is enforced, you will need
     * to make your pircBot <i>identify</i> itself to NickServ before you can send
     * private messages. Assuming you have already registered your pircBot's
     * nick with NickServ, this method can be used to <i>identify</i> with
     * the supplied password. It usually makes sense to identify with NickServ
     * immediately after connecting to a server.
     *  <p>
     * This method issues a raw NICKSERV command to the server, and is therefore
     * safer than the alternative approach of sending a private message to
     * NickServ. The latter approach is considered dangerous, as it may cause
     * you to inadvertently transmit your password to an untrusted party if you
     * connect to a network which does not run a NickServ service and where the
     * untrusted party has assumed the nick "NickServ".  However, if your IRC
     * network is only compatible with the private message approach, you may
     * typically identify like so:
     * <pre>sendMessage("NickServ", "identify PASSWORD");</pre>
     *
     * @param password The password which will be used to identify with NickServ.
     */
    public final void identify(String password) {
        this.sendRawLine("NICKSERV IDENTIFY " + password);
    }


    /**
     * Set the mode of a channel.
     * This method attempts to set the mode of a channel.  This
     * may require the pircBot to have operator status on the channel.
     * For example, if the pircBot has operator status, we can grant
     * operator status to "Dave" on the #cs channel
     * by calling setMode("#cs", "+o Dave");
     * An alternative way of doing this would be to use the op method.
     *
     * @param channel The channel on which to perform the mode change.
     * @param mode    The new mode to apply to the channel.  This may include
     *                zero or more arguments if necessary.
     *
     * @see #op(String,String) op
     */
    public final void setMode(String channel, String mode) {
        this.sendRawLine("MODE " + channel + " " + mode);
    }


    /**
     * Sends an invitation to join a channel.  Some channels can be marked
     * as "invite-only", so it may be useful to allow a pircBot to invite people
     * into it.
     *
     * @param nick    The nick of the user to invite
     * @param channel The channel you are inviting the user to join.
     *
     */
    public final void sendInvite(String nick, String channel) {
        this.sendRawLine("INVITE " + nick + " :" + channel);
    }


    /**
     * Bans a user from a channel.  An example of a valid hostmask is
     * "*!*compu@*.18hp.net".  This may be used in conjunction with the
     * kick method to permanently remove a user from a channel.
     * Successful use of this method may require the pircBot to have operator
     * status itself.
     *
     * @param channel The channel to ban the user from.
     * @param hostmask A hostmask representing the user we're banning.
     */
    public final void ban(String channel, String hostmask) {
        this.sendRawLine("MODE " + channel + " +b " + hostmask);
    }


    /**
     * Unbans a user from a channel.  An example of a valid hostmask is
     * "*!*compu@*.18hp.net".
     * Successful use of this method may require the pircBot to have operator
     * status itself.
     *
     * @param channel The channel to unban the user from.
     * @param hostmask A hostmask representing the user we're unbanning.
     */
    public final void unBan(String channel, String hostmask) {
        this.sendRawLine("MODE " + channel + " -b " + hostmask);
    }


    /**
     * Grants operator privilidges to a user on a channel.
     * Successful use of this method may require the pircBot to have operator
     * status itself.
     *
     * @param channel The channel we're opping the user on.
     * @param nick The nick of the user we are opping.
     */
    public final void op(String channel, String nick) {
        this.setMode(channel, "+o " + nick);
    }


    /**
     * Removes operator privilidges from a user on a channel.
     * Successful use of this method may require the pircBot to have operator
     * status itself.
     *
     * @param channel The channel we're deopping the user on.
     * @param nick The nick of the user we are deopping.
     */
    public final void deOp(String channel, String nick) {
        this.setMode(channel, "-o " + nick);
    }


    /**
     * Grants voice privilidges to a user on a channel.
     * Successful use of this method may require the pircBot to have operator
     * status itself.
     *
     * @param channel The channel we're voicing the user on.
     * @param nick The nick of the user we are voicing.
     */
    public final void voice(String channel, String nick) {
        this.setMode(channel, "+v " + nick);
    }


    /**
     * Removes voice privilidges from a user on a channel.
     * Successful use of this method may require the pircBot to have operator
     * status itself.
     *
     * @param channel The channel we're devoicing the user on.
     * @param nick The nick of the user we are devoicing.
     */
    public final void deVoice(String channel, String nick) {
        this.setMode(channel, "-v " + nick);
    }


    /**
     * Set the topic for a channel.
     * This method attempts to set the topic of a channel.  This
     * may require the pircBot to have operator status if the topic
     * is protected.
     *
     * @param channel The channel on which to perform the mode change.
     * @param topic   The new topic for the channel.
     *
     */
    public final void setTopic(String channel, String topic) {
        this.sendRawLine("TOPIC " + channel + " :" + topic);
    }


    /**
     * Kicks a user from a channel.
     * This method attempts to kick a user from a channel and
     * may require the pircBot to have operator status in the channel.
     *
     * @param channel The channel to kick the user from.
     * @param nick    The nick of the user to kick.
     */
    public final void kick(String channel, String nick) {
        this.kick(channel, nick, "");
    }


    /**
     * Kicks a user from a channel, giving a reason.
     * This method attempts to kick a user from a channel and
     * may require the pircBot to have operator status in the channel.
     *
     * @param channel The channel to kick the user from.
     * @param nick    The nick of the user to kick.
     * @param reason  A description of the reason for kicking a user.
     */
    public final void kick(String channel, String nick, String reason) {
        this.sendRawLine("KICK " + channel + " " + nick + " :" + reason);
    }


    /**
     * Issues a request for a list of all channels on the IRC server.
     * When the PircBot receives information for each channel, it will
     * call the onChannelInfo method, which you will need to override
     * if you want it to do anything useful.
     *
     * @see #onChannelInfo(String,int,String) onChannelInfo
     */
    public final void listChannels() {
        this.listChannels(null);
    }


    /**
     * Issues a request for a list of all channels on the IRC server.
     * When the PircBot receives information for each channel, it will
     * call the onChannelInfo method, which you will need to override
     * if you want it to do anything useful.
     *  <p>
     * Some IRC servers support certain parameters for LIST requests.
     * One example is a parameter of ">10" to list only those channels
     * that have more than 10 users in them.  Whether these parameters
     * are supported or not will depend on the IRC server software.
     *
     * @param parameters The parameters to supply when requesting the
     *                   list.
     *
     * @see #onChannelInfo(String,int,String) onChannelInfo
     */
    public final void listChannels(String parameters) {
        if (parameters == null) {
            this.sendRawLine("LIST");
        }
        else {
            this.sendRawLine("LIST " + parameters);
        }
    }


    /**
     * Sends a file to another user.  Resuming is supported.
     * The other user must be able to connect directly to your pircBot to be
     * able to receive the file.
     *  <p>
     * You may throttle the speed of this file transfer by calling the
     * setPacketDelay method on the DccFileTransfer that is returned.
     *  <p>
     * This method may not be overridden.
     *
     * @since 0.9c
     *
     * @param file The file to send.
     * @param nick The user to whom the file is to be sent.
     * @param timeout The number of milliseconds to wait for the recipient to
     *                acccept the file (we recommend about 120000).
     *
     * @return The DccFileTransfer that can be used to monitor this transfer.
     *
     * @see DccFileTransfer
     *
     */
    public final DccFileTransfer dccSendFile(File file, String nick, int timeout) {
        DccFileTransfer transfer = new DccFileTransfer(this, _dccManager, file, nick, timeout);
        transfer.doSend(true);
        return transfer;
    }


    /**
     * Receives a file that is being sent to us by a DCC SEND request.
     * Please use the onIncomingFileTransfer method to receive files.
     *
     * @deprecated As of PircBot 1.2.0, use {@link #onIncomingFileTransfer(DccFileTransfer)}
     */
    protected final void dccReceiveFile(File file, long address, int port, int size) {
        throw new RuntimeException("dccReceiveFile is deprecated, please use sendFile");
    }


    /**
     * Attempts to establish a DCC CHAT session with a client.  This method
     * issues the connection request to the client and then waits for the
     * client to respond.  If the connection is successfully made, then a
     * DccChat object is returned by this method.  If the connection is not
     * made within the time limit specified by the timeout value, then null
     * is returned.
     *  <p>
     * It is <b>strongly recommended</b> that you call this method within a new
     * Thread, as it may take a long time to return.
     *  <p>
     * This method may not be overridden.
     *
     * @since PircBot 0.9.8
     *
     * @param nick The nick of the user we are trying to establish a chat with.
     * @param timeout The number of milliseconds to wait for the recipient to
     *                accept the chat connection (we recommend about 120000).
     *
     * @return a DccChat object that can be used to send and recieve lines of
     *         text.  Returns <b>null</b> if the connection could not be made.
     *
     * @see DccChat
     */
    public final DccChat dccSendChatRequest(String nick, int timeout) {
        DccChat chat = null;
        try {
            ServerSocket ss = null;

            int[] ports = getDccPorts();
            if (ports == null) {
                // Use any free port.
                ss = new ServerSocket(0);
            }
            else {
                for (int i = 0; i < ports.length; i++) {
                    try {
                        ss = new ServerSocket(ports[i]);
                        // Found a port number we could use.
                        break;
                    }
                    catch (Exception e) {
                        // Do nothing; go round and try another port.
                    }
                }
                if (ss == null) {
                    // No ports could be used.
                    throw new IOException("All ports returned by getDccPorts() are in use.");
                }
            }

            ss.setSoTimeout(timeout);
            int port = ss.getLocalPort();

            InetAddress inetAddress = getDccInetAddress();
            if (inetAddress == null) {
                inetAddress = getInetAddress();
            }
            byte[] ip = inetAddress.getAddress();
            long ipNum = Utils.ipToLong(ip);

            sendCTCPCommand(nick, "DCC CHAT chat " + ipNum + " " + port);

            // The client may now connect to us to chat.
            Socket socket = ss.accept();

            // Close the server socket now that we've finished with it.
            ss.close();

            chat = new DccChat(this, nick, socket);
        }
        catch (Exception e) {
            // Do nothing.
        }
        return chat;
    }


    /**
     * Attempts to accept a DCC CHAT request by a client.
     * Please use the onIncomingChatRequest method to receive files.
     *
     * @deprecated As of PircBot 1.2.0, use {@link #onIncomingChatRequest(DccChat)}
     */
    protected final DccChat dccAcceptChatRequest(String sourceNick, long address, int port) {
        throw new RuntimeException("dccAcceptChatRequest is deprecated, please use onIncomingChatRequest");
    }



    /**
     * This method handles events when any line of text arrives from the server,
     * then calling the appropriate method in the PircBot.  This method is
     * protected and only called by the InputThread for this instance.
     *  <p>
     * This method may not be overridden!
     *
     * @param line The raw line of text from the server.
     */
    protected void handleLine(String line) {
        //log.trace(line);

        // Check for server pings.
        if (line.startsWith("PING ")) {
            // Respond to the ping and return immediately.
            new OnServerPingEventHandler(this,line.substring(5)).execute();
            return;
        }

        String sourceNick = "";
        String sourceLogin = "";
        String sourceHostname = "";

        StringTokenizer tokenizer = new StringTokenizer(line);
        String senderInfo = tokenizer.nextToken();
        String command = tokenizer.nextToken();
        String target = null;

        int exclamation = senderInfo.indexOf("!");
        int at = senderInfo.indexOf("@");
        if (senderInfo.startsWith(":")) {
            if (exclamation > 0 && at > 0 && exclamation < at) {
                sourceNick = senderInfo.substring(1, exclamation);
                sourceLogin = senderInfo.substring(exclamation + 1, at);
                sourceHostname = senderInfo.substring(at + 1);
            }
            else {

                if (tokenizer.hasMoreTokens()) {
                    String token = command;

                    int code = -1;
                    try {
                        code = Integer.parseInt(token);
                    }
                    catch (NumberFormatException e) {
                        // Keep the existing value.
                    }

                    if (code != -1) {
                        String errorStr = token;
                        String response = line.substring(line.indexOf(errorStr, senderInfo.length()) + 4, line.length());
                        this.processServerResponse(code, response);
                        // Return from the method.
                        return;
                    }
                    else {
                        // This is not a server response.
                        // It must be a nick without login and hostname.
                        // (or maybe a NOTICE or suchlike from the server)
                        sourceNick = senderInfo;
                        target = token;
                    }
                }
                else {
                    // We don't know what this line means.
                    new OnUnknownEventHandler(line).execute();
                    // Return from the method;
                    return;
                }

            }
        }

        command = command.toUpperCase();
        if (sourceNick.startsWith(":")) {
            sourceNick = sourceNick.substring(1);
        }
        if (target == null) {
            target = tokenizer.nextToken();
        }
        if (target.startsWith(":")) {
            target = target.substring(1);
        }

        // Check for CTCP requests.
        if (command.equals("PRIVMSG") && line.indexOf(":\u0001") > 0 && line.endsWith("\u0001")) {
            String request = line.substring(line.indexOf(":\u0001") + 2, line.length() - 1);
            if (request.equals("VERSION")) {
                // VERSION request
                new OnVersionEventHandler(this,sourceNick, sourceLogin, sourceHostname, target).execute();

            }
            else if (request.startsWith("ACTION ")) {
                // ACTION request
                new OnActionEventHandler(sourceNick, sourceLogin, sourceHostname, target, request.substring(7)).execute();
            }
            else if (request.startsWith("PING ")) {
                // PING request
                new OnPingEventHandler(this,sourceNick,request.substring(5)).execute();
            }
            else if (request.equals("TIME")) {
                // TIME request
                new OnTimeEventHandler(this, sourceNick,sourceLogin,sourceHostname,target,null).execute();
            }
            else if (request.equals("FINGER")) {
                // FINGER request
                new OnFingerEventHandler(this,sourceNick, sourceLogin, sourceHostname, target,null).execute();
            }
            else if ((tokenizer = new StringTokenizer(request)).countTokens() >= 5 && tokenizer.nextToken().equals("DCC")) {
                // This is a DCC request.
                boolean success = _dccManager.processRequest(sourceNick, sourceLogin, sourceHostname, request);
                if (!success) {
                    // The DccManager didn't know what to do with the line.
                    new OnUnknownEventHandler(line).execute();
                }
            }
            else {
                // An unknown CTCP message - ignore it.
                new OnUnknownEventHandler(line).execute();
            }
        }
        else if (command.equals("PRIVMSG") && _channelPrefixes.indexOf(target.charAt(0)) >= 0) {
            // This is a normal message to a channel.
            new OnMessageEventHandler(this,target,sourceNick,sourceLogin,sourceHostname,line.substring(line.indexOf(" :") + 2)).execute();
        }
        else if (command.equals("PRIVMSG")) {
            // This is a private message to us.
            new OnPrivateMessageEventHandler(this,sourceNick, sourceLogin, sourceHostname, null, line.substring(line.indexOf(" :") + 2)).execute();
        }
        else if (command.equals("JOIN")) {
            // Someone is joining a channel.
            String channel = target;
            this.addUser(channel, new User("", sourceNick));
            new OnJoinEventHandler(this,channel,sourceNick,sourceLogin,sourceHostname,null).execute();
        }
        else if (command.equals("PART")) {
            // Someone is parting from a channel.
            this.removeUser(target, sourceNick);
            if (sourceNick.equals(this.getNick())) {
                this.removeChannel(target);
            }
            new OnPartEventHandler(this,target,sourceNick,sourceLogin,sourceHostname,null).execute();
        }
        else if (command.equals("NICK")) {
            // Somebody is changing their nick.
            String newNick = target;
            this.renameUser(sourceNick, newNick);
            if (sourceNick.equals(this.getNick())) {
                // Update our nick if it was us that changed nick.
                this.setNick(newNick);
            }
            new OnNickChangeEventHandler(this,null,sourceNick,sourceLogin,sourceHostname,newNick).execute();
        }
        else if (command.equals("NOTICE")) {
            // Someone is sending a notice.
            new OnNoticeEventHandler(this,null, sourceNick,sourceLogin,target,line.substring(line.indexOf(" :") + 2)).execute();
        }
        else if (command.equals("QUIT")) {
            // Someone has quit from the IRC server.
            if (sourceNick.equals(this.getNick())) {
                this.removeAllChannels();
            }
            else {
                this.removeUser(sourceNick);
            }
            new OnQuitEventHandler(this,null,sourceNick,sourceLogin,sourceHostname,line.substring(line.indexOf(" :") + 2)).execute();
        }
        else if (command.equals("KICK")) {
            // Somebody has been kicked from a channel.
            String recipient = tokenizer.nextToken();
            if (recipient.equals(this.getNick())) {
                this.removeChannel(target);
            }
            this.removeUser(target, recipient);
            new OnKickEventHandler(this, target, sourceNick, sourceLogin, sourceHostname, recipient, line.substring(line.indexOf(" :") + 2)).execute();
        }
        else if (command.equals("MODE")) {
            // Somebody is changing the mode on a channel or user.
            String mode = line.substring(line.indexOf(target, 2) + target.length() + 1);
            if (mode.startsWith(":")) {
                mode = mode.substring(1);
            }
            this.processMode(target, sourceNick, sourceLogin, sourceHostname, mode);
        }
        else if (command.equals("TOPIC")) {
            // Someone is changing the topic.
            new OnTopicEventHandler(this, target, line.substring(line.indexOf(" :") + 2), sourceNick, System.currentTimeMillis(), true).execute();
        }
        else if (command.equals("INVITE")) {
            // Somebody is inviting somebody else into a channel.
            new OnInviteEventHandler(this, target, sourceNick, sourceLogin, sourceHostname, line.substring(line.indexOf(" :") + 2)).execute();
        }
        else {
            // If we reach this point, then we've found something that the PircBot
            // Doesn't currently deal with.
            new OnUnknownEventHandler(line).execute();
        }

    }



    /**
     * This method is called by the PircBot when a numeric response
     * is received from the IRC server.  We use this method to
     * allow PircBot to process various responses from the server
     * before then passing them on to the onServerResponse method.
     *  <p>
     * Note that this method is private and should not appear in any
     * of the javadoc generated documenation.
     *
     * @param code The three-digit numerical code for the response.
     * @param response The full response from the IRC server.
     */
    private void processServerResponse(int code, String response) {

        if (code == RPL_LIST) {
            // This is a bit of information about a channel.
            int firstSpace = response.indexOf(' ');
            int secondSpace = response.indexOf(' ', firstSpace + 1);
            int thirdSpace = response.indexOf(' ', secondSpace + 1);
            int colon = response.indexOf(':');
            String channel = response.substring(firstSpace + 1, secondSpace);
            int userCount = 0;
            try {
                userCount = Integer.parseInt(response.substring(secondSpace + 1, thirdSpace));
            }
            catch (NumberFormatException e) {
                // Stick with the value of zero.
            }
            String topic = response.substring(colon + 1);
            new OnChannelInfoEventHandler(this,channel,userCount,topic).execute();

        }
        else if (code == RPL_TOPIC) {
            // This is topic information about a channel we've just joined.
            int firstSpace = response.indexOf(' ');
            int secondSpace = response.indexOf(' ', firstSpace + 1);
            int colon = response.indexOf(':');
            String channel = response.substring(firstSpace + 1, secondSpace);
            String topic = response.substring(colon + 1);

            _topics.put(channel, topic);

            // For backwards compatibility only - this onTopic method is deprecated.
            new OnTopicEventHandler(this,channel,topic).execute();
        }
        else if (code == RPL_TOPICINFO) {
            StringTokenizer tokenizer = new StringTokenizer(response);
            tokenizer.nextToken();
            String channel = tokenizer.nextToken();
            String setBy = tokenizer.nextToken();
            long date = 0;
            try {
                date = Long.parseLong(tokenizer.nextToken()) * 1000;
            }
            catch (NumberFormatException e) {
                // Stick with the default value of zero.
            }

            String topic = (String) _topics.get(channel);
            _topics.remove(channel);

            new OnTopicEventHandler(this,channel,topic,setBy,date,false).execute();
        }
        else if (code == RPL_NAMREPLY) {
            // This is a list of nicks in a channel that we've just joined.
            int channelEndIndex = response.indexOf(" :");
            String channel = response.substring(response.lastIndexOf(' ', channelEndIndex - 1) + 1, channelEndIndex);

            StringTokenizer tokenizer = new StringTokenizer(response.substring(response.indexOf(" :") + 2));
            while (tokenizer.hasMoreTokens()) {
                String nick = tokenizer.nextToken();
                String prefix = "";
                if (nick.startsWith("@")) {
                    // User is an operator in this channel.
                    prefix = "@";
                }
                else if (nick.startsWith("+")) {
                    // User is voiced in this channel.
                    prefix = "+";
                }
                else if (nick.startsWith(".")) {
                    // Some wibbly status I've never seen before...
                    prefix = ".";
                }
                nick = nick.substring(prefix.length());
                this.addUser(channel, new User(prefix, nick));
            }
        }
        else if (code == RPL_ENDOFNAMES) {
            // This is the end of a NAMES list, so we know that we've got
            // the full list of users in the channel that we just joined.
            String channel = response.substring(response.indexOf(' ') + 1, response.indexOf(" :"));
            User[] users = this.getUsers(channel);
            new OnUserListEventHandler(this,channel,users).execute();
        }

        new OnServerResponseEventHandler(this,code,response).execute();
    }



    /**
     * Called when the mode of a channel is set.  We process this in
     * order to call the appropriate onOp, onDeop, etc method before
     * finally calling the override-able onMode method.
     *  <p>
     * Note that this method is private and is not intended to appear
     * in the javadoc generated documentation.
     *
     * @param target The channel or nick that the mode operation applies to.
     * @param sourceNick The nick of the user that set the mode.
     * @param sourceLogin The login of the user that set the mode.
     * @param sourceHostname The hostname of the user that set the mode.
     * @param mode  The mode that has been set.
     */
    private void processMode(String target, String sourceNick, String sourceLogin, String sourceHostname, String mode) {

        if (_channelPrefixes.indexOf(target.charAt(0)) >= 0) {
            // The mode of a channel is being changed.
            String channel = target;
            StringTokenizer tok = new StringTokenizer(mode);
            String[] params = new String[tok.countTokens()];

            int t = 0;
            while (tok.hasMoreTokens()) {
                params[t] = tok.nextToken();
                t++;
            }

            char pn = ' ';
            int p = 1;

            // All of this is very large and ugly, but it's the only way of providing
            // what the users want :-/
            for (int i = 0; i < params[0].length(); i++) {
                char atPos = params[0].charAt(i);

                if (atPos == '+' || atPos == '-') {
                    pn = atPos;
                }
                else if (atPos == 'o') {
                   if (pn == '+') {
                       this.updateUser(channel, OP_ADD, params[p]);
                       new OnOpEventHandler(this, channel, sourceNick, sourceLogin, sourceHostname, params[p]).execute();
                   }
                   else {
                       this.updateUser(channel, OP_REMOVE, params[p]);
                       new OnDeopEventHandler(this,channel, sourceNick, sourceLogin, sourceHostname, params[p]).execute();
                   }
                   p++;
                }
                else if (atPos == 'v') {
                    if (pn == '+') {
                        this.updateUser(channel, VOICE_ADD, params[p]);
                        new OnVoiceEventHandler(this, channel, sourceNick, sourceLogin, sourceHostname, params[p]).execute();
                    }
                    else {
                        this.updateUser(channel, VOICE_REMOVE, params[p]);
                        new OnDeVoiceEventHandler(this,channel,sourceNick,sourceLogin,sourceHostname,params[p]).execute();
                    }
                    p++;
                 }
                 else if (atPos == 'k') {
                     if (pn == '+') {
                         new OnSetChannelKeyEventHandler(this,channel, sourceNick,sourceLogin,sourceHostname,params[p]).execute();
                     }
                     else {
                         new OnRemoveChannelKeyEventHandler(this,channel, sourceNick, sourceLogin, sourceHostname, params[p]).execute();
                     }
                     p++;
                 }
                 else if (atPos == 'l') {
                     if (pn == '+') {
                         new OnSetChannelLimitEventHandler(this,channel,sourceNick,sourceLogin,sourceHostname,Integer.parseInt(params[p])).execute();
                         p++;
                     }
                     else {
                         new OnRemoveChannelLimitEventHandler(this, channel, sourceNick, sourceLogin, sourceHostname).execute();
                     }
                 }
                 else if (atPos == 'b') {
                     if (pn == '+') {
                         new OnSetChannelBanEventHandler(this,channel,sourceNick,sourceLogin,sourceHostname,params[p]).execute();
                     }
                     else {
                         new OnRemoveChannelBanEventHandler(this,channel,sourceNick,sourceLogin,sourceHostname,params[p]).execute();
                     }
                     p++;
                 }
                 else if (atPos == 't') {
                     if (pn == '+') {

                          new OnSetTopicProtectionEventHandler(this,null,sourceNick,sourceLogin,sourceHostname,null ).execute();
                     }
                     else {
                         new OnRemoveTopicProtectionEventHandler(this,channel,sourceNick,sourceLogin,sourceHostname,null).execute();
                     }
                 }
                 else if (atPos == 'n') {
                     if (pn == '+') {
                         new OnSetNoExternalMessagesEventHandler(this,channel,sourceNick,sourceLogin,sourceHostname,null).execute();
                     }
                     else {
                         new OnRemoveNoExternalMessagesEventHandler(this,channel,sourceNick,sourceLogin,sourceHostname,null).execute();
                     }
                 }
                 else if (atPos == 'i') {
                     if (pn == '+') {
                         new OnSetInviteOnlyEventHandler(this,channel,sourceLogin,sourceLogin,sourceHostname,null).execute();
                     }
                     else {
                         new OnRemoveInviteOnlyEventHandler(this,channel,sourceNick,sourceLogin,sourceHostname, null).execute();
                     }
                 }
                 else if (atPos == 'm') {
                     if (pn == '+') {
                         new OnSetModeratedEventHandler(this,channel,sourceNick,sourceLogin,sourceHostname,null).execute();
                     }
                     else {
                         new OnRemoveModeratedEventHandler(this,channel,sourceNick,sourceLogin,sourceHostname,null).execute();
                     }
                 }
                 else if (atPos == 'p') {
                     if (pn == '+') {
                         new OnSetPrivateEventHandler(this, channel, sourceNick, sourceLogin, sourceHostname, null).execute();
                     }
                     else {
                         new OnRemovePrivateEventHandler(this,channel,sourceNick,sourceLogin,sourceHostname,null).execute();
                     }
                 }
                 else if (atPos == 's') {
                     if (pn == '+') {
                         new OnSetSecretEventHandler(this,channel,sourceNick,sourceLogin,sourceHostname,null).execute();
                     }
                     else {
                         new OnRemoveSecretEventHandler(this,channel,sourceNick,sourceLogin,null,null).execute();
                     }
                 }
            }

            new OnModeEventHandler(this,channel,sourceNick,sourceLogin,sourceHostname,mode).execute();
        }
        else {
            // The mode of a user is being changed.
            String nick = target;
            new OnUserModeEventHandler(this,nick,sourceNick,sourceLogin,sourceHostname,mode).execute();
        }
    }



    /**
     * Sets the verbose mode. If verbose mode is set to true, then log entries
     * will be printed to the standard output. The default value is false and
     * will result in no output. For general development, we strongly recommend
     * setting the verbose mode to true.
     *
     * @param verbose true if verbose mode is to be used.  Default is false.
     * @TODO  Move to some Configuration class.
     */
    public final void setVerbose(boolean verbose) {
        _verbose = verbose;
    }


    /**
     * Sets the name of the pircBot, which will be used as its nick when it
     * tries to join an IRC server.  This should be set before joining
     * any servers, otherwise the default nick will be used.  You would
     * typically call this method from the constructor of the class that
     * extends PircBot.
     *  <p>
     * The changeNick method should be used if you wish to change your nick
     * when you are connected to a server.
     *
     * @param name The new name of the Bot.
     * @TODO  Move to some Configuration class.
     */
    public final void setName(String name) {
        _name = name;
    }


    /**
     * Sets the internal nick of the pircBot.  This is only to be called by the
     * PircBot class in response to notification of nick changes that apply
     * to us.
     *
     * @param nick The new nick.
     */
    private void setNick(String nick) {
        _nick = nick;
    }


    /**
     * Sets the internal login of the Bot.  This should be set before joining
     * any servers.
     *
     * @param login The new login of the Bot.
     * @deprecated As of version 1.6, replaced by {@link #setUserName}
     * @TODO  Move to some Configuration class.
     */
    @Deprecated protected final void setLogin(String login) {
        _username = login;
    }


    /**
     * Sets the internal username of the Bot.  This should be set before
     * joining any servers.
     *
     * @param username The new username of the Bot.
     * @TODO  Move to some Configuration class.
     */
    public final void setUserName(String username) {
        _username = username;
    }


    /**
     * Sets the internal realname of the Bot.  This should be set before
     * joining any servers.
     *
     * @param realname The new realname of the Bot.
     * @TODO  Move to some Configuration class.
     */
    public final void setRealName(String realname) {
        _realname = realname;
    }


    /**
     * Sets the internal version of the Bot.  This should be set before joining
     * any servers.
     *
     * @param version The new version of the Bot.
     * @TODO  Move to some Configuration class.
     */
    protected final void setVersion(String version) {
        _version = version;
    }


    /**
     * Sets the interal finger message.  This should be set before joining
     * any servers.
     *
     * @param finger The new finger message for the Bot.
     * @TODO  Move to some Configuration class.
     */
    protected final void setFinger(String finger) {
        _finger = finger;
    }


    /**
     * Gets the name of the PircBot. This is the name that will be used as
     * as a nick when we try to join servers.
     *
     * @return The name of the PircBot.
     * @TODO  Move to some Configuration class.
     */
    public final String getName() {
        return _name;
    }


    /**
     * Returns the current nick of the pircBot. Note that if you have just changed
     * your nick, this method will still return the old nick until confirmation
     * of the nick change is received from the server.
     *  <p>
     * The nick returned by this method is maintained only by the PircBot
     * class and is guaranteed to be correct in the context of the IRC server.
     *
     * @since PircBot 1.0.0
     *
     * @return The current nick of the pircBot.
     */
    public String getNick() {
        return _nick;
    }


    /**
     * Gets the internal login of the PircBot.
     *
     * @return The login of the PircBot.
     * @deprecated As of version 1.6, replaced by {@link #getUserName()}
     */
    @Deprecated public final String getLogin() {
        return _username;
    }


    /**
     * Gets the internal username of the PircBot.
     *
     * @return The username of the PircBot.
     */
    public final String getUserName() {
        return _username;
    }


    /**
     * Gets the internal realname of the PircBot.
     *
     * @return The realname of the PircBot.
     */
    public final String getRealName() {
        return _realname;
    }


    /**
     * Gets the internal version of the PircBot.
     *
     * @return The version of the PircBot.
     */
    public final String getVersion() {
        return _version;
    }


    /**
     * Gets the internal finger message of the PircBot.
     *
     * @return The finger message of the PircBot.
     */
    public final String getFinger() {
        return _finger;
    }


    /**
     * Returns whether or not the PircBot is currently connected to a server.
     * The result of this method should only act as a rough guide,
     * as the result may not be valid by the time you act upon it.
     *
     * @return True if and only if the PircBot is currently connected to a server.
     */
    public final synchronized boolean isConnected() {
        return _inputThread != null && _inputThread.isConnected();
    }


    /**
     * Sets the number of milliseconds to delay between consecutive
     * messages when there are multiple messages waiting in the
     * outgoing message queue.  This has a default value of 1000ms.
     * It is a good idea to stick to this default value, as it will
     * prevent your pircBot from spamming servers and facing the subsequent
     * wrath!  However, if you do need to change this delay value (<b>not
     * recommended</b>), then this is the method to use.
     *
     * @param delay The number of milliseconds between each outgoing message.
     *
     */
    public final void setMessageDelay(long delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("Cannot have a negative time.");
        }
        _messageDelay = delay;
    }


    /**
     * Returns the number of milliseconds that will be used to separate
     * consecutive messages to the server from the outgoing message queue.
     *
     * @return Number of milliseconds.
     */
    public final long getMessageDelay() {
        return _messageDelay;
    }


    /**
     * Gets the maximum length of any line that is sent via the IRC protocol.
     * The IRC RFC specifies that line lengths, including the trailing \r\n
     * must not exceed 512 bytes.  Hence, there is currently no option to
     * change this value in PircBot.  All lines greater than this length
     * will be truncated before being sent to the IRC server.
     *
     * @return The maximum line length (currently fixed at 512)
     */
    public final int getMaxLineLength() {
        return InputThread.MAX_LINE_LENGTH;
    }


    /**
     * Gets the number of lines currently waiting in the outgoing message Queue.
     * If this returns 0, then the Queue is empty and any new message is likely
     * to be sent to the IRC server immediately.
     *
     * @since PircBot 0.9.9
     *
     * @return The number of lines in the outgoing message Queue.
     */
    public final int getOutgoingQueueSize() {
        return _outQueue.size();
    }


    /**
     * Returns the name of the last IRC server the PircBot tried to connect to.
     * This does not imply that the connection attempt to the server was
     * successful (we suggest you look at the onConnect method).
     * A value of null is returned if the PircBot has never tried to connect
     * to a server.
     *
     * @return The name of the last machine we tried to connect to. Returns
     *         null if no connection attempts have ever been made.
     */
    public final String getServer() {
        if (_connectionSettings == null)
            return null;
        return _connectionSettings.server;
    }


    /**
     * Returns the port number of the last IRC server that the PircBot tried
     * to connect to.
     * This does not imply that the connection attempt to the server was
     * successful (we suggest you look at the onConnect method).
     * A value of -1 is returned if the PircBot has never tried to connect
     * to a server.
     *
     * @since PircBot 0.9.9
     *
     * @return The port number of the last IRC server we connected to.
     *         Returns -1 if no connection attempts have ever been made.
     */
    public final int getPort() {
        if (_connectionSettings == null)
            return -1;
        return _connectionSettings.port;
    }

    /**
     * Returns whether PircBot used SSL with the last IRC server that
     * it tried to connect to.
     *
     * @since PircBot 1.6
     *
     * @return Whether SSL was used in the last connection attempt.
     *         Returns false if no connection attempts have ever been made.
     */
    public final boolean useSSL() {
        if (_connectionSettings == null)
            return false;
        return _connectionSettings.useSSL;
    }

    /**
     * Returns the last password that we used when connecting to an IRC server.
     * This does not imply that the connection attempt to the server was
     * successful (we suggest you look at the onConnect method).
     * A value of null is returned if the PircBot has never tried to connect
     * to a server using a password.
     *
     * @since PircBot 0.9.9
     *
     * @return The last password that we used when connecting to an IRC server.
     *         Returns null if we have not previously connected using a password.
     */
    public final String getPassword() {
        if (_connectionSettings == null)
            return null;
        return _connectionSettings.password;
    }


    /**
     * Sets the encoding charset to be used when sending or receiving lines
     * from the IRC server.  If set to null, then the platform's default
     * charset is used.  You should only use this method if you are
     * trying to send text to an IRC server in a different charset, e.g.
     * "GB2312" for Chinese encoding.  If a PircBot is currently connected
     * to a server, then it must reconnect before this change takes effect.
     *
     * @since PircBot 1.0.4
     *
     * @param charset The new encoding charset to be used by PircBot.
     *
     * @throws UnsupportedEncodingException If the named charset is not
     *                                      supported.
     */
    public void setEncoding(String charset) throws UnsupportedEncodingException {
        // Just try to see if the charset is supported first...
        "".getBytes(charset);

        _charset = charset;
    }


    /**
     * Returns the encoding used to send and receive lines from
     * the IRC server, or null if not set.  Use the setEncoding
     * method to change the encoding charset.
     *
     * @since PircBot 1.0.4
     *
     * @return The encoding used to send outgoing messages, or
     *         null if not set.
     */
    public String getEncoding() {
        return _charset;
    }

    /**
     * Returns the InetAddress used by the PircBot.
     * This can be used to find the I.P. address from which the PircBot is
     * connected to a server.
     *
     * @since PircBot 1.4.4
     *
     * @return The current local InetAddress, or null if never connected.
     */
    public InetAddress getInetAddress() {
        return _inetAddress;
    }


    /**
     * Sets the InetAddress to be used when sending DCC chat or file transfers.
     * This can be very useful when you are running a pircBot on a machine which
     * is behind a firewall and you need to tell receiving clients to connect
     * to a NAT/router, which then forwards the connection.
     *
     * @since PircBot 1.4.4
     *
     * @param dccInetAddress The new InetAddress, or null to use the default.
     */
    public void setDccInetAddress(InetAddress dccInetAddress) {
        _dccInetAddress = dccInetAddress;
    }


    /**
     * Returns the InetAddress used when sending DCC chat or file transfers.
     * If this is null, the default InetAddress will be used.
     *
     * @since PircBot 1.4.4
     *
     * @return The current DCC InetAddress, or null if left as default.
     */
    public InetAddress getDccInetAddress() {
        return _dccInetAddress;
    }


    /**
     * Returns the set of port numbers to be used when sending a DCC chat
     * or file transfer. This is useful when you are behind a firewall and
     * need to set up port forwarding. The array of port numbers is traversed
     * in sequence until a free port is found to listen on. A DCC tranfer will
     * fail if all ports are already in use.
     * If set to null, <i>any</i> free port number will be used.
     *
     * @since PircBot 1.4.4
     *
     * @return An array of port numbers that PircBot can use to send DCC
     *         transfers, or null if any port is allowed.
     */
    public int[] getDccPorts() {
        if (_dccPorts == null || _dccPorts.length == 0) {
            return null;
        }
        // Clone the array to prevent external modification.
        return (int[]) _dccPorts.clone();
    }


    /**
     * Sets the choice of port numbers that can be used when sending a DCC chat
     * or file transfer. This is useful when you are behind a firewall and
     * need to set up port forwarding. The array of port numbers is traversed
     * in sequence until a free port is found to listen on. A DCC tranfer will
     * fail if all ports are already in use.
     * If set to null, <i>any</i> free port number will be used.
     *
     * @since PircBot 1.4.4
     *
     * @param ports The set of port numbers that PircBot may use for DCC
     *              transfers, or null to let it use any free port (default).
     *
     */
    public void setDccPorts(int[] ports) {
        if (ports == null || ports.length == 0) {
            _dccPorts = null;
        }
        else {
            // Clone the array to prevent external modification.
            _dccPorts = (int[]) ports.clone();
        }
    }


    /**
     * Returns true if and only if the object being compared is the exact
     * same instance as this PircBot. This may be useful if you are writing
     * a multiple server IRC pircBot that uses more than one instance of PircBot.
     *
     * @since PircBot 0.9.9
     *
     * @return true if and only if Object o is a PircBot and equal to this.
     */
    public boolean equals(Object o) {
        // This probably has the same effect as Object.equals, but that may change...
        if (o instanceof IrcServerConnection) {
            IrcServerConnection other = (IrcServerConnection) o;
            return other == this;
        }
        return false;
    }


    /**
     * Returns the hashCode of this PircBot. This method can be called by hashed
     * collection classes and is useful for managing multiple instances of
     * PircBots in such collections.
     *
     * @since PircBot 0.9.9
     *
     * @return the hash code for this instance of PircBot.
     */
    public int hashCode() {
        return super.hashCode();
    }


    /**
     * Returns a String representation of this object.
     * You may find this useful for debugging purposes, particularly
     * if you are using more than one PircBot instance to achieve
     * multiple server connectivity. The format of
     * this String may change between different versions of PircBot
     * but is currently something of the form
     * <code>
     *   Version{PircBot x.y.z Java IRC Bot - www.jibble.org}
     *   Connected{true}
     *   Server{irc.dal.net}
     *   Port{6667}
     *   Password{}
     * </code>
     *
     * @since PircBot 0.9.10
     *
     * @return a String representation of this object.
     */
    public String toString() {
        return "Version{" + getVersion() + "}" +
                " Connected{" + isConnected() + "}" +
                " Server{" + getServer() + "}" +
                " Port{" + getPort() + "}" +
                " Password{" + getPassword() + "}";
    }


    /**
     * Returns an array of all users in the specified channel.
     *  <p>
     * There are some important things to note about this method:-
     * <ul>
     *  <li>This method may not return a full list of users if you call it
     *      before the complete nick list has arrived from the IRC server.
     *  </li>
     *  <li>If you wish to find out which users are in a channel as soon
     *      as you join it, then you should override the onUserList method
     *      instead of calling this method, as the onUserList method is only
     *      called as soon as the full user list has been received.
     *  </li>
     *  <li>This method will return immediately, as it does not require any
     *      interaction with the IRC server.
     *  </li>
     *  <li>The pircBot must be in a channel to be able to know which users are
     *      in it.
     *  </li>
     * </ul>
     *
     * @since PircBot 1.0.0
     *
     * @param channel The name of the channel to list.
     *
     * @return An array of User objects. This array is empty if we are not
     *         in the channel.
     *
     * @see #onUserList(String,User[]) onUserList
     */
    public final User[] getUsers(String channel) {
        channel = channel.toLowerCase();
        User[] userArray = new User[0];
        synchronized (_channels) {
            Hashtable users = (Hashtable) _channels.get(channel);
            if (users != null) {
                userArray = new User[users.size()];
                Enumeration enumeration = users.elements();
                for (int i = 0; i < userArray.length; i++) {
                    User user = (User) enumeration.nextElement();
                    userArray[i] = user;
                }
            }
        }
        return userArray;
    }

    /**
     *  Returns null if we are not in given $channel.
     *  Returns whether the user with given $nick is present in given $channel.
     */
    public Boolean isUserInChannel( String nick, String channel ){
        synchronized (_channels) {
            Hashtable users = (Hashtable) _channels.get(channel);
            if(users == null)
                return null;
            
            User user = (User) users.get( nick );
            return null != user;
        }
    }

    /**
     * Returns an array of all channels that we are in.  Note that if you
     * call this method immediately after joining a new channel, the new
     * channel may not appear in this array as it is not possible to tell
     * if the join was successful until a response is received from the
     * IRC server.
     *
     * @since PircBot 1.0.0
     *
     * @return A String array containing the names of all channels that we
     *         are in.
     */
    public final String[] getChannels() {
        String[] channels = new String[0];
        synchronized (_channels) {
            channels = new String[_channels.size()];
            Enumeration enumeration = _channels.keys();
            for (int i = 0; i < channels.length; i++) {
                channels[i] = (String) enumeration.nextElement();
            }
        }
        return channels;
    }


    /**
     * Disposes of all thread resources used by this PircBot. This may be
     * useful when writing bots or clients that use multiple servers (and
     * therefore multiple PircBot instances) or when integrating a PircBot
     * with an existing program.
     *  <p>
     * Each PircBot runs its own threads for dispatching messages from its
     * outgoing message queue and receiving messages from the server.
     * Calling dispose() ensures that these threads are
     * stopped, thus freeing up system resources and allowing the PircBot
     * object to be garbage collected if there are no other references to
     * it.
     *  <p>
     * Once a PircBot object has been disposed, it should not be used again.
     * Attempting to use a PircBot that has been disposed may result in
     * unpredictable behaviour.
     *
     * @since 1.2.2
     */
    public synchronized void dispose() {
        //System.out.println("disposing...");
        _outputThread.interrupt();
        _inputThread.dispose();
    }


    /**
     * Add a user to the specified channel in our memory.
     * Overwrite the existing entry if it exists.
     */
    private void addUser(String channel, User user) {
        channel = channel.toLowerCase();
        synchronized (_channels) {
            Hashtable users = (Hashtable) _channels.get(channel);
            if (users == null) {
                users = new Hashtable();
                _channels.put(channel, users);
            }
            users.put(user, user);
        }
    }


    /**
     * Remove a user from the specified channel in our memory.
     */
    private User removeUser(String channel, String nick) {
        channel = channel.toLowerCase();
        User user = new User("", nick);
        synchronized (_channels) {
            Hashtable users = (Hashtable) _channels.get(channel);
            if (users != null) {
                return (User) users.remove(user);
            }
        }
        return null;
    }


    /**
     * Remove a user from all channels in our memory.
     */
    private void removeUser(String nick) {
        synchronized (_channels) {
            Enumeration enumeration = _channels.keys();
            while (enumeration.hasMoreElements()) {
                String channel = (String) enumeration.nextElement();
                this.removeUser(channel, nick);
            }
        }
    }


    /**
     * Rename a user if they appear in any of the channels we know about.
     */
    private void renameUser(String oldNick, String newNick) {
        synchronized (_channels) {
            Enumeration enumeration = _channels.keys();
            while (enumeration.hasMoreElements()) {
                String channel = (String) enumeration.nextElement();
                User user = this.removeUser(channel, oldNick);
                if (user != null) {
                    user = new User(user.getPrefix(), newNick);
                    this.addUser(channel, user);
                }
            }
        }
    }


    /**
     * Removes an entire channel from our memory of users.
     */
    private void removeChannel(String channel) {
        channel = channel.toLowerCase();
        synchronized (_channels) {
            _channels.remove(channel);
        }
    }


    /**
     * Removes all channels from our memory of users.
     */
    private void removeAllChannels() {
        synchronized(_channels) {
            _channels = new Hashtable(); // !!!
        }
    }


    private void updateUser(String channel, int userMode, String nick) {
        channel = channel.toLowerCase();
        synchronized (_channels) {
            Hashtable users = (Hashtable) _channels.get(channel);
            User newUser = null;
            if (users != null) {
                Enumeration enumeration = users.elements();
                while(enumeration.hasMoreElements()) {
                    User userObj = (User) enumeration.nextElement();
                    if (userObj.getNick().equalsIgnoreCase(nick)) {
                        if (userMode == OP_ADD) {
                            if (userObj.hasVoice()) {
                                newUser = new User("@+", nick);
                            }
                            else {
                                newUser = new User("@", nick);
                            }
                        }
                        else if (userMode == OP_REMOVE) {
                            if(userObj.hasVoice()) {
                                newUser = new User("+", nick);
                            }
                            else {
                                newUser = new User("", nick);
                            }
                        }
                        else if (userMode == VOICE_ADD) {
                            if(userObj.isOp()) {
                                newUser = new User("@+", nick);
                            }
                            else {
                                newUser = new User("+", nick);
                            }
                        }
                        else if (userMode == VOICE_REMOVE) {
                            if(userObj.isOp()) {
                                newUser = new User("@", nick);
                            }
                            else {
                                newUser = new User("", nick);
                            }
                        }
                    }
                }
            }
            if (newUser != null) {
                users.put(newUser, newUser);
            }
            else {
                // just in case ...
                newUser = new User("", nick);
                users.put(newUser, newUser);
            }
        }
    }


    // Connection stuff.
    private InputThread _inputThread = null;
    private OutputThread _outputThread = null;
    private String _charset = null;
    private InetAddress _inetAddress = null;

    // Details about the last server that we connected to.
    private ConnectionSettings _connectionSettings = null;

    // Outgoing message stuff.
    private Queue _outQueue = new Queue();
    private long _messageDelay = 1000;

    // A Hashtable of channels that points to a selfreferential Hashtable of
    // User objects (used to remember which users are in which channels).
    private Hashtable _channels = new Hashtable();

    // A Hashtable to temporarily store channel topics when we join them
    // until we find out who set that topic.
    private Hashtable _topics = new Hashtable();

    // DccManager to process and handle all DCC events.
    private DccManager _dccManager = new DccManager(this);
    private int[] _dccPorts = null;
    private InetAddress _dccInetAddress = null;

    // Default settings for the PircBot.
    private boolean _autoNickChange = false;
    private boolean _verbose = false;
    private String _name = "PircBot";
    private String _nick = _name;
    private String _username = "PircBot";
    private String _realname = "PircBot";
    private String _version = "PircBot " + VERSION + " Java IRC Bot - www.jibble.org";
    private String _finger = "You ought to be arrested for fingering a pircBot!";

    private String _channelPrefixes = "#&+!";
}
