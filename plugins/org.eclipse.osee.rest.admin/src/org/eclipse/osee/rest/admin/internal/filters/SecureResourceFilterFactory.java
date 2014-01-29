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
package org.eclipse.osee.rest.admin.internal.filters;

import java.util.ArrayList;
import java.util.List;
import com.sun.jersey.api.container.filter.servlet.RolesAllowedResourceFilterFactory;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ResourceFilter;

/**
 * Ensures the security context filter {@link SecurityContextFilter} is always the first filter in the chain.
 * 
 * @author Roberto E. Escobar
 */
public class SecureResourceFilterFactory extends RolesAllowedResourceFilterFactory {

   private final SecurityContextFilter securityContextFilter;

   public SecureResourceFilterFactory(SecurityContextFilter securityContextFilter) {
      super();
      this.securityContextFilter = securityContextFilter;
   }

   @Override
   public List<ResourceFilter> create(AbstractMethod am) {
      List<ResourceFilter> filters = super.create(am);
      if (filters == null) {
         filters = new ArrayList<ResourceFilter>();
      }
      List<ResourceFilter> securityFilters = new ArrayList<ResourceFilter>(filters);
      securityFilters.add(0, securityContextFilter);
      return securityFilters;
   }
}