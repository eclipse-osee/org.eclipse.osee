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
package org.eclipse.osee.framework.server.ide.data;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Donald G. Dunne
 */
public class HttpSystemManagerCreationInfo {

   private final String userId;
   private final String sessionId;

   public HttpSystemManagerCreationInfo(HttpServletRequest req) {
      userId = req.getParameter("userId");
      sessionId = req.getParameter("sessionId");
   }

   public String getUserId() {
      return userId;
   }

   public String getSessionId() {
      return sessionId;
   }
}