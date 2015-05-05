/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.internal.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.core.client.IAtsClient;
import org.eclipse.osee.ats.core.query.AbstractAtsQueryImpl;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.QueryBuilderArtifact;

/**
 * @author Donald G. Dunne
 */
public class AtsQueryImpl extends AbstractAtsQueryImpl {

   private final IAtsClient atsClient;

   public AtsQueryImpl(IAtsClient atsClient) {
      super(atsClient.getWorkItemService());
      this.atsClient = atsClient;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T extends IAtsWorkItem> Collection<T> getItems() throws OseeCoreException {
      QueryBuilderArtifact query = ArtifactQuery.createQueryBuilder(AtsUtilCore.getAtsBranch());

      // WorkItem type
      if (clazz != null) {
         List<IArtifactType> artifactTypes = getArtifactTypes();
         query.andTypeEquals(artifactTypes.toArray(new IArtifactType[artifactTypes.size()]));
      }

      // team
      if (teamDef != null) {
         query.and(AtsAttributeTypes.TeamDefinition, Collections.singleton(AtsUtilCore.getGuid(teamDef)));
      }

      // state
      if (stateType != null) {
         List<String> stateTypes = new ArrayList<String>();
         for (StateType type : stateType) {
            stateTypes.add(type.name());
         }
         query.and(AtsAttributeTypes.CurrentStateType, stateTypes);
      }

      // attributes
      if (!andAttr.isEmpty()) {
         for (Entry<IAttributeType, Collection<String>> entry : andAttr.entrySet()) {
            QueryOption[] opts = getQueryOptions(entry.getKey());
            query.and(entry.getKey(), entry.getValue(), opts);
         }
      }

      Set<T> workItems = new HashSet<T>();
      Iterator<Artifact> iterator = query.getResults().iterator();
      while (iterator.hasNext()) {
         workItems.add((T) atsClient.getWorkItemFactory().getWorkItem(iterator.next()));
      }
      return workItems;

   }
}
