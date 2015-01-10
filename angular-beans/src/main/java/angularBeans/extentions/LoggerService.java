package angularBeans.extentions;

import angularBeans.Extention;
import angularBeans.NGExtention;

@NGExtention
public class LoggerService implements Extention {

	public String render() {
		String result = "";

		result += "	app.service('logger',function(){";
		result += "		this.log=function(logMessages){";
		result += "			for (var i in logMessages) ";
		result += "			{";
		result += "			var message=logMessages[i].message;";
		result += "			var level=logMessages[i].level;";
		result += "				if(level===\"info\"){console.info(message);};";
		result += "				if(level===\"error\"){console.error(message);};";
		result += "				if(level===\"warn\"){console.warn(message);};";
		result += "				if(level===\"debug\"){console.debug(message);};";
		result += "				}";
		result += "			 }});";

		return result;
	}

}
