/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.internal.search.branch.BranchQueryResource;

/**
 * Get application.wadl at this context to get rest documentation
 * 
 * @author Roberto E. Escobar
 */
public class OrcsApplication extends Application {

   private final Set<Object> resources = new HashSet<Object>();
   private final Set<Class<?>> classes = new HashSet<Class<?>>();
   private static OrcsApi orcsApi;

   public void setOrcsApi(OrcsApi orcsApi) {
      OrcsApplication.orcsApi = orcsApi;
   }

   public static OrcsApi getOrcsApi() {
      return orcsApi;
   }

   public void start() {
      classes.add(BranchesResource.class);
      resources.add(new BranchQueryResource(orcsApi));
   }

   public void stop() {
      resources.clear();
      classes.clear();
   }

   @Override
   public Set<Class<?>> getClasses() {
      return classes;
   }

   @Override
   public Set<Object> getSingletons() {
      return resources;
   }

}
