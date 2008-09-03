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
package org.eclipse.osee.framework.resource.common;

/**
 * @author Roberto E. Escobar
 */
public interface IApplicationServerManager {

   public boolean areRequestsAllowed(String contextPath, String operation);

   /**
    * Initiates an orderly shutdown in which previously submitted tasks are executed, but no new tasks will be accepted.
    * Invocation has no additional effect if already shut down.
    * 
    * @throws SecurityException if a security manager exists and shutting down this ExecutorService may manipulate
    *            threads that the caller is not permitted to modify because it does not hold
    *            {@link java.lang.RuntimePermission}<tt>("modifyThread")</tt>, or the security manager's
    *            <tt>checkAccess</tt> method denies access.
    */
   public void shutdown();
}
