package DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import Model.Message;
import Util.ConnectionUtil;

public class MessageDAO {
	// Create an instance of the ConnectionUtil() object (for database connectivity)
	ConnectionUtil connection = new ConnectionUtil();
	
	// A Constructor-method appears to be unnecessary (see WK04 : D04 : Data Access Object)

	public boolean createMessage(Message message) throws SQLException {
		// Open a database connection (using ConnectionUtil() singleton)
		ConnectionUtil.getConnection();
		
		// Check if account_id exists BEFORE proceeding
		String unsafeSql = "SELECT account_id FROM account WHERE account_id = ?";
		PreparedStatement safeSql = ConnectionUtil.getConnection().prepareStatement(unsafeSql);
		safeSql.setInt(1, message.getPosted_by());
		ResultSet rs = safeSql.executeQuery();
		int numOfEntries = 0;
		while(rs.next()) {
			numOfEntries = numOfEntries + 1;
		}
		
		// If number of entries is zero, then the user must not exist
		if (numOfEntries == 0) {
			// Close the database connection
			ConnectionUtil.getConnection().close();
			return false;
		} else {
			// account_id exists, so proceed with message insertion...
			// Insert the message to the database
			unsafeSql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
			safeSql = ConnectionUtil.getConnection().prepareStatement(unsafeSql);
			safeSql.setInt(1, message.getPosted_by());
			safeSql.setString(2, message.getMessage_text());
			safeSql.setLong(3, message.getTime_posted_epoch());
			numOfEntries = safeSql.executeUpdate();
			
			// Get ID of most recently inserted value (works for single-connection situations ONLY)
			unsafeSql = "SELECT * FROM message WHERE message_id = (SELECT MAX(message_id) FROM message)";
			safeSql = ConnectionUtil.getConnection().prepareStatement(unsafeSql);
			rs = safeSql.executeQuery();
			int messageId = 0;
			while(rs.next()) {
				messageId = rs.getInt("message_id");
			}
			
			// Determine if insert was successful
			if(numOfEntries == 1) {
				// Set message-instance message_id to the max ID found in database (should be most recent)
                message.setMessage_id(messageId);
				
				// Close a database connection (using ConnectionUtil())
				ConnectionUtil.getConnection().close();
				return true;
			} else {
				// Close a database connection (using ConnectionUtil())
				ConnectionUtil.getConnection().close();				
				return false;
			}
		}
	}
	
	public void selectAllMessages(List<Message> msgs) throws SQLException {
        // Select all columns from the message database
		String unsafeSql = "SELECT * FROM message";
		PreparedStatement safeSql = ConnectionUtil.getConnection().prepareStatement(unsafeSql);
		ResultSet rs = safeSql.executeQuery();

        // Update message-instance data-points then append message-instance to list
		while(rs.next()) {
			Message message = new Message();
			message.setMessage_id(rs.getInt("message_id"));
			message.setPosted_by(rs.getInt("posted_by"));
			message.setMessage_text(rs.getString("message_text"));
			message.setTime_posted_epoch(rs.getLong("time_posted_epoch"));
			msgs.add(message);
		}

        // Close database connection
		ConnectionUtil.getConnection().close();
	}
	
	public Message selectMessageById(int msgId, Message msg) throws SQLException {
        // Select all columns where message_id equals value provided
		String unsafeSql = "SELECT * FROM message WHERE message_id = ?";
		PreparedStatement safeSql = ConnectionUtil.getConnection().prepareStatement(unsafeSql);
		safeSql.setInt(1, msgId);
		ResultSet rs = safeSql.executeQuery();
		
        // Update message-instance data-points with result (for return data)
		while(rs.next()) {
			msg.setMessage_id(rs.getInt("message_id"));
			msg.setPosted_by(rs.getInt("posted_by"));
			msg.setMessage_text(rs.getString("message_text"));
			msg.setTime_posted_epoch(rs.getLong("time_posted_epoch"));
		}

        // Close database connection
		ConnectionUtil.getConnection().close();
		
        // Return the updated message
        return msg;
	}
	
	public Message deleteMessageById(int msgId, Message msg) throws SQLException {
		// Locate the message and set it to the 'message' instance
		String unsafeSql = "SELECT * FROM message WHERE message_id = ?";
		PreparedStatement safeSql = ConnectionUtil.getConnection().prepareStatement(unsafeSql);
		safeSql.setInt(1, msgId);
		ResultSet rs = safeSql.executeQuery();
		int numOfEntries = 0;
		
        // Update message-instance data-points (for return data)
		while(rs.next()) {
			msg.setMessage_id(rs.getInt("message_id"));
			msg.setPosted_by(rs.getInt("posted_by"));
			msg.setMessage_text(rs.getString("message_text"));
			msg.setTime_posted_epoch(rs.getLong("time_posted_epoch"));
			numOfEntries = numOfEntries + 1;
		}
		
		// If the message was found, then delete it
		if(numOfEntries != 0) {
			unsafeSql = "DELETE FROM message WHERE message_id = ?";
			safeSql = ConnectionUtil.getConnection().prepareStatement(unsafeSql);
			safeSql.setInt(1, msgId);
			safeSql.executeUpdate();
		}

        // Close database connection
		ConnectionUtil.getConnection().close();
		
        // Return the message-instance
        return msg;
	}
	
	public Message updateMessageById(int msgId, Message msg) throws SQLException {
		// Locate the message and set it to the 'message' instance
		String unsafeSql = "SELECT * FROM message WHERE message_id = ?";
		PreparedStatement safeSql = ConnectionUtil.getConnection().prepareStatement(unsafeSql);
		safeSql.setInt(1, msgId);
		ResultSet rs = safeSql.executeQuery();
		int numOfEntries = 0;
		
		while(rs.next()) {
			// Set all unchanged database data to message instance (ignore message_text)
			// message_text should retain the value that was passed
			msg.setMessage_id(rs.getInt("message_id"));
			msg.setPosted_by(rs.getInt("posted_by"));
			msg.setTime_posted_epoch(rs.getLong("time_posted_epoch"));
			numOfEntries = numOfEntries + 1;
		}
		
		// If the message was found, then update it
		if(numOfEntries != 0) {
			unsafeSql = "UPDATE message SET message_text = ? WHERE message_id = ?";
			safeSql = ConnectionUtil.getConnection().prepareStatement(unsafeSql);
			safeSql.setString(1, msg.getMessage_text());
			safeSql.setInt(2, msgId);
			safeSql.executeUpdate();
			
			ConnectionUtil.getConnection().close();
			return msg;
		} else {
			// ... otherwise, set message_text to null so an error is returned
			msg.setMessage_text(null);
			
			ConnectionUtil.getConnection().close();
			return msg;
		}
	}
	
	public void getAllMessagesByAccountId(List<Message> msgs, int acctId) throws SQLException{
        // Select all columns where posted_by matches provided value
		String unsafeSql = "SELECT * FROM message WHERE posted_by = ?";
		PreparedStatement safeSql = ConnectionUtil.getConnection().prepareStatement(unsafeSql);
		safeSql.setInt(1, acctId);
		ResultSet rs = safeSql.executeQuery();
		
        // Update message-instance data-points then append message-instance to list
		while(rs.next()) {
			Message message = new Message();
			message.setMessage_id(rs.getInt("message_id"));
			message.setPosted_by(rs.getInt("posted_by"));
			message.setMessage_text(rs.getString("message_text"));
			message.setTime_posted_epoch(rs.getLong("time_posted_epoch"));
			msgs.add(message);
		}
		
        // Close database connection
		ConnectionUtil.getConnection().close();
	}
}
