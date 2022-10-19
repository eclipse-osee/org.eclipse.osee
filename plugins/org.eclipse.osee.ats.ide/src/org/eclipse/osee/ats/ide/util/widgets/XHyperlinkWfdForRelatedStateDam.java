/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.AttributeWidget;
import org.eclipse.osee.framework.ui.swt.Widgets;

/**
 * @author Donald G. Dunne
 */
public class XHyperlinkWfdForRelatedStateDam extends XHyperlinkWfdForRelatedState implements AttributeWidget {

   private static final String CLEAR = "-- clear --";

   public XHyperlinkWfdForRelatedStateDam() {
      super(Collections.emptyList());
   }

   private Artifact artifact;
   private AttributeTypeToken attributeTypeToken;

   @Override
   public void handleSelectionPersist(String selected) {
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Set Related State");
      if (selected.equals(CLEAR)) {
         changes.deleteAttributes(artifact, attributeTypeToken);
      } else {
         changes.setSoleAttributeValue(artifact, attributeTypeToken, selected);
      }
      changes.executeIfNeeded();
   }

   @Override
   public Artifact getArtifact() {
      return artifact;
   }

   @Override
   public void saveToArtifact() {
      // do nothing
   }

   @Override
   public void revert() {
      // do nothing
   }

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

   @Override
   public void setAttributeType(Artifact artifact, AttributeTypeToken attributeTypeToken) {
      this.artifact = artifact;
      this.attributeTypeToken = attributeTypeToken;
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeTypeToken;
   }

   @Override
   public Collection<String> getSelectable() {
      if (artifact.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
         IAtsTeamWorkflow teamWf = ((IAtsWorkItem) artifact).getParentTeamWorkflow();
         if (teamWf != null) {
            Collection<String> stateNames =
               AtsApiService.get().getWorkDefinitionService().getStateNames(teamWf.getWorkDefinition());
            stateNames.add(CLEAR);
            return stateNames;
         }
      }
      return Collections.emptyList();
   }

   @Override
   public String getCurrentValue() {
      if (artifact == null) {
         return Widgets.NOT_SET;
      }
      return artifact.getSoleAttributeValueAsString(attributeTypeToken, Widgets.NOT_SET);
   }

}
