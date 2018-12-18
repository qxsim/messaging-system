import java.net.*;
import java.io.*;
import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;

// Continuously reads from message queue for a particular client,
// forwarding to the client.

public class ServerSender extends Thread {
	
	private BlockingQueue<Message> clientQueue;
	private PrintStream client;
 	private Boolean loginCheck;

  	public ServerSender(BlockingQueue<Message> q, PrintStream c, Boolean b) {
    	clientQueue = q;   
    	client = c;
    	loginCheck = b;
  	}

  	public void run() {
    
    	try {
      		while (true) {
        		Message msg = clientQueue.take(); // Matches EEEEE in ServerReceiver
        		client.println(msg); // Matches FFFFF in ClientReceiver
      		}
    	}

    	catch (InterruptedException e) {
      		Report.behaviour("Server sender ending");
      		// Do nothing and go back to the infinite while loop.
    	}
  	}
}
/*

 * Throws InterruptedException if interrupted while waiting

 * See https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html#take--

 */