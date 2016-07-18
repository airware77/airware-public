package xmax.crs.Amadeus;

import org.w3c.dom.Document;

import xmax.TranServer.TranServerException;
import xmax.crs.GdsResponseException;

public interface AmadeusAPICrsConnection
{
	  String getConvHandle();
	  
	  boolean isConnected();
	  void openConnection() throws TranServerException, GdsResponseException;
	  void closeConnection();
	  	  
	  Document sendAndReceive(Document domQuery) throws GdsResponseException;
	  String sendAndReceive(String xmlRequest) throws GdsResponseException;
}
