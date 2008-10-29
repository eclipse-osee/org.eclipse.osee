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

import org.eclipse.osee.framework.core.server.internal.InternalHttpServiceTracker;
import org.osgi.framework.BundleContext;

/**
 * @author Roberto E. Escobar
 */
public class OseeHttpServiceTracker extends InternalHttpServiceTracker {

   /**
    * @param context
    * @param contextName
    * @param servletClass
    */
   public OseeHttpServiceTracker(BundleContext context, String contextName, Class<? extends OseeHttpServlet> servletClass) {
      super(context, contextName, servletClass);
   }
}
