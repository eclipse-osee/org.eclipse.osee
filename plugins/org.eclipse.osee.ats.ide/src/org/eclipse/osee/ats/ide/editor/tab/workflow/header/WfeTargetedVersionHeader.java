/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.editor.tab.workflow.header;

import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.version.VersionLockedType;
import org.eclipse.osee.ats.api.version.VersionReleaseType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.column.TargetedVersionColumnUI;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.event.IWfeEventHandle;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
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
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class WfeTargetedVersionHeader extends Composite implements IWfeEventHandle {

   private final static String TARGET_VERSION = "Target Version:";
   Label valueLabel;
   Label origLabel;
   Hyperlink link;
   private final IAtsTeamWorkflow teamWf;

   public WfeTargetedVersionHeader(Composite parent, int style, final IAtsTeamWorkflow teamWf, final WorkflowEditor editor) {
      super(parent, style);
      this.teamWf = teamWf;
      setLayoutData(new GridData());
      setLayout(ALayout.getZeroMarginLayout(2, false));
      editor.getToolkit().adapt(this);

      try {
         link = editor.getToolkit().createHyperlink(this, TARGET_VERSION, SWT.NONE);
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
               if (editor.isDirty()) {
                  editor.doSave(null);
               }
               if (chooseVersion(teamWf)) {
                  refresh();

               }

               editor.onDirtied();
            }
         });

         valueLabel = editor.getToolkit().createLabel(this, "Not Set");
         valueLabel.setLayoutData(new GridData());
         refresh();
         editor.registerEvent(this, AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);

      } catch (OseeCoreException ex) {
         Label errorLabel = editor.getToolkit().createLabel(this, "Error: " + ex.getLocalizedMessage());
         errorLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

   }

   public static boolean chooseVersion(final IAtsTeamWorkflow teamWf) {
      try {
         WorkflowEditor editor = WorkflowEditor.getWorkflowEditor(teamWf);

         if (editor.isDirty()) {
            editor.doSave(null);
         }
         if (TargetedVersionColumnUI.promptChangeVersion((TeamWorkFlowArtifact) teamWf,
            AtsClientService.get().getUserService().isAtsAdmin() ? VersionReleaseType.Both : VersionReleaseType.UnReleased,
            VersionLockedType.UnLocked)) {

            return true;
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   @Override
   public void refresh() {
      if (Widgets.isAccessible(valueLabel)) {
         String value = "Not Set";
         if (AtsClientService.get().getVersionService().hasTargetedVersion(teamWf)) {
            value = AtsClientService.get().getVersionService().getTargetedVersion(teamWf).getName();
         }
         valueLabel.setText(value);
         valueLabel.getParent().getParent().layout();
      }
   }

   @Override
   public void setBackground(Color color) {
      super.setBackground(color);
      if (Widgets.isAccessible(valueLabel)) {
         valueLabel.setBackground(color);
      }
      if (Widgets.isAccessible(origLabel)) {
         origLabel.setBackground(color);
      }
      if (Widgets.isAccessible(link)) {
         link.setBackground(color);
      }
   }

   @Override
   public IAtsWorkItem getWorkItem() {
      return teamWf;
   }

}
