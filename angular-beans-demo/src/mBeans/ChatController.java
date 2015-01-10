package mBeans;

import java.io.Serializable;

import javax.inject.Inject;

import angularBeans.api.NGController;
import angularBeans.api.NGSubmit;
import angularBeans.context.NGSessionScoped;
import angularBeans.wsocket.WSocketClient;
import angularBeans.wsocket.WSocketMessage;
import angularBeans.wsocket.WebSocket;
import angularBeans.wsocket.annotations.Subscribe;

//@Named("chatController")
@NGController
@NGSessionScoped
@Subscribe(channels={"chatChannel"})
public class ChatController implements Serializable {
	
	private String newMessage;
	private String sender;
	
	
	@Inject
	WSocketClient client;
	
	@WebSocket
	@NGSubmit()
	public void send(){
		
		
		ChatMessage chatMessage=new ChatMessage();
		
		chatMessage.setSender(sender);
		chatMessage.setMessage(newMessage);
		
		client.publishToAll("chatChannel", new WSocketMessage()
		.add("receivedMesssage", chatMessage)
		
				);
	}

	

	public String getNewMessage() {
		return newMessage;
	}

	public void setNewMessage(String newMessage) {
		this.newMessage = newMessage;
	}




	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}


	



}
