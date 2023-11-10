/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.ide.editor.tab.workflow.header;

import java.util.Collections;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.PromptChangeUtil;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class WfeWorkPackage extends Composite {

   private final static String WORK_PACKAGE = "Work Package:";
   Text valueLabel;
   Hyperlink link;
   private final IAtsTeamWorkflow teamWf;
   AtsApi atsApi;

   public WfeWorkPackage(Composite parent, int style, final IAtsTeamWorkflow teamWf, final WorkflowEditor editor) {
      super(parent, style);
      this.teamWf = teamWf;
      this.atsApi = AtsApiService.get();
      setLayoutData(new GridData());
      setLayout(ALayout.getZeroMarginLayout(2, false));
      editor.getToolkit().adapt(this);

      try {
         link = editor.getToolkit().createHyperlink(this, WORK_PACKAGE, SWT.NONE);
         link.addHyperlinkListener(new IHyperlinkListener() {

            @Override
            public void linkEntered(HyperlinkEvent e) {
               // do nothing
            }

            @Override
            public void linkExited(HyperlinkEvent e) {
               // do nothing
            }

            @Override
            public void linkActivated(HyperlinkEvent e) {
               try {
                  if (editor.isDirty()) {
                     editor.doSave(null);
                  }
                  if (atsApi.getEarnedValueService().isUseTextWorkPackages(Collections.singleton(teamWf))) {
                     PromptChangeUtil.promptChangeAttribute(
                        Collections.singleton((AbstractWorkflowArtifact) teamWf.getStoreObject()),
                        AtsAttributeTypes.WorkPackage, true);
                  }
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });

         valueLabel = new Text(this, SWT.NO_TRIM);
         valueLabel.setLayoutData(new GridData());
         editor.getToolkit().adapt(valueLabel, true, true);
         valueLabel.setText(Widgets.NOT_SET);
         refresh();
      } catch (OseeCoreException ex) {
         Label errorLabel = editor.getToolkit().createLabel(this, "Error: " + ex.getLocalizedMessage());
         errorLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

   }

   public void refresh() {
      if (Widgets.isAccessible(valueLabel)) {
         String value = Widgets.NOT_SET;
         if (atsApi.getEarnedValueService().isUseTextWorkPackages(Collections.singleton(teamWf))) {
            value = atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.WorkPackage, "");
         } else {
            IAtsWorkPackage workPackage = atsApi.getEarnedValueService().getWorkPackage(teamWf);
            if (workPackage != null) {
               value = workPackage.toString();
            }
         }
         valueLabel.setText(value);
         valueLabel.getParent().layout();
         valueLabel.getParent().getParent().layout();
      }
   }

   @Override
   public void setBackground(Color color) {
      super.setBackground(color);
      if (Widgets.isAccessible(valueLabel)) {
         valueLabel.setBackground(color);
      }
      if (Widgets.isAccessible(link)) {
         link.setBackground(color);
      }
   }

}
