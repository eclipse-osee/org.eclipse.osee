/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.jaxrs;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.framework.core.data.OseeClient;

/**
 * @author Donald G. Dunne
 */
@Provider
public class OseeAccountClientRequestFilter implements ClientRequestFilter {

   @Override
   public void filter(ClientRequestContext context) {
      context.getHeaders().putSingle(OseeClient.OSEE_ACCOUNT_ID,
         JaxRsClient.getAccountId() == null ? "0" : JaxRsClient.getAccountId().toString());
      context.getHeaders().putSingle("osee.client.id",
         JaxRsClient.getClientId() == null ? "0" : JaxRsClient.getClientId().toString());
   }
}