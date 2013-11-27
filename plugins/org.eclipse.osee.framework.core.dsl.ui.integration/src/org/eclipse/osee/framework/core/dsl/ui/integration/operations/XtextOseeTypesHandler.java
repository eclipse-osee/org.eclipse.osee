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
package org.eclipse.osee.framework.core.dsl.ui.integration.operations;

import java.net.URI;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.importing.IOseeTypesHandler;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Roberto E. Escobar
 */
public class XtextOseeTypesHandler implements IOseeTypesHandler {

   @Override
   public void execute(IProgressMonitor monitor, URI uri) throws OseeCoreException {
      IOseeCachingService cacheService = getCachingService();
      IOperation operation = new OseeTypesImportOperation(cacheService, uri, false);
      Operations.executeWorkAndCheckStatus(operation, monitor);
   }

   @Override
   public boolean isApplicable(String resource) {
      return Strings.isValid(resource) && resource.endsWith(".osee");
   }

   private IOseeCachingService getCachingService() throws OseeCoreException {
      Bundle bundle = FrameworkUtil.getBundle(getClass());
      Conditions.checkNotNull(bundle, "bundle");
      BundleContext bundleContext = bundle.getBundleContext();
      Conditions.checkNotNull(bundleContext, "bundleContext");
      ServiceReference<IOseeCachingService> reference = bundleContext.getServiceReference(IOseeCachingService.class);
      IOseeCachingService cacheService = bundleContext.getService(reference);
      Conditions.checkNotNull(cacheService, "cacheService");
      return cacheService;
   }

}
