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
package org.eclipse.osee.framework.core.datastore;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Roberto E. Escobar
 */
public class OseeSchemaProvider implements IOseeSchemaProvider {

   public Collection<IOseeSchemaResource> getSchemaResources() {
      Collection<IOseeSchemaResource> providers = new ArrayList<IOseeSchemaResource>();

      Bundle bundle = Platform.getBundle("org.eclipse.osee.framework.core.datastore");
      ServiceTracker serviceTracker =
            new ServiceTracker(bundle.getBundleContext(), IOseeSchemaResource.class.getName(), null);
      serviceTracker.open(true);
      Object[] services = serviceTracker.getServices();
      if (services != null) {
         for (Object object : services) {
            if (object instanceof IOseeSchemaResource) {
               providers.add((IOseeSchemaResource) object);
            }
         }
      }
      serviceTracker.close();
      return providers;
   }
}
