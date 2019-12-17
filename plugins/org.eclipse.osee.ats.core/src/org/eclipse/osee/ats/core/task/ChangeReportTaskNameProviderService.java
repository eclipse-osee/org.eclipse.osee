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
package org.eclipse.osee.ats.core.task;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskNameProviderToken;
import org.eclipse.osee.ats.api.task.create.IAtsChangeReportTaskNameProvider;

/**
 * @author Donald G. Dunne
 */
public class ChangeReportTaskNameProviderService {

   private static final List<IAtsChangeReportTaskNameProvider> changeReportOptionNameProviders = new ArrayList<>();

   public ChangeReportTaskNameProviderService() {
      // for jax-rs
   }

   public void addChangeReportOptionsNameProvider(IAtsChangeReportTaskNameProvider provider) {
      changeReportOptionNameProviders.add(provider);
   }

   public static IAtsChangeReportTaskNameProvider getChangeReportOptionNameProvider(ChangeReportTaskNameProviderToken token) {
      for (IAtsChangeReportTaskNameProvider provider : changeReportOptionNameProviders) {
         if (provider.getId().equals(token)) {
            return provider;
         }
      }
      return null;
   }

}
