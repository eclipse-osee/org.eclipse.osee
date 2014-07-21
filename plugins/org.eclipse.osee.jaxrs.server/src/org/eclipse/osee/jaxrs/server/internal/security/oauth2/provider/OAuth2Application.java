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

import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * @author Roberto E. Escobar
 */
@ApplicationPath("/oauth2")
public class OAuth2Application extends Application {

   private final Set<Object> jaxRsEndpoints;

   public OAuth2Application(Set<Object> jaxRsEndpoints) {
      super();
      this.jaxRsEndpoints = jaxRsEndpoints;
   }

   @Override
   public Set<Object> getSingletons() {
      return jaxRsEndpoints;
   }
}