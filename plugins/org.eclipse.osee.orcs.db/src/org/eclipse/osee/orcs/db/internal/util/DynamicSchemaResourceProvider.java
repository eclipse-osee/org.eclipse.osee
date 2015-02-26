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
import org.eclipse.osee.jdbc.JdbcMigrationResource;
import org.eclipse.osee.logger.Log;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import com.google.common.base.Supplier;

/**
 * @author Roberto E. Escobar
 */
public class DynamicSchemaResourceProvider implements Supplier<Iterable<JdbcMigrationResource>> {

   private final Log logger;

   public DynamicSchemaResourceProvider(Log logger) {
      super();
      this.logger = logger;
   }

   @Override
   public Iterable<JdbcMigrationResource> get() {
      List<JdbcMigrationResource> resources = new ArrayList<JdbcMigrationResource>();

      Bundle bundle = FrameworkUtil.getBundle(this.getClass());
      BundleContext context = bundle.getBundleContext();
      try {
         Collection<ServiceReference<JdbcMigrationResource>> references =
            context.getServiceReferences(JdbcMigrationResource.class, null);

         for (ServiceReference<JdbcMigrationResource> ref : references) {
            JdbcMigrationResource resource = context.getService(ref);
            resources.add(resource);
         }

      } catch (InvalidSyntaxException ex) {
         logger.warn(ex.toString(), ex);
      }
      return resources;
   }

}
