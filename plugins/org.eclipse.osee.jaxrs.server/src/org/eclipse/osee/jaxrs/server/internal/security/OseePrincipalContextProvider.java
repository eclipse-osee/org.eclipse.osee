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
package org.eclipse.osee.jaxrs.server.internal.security;

import java.security.Principal;
import javax.ws.rs.ext.Provider;
import org.apache.cxf.jaxrs.ext.ContextProvider;
import org.apache.cxf.message.Message;
import org.apache.cxf.security.SecurityContext;
import org.eclipse.osee.account.admin.OseePrincipal;

/**
 * @author Roberto E. Escobar
 */
@Provider
public class OseePrincipalContextProvider implements ContextProvider<OseePrincipal> {

   @Override
   public OseePrincipal createContext(Message message) {
      OseePrincipal toReturn = null;
      SecurityContext sc = message.get(SecurityContext.class);
      if (sc != null) {
         Principal userPrincipal = sc.getUserPrincipal();
         if (userPrincipal instanceof OseePrincipal) {
            toReturn = (OseePrincipal) userPrincipal;
         }
      }
      return toReturn;
   }

}