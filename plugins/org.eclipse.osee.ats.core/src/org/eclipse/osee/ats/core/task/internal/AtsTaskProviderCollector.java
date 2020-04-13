/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.task.internal;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.task.IAtsTaskProvider;

/**
 * Should not be used directly, use through IAtsTaskService
 *
 * @author Donald G. Dunne
 */
public class AtsTaskProviderCollector {

   private static List<IAtsTaskProvider> taskProviders = new ArrayList<IAtsTaskProvider>();

   public AtsTaskProviderCollector() {
      // for jax-rs
   }

   public void addTaskProvider(IAtsTaskProvider taskProvider) {
      taskProviders.add(taskProvider);
   }

   public static List<IAtsTaskProvider> getTaskProviders() {
      return taskProviders;
   }

}
