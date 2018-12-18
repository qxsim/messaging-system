// Each nickname has a different incomming-message queue.
import java.net.*;
import java.io.*;
import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;

public class ClientTable {

	private List<String> loggedIn = new ArrayList<String>();

  	private ConcurrentMap<String,BlockingQueue<Message>> queueTable = new ConcurrentHashMap<String,BlockingQueue<Message>>();

  	// The following overrides any previously existing nickname, and
  	// hence the last client to use this nickname will get the messages
  	// for that nickname, and the previously exisiting clients with that
  	// nickname won't be able to get messages. Obviously, this is not a
  	// good design of a messaging system. So I don't get full marks:

  	public Boolean checkUser(String nickname) {
    	if (loggedIn.contains(nickname)) {
      		return true;
    	}

    	else {
      		return false;
    	}
  	}

  	public void addUser(String nickname) {
    	loggedIn.add(nickname);
  	}

  	public void removeUser(String nickname) {
    	loggedIn.remove(nickname);
  	}

  	public void add(String nickname) {
    	queueTable.put(nickname, new LinkedBlockingQueue<Message>());
  	}

  	public void remove(String nickname) {
    	queueTable.remove(nickname);
  	}

  	public List<String> getLoggedIn() {
  		return loggedIn;
  	}

  	// Returns null if the nickname is not in the table:
  	public BlockingQueue<Message> getQueue(String nickname) {
    	return queueTable.get(nickname);
  	}
}