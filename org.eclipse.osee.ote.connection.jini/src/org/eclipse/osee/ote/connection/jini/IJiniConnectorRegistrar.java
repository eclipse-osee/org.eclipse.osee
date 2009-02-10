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
package org.eclipse.osee.ote.connection.jini;

import java.io.IOException;
import java.net.MalformedURLException;

import net.jini.core.discovery.LookupLocator;

/**
 * @author b1529404
 */
public interface IJiniConnectorRegistrar {
   void addLocators(String... hosts) throws MalformedURLException, ClassNotFoundException, IOException;

   LookupLocator[] getLocators();

   void addGroup(String... groups) throws IOException;

   String[] getGroups();
}
