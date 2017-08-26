/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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