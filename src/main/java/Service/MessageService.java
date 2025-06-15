package Service;

import java.sql.SQLException;
import java.util.List;

import DAO.MessageDAO;
import Model.Message;

public class MessageService {
	
	private MessageDAO messageDao;
	
	public MessageService() {
		// Create an instance of the Data Access Object to work with database
		messageDao = new MessageDAO();
	}
	
	public boolean createNewBlogMessage(Message message) throws SQLException {
		// Determine if message length meets criteria (length > 0 and length <= 255)
		int messageLength = message.getMessage_text().length();
		if (messageLength == 0 || messageLength > 255) {
			return false;
		} else {
			return messageDao.createMessage(message);
		}
	}
	
	public void getAllMessages(List<Message> msgs) throws SQLException {
		messageDao.selectAllMessages(msgs);
	}
	
	public Message getMessageById(int msgId, Message msg) throws SQLException {
		return messageDao.selectMessageById(msgId, msg);
	}
	
	public Message removeMessageById(int msgId, Message msg) throws SQLException {
		return messageDao.deleteMessageById(msgId, msg);
	}
	
	public Message updateMessageById(int msgId, Message msg) throws SQLException{
		// Get message length
		int messageLength = msg.getMessage_text().length();
		
		// If message length is zero OR greater-than 255 characters...
		if(messageLength == 0 || messageLength > 255) {
			// ... return a 'null' message_text
			msg.setMessage_text(null);
			return msg;
		} else {
			// ... otherwise, proceed with updating logic
			return messageDao.updateMessageById(msgId, msg);
		}
	}
	
	public void getAllMessagesByAccountId (List<Message> msgs, int acctId) throws SQLException {
		messageDao.getAllMessagesByAccountId(msgs, acctId);
	}
}
