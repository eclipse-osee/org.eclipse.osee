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
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XHyperlinkWfdForRelatedStateArtWidget extends XHyperlinkWfdForRelatedStateWidget {

   public static final WidgetId ID = WidgetIdAts.XHyperlinkWfdForRelatedStateArtWidget;

   private static final String CLEAR = "-- clear --";

   public XHyperlinkWfdForRelatedStateArtWidget() {
      super(ID, Collections.emptyList());
   }

   @Override
   public void handleSelectionPersist(String selected) {
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Set Related State");
      if (selected.equals(CLEAR)) {
         changes.deleteAttributes(getArtifact(), getAttributeType());
      } else {
         changes.setSoleAttributeValue(getArtifact(), getAttributeType(), selected);
      }
      changes.executeIfNeeded();
   }

   @Override
   public Collection<String> getSelectable() {
      if (getArtifact().isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
         IAtsTeamWorkflow teamWf = ((IAtsWorkItem) getArtifact()).getParentTeamWorkflow();
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
      if (getArtifact() == null) {
         return Widgets.NOT_SET;
      }
      return getArtifact().getSoleAttributeValueAsString(getAttributeType(), Widgets.NOT_SET);
   }

}
