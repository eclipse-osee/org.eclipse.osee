/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.blam.sections;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Roberto E. Escobar
 */
public class BlamOutputSection extends BaseBlamSection {

   private Text formText;
   private final OperationLogger OperationLogger;
   private final Action executBlamAction;

   public BlamOutputSection(FormEditor editor, AbstractBlam abstractBlam, Composite parent, FormToolkit toolkit, int style, Action executBlamAction) {
      super(editor, abstractBlam, parent, toolkit, style);
      this.executBlamAction = executBlamAction;
      this.OperationLogger = new InternalLogger();
   }

   public void simluateRun() {
      this.executBlamAction.run();
   }

   @Override
   public void initialize(IManagedForm form) {
      super.initialize(form);
      Section section = getSection();
      section.setText("Execute");
      section.setLayout(new GridLayout());
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      final FormToolkit toolkit = getManagedForm().getToolkit();
      Composite composite = toolkit.createComposite(getSection(), toolkit.getBorderStyle() | SWT.WRAP);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      if (executBlamAction != null) {
         ActionContributionItem contributionItem = new ActionContributionItem(executBlamAction);
         contributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
         contributionItem.fill(composite);
      }

      formText = toolkit.createText(composite, abstractBlam.getOutputMessage(), SWT.WRAP);
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.heightHint = 500;
      gd.widthHint = 200;
      formText.setLayoutData(gd);

      getSection().setClient(composite);
      toolkit.paintBordersFor(composite);

      section.layout(true);
      form.getForm().getBody().layout(true);

   }

   public void appendText(final String text) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (Widgets.isAccessible(formText)) {
               formText.append(text);
               getManagedForm().reflow(true);
            }
         }
      });
   }

   public void setText(final String text) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (Widgets.isAccessible(formText)) {
               formText.setText(text);
               getManagedForm().reflow(true);
            }
         }
      });
   }

   public OperationLogger getOutput() {
      return OperationLogger;
   }

   @Override
   public void dispose() {
      if (Widgets.isAccessible(formText)) {
         formText.dispose();
      }
      super.dispose();
   }

   @Override
   public void refresh() {
      super.refresh();
      int height = formText.getLineCount() * formText.getLineHeight();
      GridData formTextGd = new GridData(SWT.FILL, SWT.FILL, true, true);
      formTextGd.heightHint = height;
      formTextGd.widthHint = 200;
      formText.setLayoutData(formTextGd);
      getManagedForm().reflow(true);
   }

   private final class InternalLogger extends OperationLogger {
      @Override
      public void log(String... row) {
         appendText(Collections.toString(", ", (Object[]) row) + "\n");
      }

      @Override
      public void log(Throwable th) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, th);
      }
   }
}