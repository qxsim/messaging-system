# My Messaging System Solution

## Client

I introduced a method called checkInput(String nickname), which takes a nickname as an argument, and checks it against a list of pre-determined restiricted usernames. If the username in question is found to match any of the restricted usernames, then it will return true, and if not, then it will return false.

The method is shown below:

	public static boolean checkInput(String nickname) {
		String[] restrictedNames = {"quit", "register", "login", "logout", "send", "previous", "next", "delete"};
    	
    	for(int i=0; i<restrictedNames.length; i++) {
    		if(nickname.equals(restrictedNames[i])) {
        		return true;
      		}
    	}
    	return false; 
	}

Only a maximum of 1 argument is allowed to entered for the Client class, and this is enforced by the following code:

	if (args.length != 1) {
		Report.errorAndGiveUp("Usage: java Client <server machine>");
	}

The only initial commands allowed by the Client are either 'register' and 'login', and an error will be returned (and the thread killed) if this rule is violated.

I dealt with the problem of not allowing restricted usernames by allocating a random username in the format abcX, where X is a random number between 1 and 1000. This reassigned nickname is then processed by the rest of the program as normal.

For instance, if a user is to enter:

	$ java Client localhost
	register
	quit

The following snippet of code will reassign quit to a valid username:

	if (checkInput(nickname)) {
		int  n = rand.nextInt(1000) + 1;
		nickname = "abc" + n;
		Report.error("This is not a valid nickname. Your nickname has randomly been assigned to " +nickname +".");
	}

This is neccessary to avoid any conflicts in messaging a user, who may happen to be named 'quit', and calling the quit function.

My approach to the register command, is that once a user has registered it will automatically also log that particular user in. If an attempt is made to register a user who is already registered, an error message will be returned, as well as a random username being generated for the user in question.

	register
	qasim

The above is a valid example of how to use the register command.

	login
	qasim

The above is a valid example of how to use the login command.

My approach to the login command is that, no more than one person may log in at a time on the Server, and an error message will be returned if an attempt is made. A random username will also be generated in this case.

## ClientSender

My CLientSender class sends 3 lines to the server. These are command(cmd), recipient of the message(recipient) and the message itself(text).
It carries out a series of checks and performs the appropriate actions, based on the input of the allowed commands. 
It only allows for the recipient and text lines to be read, if the 'send' command is entered.
All of the lines that are read by the ClientSender are then sent to the Server via the ServerReceiver.

An example of one of the checks as described above:

	if (cmd.equals("quit")) {
	    Report.errorAndGiveUp("You have request to quit the chat.");
	   	server.println(cmd);
	}


In order to send a message one must use the following format:

	send
	recipient
	message

## ClientReceiver

My ClientReceiver class is unchanged from the one given in the sample 'quit' solution, although I have made some changes in formatting the code slightly differently, by making the indentation consistent, and making the code easier to read.

## Message

My Message class is unchanged from the one given in the sample 'quit' solution, although I have made some changes in formatting the code slightly differently, by making the indentation consistent, and making the code easier to read.

## Port

My Port class is unchanged from the one given in the sample 'quit' solution, although I have made some changes in formatting the code slightly differently, by making the indentation consistent, and making the code easier to read.

## Report

My Report class is unchanged from the one given in the sample 'quit' solution, although I have made some changes in formatting the code slightly differently, by making the indentation consistent, and making the code easier to read.

## ServerSender

My ServerSender class is unchanged from the one given in the sample 'quit' solution, although I have made some changes in formatting the code slightly differently, by making the indentation consistent, and making the code easier to read.

## ClientTable

My ClientTable class has had an ArrayList added to it called loggedIn, which as the name suggests keeps track of all the currently logged-in registered users. There are also some supporting methods that perform actions on this ArrayList, such as checkUser, addUser, removeUser, getLoggedIn etc. These all perform actions that check the content of, or modify the loggedIn list.

## User

I have also created a new class called User. This class is designed to help keep track of all the messages received by any user that has been registered on the Server. It also has the function of being able to keep track of the login status of any User on the Server too. The User class is where the next, previous and delete commands are all proccessed. The delete method is show below:

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

This method shows that once I have deleted a message, it will attempt to show the previous message, and if this fails, then it will attempt to show the next message, and if this also happens to fail, then it will print out the following statement:

	"There are no more messages to display."

The User class also keeps track of the currently selected message, so that you can search through every single message in a conversation by calling previous/next multiple times.

The User class has a store of every single messsage sent to that particular user, as well as the sender who sent the message. The messages are stored in an ArrayList, as well as the sender. The deleteMessage() method removes elements from these ArrayLists, whilst the addMessage() method adds elements to these ArrayLists.

## Server

In my Server, one of the changes I have made is introducing the User class. I created a ConcurrentHashMap (called userTable) to store all instantiations of my User Class, in which the User will always be called 'member', as values, and the nickname of the specified User, as its key.

I also have a standard ArrayList called userList, in which all currently registered users are all stored.

I then have a series of methods that perform appropriate actions on these 2 data structures.

My 'login' and 'register' requests are also handled here, rather than in the ServerReciever, where all my other requests are handled. My reason being, that the Server threads are only created once the 'login' or 'register' commands are processed.

In both of my 'login' and 'register' checks, a random user is generated, if the checks fail for either of them. My reasoning for this being, that a user will be able to start to use the chat system as quickly as possible, if they are having trouble with the register/login procedure, similar to how usernames are usually suggested on many websites, when your username of choice is already taken, or unavailable for any reason.

It is, in every other respect, the same as the Server class given to us in the sample solution.

## ServerReceiver

In my ServerReceiver, I read 3 lines from the client. These are userInput, recipient, and text, respecitvely. userInput deals with the commands: quit, logout, previous, next and delete. 

If 'quit' is called, the user in question is removed from the clientTable, is logged out of the clientTable, is removed from the userList on the Server, is marked as logged out in its User class, is removed from the userTable on the server, and is prompted that they have chosen to quit the chat.

If 'logout' is called, the user in question is logged out of the clientTable, is marked as logged out in its User class, and is promnpted that they have chosen to logout of the chat.

If 'previous' is called, the getPrev() method from its respective User class is called, and the previous message is printed out in the client window. If there is no previous message, an appropriate message will be displayed instead.

If 'next' is called, the getNext() method from its respective User class is called, and the next message is printed out in the client window. If there is no next message, an appropriate message will be displayed instead.

If 'delete' is called, the deleteMessage() method is called from its respective User class, and its function is described above in the User section. It will print out the result to the client window.

If none of these commands are called, then it is assumed 'send' is called, since it is the only other command that will be allowed by the clientSender, without returning an error. 

After this, the recipient and text lines are read from the client, as normal. The queue for the recipient is retrieved and the lastest message is added to the head of the queue. The message is also added to a list of messages stored on the User class for the recipient. The BlockingQueue is then passed to the ServerSender and the rest works as expected, and as it does in the 'quit' sample solution.

## Conclusion

The above describes a working solution, with some thorough testing, and all implemented functions work as intented.