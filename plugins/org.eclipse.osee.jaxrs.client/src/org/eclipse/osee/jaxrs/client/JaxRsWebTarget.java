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
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

/**
 * Facade over WebTarget
 * 
 * @author Roberto E. Escobar
 */
public interface JaxRsWebTarget extends Configurable<JaxRsWebTarget> {

   /**
    * Get the URI identifying the resource.
    * 
    * @return the resource URI.
    */
   public URI getUri();

   /**
    * Get the URI builder initialized with the {@link URI} of the current resource target. The returned URI builder is
    * detached from the target, i.e. any updates in the URI builder MUST NOT have any effects on the URI of the
    * originating target.
    * 
    * @return the initialized URI builder.
    */
   public UriBuilder getUriBuilder();

   /**
    * Start building a request to the targeted web resource.
    * 
    * @return builder for a request targeted at the URI referenced by this target instance.
    */
   public Invocation.Builder request();

   /**
    * Start building a request to the targeted web resource and define the accepted response media types.
    * <p>
    * Invoking this method is identical to:
    * </p>
    * 
    * <pre>
    * webTarget.request().accept(types);
    * </pre>
    * 
    * @param acceptedResponseTypes accepted response media types.
    * @return builder for a request targeted at the URI referenced by this target instance.
    */
   public Invocation.Builder request(String... acceptedResponseTypes);

   /**
    * Start building a request to the targeted web resource and define the accepted response media types.
    * <p>
    * Invoking this method is identical to:
    * </p>
    * 
    * <pre>
    * webTarget.request().accept(types);
    * </pre>
    * 
    * @param acceptedResponseTypes accepted response media types.
    * @return builder for a request targeted at the URI referenced by this target instance.
    */
   public Invocation.Builder request(MediaType... acceptedResponseTypes);

   /**
    * Convert to a web-proxy class (class containing JAX-RS annotations)
    * 
    * @return web-proxy object
    */
   public <T> T newProxy(Class<T> clazz);

}