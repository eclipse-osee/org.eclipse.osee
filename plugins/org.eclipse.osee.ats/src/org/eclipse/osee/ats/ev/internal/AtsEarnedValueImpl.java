/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ev.internal;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.ev.AtsWorkPackageEndpointApi;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.ev.JaxWorkPackageData;
import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.core.util.AtsAbstractEarnedValueImpl;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.TopicEvent;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G. Dunne
 */
public class AtsEarnedValueImpl extends AtsAbstractEarnedValueImpl {

   public AtsEarnedValueImpl(Log logger, AtsApi atsApi) {
      super(logger, atsApi);
   }

   @Override
   public ArtifactId getWorkPackageId(IAtsWorkItem workItem) {
      ArtifactId id = ArtifactId.SENTINEL;
      Artifact artifact = AtsClientService.get().getQueryServiceClient().getArtifact(workItem);
      Conditions.checkNotNull(artifact, "workItem", "Can't Find Work Package matching %s", workItem.toStringWithId());
      if (artifact instanceof AbstractWorkflowArtifact) {
         AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) artifact;
         id = awa.getSoleAttributeValue(AtsAttributeTypes.WorkPackageReference, id);
      }
      return id;
   }

   @Override
   public void setWorkPackage(IAtsWorkPackage workPackage, Collection<IAtsWorkItem> workItems) {
      changeWorkPackage(workPackage, workItems, false);
   }

   @Override
   public void removeWorkPackage(IAtsWorkPackage workPackage, Collection<IAtsWorkItem> workItems) {
      changeWorkPackage(workPackage, workItems, true);
   }

   private void changeWorkPackage(IAtsWorkPackage workPackage, Collection<IAtsWorkItem> workItems, boolean remove) {
      JaxWorkPackageData data = new JaxWorkPackageData();
      data.setAsUserId(atsApi.getUserService().getCurrentUserId());
      for (IAtsWorkItem workItem : workItems) {
         data.getWorkItemIds().add(workItem.getId());
      }

      AtsWorkPackageEndpointApi workPackageEp = AtsClientService.getWorkPackageEndpoint();
      if (remove) {
         workPackageEp.deleteWorkPackageItems(workPackage == null ? 0L : workPackage.getId(), data);
      } else {
         workPackageEp.setWorkPackage(workPackage.getId(), data);
      }

      TopicEvent event = new TopicEvent(AtsTopicEvent.WORK_ITEM_MODIFIED, AtsTopicEvent.WORK_ITEM_IDS_KEY,
         AtsObjects.toIdsString(";", workItems));
      OseeEventManager.kickTopicEvent(getClass(), event);

      atsApi.getStoreService().reload(workItems);
   }

   @Override
   public Collection<String> getColorTeams() {
      return AttributeTypeManager.getEnumerationValues(AtsAttributeTypes.ColorTeam);
   }

}
