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