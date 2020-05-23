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

import javax.servlet.http.HttpServletRequest;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class UnsecuredOseeHttpServlet extends OseeHttpServlet {

   private static final long serialVersionUID = 6633402530554659368L;

   public UnsecuredOseeHttpServlet(Log logger) {
      super(logger);
   }

   @Override
   protected final void checkAccessControl(HttpServletRequest request) {
      //
   }

}
