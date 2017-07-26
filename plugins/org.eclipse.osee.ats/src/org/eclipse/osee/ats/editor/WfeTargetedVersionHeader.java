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
package org.eclipse.osee.ats.editor;

import java.util.logging.Level;
import org.eclipse.osee.ats.api.version.VersionLockedType;
import org.eclipse.osee.ats.api.version.VersionReleaseType;
import org.eclipse.osee.ats.column.TargetedVersionColumnUI;
import org.eclipse.osee.ats.core.client.util.AtsUtilClient;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
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
public class WfeTargetedVersionHeader extends Composite {

   private final static String TARGET_VERSION = "Target Version:";
   Label valueLabel;
   Label origLabel;
   Hyperlink link;

   public WfeTargetedVersionHeader(Composite parent, int style, final AbstractWorkflowArtifact sma, final WorkflowEditor editor) {
      super(parent, style);
      setLayoutData(new GridData());
      setLayout(ALayout.getZeroMarginLayout(2, false));
      editor.getToolkit().adapt(this);

      try {
         if (editor.isPrivilegedEditModeEnabled() || !sma.isCancelled() && !sma.isCompleted()) {
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
                  try {
                     if (editor.isDirty()) {
                        editor.doSave(null);
                     }
                     if (TargetedVersionColumnUI.promptChangeVersion(sma,
                        AtsClientService.get().getUserService().isAtsAdmin() ? VersionReleaseType.Both : VersionReleaseType.UnReleased,
                        VersionLockedType.UnLocked)) {
                        updateLabel(sma);
                        editor.onDirtied();
                     }
                  } catch (Exception ex) {
                     OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
                  }
               }
            });
         } else {
            origLabel = editor.getToolkit().createLabel(this, TARGET_VERSION);
            origLabel.setLayoutData(new GridData());
         }

         valueLabel = editor.getToolkit().createLabel(this, "Not Set");
         valueLabel.setLayoutData(new GridData());
         updateLabel(sma);

      } catch (OseeCoreException ex) {
         Label errorLabel = editor.getToolkit().createLabel(this, "Error: " + ex.getLocalizedMessage());
         errorLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

   }

   private void updateLabel(AbstractWorkflowArtifact sma) throws OseeCoreException {
      if (Widgets.isAccessible(valueLabel)) {
         String value = "Not Set";
         if (AtsClientService.get().getVersionService().hasTargetedVersion(sma)) {
            value = AtsClientService.get().getVersionService().getTargetedVersion(sma).getName();
         }
         valueLabel.setText(value);
         valueLabel.getParent().layout();
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

}
