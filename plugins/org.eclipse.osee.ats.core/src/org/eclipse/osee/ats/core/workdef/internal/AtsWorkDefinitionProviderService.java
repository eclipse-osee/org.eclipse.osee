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
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionProvider;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionProviderService;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * Service to retrieve all work definitions that have been registered. Should not be used by applications, only by the
 * WorkDefinitionService
 *
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionProviderService implements IAtsWorkDefinitionProviderService {

   private static Map<Long, WorkDefinition> idToWorkDef = new HashMap<>();
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
      int cacheSize = idToWorkDef.keySet().size();
      // Add any not processed
      for (IAtsWorkDefinitionProvider workDefProvider : new CopyOnWriteArrayList<>(workDefProviders)) {
         handleProvider(workDefProvider);
      }
      // Don't process if no new work defs
      if (cacheSize == idToWorkDef.keySet().size()) {
         return;
      }
      XResultData rd = new XResultData();
      for (WorkDefinition workDef : idToWorkDef.values()) {
         if (workDef.getResults().isErrors()) {
            rd.merge(workDef.getResults());
         }
      }
      if (rd.isErrors()) {
         throw new OseeArgumentException("Exception Building WorkDef(s) %s", rd.toString());
      }
   }

   private void handleProvider(IAtsWorkDefinitionProvider workDefProvider) {
      if (!workDefProviderProcessed.contains(workDefProvider)) {
         for (WorkDefinition workDef : workDefProvider.getWorkDefinitions()) {
            idToWorkDef.put(workDef.getId(), workDef);
         }
         workDefProviderProcessed.add(workDefProvider);
      }
   }

   @Override
   public WorkDefinition getWorkDefinition(Long id) {
      ensureLoaded();
      return idToWorkDef.get(id);
   }

   @Override
   public Collection<WorkDefinition> getAll() {
      ensureLoaded();
      return idToWorkDef.values();
   }

   @Override
   public void addWorkDefinition(WorkDefinition workDef) {
      ensureLoaded();
      idToWorkDef.put(workDef.getId(), workDef);
   }

}
