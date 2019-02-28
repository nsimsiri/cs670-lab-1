import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ITradable extends Remote{
    /***
     * this procedure should search the network; all matching sellers respond to this
     * message with their IDs. The hopcount is decremented at each hop and the message is discarded when it reaches 0.
     * @param productName
     * @param hopCount
     */
    void lookup(String productName, int hopCount, Stack<String>) throws RemoteException;

    /***
     * this is a reply message with the peerID of the seller
     * @param sellerID
     */
    void reply(Long sellerID, Stack<String>) throws RemoteException;

    /***
     *  if multiple sellers respond, the buyer picks one at random, and contacts it directly with the buy message.
     *  A buy causes the seller to decrement the number of items in stock.
     * @param peerID
     */
    void buy(Long peerID) throws RemoteException;

    String getName() throws RemoteException;
}
