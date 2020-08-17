/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.core.workdef.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionProvider;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionProviderService;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;

/**
 * Service to retrieve all work definitions that have been registered. Should not be used by applications, only by the
 * WorkDefinitionService
 *
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionProviderService implements IAtsWorkDefinitionProviderService {

   private static Map<Long, IAtsWorkDefinition> idToWorkDef = new HashMap<>();
   private final Collection<IAtsWorkDefinitionProvider> workDefProviders = new ArrayList<>();
   private final Collection<IAtsWorkDefinitionProvider> workDefProviderProcessed = new ArrayList<>();
   private AtsWorkDefinitionProvider atsWorkDefProv;

   @Override
   public void addWorkDefinitionProvider(final IAtsWorkDefinitionProvider workDefProvider) {
      this.workDefProviders.add(workDefProvider);

      Thread load = new Thread("Load Work Defs") {

         @Override
         public void run() {
            handleProvider(workDefProvider);
         }

      };
      load.start();
   }

   public void ensureLoaded() {
      // Add default
      if (!workDefProviderProcessed.contains(atsWorkDefProv)) {
         atsWorkDefProv = new AtsWorkDefinitionProvider();
         workDefProviders.add(atsWorkDefProv);
      }
      // Add any not processed
      for (IAtsWorkDefinitionProvider workDefProvider : new CopyOnWriteArrayList<>(workDefProviders)) {
         handleProvider(workDefProvider);
      }
   }

   private void handleProvider(IAtsWorkDefinitionProvider workDefProvider) {
      if (!workDefProviderProcessed.contains(workDefProvider)) {
         for (IAtsWorkDefinition workDef : workDefProvider.getWorkDefinitions()) {
            idToWorkDef.put(workDef.getId(), workDef);
         }
         workDefProviderProcessed.add(workDefProvider);
      }
   }

   @Override
   public IAtsWorkDefinition getWorkDefinition(Long id) {
      ensureLoaded();
      return idToWorkDef.get(id);
   }

   @Override
   public Collection<IAtsWorkDefinition> getAll() {
      ensureLoaded();
      return idToWorkDef.values();
   }

   @Override
   public void addWorkDefinition(WorkDefinition workDef) {
      ensureLoaded();
      idToWorkDef.put(workDef.getId(), workDef);
   }

}
