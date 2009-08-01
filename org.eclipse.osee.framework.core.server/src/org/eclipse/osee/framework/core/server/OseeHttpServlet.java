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

import javax.servlet.http.HttpServletRequest;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.server.internal.InternalOseeHttpServlet;

/**
 * @author Roberto E. Escobar
 */
public class OseeHttpServlet extends InternalOseeHttpServlet {

   private static final long serialVersionUID = -4747761442607851113L;

   @Override
   protected void checkAccessControl(HttpServletRequest request) throws OseeCoreException {
      String sessionId = request.getParameter("sessionId");
      String interaction =
            String.format("%s %s %s", request.getMethod(), request.getRequestURI(), request.getQueryString());
      CoreServerActivator.getSessionManager().updateSessionActivity(sessionId, interaction);
   }

}
