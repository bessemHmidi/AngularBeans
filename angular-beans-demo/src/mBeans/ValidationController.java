package mBeans;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import angularBeans.api.NGController;
import angularBeans.api.NGSubmit;
import angularBeans.context.NGSessionScoped;
import angularBeans.validation.BeanValidator;
import angularBeans.wsocket.WSocketEvent;
import angularBeans.wsocket.WebSocket;


@NGController
@NGSessionScoped
@Named("validationController")
public class ValidationController implements Serializable{


	private String name="";
	private String userName="";
	private String email="";
	private String expression="";
	private int number;
	
	@Inject
	BeanValidator validator;
	
	
	@NotNull()
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Size(min=3,message="{{ahla}}")
	@NotNull
	public String getUserName() {
		return userName;
	}
	
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	//@org.hibernate.validator.constraints.Email
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Pattern(regexp="/^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$/")
	public String getExpression() {
		return expression;
	}
	
	
	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	@NGSubmit()
	@WebSocket
	public void validate(WSocketEvent event){
		System.out.println(event.getData());
		System.out.println(userName);
		validator.validate(this);
	}
	//@min() @max()
	@NotNull
	@Min(10)
	@Max(100)
	public int getNumber() {
		return number;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
}
