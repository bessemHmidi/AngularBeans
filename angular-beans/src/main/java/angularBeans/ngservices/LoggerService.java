/*
 * AngularBeans, CDI-AngularJS bridge 
 *
 * Copyright (c) 2014, Bessem Hmidi. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 */

/**
 @author Bessem Hmidi
 */
package angularBeans.ngservices;


/**
 * AngularBeans logger service
 * @author Bessem Hmidi
 *
 */

@NGExtension
public class LoggerService implements NGService {

	public String render() {
		String result = "";

		result += "	app.service('logger',[function(){";
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
		result += "			 }}]);";

		return result;
	}

}
