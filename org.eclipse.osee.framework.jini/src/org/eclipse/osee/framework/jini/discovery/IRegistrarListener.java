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
package org.eclipse.osee.framework.jini.discovery;

import java.util.List;
import net.jini.core.lookup.ServiceRegistrar;

/**
 * @author Andrew M. Finkbeiner
 */
public interface IRegistrarListener {
   public void reggieAdded(List<ServiceRegistrar> serviceRegistrars);

   public void reggieRemoved(List<ServiceRegistrar> serviceRegistrars);

   public void reggieChanged(List<ServiceRegistrar> serviceRegistrars);
}
