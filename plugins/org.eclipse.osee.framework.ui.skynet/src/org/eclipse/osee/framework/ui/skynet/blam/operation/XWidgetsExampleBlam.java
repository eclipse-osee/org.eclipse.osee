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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XRadioButtonTest;
import org.eclipse.osee.framework.ui.skynet.widgets.XSelectFromMultiChoiceBranch;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 * @author Karol M. Wilk
 */
public class XWidgetsExampleBlam extends AbstractBlam {

   private static final String description =
      "This BLAM provides an example of all available XWidgets for use by developers of BLAMs and other UIs";

   public XWidgetsExampleBlam() {
      super(null, description, BlamUiSource.FILE);
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      logf("Nothing to do here, this is only an example BLAM");
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Util");
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable)  {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (xWidget.getLabel().equals("XSelectFromMultiChoiceBranch")) {
         XSelectFromMultiChoiceBranch sel = (XSelectFromMultiChoiceBranch) xWidget;

         Button button = new Button(sel.getStyledText().getParent(), SWT.PUSH);
         button.setText("Click and double-click to see Event type");
         button.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               System.err.println(String.format("handleWidgetSelected " + e.detail + " - " + e.time));
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
               System.err.println(String.format("haneldWidgetDefaultSelected " + e.detail));
            }

         });

         Button button2 = new Button(sel.getStyledText().getParent(), SWT.PUSH);
         button2.setText("Click and double-click to see MouseEvent type");
         button2.addMouseListener(new MouseListener() {

            @Override
            public void mouseUp(MouseEvent e) {
               System.err.println("mouseUp " + e.count);
            }

            @Override
            public void mouseDown(MouseEvent e) {
               System.err.println("mouseDown " + e.count);
            }

            @Override
            public void mouseDoubleClick(MouseEvent e) {
               System.err.println("mouseDoubleClick " + e.count);
            }
         });

         new XRadioButtonTest(sel.getStyledText().getParent(), SWT.BORDER);

      }
   }

}