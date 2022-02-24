/*******************************************************************************
 * Copyright (c) 2022 Boeing.
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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.util.StringNameComparator;
import org.eclipse.osee.framework.ui.skynet.widgets.AttributeWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelValueSelection;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeDialog;

/**
 * @author Donald G. Dunne
 */
public class XHyperlinkWorkDefDam extends XHyperlinkLabelValueSelection implements AttributeWidget {

   private AttributeTypeToken attributeTypeToken;
   private final AtsApi atsApi;
   private String value = "Not Set";
   private Artifact artifact;

   public XHyperlinkWorkDefDam() {
      this("Workflow Definition");
   }

   public XHyperlinkWorkDefDam(String label) {
      super(label);
      atsApi = AtsApiService.get();
   }

   @Override
   public String getCurrentValue() {
      if (artifact == null) {
         value = "Not Set";
      } else {
         ArtifactId workDefId =
            atsApi.getAttributeResolver().getSoleAttributeValue(artifact, attributeTypeToken, ArtifactId.SENTINEL);
         if (workDefId.isInvalid()) {
            value = "Not Set";
         } else {
            IAtsWorkDefinition workDefinition = atsApi.getWorkDefinitionService().getWorkDefinition(workDefId);
            value = workDefinition.getName();
         }
      }
      return value;
   }

   @Override
   public Artifact getArtifact() {
      if (artifact == null) {
         return null;
      }
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
      this.attributeTypeToken = attributeTypeToken;
      this.artifact = artifact;
   }

   @Override
   public boolean handleSelection() {
      if (!atsApi.getUserService().isAtsAdmin()) {
         AWorkbench.popup("Only ATS Admins can set this value and it should normally not be changed");
         return false;
      }
      try {
         if (MessageDialog.openConfirm(AWorkbench.getActiveShell(), "Change Work Definition",
            "This is an ATS Admin function only and NOT an normal function due to incompatibilty between Work Definitions\n\nAre you sure?")) {
            FilteredTreeDialog dialog = new FilteredTreeDialog("Select Work Definition", "Select Work Definition",
               new ArrayTreeContentProvider(), new StringLabelProvider(), new StringNameComparator());
            dialog.setInput(atsApi.getWorkDefinitionService().getAllWorkDefinitions());
            if (dialog.open() == Window.OK) {
               IAtsWorkDefinition workDef = dialog.getSelectedFirst();

               IAtsChangeSet changes = atsApi.createChangeSet(getLabel());
               changes.setSoleAttributeValue(artifact, attributeTypeToken, ArtifactId.valueOf(workDef.getId()));
               changes.execute();

               refresh();
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeTypeToken;
   }

}
