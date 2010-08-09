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
package org.eclipse.osee.framework.core.util;

import java.util.Collection;
import java.util.Map;
import org.osgi.framework.BundleContext;

/**
 * @author Andrew M. Finkbeiner
 * @author Roberto E. Escobar
 */
public final class SingletonServiceBinder extends AbstractServiceBinder {

   public SingletonServiceBinder(Map<Class<?>, Collection<Object>> serviceMap, BundleContext context, AbstractTrackingHandler handler) {
      super(serviceMap, context, handler);
   }

   @Override
   protected void doAdd(Collection<Object> associatedServices, Object service) {
      if (!associatedServices.isEmpty()) {
         if (!associatedServices.contains(service)) {
            throw new IllegalStateException(String.format("Attempting to overwrite existing service reference: [%s]",
               service.getClass().getName()));
         }
      }
      associatedServices.add(service);
   }
}
