/*********************************************************************
 * Copyright (c) 2020 Boeing
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
