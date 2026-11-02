/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.ats.ide.workflow.transition;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Donald G. Dunne
 */
public class TransitionResultsUi {

   private TransitionResultsUi() {
      // Utility class
   }

   public static void report(String name, TransitionResults transResults) {
      String html = transResults.getResultString();
      html = AHTML.textToHtml(html);
      ResultsEditor.open("results", name, AHTML.simplePage(html));
   }

   public static void reportDialog(String name, TransitionResults transResults) {
      if (AtsUtil.isInTest()) {
         return;
      }
      String text = transResults.getResultString();
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            TransitionResultsDialog dialog = new TransitionResultsDialog(Displays.getActiveShell(), name, text);
            dialog.open();
         }
      });
   }

   private static class TransitionResultsDialog extends TitleAreaDialog {

      private final String title;
      private final String content;

      public TransitionResultsDialog(Shell parentShell, String title, String content) {
         super(parentShell);
         this.title = title;
         this.content = content;
         setShellStyle(getShellStyle() | SWT.RESIZE);
      }

      @Override
      protected void configureShell(Shell newShell) {
         super.configureShell(newShell);
         newShell.setText(title);
      }

      @Override
      protected Control createDialogArea(Composite parent) {
         setTitle(title);
         setMessage("Resolve the following issues to transition.");

         Composite area = (Composite) super.createDialogArea(parent);
         Composite container = new Composite(area, SWT.NONE);
         container.setLayoutData(new GridData(GridData.FILL_BOTH));
         container.setLayout(new GridLayout(1, false));

         StyledText styledText =
            new StyledText(container, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP);
         styledText.setEditable(false);
         styledText.setCaret(null);
         styledText.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
         GridData gd = new GridData(GridData.FILL_BOTH);
         gd.heightHint = 300;
         gd.widthHint = 500;
         styledText.setLayoutData(gd);
         styledText.setText(content);

         return area;
      }

      @Override
      protected void createButtonsForButtonBar(Composite parent) {
         createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
      }
   }

}
