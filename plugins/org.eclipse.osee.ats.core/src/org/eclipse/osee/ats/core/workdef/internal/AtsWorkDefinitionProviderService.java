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
package org.eclipse.osee.ats.core.workdef.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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

   @Override
   public void addWorkDefinitionProvider(IAtsWorkDefinitionProvider workDefProvider) {
      this.workDefProviders.add(workDefProvider);
   }

   public void ensureLoaded() {
      if (idToWorkDef.isEmpty()) {
         workDefProviders.add(new AtsWorkDefinitionProvider());
         for (IAtsWorkDefinitionProvider workDefProvider : workDefProviders) {
            for (IAtsWorkDefinition workDef : workDefProvider.getWorkDefinitions()) {
               idToWorkDef.put(workDef.getId(), workDef);
            }
         }
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
