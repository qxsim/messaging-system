import java.net.*;
import java.io.*;
import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;

// Gets messages from client and puts them in a queue, for another
// thread to forward to the appropriate client.

public class ServerReceiver extends Thread {
	
	private String myClientsName;
	private BufferedReader myClient;
	private ClientTable clientTable;
	private Boolean loginCheck;
	public User member;
	private ServerSender companion;
	private PrintStream toClient;

	public ServerReceiver(String n, BufferedReader c, ClientTable t, Boolean b, User u, ServerSender s, PrintStream p) {
    	myClientsName = n;
    	myClient = c;
    	clientTable = t;
    	loginCheck = b;
    	member = u;
    	companion = s;
    	toClient = p;
 	}

	public void run() {
	  	try {
	    	while (true) {
	    		String userInput = myClient.readLine();
		        	
		        if (userInput == null) {
		        	break;
		        }

	        	else if (userInput.equals("quit")) {
	        		clientTable.remove(myClientsName);
		       		clientTable.removeUser(myClientsName);
		        	Server.removeReg(myClientsName);
		        	member.logout();
		        	Server.userRemove(myClientsName);
		        	Report.behaviour(myClientsName +" has quit the chat.");
		        	break;
	        	}

	        	else if (userInput.equals("logout")) {
	            	clientTable.removeUser(myClientsName);
		       		member.logout();
		       		Report.behaviour(myClientsName +" has logout.");
		       		continue;
	        	}

	        	else if (userInput.equals("previous")) {
	        		toClient.println(member.getPrev());
	        		continue;
	        	}

	        	else if (userInput.equals("next")) {
	        		toClient.println(member.getNext());
	        		continue;
	        	}

	        	else if (userInput.equals("delete")) {
	        		toClient.println(member.deleteMessage());
	        		continue;
	        	}

	        	else {

	        	}

	        	String recipient = myClient.readLine(); // Matches CCCCC in ClientSender.java

	        	if (recipient == null) {
		        	break;
		        }

		        else {

		        }

	        	String text = myClient.readLine();      // Matches DDDDD in ClientSender.java

	        	User recipientUsr = Server.userGet(recipient);
	        	
				if (text != null) {
          			Message msg = new Message(myClientsName, text);
          			BlockingQueue<Message> recipientsQueue = clientTable.getQueue(recipient); // Matches EEEEE in ServerSender.java
          			if (recipientsQueue != null) {
            			recipientsQueue.offer(msg);
            			recipientUsr.addMessage(myClientsName, text);
          			} 
          			
          			else {
            			Report.error("Message for unexistent client " + recipient + ": " + text);
         			}
       			} 

       			else {
          			// No point in closing socket. Just give up.
          			return;
        		}
			}
			return;	        
	    }
	    
	    catch (IOException e) {
	      Report.error("Something went wrong with the client " + myClientsName + " " + e.getMessage()); 
	      // No point in trying to close sockets. Just give up.
	      // We end this thread (we don't do System.exit(1)).
	    }
	    companion.interrupt();
	}
}