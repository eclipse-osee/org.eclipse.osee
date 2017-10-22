/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jaxrs.client.internal;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.jaxrs.client.JaxRsClient;

/**
 * @author Donald G. Dunne
 */
@Provider
public class OseeAccountClientRequestFilter implements ClientRequestFilter {

   @Override
   public void filter(ClientRequestContext context) {
      context.getHeaders().putSingle("osee.account.id",
         JaxRsClient.getAccountId() == null ? "0" : JaxRsClient.getAccountId().toString());
      context.getHeaders().putSingle("osee.client.id",
         JaxRsClient.getClientId() == null ? "0" : JaxRsClient.getClientId().toString());
   }
}