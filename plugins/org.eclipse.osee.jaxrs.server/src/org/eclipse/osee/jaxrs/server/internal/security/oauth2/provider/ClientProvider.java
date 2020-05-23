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

import com.google.common.io.ByteSource;
import javax.ws.rs.core.UriInfo;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.eclipse.osee.account.admin.OseePrincipal;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints.ClientFormData;

/**
 * @author Roberto E. Escobar
 */
public interface ClientProvider extends ClientLogoUriResolver {

   long getClientId(Client client);

   Client getClient(String clientId);

   Client createClient(UriInfo uriInfo, OseePrincipal principal, ClientFormData data);

   ByteSource getClientLogoSupplier(UriInfo uriInfo, String applicationGuid);

}