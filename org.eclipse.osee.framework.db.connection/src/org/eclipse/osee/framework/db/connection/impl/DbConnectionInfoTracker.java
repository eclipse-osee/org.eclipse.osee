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

package org.eclipse.osee.framework.db.connection.impl;

import org.eclipse.osee.framework.db.connection.IDbConnectionInformation;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Andrew M. Finkbeiner
 */
public class DbConnectionInfoTracker extends ServiceTracker {

   private IDbConnectionInformation service;

   /**
    * @param context
    * @param filter
    * @param customizer
    */
   public DbConnectionInfoTracker(BundleContext context) {
      super(context, IDbConnectionInformation.class.getName(), null);
   }

   /* (non-Javadoc)
    * @see org.osgi.util.tracker.ServiceTracker#addingService(org.osgi.framework.ServiceReference)
    */
   @Override
   public Object addingService(ServiceReference reference) {
      service = (IDbConnectionInformation) context.getService(reference);
      return service;
   }

   /* (non-Javadoc)
    * @see org.osgi.util.tracker.ServiceTracker#removedService(org.osgi.framework.ServiceReference, java.lang.Object)
    */
   @Override
   public void removedService(ServiceReference reference, Object service) {
      context.ungetService(reference);
   }

   public IDbConnectionInformation get() {
      return service;
   }

}
