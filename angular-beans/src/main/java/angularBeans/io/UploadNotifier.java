package angularBeans.io;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;

import angularBeans.context.NGSessionScoped;

@NGSessionScoped
public class UploadNotifier implements Serializable  {

	@Inject
	@Named
	@FileUpload
	Event<Upload> singleUploadEvent;

	@Inject
	@Named
	@FileUpload
	Event<List<Upload>> multipleUploadEvent;

	public void fireMultipart(Part part,String id) {
	
		singleUploadEvent.fire(new Upload(part, id));

	}
 
	public void fireMultipart(List<Upload> uploads) {
		
		multipleUploadEvent.fire(uploads);
		
	}

	

}
