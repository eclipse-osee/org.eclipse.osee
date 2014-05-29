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
package org.eclipse.osee.ote.rest.internal;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Get application.wadl at this context to get rest documentation
 * 
 * @author Roberto E. Escobar
 */
@ApplicationPath("ote")
public class OteRestApplication extends Application {

   private static OteConfigurationStore store;

   public void setOteConfigurationStore(OteConfigurationStore store) {
      OteRestApplication.store = store;
   }

   public static OteConfigurationStore get() {
      return store;
   }

   @Override
   public Set<Class<?>> getClasses() {
      Set<Class<?>> classes = new HashSet<Class<?>>();
      classes.add(OteRootResource.class);
      classes.add(OteConfigurationResource.class);
      classes.add(OteJobsResource.class);
      classes.add(OteFilesResource.class);
      classes.add(OteRunTestResource.class);
      classes.add(OteBatchesResource.class);
      return classes;
   }

}
