/**
 * This class is called whenever a DCC SEND request is sent to the PircBot.
 * This means that a client has requested to send a file to us.
 * This abstract implementation performs no action, which means that all
 * DCC SEND requests will be ignored by default. If you wish to receive
 * the file, then you may override this method and call the receive method
 * on the DccFileTransfer object, which connects to the sender and downloads
 * the file.
 *  <p>
 * Example:
 * <pre> public void onIncomingFileTransfer(DccFileTransfer transfer) {
 *     // Use the suggested file name.
 *     File file = transfer.getFile();
 *     // Receive the transfer and save it to the file, allowing resuming.
 *     transfer.receive(file, true);
 * }</pre>
 *  <p>
 * <b>Warning:</b> Receiving an incoming file transfer will cause a file
 * to be written to disk. Please ensure that you make adequate security
 * checks so that this file does not overwrite anything important!
 *  <p>
 * Each time a file is received, it happens within a new Thread
 * in order to allow multiple files to be downloaded by the PircBot
 * at the same time.
 *  <p>
 * If you allow resuming and the file already partly exists, it will
 * be appended to instead of overwritten.  If resuming is not enabled,
 * the file will be overwritten if it already exists.
 *  <p>
 * You can throttle the speed of the transfer by calling the setPacketDelay
 * method on the DccFileTransfer object, either before you receive the
 * file or at any moment during the transfer.
 *  <p>
 * The implementation of this method in the PircBot abstract class
 * performs no actions and may be overridden as required.
 *
 * @since PircBot 1.2.0
 *
 * @param transfer The DcccFileTransfer that you may accept.
 *
 * @see DccFileTransfer
 *
 */

package org.jibble.pircbot.handlers;

import org.jibble.pircbot.DccFileTransfer;
import org.jibble.pircbot.IrcServerConnection;

public class OnIncomingFileTransferEventHandler extends AbstractEventHandler {
    DccFileTransfer transfer ;

    public OnIncomingFileTransferEventHandler(IrcServerConnection pircBot, DccFileTransfer transfer){
        super.pircBot = pircBot;
        this.transfer = transfer;
    }

}
