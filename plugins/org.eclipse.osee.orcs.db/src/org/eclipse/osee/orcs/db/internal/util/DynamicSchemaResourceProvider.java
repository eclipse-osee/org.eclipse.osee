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
package org.eclipse.osee.orcs.db.internal.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.database.schema.SchemaResource;
import org.eclipse.osee.database.schema.SchemaResourceProvider;
import org.eclipse.osee.logger.Log;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * @author Roberto E. Escobar
 */
public class DynamicSchemaResourceProvider implements SchemaResourceProvider {

   private final Log logger;

   public DynamicSchemaResourceProvider(Log logger) {
      super();
      this.logger = logger;
   }

   @Override
   public Collection<SchemaResource> getSchemaResources() {
      List<SchemaResource> resources = new ArrayList<SchemaResource>();

      Bundle bundle = FrameworkUtil.getBundle(this.getClass());
      BundleContext context = bundle.getBundleContext();
      try {
         Collection<ServiceReference<SchemaResource>> references =
            context.getServiceReferences(SchemaResource.class, null);

         for (ServiceReference<SchemaResource> ref : references) {
            SchemaResource resource = context.getService(ref);
            resources.add(resource);
         }

      } catch (InvalidSyntaxException ex) {
         logger.warn(ex.toString(), ex);
      }

      return resources;
   }

}
