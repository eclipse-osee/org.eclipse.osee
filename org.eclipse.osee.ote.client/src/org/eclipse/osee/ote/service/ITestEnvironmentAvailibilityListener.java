/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.service;

import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;

/**
 * This listener will be notified of test host availability events. Clients must register implementations of this
 * listener by calling {@link IOteClientService#addEnvironmentAvailibiltyListener(ITestEnvironmentAvailibilityListener)}
 * 
 * @author Ken J. Aguilar
 */
public interface ITestEnvironmentAvailibilityListener {

   /**
    * this method will be called when a {@link IHostTestEnvironment} becomes available for use.
    * 
    * @param testEnvironment
    * @param connector
    * @param properties
    */
   void environmentAvailable(IHostTestEnvironment testEnvironment, IServiceConnector connector, OteServiceProperties properties);

   /**
    * this method will be called whenever a {@link IHostTestEnvironment} becomes unavailable.
    * 
    * @param testEnvironment
    * @param connector
    * @param properties
    */
   void environmentUnavailable(IHostTestEnvironment testEnvironment, IServiceConnector connector, OteServiceProperties properties);
}
