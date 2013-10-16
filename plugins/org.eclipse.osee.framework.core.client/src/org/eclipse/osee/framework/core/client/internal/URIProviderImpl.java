/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.client.internal;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilderClient;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.services.URIProvider;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author John Misinco
 * @author Roberto E. Escobar
 */
public class URIProviderImpl implements URIProvider {

   @Override
   public URI getApplicationServerURI() throws OseeCoreException {
      URI toReturn = null;
      try {
         toReturn = new URI(HttpUrlBuilderClient.getInstance().getApplicationServerPrefix());
      } catch (URISyntaxException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return toReturn;
   }

   @Override
   public URI getEncodedURI(String context, Map<String, String> params) throws OseeCoreException {
      URI toReturn = null;
      try {
         toReturn = new URI(HttpUrlBuilderClient.getInstance().getOsgiServletServiceUrl(context, params));
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return toReturn;
   }

}
