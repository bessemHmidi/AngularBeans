package mBeans;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import libraries.Book;
import angularBeans.api.NGController;
import angularBeans.api.NGRedirect;
import angularBeans.api.NGReturn;
import angularBeans.api.NGSubmit;
import angularBeans.context.NGSessionScoped;
import angularBeans.log.NGLogger;
import angularBeans.log.NGLogger.Level;
import angularBeans.wsocket.WSocketClient;
import angularBeans.wsocket.WSocketEvent;
import angularBeans.wsocket.WSocketMessage;
import angularBeans.wsocket.WebSocket;
import angularBeans.wsocket.annotations.Subscribe;
import angularBeans.wsocket.annotations.WSocketReceiveEvent;

//@Named("booksController")
// if not provided by default ctrl name is in this case 'myController'
@NGSessionScoped
// required to register the controller
@NGController
@Subscribe(channels = "channel1")

public class MyController implements Serializable {

	
	
	public void toto(@Observes  @WSocketReceiveEvent WSocketEvent event){
		System.out.println(this+"RECEIVED....." +event.getData());
	}
	
	@Inject
	@NGController
	ChatController chatController;
	
	@Inject
	BooksService service;

	@Inject
	WSocketClient client;

	

	@Inject
	NGLogger logger;

	private String isbn="";
	private String title="";
	private boolean free=false;
	private int pages;
	private String selectedCategorie;

	private Book otherBook;

	// for each getter a model is generated in angular side (here the model is
	// 'isbn')

	
	
	@NotNull
	// will add attribute required to the model
	public String getIsbn() {
		return isbn;
	}

	public List<Book> getAllBooks() {

		return service.getAllBooks();

	}

	@NGReturn(model = "bookFound", updates = { "title", "pages", "free",
			"selectedCategorie", "allBooks" })
	// model(s) name(s) that the method affect (the result)
	// by default the @NGSubmit will take effect for each field with setter(in
	// the controller scope uptading server side models before executing the
	// action)
	// but used with updateModels attribute this will update listed models only
	@NGSubmit(updateModels = { "isbn" })
	public Book findByIsbn() {

		Book book = service.findByISBN(isbn);
		title = "modified";
		//pages = book.getPages();
		//selectedCategorie = book.getCategory();
		return book;
	}

	@NGRedirect()
	// with @NGRedirect : after execution the string returned will be parsed as
	// redirection page
	// you can also use @NGSubmit with it to to do redirection based on the
	// models status.
	public String about() {

		// logger.log(NGLogger.Level.WARN,"new page");
		return "about.html";
	}

	@NGSubmit()
	@NGReturn(model = "allBooks", updates = { "title" })
	public List<Book> addBook() {
		
		//System.out.println(isbn);
		
		Book book = new Book();
		book.setIsbn(isbn);
		book.setTitle(title);
		book.setFree(free);
		book.setPages(pages);
		book.setCategory(selectedCategorie);
		service.add(book);

		logger.log(NGLogger.Level.INFO, "book" + isbn + "added!!");
		
		client.publishToAll( "channel1",
				new WSocketMessage()
		.add("allBooks", getAllBooks()));
		

		return service.getAllBooks();

	}

	
	
	@NGSubmit()
	@NGReturn(model = "allBooks")
	public List<Book> clear() {
		
		
		
		service.clear();
		
		//client.publish(channel, message);
		
		client.publishToAll( "channel1",
				new WSocketMessage().add("title", "hihihihih...").add("allBooks", getAllBooks()));
		logger.log(Level.WARN, "top!!!!!!!!");

		
		
		
		return service.getAllBooks();
	}

	public List<String> getCategories() {
		return Arrays.asList("History", "Science");
	}

	public void setIsbn(String isbn) {

		this.isbn = isbn;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean getFree() {
		return free;
	}

	public void setFree(boolean free) {
		this.free = free;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public String getSelectedCategorie() {
		return selectedCategorie;
	}

	public void setSelectedCategorie(String selectedCategorie) {
		this.selectedCategorie = selectedCategorie;
	}

	public MyController() {
		
		

	}

	@PostConstruct
	public void init() {
		otherBook = new Book();
		otherBook.setTitle("blablabla");
		otherBook.setFree(true);
		otherBook.setIsbn("001");
	}

	public Book getOtherBook() {

		

		return otherBook;
	}

//	@WebSocket
//	public void testSockVoid(WSocketEvent event) {
//
//		
//		
//		
//		System.out.println(event);
//
//		// client.send(new WSocketMessage("hahaha"));
//
//	}

	@WebSocket
	@NGSubmit()
	@NGReturn(model = "isbn", updates = { "title", "pages" })
	public Object testSock() {
		
		pages=300;
		title = "modified via web sockets!!";
		logger.log(Level.ERROR, "hahaha");
		
		return "toto";

		// client.send(new WSocketMessage("hahaha"));

	}

	public void setOtherBook(Book otherBook) {
		this.otherBook = otherBook;
	}

	

}
