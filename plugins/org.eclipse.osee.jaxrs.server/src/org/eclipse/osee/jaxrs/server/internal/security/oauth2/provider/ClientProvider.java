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

import org.apache.cxf.rs.security.oauth2.common.Client;

/**
 * @author Roberto E. Escobar
 */
public interface ClientProvider {

   Client getClient(String clientId);

   Client createClient();

   long getClientId(Client client);

}