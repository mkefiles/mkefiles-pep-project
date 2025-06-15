package Controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. 
 * The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        
        // ST01: Create a new account (Method: POST - URL: /register)
        app.post("register", this::registerHandler);
        
        // ST02: Process user logins (Method: POST - URL: /login)
        app.post("login", this::loginHandler);
        
        // ST03: Process new blog messages (Method: POST - URL: /messages)
        app.post("messages", this::newBlogHandler);
        
        // ST04: Retrieve all messages (Method: GET - URL: /messages)
        app.get("messages", this::retrieveAllMessagesHandler);
        
        // ST05: Retrieve message by ID (Method: GET - URL: /messages/{message_id})
        app.get("messages/{message_id}", this::retrieveMessageByIDHandler);
        
        // ST06: Delete message by ID (Method: DELETE - URL: /messages/{message_id})
        app.delete("messages/{message_id}", this::removeMessageByIDHandler);
        
        // ST07: Update message by ID (Method: PATCH - URL: /messages/{message_id})
        app.patch("messages/{message_id}", this::updateMessageById);
        
        // ST08: Retrieve messages by user (Method: GET - URL: /accounts/{account_id}/messages)
        app.get("accounts/{account_id}/messages", this::retrieveMessagesByUserHandler);
        
        return app;
    }
    
    // New account handler
    private void registerHandler(Context context) throws SQLException {
    	// Create instance of AccountService()
    	AccountService accountService = new AccountService();
    	
    	// Obtain JSON input from end-user and 'deserialize' to Account instance
    	Account account = context.bodyAsClass(Account.class);
    	
    	// Feed 'account' to Service, which will verify data then pass to DAO
    	if(accountService.verifyAndCreateAccount(account) == true) {

    		// Registration was successful...
    		context.status(200);
    		
    		// Output JSON String of Account (incl. Account ID)
    		context.json(account);
    		
    	} else {
    		// Registration failed...
    		context.status(400);
    	}
    }
    
    private void loginHandler(Context context) throws SQLException {
    	// Create instance of AccountService()
    	AccountService accountService = new AccountService();
    	
    	// Obtain JSON input from end-user and 'deserialize' to Account instance
    	Account account = context.bodyAsClass(Account.class);
    	
    	// Feed 'account' to Service, which will verify data then pass to DAO
    	if(accountService.loginAccount(account) == true) {
    		// Login was successful...
    		context.status(200);
    		
    		// Output JSON String of Account (incl. Account ID)
    		context.json(account);
    		
    	} else {
    		// Login unsuccessful (unauthorized)
    		context.status(401);
    	}
    }
    
    // Create new blog-message handler
    private void newBlogHandler(Context context) throws SQLException {
    	// Create instance of AccountService()
    	MessageService messageService = new MessageService();
    	
    	// Obtain JSON input from end-user and 'deserialize' to Message instance
    	Message message = context.bodyAsClass(Message.class);
    	
    	// Feed 'message' to Service, which will verify data then pass to DAO
    	if (messageService.createNewBlogMessage(message) == true) {    		
    		// Message-post was successful
    		context.status(200);
    		
    		// Output message information
    		context.json(message);
    	} else {
    		// Message-post failure
    		context.status(400);
    	}
    }
    
    // Retrieve all messages handler
    private void retrieveAllMessagesHandler(Context context) throws SQLException {
    	// Create instance of AccountService()
    	MessageService messageService = new MessageService();
    	
    	// An empty ArrayList of Message objects
    	List<Message> allMessages = new ArrayList<>();
    	
    	// Fetch messages
    	messageService.getAllMessages(allMessages);
    	
    	// Output HTTP status for success
    	context.status(200);
    	
    	// Output all messages
    	context.json(allMessages);
    }
    
    // Retrieve messages by ID handler
    private void retrieveMessageByIDHandler(Context context) throws SQLException {
    	// Create instance of AccountService()
    	MessageService messageService = new MessageService();
    	
    	// Create an instance of Message()
    	Message message = new Message();
    	
    	// Get ID from URL
    	int messageId = Integer.parseInt(context.pathParam("message_id")); 
    	
    	// Fetch message by id (if applicable)
    	messageService.getMessageById(messageId, message);
    	
    	// Determine if message contains any values
    	if (message.getMessage_text() != null) {
        	// Output HTTP status for success
        	context.status(200);
        	
    		// Message is not empty, so send it back to end-user
    		context.json(message);
    	} else {
        	// Output HTTP status for success
        	context.status(200);
    	}
    }
    
    // Delete message by ID handler
    private void removeMessageByIDHandler(Context context) throws SQLException {
    	// Create instance of AccountService()
    	MessageService messageService = new MessageService();
    	
    	// Create an instance of Message()
    	Message message = new Message();
    	
    	// Get ID from URL
    	int messageId = Integer.parseInt(context.pathParam("message_id")); 
    	
    	// Fetch message by id (if applicable)
    	messageService.removeMessageById(messageId, message);
    	
    	// Determine if message contains any values
    	if (message.getMessage_text() != null) {
        	// Output HTTP status for success
        	context.status(200);
        	
    		// Message is not empty, so send it back to end-user
    		context.json(message);
    	} else {
        	// Output HTTP status for success
        	context.status(200);
    	}
    }
    
    // Update message by ID handler
    private void updateMessageById(Context context) throws SQLException{
    	// Create instance of AccountService()
    	MessageService messageService = new MessageService();
    	
    	// Obtain JSON input from end-user and 'deserialize' to Message instance
    	Message message = context.bodyAsClass(Message.class);
    	
    	// Get ID from URL
    	int messageId = Integer.parseInt(context.pathParam("message_id")); 
    	
    	// Fetch message by id (if applicable)
    	messageService.updateMessageById(messageId, message);
    	
    	// Determine if message contains any values
    	if (message.getMessage_text() != null) {
    		// Output HTTP status for success
    		context.status(200);
    		
    		// Message is not empty, so send it back to end-user
    		context.json(message);
    	} else {
    		// Output HTTP status for failure
    		context.status(400);
    	}
    }
    
    // Retrieve messages by user handler
    private void retrieveMessagesByUserHandler(Context context) throws SQLException {
    	// Create instance of AccountService()
    	MessageService messageService = new MessageService();
    	
    	// An empty ArrayList of Message objects
    	List<Message> allMessages = new ArrayList<>();    
    	
    	// Get ID from URL
    	int postedById = Integer.parseInt(context.pathParam("account_id"));

    	// Fetch messages
    	messageService.getAllMessagesByAccountId(allMessages, postedById);
    	
    	// Output HTTP status for success
    	context.status(200);
    	
    	// Output all messages
    	context.json(allMessages);
    }
}