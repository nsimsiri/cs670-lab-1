import java.rmi.Remote;

public interface ITradable extends Remote {

    /***
     * this procedure should search the network; all matching sellers respond to this
     * message with their IDs. The hopcount is decremented at each hop and the message is discarded when it reaches 0.
     * @param productName
     * @param hopCount
     */
    public void lookup(String productName, int hopCount);

    /***
     * this is a reply message with the peerID of the seller
     * @param sellerID
     */
    public void reply(Long sellerID);

    /***
     *  if multiple sellers respond, the buyer picks one at random, and contacts it directly with the buy message.
     *  A buy causes the seller to decrement the number of items in stock.
     * @param peerID
     */
    public void buy(Long peerID);
}
