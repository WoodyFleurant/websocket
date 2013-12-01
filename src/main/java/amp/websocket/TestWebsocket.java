package amp.websocket;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;

@WebServlet("/testWebsocket/*")
public class TestWebsocket extends WebSocketServlet {
	private static final long serialVersionUID = 1L;

	// SINGLETON
	private StreamInbound INSTANCE = null;
	
	public StreamInbound getSocketInbound(){
		if (INSTANCE == null){
			INSTANCE = new PresentationMessageInbound();
		}
		return INSTANCE;
	}
	
	@Override
	protected StreamInbound createWebSocketInbound(String arg0, HttpServletRequest arg1) {
		return getSocketInbound();
	}
}