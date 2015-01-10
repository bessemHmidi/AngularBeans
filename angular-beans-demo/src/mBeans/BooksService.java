package mBeans;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import libraries.Book;
import angularBeans.wsocket.WSocketEvent;
import angularBeans.wsocket.WSocketMessage;
import angularBeans.wsocket.annotations.WSocketReceiveEvent;



@Singleton
@LocalBean
public class BooksService  {

	
	
	private List<Book> allBooks = new ArrayList<Book>();

	public List<Book> getAllBooks() {

		return allBooks;
	}

	public void add(Book book) {
		allBooks.add(book);
	}

	public Book findByISBN(String isbn) {
		for (Book l : allBooks)
			if (l.getIsbn().equals(isbn))
				return l;
		return null;
	}

	public void clear() {
		allBooks.clear();

	}

	
//	@Inject
//	Session session;
	
//	@Schedule(hour="*",minute="*",second="*/20")
//	public void alarm(){
//		client.publishToAll("channel1", new WSocketMessage().add("title", "boom"));
//		System.out.println("------ **** ----------");
		//client.publish("channel1",new WSocketMessage().add("title","hello"));
//		try {
//			session.getBasicRemote().sendText("BLABLABALA");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		}
	
	
	public void hava(@Observes @WSocketReceiveEvent WSocketEvent event){
		event.getClient().publishToAll("channel1", new WSocketMessage().add("title", "boom"));
	}
	
	
}
