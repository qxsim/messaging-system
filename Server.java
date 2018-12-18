// Usage:
//        java Server
//
// There is no provision for ending the server gracefully.  It will
// end if (and only if) something exceptional happens.

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Server {

	private static ConcurrentMap<String, User> userTable = new ConcurrentHashMap<String, User>(); //Table of Users
  	public static List<String> userList = new ArrayList<String>(); //List of all registered users

  	public static void userAdd(String nickname, User member) {
  		userTable.put(nickname, member);
  	}

  	public static void userRemove(String nickname) {
  		userTable.remove(nickname);
  	}

  	public static User userGet(String nickname) {
  		return userTable.get(nickname);
  	}

  	public static String nameGen(String nickname) {
  		Random rand = new Random(); 
  		int  n = rand.nextInt(1000) + 1;
		nickname = "abc" + n;
		return nickname;
  	}

  	public static void removeReg(String userReg) {
  		userList.remove(userReg);
  	}

  	public static void addReg(String userReg) {
    	userList.add(userReg);
  	}

  	public static boolean checkReg(String command, String userReg) {
    	if (command.equals("register")) {
      		if (userList.contains(userReg)) {
        		return true;
      		}
      		else {
        		return false;
      		}
    	}
    	return false;
  	}

  	public static boolean checkLogin(String command, String userLogin) {
    	if (command.equals("login")) {
      		if (userList.contains(userLogin)) {
        		return true;
      		}
      		else {
        		return false;
      		}
    	}
    	return false;
  	}

	public static void main(String [] args) {
    
	    // This table will be shared by the server threads:
	    ClientTable clientTable = new ClientTable();
	    User member = new User(null);
	    String current = "";
	    Random rand = new Random(); 
	    ServerSocket serverSocket = null;

	    try {
	    	serverSocket = new ServerSocket(Port.number);
	    } 
	    catch (IOException e) {
	    	Report.errorAndGiveUp("Couldn't listen on port " + Port.number);
	    }
	    
	    try { 
	      	// We loop for ever, as servers usually do.
	    	while (true) {	
	        	// Listen to the socket, accepting connections from new clients:
		        Socket socket = serverSocket.accept(); // Matches AAAAA in Client
			
		        // This is so that we can use readLine():
		        BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		        // We create and start a new thread to write to the client:
		        PrintStream toClient = new PrintStream(socket.getOutputStream());
		     
		        String command = fromClient.readLine();
		        String nameCheck = fromClient.readLine();
		        String nickname = nameCheck; 
		        String loginBypass = "register";
				
		        if (nickname.equals(nameCheck)) {
		         	nickname = nameGen(nickname);
		      	}

		        if (command.equals("register")) {
		          	String userReg = nameCheck;
		          	if (checkReg(command, userReg)) {
		            	toClient.println("Error: This user already exists.");
		            	toClient.println("Your nickname has randomly been assigned to " +nickname +".");
		            	addReg(nickname);
				        clientTable.addUser(nickname);
				        member = new User(nickname);
				        userAdd(nickname, member);
				        member.login();
		          	}

		          	else {
		            	nickname = userReg;
		            	addReg(nickname);
		            	clientTable.addUser(nickname);
		            	member = new User(nickname);
		            	userAdd(nickname, member);
		            	member.login();
		          	}
		        }

		        else if (command.equals("login")) {
			        String userLogin = nameCheck;
			        if (checkLogin(command, userLogin) && (!(clientTable.checkUser(userLogin)))) {
			            nickname = userLogin;
			            clientTable.addUser(nickname);
			            userGet(nickname);
			            member.login();
			            toClient.println(member.getCurrent());			            
			        }

			        else if (!(checkLogin(command, userLogin))) {
			        	toClient.println("Error: This user does not exist.");
				        toClient.println("Your nickname has randomly been assigned to " +nickname +".");
				        addReg(nickname);
				        clientTable.addUser(nickname);
				        member = new User(nickname);
				        userAdd(nickname, member);
				        member.login();
			        }

			        else if (clientTable.checkUser(userLogin)) {
			        	toClient.println("Error: This user is already logged in.");
				        toClient.println("Your nickname has randomly been assigned to " +nickname +".");
				        addReg(nickname);
				        clientTable.addUser(nickname);
				        member = new User(nickname);
				        userAdd(nickname, member);
				        member.login();
			        }			        
		        }
		    
		        else {
		  
		        }

		        // We ask the client what its name is:
		        String clientName = nickname; // Matches BBBBB in Client

		        Report.behaviour(clientName + " connected");
		        
		        // We add the client to the table:
		        clientTable.add(clientName);

		        // We create and start a new thread to write to the client:
		        //PrintStream toClient = new PrintStream(socket.getOutputStream());
		        ServerSender serverSender = new ServerSender(clientTable.getQueue(clientName), toClient, clientTable.checkUser(clientName));
		        serverSender.start();
		        
		        // We create and start a new thread to read from the client:
		        (new ServerReceiver(clientName, fromClient, clientTable, clientTable.checkUser(clientName), userGet(clientName), serverSender, toClient)).start();
	      	}
	    } 
	    catch (IOException e) {
	    	// Lazy approach:
	    	Report.error("IO error " + e.getMessage());
	    	// A more sophisticated approach could try to establish a new
	    	// connection. But this is beyond the scope of this simple exercise.
	    }
	} 
}