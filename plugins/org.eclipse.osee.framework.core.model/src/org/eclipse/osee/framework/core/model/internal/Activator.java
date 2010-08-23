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
package org.eclipse.osee.framework.core.model.internal;

import org.eclipse.osee.framework.core.model.BranchFactory;
import org.eclipse.osee.framework.core.model.OseeModelFactoryService;
import org.eclipse.osee.framework.core.model.TransactionRecordFactory;
import org.eclipse.osee.framework.core.model.type.ArtifactTypeFactory;
import org.eclipse.osee.framework.core.model.type.AttributeTypeFactory;
import org.eclipse.osee.framework.core.model.type.OseeEnumTypeFactory;
import org.eclipse.osee.framework.core.model.type.RelationTypeFactory;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

   private ServiceRegistration registration;

   @Override
   public void start(BundleContext context) throws Exception {
      IOseeModelFactoryService service = createFactoryService();
      registration = context.registerService(IOseeModelFactoryService.class.getName(), service, null);
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      if (registration != null) {
         registration.unregister();
      }
   }

   private IOseeModelFactoryService createFactoryService() {
      return new OseeModelFactoryService(new BranchFactory(), new TransactionRecordFactory(),
         new ArtifactTypeFactory(), new AttributeTypeFactory(), new RelationTypeFactory(), new OseeEnumTypeFactory());
   }

}
