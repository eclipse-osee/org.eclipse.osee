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

import java.util.concurrent.ThreadFactory;
import org.eclipse.osee.framework.core.server.internal.ServerActivator;

/**
 * @author Roberto E. Escobar
 */
public final class ServerThreads {

   public static ThreadFactory createNewThreadFactory(String name) {
      return ServerActivator.getApplicationServerManager().createNewThreadFactory(name, Thread.NORM_PRIORITY);
   }
}
