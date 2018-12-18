// Usage:
//        java Client user-nickname server-hostname
//
// After initializing and opening appropriate sockets, we start two
// client threads, one to send messages, and another one to get
// messages.
//
// A limitation of our implementation is that there is no provision
// for a client to end after we start it. However, we implemented
// things so that pressing ctrl-c will cause the client to end
// gracefully without causing the server to fail.
//
// Another limitation is that there is no provision to terminate when
// the server dies.


import java.io.*;
import java.net.*;
import java.util.*;

class Client {

  	public static boolean checkInput(String nickname) { //used to check if a username is valid.
  		String[] restrictedNames = {"quit", "register", "login", "logout", "send", "previous", "next", "delete"};
    
    	for(int i=0; i<restrictedNames.length; i++) {
    		if(nickname.equals(restrictedNames[i])) {
        		return true;
      		}
    	}
    	return false; 
  	}

  	public static void main(String[] args) {
    
		Scanner scanner = new Scanner( System.in );
	    Random rand = new Random(); 
	    
	    // Check correct usage:
	    if (args.length != 1) {
	    	Report.errorAndGiveUp("Usage: java Client <server machine>");
	    }
	    
	    // Initialize information:
	    String hostname = args[0];
	   
	   // Open sockets:
	    PrintStream toServer = null;
	    BufferedReader fromServer = null;
	    Socket server = null;

	    String command = scanner.nextLine().toLowerCase(); 
	    String nickname = scanner.nextLine();


	    if (command.equals("register") || command.equals("login")) {
	    	command = command;
	    }
	    
	    else {
	    	Report.errorAndGiveUp("Usage: Enter 'register' to register a new user, or 'login' to login.");
	    }

	    try {
	      	server = new Socket(hostname, Port.number); // Matches AAAAA in Server.java
	      
	      	if (checkInput(nickname)) { //assigns a new username in the form abcX, if a username is invalid.
	        	int  n = rand.nextInt(1000) + 1;
	         	nickname = "abc" + n;
	        	Report.error("This is not a valid nickname. Your nickname has randomly been assigned to " +nickname +".");
	      	}

	      	toServer = new PrintStream(server.getOutputStream());
	      	fromServer = new BufferedReader(new InputStreamReader(server.getInputStream()));
	    } 
	    
	    catch (UnknownHostException e) {
	      	Report.errorAndGiveUp("Unknown host: " + hostname);
	    }

	    catch (IOException e) {
	      	Report.errorAndGiveUp("The server doesn't seem to be running " + e.getMessage());
	    }

	    // Tell the server what my command is:
	    toServer.println(command);
	    
	    // Tell the server what my nickname is:
	    toServer.println(nickname); // Matches BBBBB in Server.java
	     
	    // Create two client threads of a diferent nature:
	    ClientSender sender = new ClientSender(nickname,toServer);
	    ClientReceiver receiver = new ClientReceiver(fromServer);

	    // Run them in parallel:
	    sender.start();
	    receiver.start();
	    
	    // Wait for them to end and close sockets.
	    try {
	      	sender.join();
	      	toServer.close();
	      	receiver.join();
	      	fromServer.close();
	      	server.close();
	    }

	    catch (IOException e) {
	      	Report.errorAndGiveUp("Something wrong " + e.getMessage());
	    }

	    catch (InterruptedException e) {
	      	Report.errorAndGiveUp("Unexpected interruption " + e.getMessage());
	    }
  	}
}