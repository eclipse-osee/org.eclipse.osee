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

import java.util.Map;
import org.osgi.framework.BundleContext;

/**
 * @author Andrew M. Finkbeiner
 * @author Roberto E. Escobar
 */
public abstract class AbstractTrackingHandler {

   protected AbstractTrackingHandler() {
   }

   protected <T> T getService(Class<T> clazz, Map<Class<?>, Object> objectMap) {
      return clazz.cast(objectMap.get(clazz));
   }

   public abstract Class<?>[] getDependencies();

   public abstract void onActivate(BundleContext context, Map<Class<?>, Object> services);

   public abstract void onDeActivate();

}