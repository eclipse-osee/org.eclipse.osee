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

package org.eclipse.osee.jaxrs.client;

import java.net.URI;

/**
 * @author Roberto E. Escobar
 */
public interface JaxRsTokenStore {

   String getToken(URI uri);

   void storeToken(URI uri, String token);

}