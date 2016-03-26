package com.angularbeans.sample.helloworld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.GET;

import angularBeans.api.AngularBean;
import angularBeans.api.NGModel;
import angularBeans.api.NGReturn;
import angularBeans.api.NGSubmit;
import angularBeans.log.NGLogger;
import angularBeans.log.NGLogger.Level;
import angularBeans.realtime.RealTimeClient;
import angularBeans.util.ModelQuery;

@AngularBean
public class HelloAngularBeans {

	private int counter = 0;
	private List<User> users;

	@Inject
	NGLogger logger;

	@Inject
	ModelQuery models;

	@Inject
	RealTimeClient client;

	@PostConstruct
	public void init() {
		users = new ArrayList<>(Arrays.asList(new User("user1", 1), new User("user2", 2)));
	}

	@NGReturn(model = "message", updates = "counter")
	@NGSubmit(backEndModels = "counter")
	public String sayHello(String name) {
		counter++;
		if (counter == 10) {
			logger.log(Level.DEBUG, "you called sayHello %d times", counter);
		}
		return "Hello " + name + " from AngularBeans !";
	}

	@NGModel
	public int getCounter() {
		return counter;
	}

	@GET
	public void getSomeUsers() {
		models.setProperty("users", users);
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
	
	public void remove(User user) {
		client.broadcast(models.removeFrom("users", user), false);
	}
}