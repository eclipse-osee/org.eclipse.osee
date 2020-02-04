/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.config.TeamDefinition;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.query.IAtsConfigCacheQuery;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigCacheQueryImpl implements IAtsConfigCacheQuery {

   protected List<ArtifactTypeToken> artifactTypes;
   protected final AtsApi atsApi;
   private Boolean active = null;
   private List<WorkType> workTypes = null;

   public AtsConfigCacheQueryImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T extends IAtsConfigObject> Collection<T> get(Class<T> clazz) {
      Set<T> results = new HashSet<>();
      if (artifactTypes.contains(AtsArtifactTypes.TeamDefinition)) {
         for (TeamDefinition teamDef : atsApi.getConfigService().getConfigurations().getIdToTeamDef().values()) {
            if (active != null) {
               if (!active.equals(teamDef.isActive())) {
                  continue;
               }
            }
            if (workTypes != null && !workTypes.isEmpty()) {
               List<String> workTypeStrs = new ArrayList<>();
               for (WorkType workType : workTypes) {
                  workTypeStrs.add(workType.name());
               }
               if (!workTypeStrs.contains(teamDef.getWorkType())) {
                  continue;
               }
            }
            results.add((T) teamDef);
         }
      }
      return results;
   }

   @Override
   public IAtsConfigCacheQuery isOfType(ArtifactTypeToken... artifactType) {
      if (this.artifactTypes != null) {
         throw new OseeArgumentException("Can only specify one artifact type");
      }
      this.artifactTypes = new LinkedList<>();
      for (ArtifactTypeToken type : artifactType) {
         this.artifactTypes.add(type);
      }
      return this;
   }

   @Override
   public IAtsConfigCacheQuery andWorkType(WorkType workType, WorkType... wts) {
      workTypes = new LinkedList<>();
      workTypes.add(workType);
      for (WorkType wt : wts) {
         workTypes.add(wt);
      }
      return this;
   }

   @Override
   public IAtsConfigCacheQuery andActive(boolean active) {
      this.active = active;
      return this;
   }

}
