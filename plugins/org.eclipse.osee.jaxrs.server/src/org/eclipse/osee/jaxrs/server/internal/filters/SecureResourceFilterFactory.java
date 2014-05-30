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
package org.eclipse.osee.jaxrs.server.internal.filters;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.jaxrs.NoSecurityFilter;
import org.eclipse.osee.logger.Log;
import com.sun.jersey.api.container.filter.servlet.RolesAllowedResourceFilterFactory;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ResourceFilter;

/**
 * Ensures the security context filter {@link SecurityContextFilter} is always the first filter in the chain.
 * 
 * @author Roberto E. Escobar
 */
public class SecureResourceFilterFactory extends RolesAllowedResourceFilterFactory {

   private static final String SECURE = "SECURE";
   private static final String INSECURE = "SKIPPED";

   private final Log logger;
   private final SecurityContextFilter securityContextFilter;

   public SecureResourceFilterFactory(Log logger, SecurityContextFilter securityContextFilter) {
      super();
      this.logger = logger;
      this.securityContextFilter = securityContextFilter;
   }

   @Override
   public List<ResourceFilter> create(AbstractMethod am) {
      List<ResourceFilter> securityFilters = super.create(am);
      if (securityFilters == null) {
         securityFilters = new ArrayList<ResourceFilter>();
      } else {
         securityFilters = new ArrayList<ResourceFilter>(securityFilters);
      }

      boolean secure = isSecured(am);
      if (secure) {
         securityFilters.add(0, securityContextFilter);
      }
      logger.info("REST Security Filter: [%s] [%s]", secure ? SECURE : INSECURE, am);
      return securityFilters;
   }

   private boolean isSecured(AbstractMethod am) {
      return !am.isAnnotationPresent(NoSecurityFilter.class) && // 
      !am.getResource().isAnnotationPresent(NoSecurityFilter.class);
   }
}