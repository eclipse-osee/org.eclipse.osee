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
package org.eclipse.osee.framework.core.server;

import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface IApplicationServerManager {

   void shutdown() ;

   ThreadFactory createNewThreadFactory(String name, int priority);

   boolean isSystemIdle();

   int getNumberOfActiveThreads();

   List<String> getCurrentProcesses();

   boolean executeLookupRegistration();

   String getId();

   URI getServerUri();

   Date getDateStarted();

   String[] getVersions();

   void register(String context, OseeHttpServlet servlets) ;

   void unregister(String key);

   Collection<String> getRegisteredServlets();

}
