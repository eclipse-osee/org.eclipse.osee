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

package org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.grants.owner.ResourceOwnerLoginHandler;
import org.apache.cxf.rs.security.oauth2.provider.ResourceOwnerNameProvider;
import org.apache.cxf.rs.security.oauth2.provider.SessionAuthenticityTokenProvider;
import org.apache.cxf.rs.security.oauth2.provider.SubjectCreator;
import org.apache.cxf.security.SecurityContext;

/**
 * @author Roberto E. Escobar
 */
public interface SubjectProvider extends SessionAuthenticityTokenProvider, SubjectCreator, ResourceOwnerNameProvider, ResourceOwnerLoginHandler {

   long getSubjectId(UserSubject subject);

   void authenticate(MessageContext mc, String scheme, String username, String password);

   SecurityContext getSecurityContextFromSession(MessageContext mc);

   UserSubject getSubjectById(long subjectId);

   void setSecretKeyAlgorithm(String secretKeyAlgorithm);

   void setSecretKeyEncoded(String encodedSecretKey);

   void setSessionTokenExpiration(long accessTokenExpiration);
}