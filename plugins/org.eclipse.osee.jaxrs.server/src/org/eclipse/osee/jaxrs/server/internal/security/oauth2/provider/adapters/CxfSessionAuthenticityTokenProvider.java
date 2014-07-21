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
package org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.adapters;

import javax.ws.rs.core.MultivaluedMap;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.provider.SessionAuthenticityTokenProvider;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class CxfSessionAuthenticityTokenProvider implements SessionAuthenticityTokenProvider {
   private final Log logger;

   public CxfSessionAuthenticityTokenProvider(Log logger) {
      super();
      this.logger = logger;
   }

   @Override
   public String createSessionToken(MessageContext mc, MultivaluedMap<String, String> params, UserSubject subject) {
      logger.debug("createSessionToken");
      return null;
   }

   @Override
   public String getSessionToken(MessageContext mc, MultivaluedMap<String, String> params, UserSubject subject) {
      logger.debug("getSessionToken");
      return null;
   }

   @Override
   public String removeSessionToken(MessageContext mc, MultivaluedMap<String, String> params, UserSubject subject) {
      logger.debug("removeSessionToken");
      return null;
   }

}
