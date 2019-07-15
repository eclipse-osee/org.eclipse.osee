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
package org.eclipse.osee.ats.ide.editor;

import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * @author Donald G. Dunne
 */
public class WfeTeamAndIdsHeader extends Composite implements IWfeEventHandle {

   private final IAtsWorkItem workItem;
   Text teamWfIdValue, parentIdValue, idValue, actionIdValue;

   public WfeTeamAndIdsHeader(Composite parent, int style, final IAtsWorkItem workItem, final WorkflowEditor editor) {
      super(parent, style);
      this.workItem = workItem;
      setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      setLayout(ALayout.getZeroMarginLayout(3, true));
      editor.getToolkit().adapt(this);

      try {

         if (workItem.isTeamWorkflow()) {
            teamWfIdValue = FormsUtil.createLabelText(editor.getToolkit(), this, "Team: ", "").getSecond();
         } else if ((workItem.isTask() || workItem.isReview()) && workItem.getParentTeamWorkflow() != null) {
            parentIdValue = FormsUtil.createLabelText(editor.getToolkit(), this, "Parent Id: ", "").getSecond();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      try {
         idValue = FormsUtil.createLabelText(editor.getToolkit(), this, workItem.getArtifactTypeName() + " Id: ",
            "").getSecond();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      try {
         IAtsAction action = workItem.getParentAction();
         if (action != null) {
            actionIdValue = FormsUtil.createLabelText(editor.getToolkit(), this, "Action Id: ", "").getSecond();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      refresh();
      if (workItem.getStoreObject() instanceof Artifact) {
         editor.registerEvent(this, (Artifact) workItem.getStoreObject());
      }

   }

   @Override
   public void refresh() {
      if (Widgets.isAccessible(idValue)) {
         String legacyPcrId = (AtsClientService.get()).getAttributeResolver().getSoleAttributeValueAsString(workItem,
            AtsAttributeTypes.LegacyPcrId, "");
         if (Strings.isValid(legacyPcrId)) {
            legacyPcrId = " | " + legacyPcrId;
         } else {
            legacyPcrId = "";
         }
         idValue.setText(workItem.getAtsId() + legacyPcrId);
         if (workItem.isTeamWorkflow()) {
            teamWfIdValue.setText(((TeamWorkFlowArtifact) workItem).getTeamName());
         } else if ((workItem.isTask() || workItem.isReview()) && workItem.getParentTeamWorkflow() != null) {
            IAtsTeamWorkflow parentTeamWorkflow = workItem.getParentTeamWorkflow();
            parentIdValue.setText(AtsClientService.get().getWorkItemService().getCombinedPcrId(parentTeamWorkflow));
         }
         IAtsAction action = workItem.getParentAction();
         if (action != null) {
            actionIdValue.setText(action.getAtsId());
         }
      }
   }

   @Override
   public IAtsWorkItem getWorkItem() {
      return workItem;
   }
}
