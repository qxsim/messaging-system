import java.io.*;


// Repeatedly reads recipient's nickname and text from the user in two
// separate lines, sending them to the server (read by ServerReceiver
// thread).

public class ClientSender extends Thread {

	private String nickname;
	private PrintStream server;

	ClientSender(String nickname, PrintStream server) {
		this.nickname = nickname;
	    this.server = server;
	}

  	public void run() {
    	// So that we can use the method readLine:
	    BufferedReader user = new BufferedReader(new InputStreamReader(System.in));

	    try {
	      // Then loop forever sending messages to recipients via the server:
	      	while (true) {
	      		String cmd = user.readLine();

	      		//checks if the commands are valid.
	      		if (cmd.equals("quit") || cmd.equals("logout") || cmd.equals("send") || cmd.equals("previous") || cmd.equals("next") || cmd.equals("delete")) {

	      			if (cmd.equals("quit")) {
	      				Report.errorAndGiveUp("You have request to quit the chat.");
	      				server.println(cmd);
			        }

			        else if (cmd.equals("logout")) {
			        	Report.errorAndGiveUp("You have request to logout.");
			        	server.println(cmd);
			        }

			        else if (cmd.equals("previous") || cmd.equals("next") || cmd.equals("delete")) {
			        	if (cmd.equals("delete")) {
			        		Report.behaviour("This message has been deleted.");
			        		server.println(cmd);
			        		continue;
			        	}
			        	server.println(cmd);
	      				continue;
	      			}

	      			// only allows for recipient and text input if 'send' is called
		      		else if (cmd.equals("send")) {
		      			server.println(cmd);

		      			String recipient = user.readLine();
				        server.println(recipient); // Matches CCCCC in ServerReceiver


				        String text = user.readLine();
				        server.println(text); // Matches DDDDD in ServerReceiver
				        continue;
	      			}

	      			else {

	      			}
	      		}
				
	      		else {
	      			Report.error("Please enter a valid command. These are 'send, 'logout', 'quit', 'previous', 'next' and 'delete'.");
	      		}
	      	}
	    }
	    
	    catch (IOException e) {
	      	Report.errorAndGiveUp("Communication broke in ClientSender"+ e.getMessage());
	    }
	}
}

/*

What happens if recipient is null? Then, according to the Java
documentation, println will send the string "null" (not the same as
null!). So maye we should check for that case! Paticularly in
extensions of this system.

 */