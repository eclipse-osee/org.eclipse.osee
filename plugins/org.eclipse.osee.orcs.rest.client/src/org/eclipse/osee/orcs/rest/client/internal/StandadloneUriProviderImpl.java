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
package org.eclipse.osee.orcs.rest.client.internal;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.services.URIProvider;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.HttpUrlBuilder;
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

   public String getServerAddress() {
      String value = serverAddress;
      if (!value.endsWith("/")) {
         value += '/';
      }
      return value;
   }

   @Override
   public URI getApplicationServerURI() throws OseeCoreException {
      URI toReturn = null;
      try {
         toReturn = new URI(getServerAddress());
      } catch (URISyntaxException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return toReturn;
   }

   @Override
   public URI getEncodedURI(String context, Map<String, String> params) throws OseeCoreException {
      URI toReturn = null;
      try {
         toReturn = new URI(HttpUrlBuilder.createURL(getServerAddress(), context, params));
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return toReturn;
   }
};
