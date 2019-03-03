import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Stack;

public interface IPeer extends Remote{
    /***
     * 128.119.202.183
     * this procedure should search the network; all matching sellers respond to this
     * message with their IDs. The hopcount is decremented at each hop and the message is discarded when it reaches 0.
     * @param lookup
     * @param hopCount
     */
    void lookup(Lookup lookup, int hopCount,
                        Stack<String> path, List<String> potentialSellers) throws RemoteException;

    /***
     * this is a reply message with the peerID of the seller
     * @param sellerID
     */
    public void reply(String sellerID, Lookup lookup, Stack<String> stack,
                      List<String> potentialSellers) throws RemoteException;

    /***
     *  if multiple sellers respond, the buyer picks one at random, and contacts it directly with the buy message.
     *  A buy causes the seller to decrement the number of items in stock.
     * @param peerID
     */
    void buy(String peerID, ItemType productName) throws RemoteException;

//    void completeTransaction(String buyerName) throws RemoteException;


    String getName() throws RemoteException;
}
