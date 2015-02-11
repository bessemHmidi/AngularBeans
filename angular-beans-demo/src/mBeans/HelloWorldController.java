package mBeans;

import angularBeans.api.NGController;
import angularBeans.api.NGReturn;
import angularBeans.api.NGSubmit;
import angularBeans.context.NGSessionScoped;

@NGController
@NGSessionScoped
public class HelloWorldController {

	private String name;

	@NGSubmit
	@NGReturn(model = "message")
	public String sayHello() {
		return "hello " + name + " from AngularBeans!";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
