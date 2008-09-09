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

import java.util.List;
import java.util.concurrent.ThreadFactory;

/**
 * @author Roberto E. Escobar
 */
public interface IApplicationServerManager {

   public void shutdown();

   public ThreadFactory createNewThreadFactory(String name, int priority);

   public boolean isSystemIdle();

   public int getNumberOfActiveThreads();

   public List<String> getCurrentProcesses();

   public void setServletRequestsAllowed(boolean value);

}
