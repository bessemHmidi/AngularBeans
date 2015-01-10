package libraries;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;


public class Book implements Serializable{
	private String isbn;
	private String title;
	private boolean free;
	private int pages;
	private String category;
	
	public Book() {
		super();
		
	}
	
	public String getIsbn() {
		return isbn;
	}
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	
	
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	
	public String getTitle() {
		return title;
	}

	public boolean isFree() {
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	
	
	
	
	

}
