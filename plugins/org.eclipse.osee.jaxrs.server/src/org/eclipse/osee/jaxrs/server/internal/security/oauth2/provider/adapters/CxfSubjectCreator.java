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

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;
import org.apache.cxf.rs.security.oauth2.provider.SubjectCreator;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class CxfSubjectCreator implements SubjectCreator {
   private final Log logger;

   public CxfSubjectCreator(Log logger) {
      super();
      this.logger = logger;
   }

   @Override
   public UserSubject createUserSubject(MessageContext mc) throws OAuthServiceException {
      logger.debug("createUserSubject called");
      return null;
   }

}
