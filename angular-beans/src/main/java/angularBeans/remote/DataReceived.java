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
package angularBeans.remote;

import com.google.gson.JsonObject;

/**
 * a superType for all DataReceived from the client events when used with @Observes
 * that will include HalfDuplexDataReceivedEvent and RealTimeDataReceivedEvent
 * 
 * @author Bessem Hmidi
 *
 */

public interface DataReceived {

	JsonObject getData();

}
