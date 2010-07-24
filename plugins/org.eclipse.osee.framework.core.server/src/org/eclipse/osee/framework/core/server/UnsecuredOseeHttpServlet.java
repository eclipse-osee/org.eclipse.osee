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

/**
 * @author Roberto E. Escobar
 */
public class UnsecuredOseeHttpServlet extends OseeHttpServlet {

   private static final long serialVersionUID = 6633402530554659368L;

   public UnsecuredOseeHttpServlet() {
      super();
   }

   @Override
   protected final void checkAccessControl(HttpServletRequest request) {
   }

}
