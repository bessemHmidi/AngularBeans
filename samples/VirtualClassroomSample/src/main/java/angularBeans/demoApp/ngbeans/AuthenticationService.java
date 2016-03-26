package angularBeans.demoApp.ngbeans;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import angularBeans.api.AngularBean;
import angularBeans.api.NGModel;
import angularBeans.api.NGPostConstruct;
import angularBeans.api.NGReturn;
import angularBeans.api.NGSubmit;
import angularBeans.api.http.Put;
import angularBeans.context.NGSessionScoped;
import angularBeans.demoApp.domain.NotificationMessage;
import angularBeans.demoApp.domain.User;
import angularBeans.demoApp.service.VirtualClassService;
import angularBeans.events.RemoteEventBus;
import angularBeans.io.FileUpload;
import angularBeans.io.LobWrapper;
import angularBeans.io.Upload;
import angularBeans.log.NGLogger;
import angularBeans.log.NGLogger.Level;
import angularBeans.realtime.RealTime;
import angularBeans.realtime.RealTimeClient;
import angularBeans.util.ModelQuery;
import angularBeans.util.RootScope;

@AngularBean
@NGSessionScoped
public class AuthenticationService implements Serializable {

	@Inject
	@AngularBean
	RemoteEventBus remoteEventBus;

	@Inject
	VirtualClassService virtualClassService;

	@Inject
	NGLogger logger;

	@Inject
	ModelQuery modelQuery;

	@Inject
	RootScope rootScope;

	@Inject
	RealTimeClient client;

	@Inject
	Event<NotificationMessage> notificationBus;

	private User connectedUser;

	private String login;
	private String password;

	@NGPostConstruct
	public void init() {

		logger.log(Level.WARN, "please log in %s  ", "and enjoy !!");

		// if (connectedUser != null) {
		// modelQueryFactory.getRootScope().setProperty("GRANT_LOGIN", true);
		// modelQueryFactory.getRootScope()
		// .setProperty("connectedUser", connectedUser);
		// }

	}

	@Put
	public void newAccount(User user) {
		virtualClassService.createAccount(user);
		user.setPhoto(new LobWrapper(avatar.getData(), user));

	}

	private LobWrapper avatar;

	public LobWrapper getAvatar() {
		return avatar;
	}

	@FileUpload(path = "/avatar")
	public void uploadAvatar(List<Upload> uploads) {

		avatar = new LobWrapper(uploads.get(0).getAsByteArray(), this);

		client.publish(modelQuery.setProperty("avatar", avatar));

	}

	@RealTime
	@NGSubmit(backEndModels = "*")
	@NGReturn(model = "users", updates = "*")
	public String authenticate() {

		remoteEventBus.subscribe("notificationChannel");

		User user = new User(login, password);

		if (virtualClassService.getUsers().contains(user)) {

			connectedUser = virtualClassService.getUsers().get(
					virtualClassService.getUsers().indexOf(user));

			rootScope.setProperty("connectedUser", connectedUser);

			rootScope.setProperty("GRANT_LOGIN", true);

			login = "";
			password = "";

			NotificationMessage message = new NotificationMessage("img",
					"GRANT-ACCESS", " Welcome !!", false);

			message.setImage("images/mini_logo.png");

			notificationBus.fire(message);

			return "/choice";
		}

		notificationBus.fire(new NotificationMessage("danger", "SECURITY",
				"UNAUTHORIZED !!", false));
		modelQuery.setProperty("message", "incorrect login or password !!");

		return "/";

	}

	@NGModel
	@NotNull
	@Pattern(regexp = "/^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$/")
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	@NGModel
	@NotNull
	@Size(min = 3, max = 12)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@NGModel
	public User getConnectedUser() {
		return connectedUser;
	}

	public void setConnectedUser(User connectedUser) {
		this.connectedUser = connectedUser;
	}

}
