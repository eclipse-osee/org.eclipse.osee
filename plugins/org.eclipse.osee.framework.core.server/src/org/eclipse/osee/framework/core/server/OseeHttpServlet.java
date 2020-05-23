/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.server;

import org.eclipse.osee.framework.core.server.internal.InternalOseeHttpServlet;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public abstract class OseeHttpServlet extends InternalOseeHttpServlet {

   private static final long serialVersionUID = -4747761442607851113L;

   public OseeHttpServlet(Log logger) {
      super(logger);
   }

   @Override
   protected Log getLogger() {
      return super.getLogger();
   }
}
