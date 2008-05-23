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
package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class XMultiXWidgetXTextTest extends Composite {

   public XMultiXWidgetXTextTest(Composite parent, int style) {
      super(parent, style);
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(new GridLayout(1, true));
      composite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
      composite.setLayoutData(new GridData(GridData.FILL_BOTH));

      final XMultiXWidget multiWidget = new XMultiXWidget("Multiple Text Entries", new XMultiXWidgetFactory() {
         /* (non-Javadoc)
          * @see org.eclipse.osee.framework.ui.skynet.widgets.XMultiXWidgetFactory#addXWidget()
          */
         @Override
         public XWidget addXWidget() {
            return createXTextWidget("New", "");
         }
      });

      for (String str : new String[] {"first", "second", "third"}) {
         multiWidget.addXWidget(createXTextWidget("XText " + str, "Value " + str));
      }

      multiWidget.createWidgets(composite, 1);
      multiWidget.addXModifiedListener(new XModifiedListener() {
         /* (non-Javadoc)
          * @see org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener#widgetModified(org.eclipse.osee.framework.ui.skynet.widgets.XWidget)
          */
         @Override
         public void widgetModified(XWidget widget) {
            System.out.println("Widget Modified");
         }
      });
   }

   public XText createXTextWidget(String name, String value) {
      XText firstText = new XText(name);
      firstText.addXTextSpellModifyDictionary(new SkynetSpellModifyDictionary());
      firstText.setText(value);
      firstText.setFillHorizontally(true);
      return firstText;
   }

   public static void main(String[] args) {
      Display Display_1 = Display.getDefault();
      Shell Shell_1 = new Shell(Display_1, SWT.SHELL_TRIM);
      Shell_1.setBounds(0, 0, 300, 300);
      Shell_1.setLayout(new GridLayout());
      Shell_1.setLayoutData(new GridData(GridData.FILL_BOTH));

      new XMultiXWidgetXTextTest(Shell_1, SWT.NONE);

      Shell_1.open();
      while (!Shell_1.isDisposed()) {
         if (!Display_1.readAndDispatch()) {
            Display_1.sleep();
         }
      }

      Display_1.dispose();
   }
}
