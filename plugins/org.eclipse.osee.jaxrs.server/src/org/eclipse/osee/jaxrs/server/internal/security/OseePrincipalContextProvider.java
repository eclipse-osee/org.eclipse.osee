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