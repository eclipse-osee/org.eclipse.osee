/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.jaxrs.server.security;

import javax.servlet.http.HttpSession;
import org.eclipse.osee.account.admin.OseePrincipal;
import org.eclipse.osee.jaxrs.server.session.SessionData;

/**
 * @author Roberto E. Escobar
 */
public interface JaxRsSessionProvider {

   String createAuthenticitySessionToken(Long subjectId);

   void storeSession(SessionData session);

   String removeSessionAuthenticityToken(Long subjectId);

   String getSessionAuthenticityToken(Long subjectId);

   SessionData getSession(String sessionId);

   SessionData removeSession(String sessionId);

   HttpSession getSession();

   OseePrincipal getSubjectById(Long subjectId);

}