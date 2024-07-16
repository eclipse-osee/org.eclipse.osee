/*********************************************************************
 * Copyright (c) 2010 Boeing
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

import java.util.Arrays;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.ide.column.OriginatorColumnUI;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkArtifactImageProvider;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class WfeOriginatorHeader extends Composite {

   private final static String ORIGINATOR = "Originator:";
   private Label userIconLabel;
   private Label origLabel;
   private final IAtsWorkItem workItem;
   private Hyperlink origLink;
   private final WorkflowEditor editor;

   public WfeOriginatorHeader(Composite parent, int style, final IAtsWorkItem workItem, final WorkflowEditor editor) {
      super(parent, style);
      this.workItem = workItem;
      this.editor = editor;
      setLayoutData(new GridData());
      setLayout(ALayout.getZeroMarginLayout(3, false));

      try {
         origLink = editor.getToolkit().createHyperlink(this, ORIGINATOR, SWT.NONE);
         origLink.addHyperlinkListener(new IHyperlinkListener() {

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
                  OriginatorColumnUI.promptChangeOriginator(workItem);
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });
         userIconLabel = editor.getToolkit().createLabel(this, "");
         origLabel = editor.getToolkit().createLabel(this, "");
         origLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         refresh();

      } catch (OseeCoreException ex) {
         origLink.setText("Error: " + ex.getLocalizedMessage());
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

   }

   public void refresh() {
      try {
         if (workItem.getCreatedBy() == null) {
            origLabel.setText("Error: No originator identified.");
            origLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
         } else {
            User origUser = UserManager.getUserByArtId(workItem.getCreatedBy());
            userIconLabel.setImage(FrameworkArtifactImageProvider.getUserImage(Arrays.asList(origUser)));
            origLabel.setText(workItem.getCreatedBy().getName());
            origLabel.setForeground(Displays.getSystemColor(SWT.COLOR_BLACK));
         }
         origLabel.getParent().layout(true);
         origLabel.getParent().getParent().layout(true);
      } catch (OseeCoreException ex) {
         Label errorLabel = editor.getToolkit().createLabel(this, "Error: " + ex.getLocalizedMessage());
         errorLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

   }
}
