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
package org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider;

import com.google.common.io.InputSupplier;
import java.io.InputStream;
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

   InputSupplier<InputStream> getClientLogoSupplier(UriInfo uriInfo, String applicationGuid);

}