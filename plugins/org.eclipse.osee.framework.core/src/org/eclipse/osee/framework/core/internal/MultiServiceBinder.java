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
package org.eclipse.osee.framework.core.internal;

import java.util.Collection;
import java.util.Map;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.osgi.framework.BundleContext;

/**
 * @author Roberto E. Escobar
 */
public final class MultiServiceBinder extends AbstractServiceBinder {

   public MultiServiceBinder(Map<Class<?>, Collection<Object>> serviceMap, BundleContext bundleContext, AbstractTrackingHandler handler) {
      super(serviceMap, bundleContext, handler);
   }

   @Override
   protected void doAdd(Collection<Object> associatedServices, Object service) {
      associatedServices.add(service);
   }
}