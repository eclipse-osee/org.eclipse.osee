/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.messaging.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Andrew M. Finkbeiner
 */
public class Activator implements BundleActivator {
   private static Activator me;
   private BundleContext context;

   @Override
   public void start(BundleContext context) throws Exception {
      this.context = context;
      me = this;
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      me = null;
      this.context = null;
   }

   public static Activator getInstance() {
      return me;
   }

   public BundleContext getContext() {
      return context;
   }
}
