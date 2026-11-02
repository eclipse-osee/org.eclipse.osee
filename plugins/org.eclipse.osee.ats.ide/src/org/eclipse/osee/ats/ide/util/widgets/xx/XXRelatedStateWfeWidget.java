/*********************************************************************
 * Copyright (c) 2022, 2026 Boeing
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
package org.eclipse.osee.ats.ide.util.widgets.xx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.column.RelatedToStateColumnUI;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.util.XXWorkItemData;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXStringsWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXRelatedStateWfeWidget extends XXStringsWidget {

   public static final WidgetId ID = WidgetIdAts.XXRelatedStateWfeWidget;

   protected XXWorkItemData xxWid = XXWorkItemData.SENTINEL;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXRelatedStateWfeWidget() {
      this(Collections.emptyList());
   }

   public XXRelatedStateWfeWidget(Collection<String> selectable) {
      this(ID, selectable);
   }

   public XXRelatedStateWfeWidget(WidgetId widgetId, Collection<String> selectable) {
      super(widgetId, "Related State");
      // Select one InWork state from the parent Team Wf's work def (see getSelectable), not free text.
      set(XOption.VAL_SINGLE_SEL);
      setAttributeType(AtsAttributeTypes.RelatedToState);
      setSelectable(selectable);
      setToolTip(RelatedToStateColumnUI.RELATED_TO_STATE_SELECTION);
      setAsProviderFor(AtsAttributeTypes.RelatedToState);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   /**
    * Selectable values are the InWork (working) states of the parent Team Workflow's work definition, in ordinal
    * order. Completed/Cancelled states are not valid targets for "Related To State".
    */
   @Override
   public Collection<String> getSelectable() {
      if (getArtifact().isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
         IAtsTeamWorkflow teamWf = ((IAtsWorkItem) getArtifact()).getParentTeamWorkflow();
         if (teamWf != null) {
            List<String> workingStateNames = new ArrayList<>();
            List<StateDefinition> states =
               AtsApiService.get().getWorkDefinitionService().getStatesOrderedByOrdinal(teamWf.getWorkDefinition());
            for (StateDefinition state : states) {
               if (state.isWorking()) {
                  workingStateNames.add(state.getName());
               }
            }
            return workingStateNames;
         }
      }
      return super.getSelectable();
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   public void setArtifact(Artifact artifact) {
      xxWid = XXWorkItemData.get(artifact);
      super.setArtifact(artifact);
   }

}
