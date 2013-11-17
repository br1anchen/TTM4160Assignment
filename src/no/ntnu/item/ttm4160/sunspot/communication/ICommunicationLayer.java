package no.ntnu.item.ttm4160.sunspot.communication;


public interface ICommunicationLayer {

	/* (non-Javadoc)
	 * @see no.ntnu.item.ttm4160.sunspot.ICommunication#sendRemoteMessage(no.ntnu.item.ttm4160.sunspot.IMessage)
	 */
	public String getCommunicationMac();
	
	public void sendRemoteMessage(Message msg);
	
	public void registerListener(ICommunicationLayerListener listener);
	

}