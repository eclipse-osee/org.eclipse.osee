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
package org.eclipse.osee.framework.manager.servlet;

import javax.servlet.http.HttpServletRequest;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;

/**
 * @author Donald G. Dunne
 */
class HttpManagerCreationInfo {

   enum ManagerFunction {
      userId
   };

   private ManagerFunction function;
   private final String userId;

   public HttpManagerCreationInfo(HttpServletRequest req) throws OseeArgumentException {
      ensureFunctionValid(req.getParameter("function"));
      userId = req.getParameter("userId");
   }

   private void ensureFunctionValid(String function) throws OseeArgumentException {
      if (function == null) {
         throw new OseeArgumentException("A 'function' parameter must be defined.");
      }
      try {
         this.function = ManagerFunction.valueOf(function);
      } catch (IllegalArgumentException ex) {
         throw new OseeArgumentException(String.format("[%s] is not a valid function.", function));
      }
   }

   /**
    * @return the function
    */
   public ManagerFunction getFunction() {
      return function;
   }

   /**
    * @return the userId
    */
   public String getUserId() {
      return userId;
   }

}