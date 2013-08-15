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
import org.eclipse.osee.orcs.rest.internal.mappers.OseeCoreExceptionMapper;
import org.eclipse.osee.orcs.rest.internal.mappers.ThrowableExceptionMapper;

/**
 * Get application.wadl at this context to get rest documentation
 * 
 * @author Roberto E. Escobar
 */
public class OrcsApplication extends Application {

   private static OrcsApi orcsApi;

   public void setOrcsApi(OrcsApi orcsApi) {
      OrcsApplication.orcsApi = orcsApi;
   }

   public static OrcsApi getOrcsApi() {
      return orcsApi;
   }

   @Override
   public Set<Class<?>> getClasses() {
      Set<Class<?>> classes = new HashSet<Class<?>>();
      classes.add(BranchesResource.class);
      classes.add(OseeCoreExceptionMapper.class);
      classes.add(ThrowableExceptionMapper.class);
      return classes;
   }

}
