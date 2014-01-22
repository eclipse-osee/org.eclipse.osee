/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.account.rest.client.internal;

import java.net.URI;
import java.net.URISyntaxException;
import org.eclipse.osee.framework.core.services.URIProvider;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.rest.client.OseeServerAddress;
import com.google.inject.Inject;

/**
 * @author Roberto E. Escobar
 */
public class StandadloneUriProviderImpl implements URIProvider {

   private final String serverAddress;

   @Inject
   public StandadloneUriProviderImpl(@OseeServerAddress String serverAddress) {
      this.serverAddress = serverAddress;
   }

   @Override
   public URI getApplicationServerURI() {
      URI toReturn = null;
      try {
         toReturn = new URI(serverAddress);
      } catch (URISyntaxException ex) {
         throw new OseeCoreException(ex);
      }
      return toReturn;
   }

};
