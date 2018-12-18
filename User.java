import java.util.*;
import java.util.ArrayList;
import java.util.List;

public class User {
	
	private String nickname;
	private List<String> senderStore = new ArrayList<String>();
	private List<String> messageStore = new ArrayList<String>();
	private Boolean loginCheck;
	private String currentSender = "";
	private String currentMessage = "";

	public User(String nickname) {
		this.nickname = nickname;
	}

	public Boolean login() {
		loginCheck =  true;
		return loginCheck;
	}

	public Boolean logout() {
		loginCheck = false;
		return loginCheck;
	}

	public Boolean loginStatus() {
		return loginCheck;
	}


	public void addMessage(String sender, String message) {
		senderStore.add(sender);
		messageStore.add(message);
		currentSender = sender;
		currentMessage = message;
	}

	public String deleteMessage() {
		senderStore.remove(currentSender);
		messageStore.remove(currentMessage);
		getPrev();

		if (currentMessage.equals("There is no previous message.")) {
			getNext();
			if (currentMessage.equals("There is no next message.")) {
				currentMessage = "There are no more messages to display.";
			}
		}

		else {

		}
		return currentMessage;
	}

	public String getCurrent() {
		String currMsg = "There is no current message.";
		currentMessage = currMsg;
		if(!(messageStore.isEmpty())) {
			currentMessage = messageStore.get(messageStore.size()-1);
			currentSender = senderStore.get(senderStore.size()-1);
			currMsg = "From " + currentSender + ": " + currentMessage;
		}
		return currMsg;
	}

	public String getNext() {
		String nextMsg = "";
		try {
			int currentPosition = messageStore.indexOf(currentMessage);
			currentMessage = messageStore.get(currentPosition+1);
			currentSender = senderStore.get(currentPosition+1);
			nextMsg = "From " + currentSender + ": " + currentMessage;
			currentMessage = nextMsg;
		}

		catch(ArrayIndexOutOfBoundsException n) {
			nextMsg = "There is no next message.";
			currentMessage = nextMsg;
		}

		catch(IndexOutOfBoundsException n) {
			nextMsg = "There is no next message.";
			currentMessage = nextMsg;
		}
		return nextMsg;	
	}

	public String getPrev() {
		String prevMsg = "";
		try {
			int currentPosition = messageStore.indexOf(currentMessage);
			currentMessage = messageStore.get(currentPosition-1);
			currentSender = senderStore.get(currentPosition-1);
			prevMsg = "From " + currentSender + ": " + currentMessage;
			currentMessage = prevMsg;
		}

		catch(ArrayIndexOutOfBoundsException n) {
			prevMsg = "There is no previous message.";
			currentMessage = prevMsg;
		}

		catch(IndexOutOfBoundsException n) {
			prevMsg = "There is no previous message.";
			currentMessage = prevMsg;
		}
		return prevMsg;	
	}
}