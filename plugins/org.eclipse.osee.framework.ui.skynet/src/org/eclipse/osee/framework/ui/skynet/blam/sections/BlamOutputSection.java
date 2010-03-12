/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.blam.sections;

import java.io.IOException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
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
   private Appendable appendableOutput;
   private final Action executBlamAction;

   public BlamOutputSection(FormEditor editor, AbstractBlam abstractBlam, Composite parent, FormToolkit toolkit, int style, Action executBlamAction) {
      super(editor, abstractBlam, parent, toolkit, style);
      this.executBlamAction = executBlamAction;
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

      formText = toolkit.createText(composite, "BLAM has not yet run\n", SWT.WRAP);
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
         public void run() {
            if (Widgets.isAccessible(formText)) {
               formText.setText(text);
               getManagedForm().reflow(true);
            }
         }
      });
   }

   public Appendable getOutput() {
      if (appendableOutput == null) {
         appendableOutput = new InternalAppendable();
      }
      return appendableOutput;
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
   }

   private final class InternalAppendable implements Appendable {

      private void write(final String text) {
         appendText(text);
      }

      @Override
      public Appendable append(CharSequence csq) throws IOException {
         if (csq == null) {
            write("null");
         } else {
            write(csq.toString());
         }
         return this;
      }

      @Override
      public Appendable append(char c) throws IOException {
         write(new String(new char[] {c}));
         return this;
      }

      @Override
      public Appendable append(CharSequence csq, int start, int end) throws IOException {
         CharSequence cs = csq == null ? "null" : csq;
         write(cs.subSequence(start, end).toString());
         return this;
      }

   }
}
