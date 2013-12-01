package amp.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;

public class PresentationMessageInbound extends MessageInbound{

	// clients subscribed
	private Set<WsOutbound> webSockets;
	
	private String score = "0-0";
	
	public PresentationMessageInbound() {
		super();
		this.webSockets = new HashSet<WsOutbound>();
	}
	
	@Override
	protected void onBinaryMessage(ByteBuffer message) throws IOException {
		throw new UnsupportedOperationException("Binary message not supported.");
	}

	@Override
	protected void onTextMessage(CharBuffer message) throws IOException {
		CharBuffer messageAltered = refreshNbClientsInMessage( message );
		for (WsOutbound connection : webSockets) {
			CharBuffer messagecopied = CharBuffer.wrap(messageAltered);
			System.out.println("for : " + connection.hashCode());
			try {
				connection.writeTextMessage(messagecopied);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onOpen(WsOutbound outbound) {
		System.out.println("Add");
		webSockets.add(outbound);
		try {
			onTextMessage(CharBuffer.wrap( ID ));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	protected void onClose(int status) {
		System.out.println("Remove");
		webSockets.remove(this.getWsOutbound());
		try {
			onTextMessage(CharBuffer.wrap( ID ));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Renvoi un char buffer avec ID-${nombre de clients}
	 * utile pour un message contenant la clef ID
	 * @param message
	 * @return
	 */
	private final String ID = "userCo-";
	private CharBuffer refreshNbClientsInMessage( CharBuffer message ){
		CharBuffer messagecopied = CharBuffer.wrap(message);
		String mes = messagecopied.toString();
		if (mes.contains( ID )) {
			messagecopied = CharBuffer.wrap( ID + webSockets.size() );
		}
		else if (mes.contains("broadcast")){
			if (!createOne.isAlive()) createOne.run();
		}
		return messagecopied;
	}
	
	Thread createOne = new Thread() {
	    public void run() {
            while (true){
            	updateScore();
            }
	    }  
	};
	
	private String matchScore = "0-0";
	
	private static final Integer point = 15;
	
	public void updateScore(){
		try {
			Random rand = new Random();
			int secs = rand.nextInt((10 - 3) + 3);
			Thread.sleep(secs*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// who score ?
		Random rand = new Random();
	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int who = rand.nextInt((1 - 0) + 1);
		System.out.println(who);
		String[] tabScore = matchScore.split("-");
		Integer newScore = new Integer(tabScore[ who ]) + point;
		if (newScore == 45) {
			newScore = 40;
		}
		String matchScoreString;
		if (newScore > 40) {
			matchScore = "0-0";
			matchScoreString = "0-0";
		}
		else {
			tabScore[ who ] = newScore.toString();
			if ( tabScore[0].equalsIgnoreCase(tabScore[1]) && !tabScore[0].equalsIgnoreCase("0") ){
				matchScoreString =  tabScore[0] + "-A";
			}
			else {
				matchScoreString = tabScore[0] + "-" + tabScore[1];
			}
			matchScore = tabScore[0] + "-" + tabScore[1];
		}
		
		//MAJ clients
		try {
			onTextMessage(CharBuffer.wrap("score_"+matchScoreString));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
}
