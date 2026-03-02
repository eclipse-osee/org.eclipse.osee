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
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.util.StringNameComparator;
import org.eclipse.osee.framework.ui.skynet.widgets.XAbstractHyperlinkLabelValueSelWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeDialog;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XHyperlinkWorkDefArtWidget extends XAbstractHyperlinkLabelValueSelWidget {

   public static final WidgetId ID = WidgetIdAts.XHyperlinkWorkDefArtWidget;

   private final AtsApi atsApi;
   private String value = Widgets.NOT_SET;

   public XHyperlinkWorkDefArtWidget() {
      this("Workflow Definition");
   }

   public XHyperlinkWorkDefArtWidget(String label) {
      super(ID, label);
      atsApi = AtsApiService.get();
   }

   @Override
   public String getCurrentValue() {
      if (getArtifact() == null) {
         value = Widgets.NOT_SET;
      } else {
         ArtifactId workDefId =
            atsApi.getAttributeResolver().getSoleAttributeValue(getArtifact(), getAttributeType(), ArtifactId.SENTINEL);
         if (workDefId.isInvalid()) {
            value = Widgets.NOT_SET;
         } else {
            WorkDefinition workDefinition = atsApi.getWorkDefinitionService().getWorkDefinition(workDefId);
            if (workDefinition != null) {
               value = workDefinition.getName();
            }
         }
      }
      return value;
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
               WorkDefinition workDef = dialog.getSelectedFirst();

               IAtsChangeSet changes = atsApi.createChangeSet(getLabel());
               changes.setSoleAttributeValue(getArtifact(), getAttributeType(), ArtifactId.valueOf(workDef.getId()));
               changes.execute();

               refresh();
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

}
