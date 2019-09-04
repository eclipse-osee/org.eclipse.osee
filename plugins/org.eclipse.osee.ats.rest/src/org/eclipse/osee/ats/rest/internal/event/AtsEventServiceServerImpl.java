/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.event;

import org.eclipse.osee.ats.core.event.AbstractAtsEventServiceImpl;
import org.osgi.framework.BundleContext;

/**
 * @author Donald G. Dunne
 */
public class AtsEventServiceServerImpl extends AbstractAtsEventServiceImpl {

   public AtsEventServiceServerImpl() {
      // for jax-rs instantiation
   }

   @Override
   public BundleContext getBundleContext(String pluginId) {
      throw new UnsupportedOperationException("Not supported on server.");
   }

}
