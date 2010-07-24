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

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface IApplicationServerManager {

	void shutdown() throws OseeCoreException;

	ThreadFactory createNewThreadFactory(String name, int priority);

	boolean isSystemIdle();

	int getNumberOfActiveThreads();

	List<String> getCurrentProcesses();

	void setServletRequestsAllowed(boolean value) throws OseeCoreException;

	boolean executeLookupRegistration();

	String getId();

	String getServerAddress();

	int getPort();

	Date getDateStarted();

	boolean isAcceptingRequests();

	String[] getSupportedVersions();

	void addSupportedVersion(String version) throws OseeCoreException;

	void removeSupportedVersion(String version) throws OseeCoreException;

	void register(String context, OseeHttpServlet servlets) throws OseeCoreException;

	void unregister(String key);

	Collection<String> getRegisteredServlets();
}
