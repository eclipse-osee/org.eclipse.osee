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

package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XRadioButtonTest;
import org.eclipse.osee.framework.ui.skynet.widgets.XSelectFromMultiChoiceBranch;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
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
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.UTILITY_EXAMPLES);
   }

   @Override
   public Image getImage() {
      return ImageManager.getImage(FrameworkImage.EXAMPLE);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.EXAMPLE);
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (xWidget.getLabel().equals("XSelectFromMultiChoiceBranch")) {
         XSelectFromMultiChoiceBranch sel = (XSelectFromMultiChoiceBranch) xWidget;

         Button button = new Button(sel.getStyledText().getParent(), SWT.PUSH);
         button.setText("Click and double-click to see Event type");
         button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               AWorkbench.popup(String.format("handleWidgetSelected " + e.detail + " - " + e.time));
            }

         });

         Button button2 = new Button(sel.getStyledText().getParent(), SWT.PUSH);
         button2.setText("Click and double-click to see MouseEvent type");
         button2.addMouseListener(new MouseListener() {

            @Override
            public void mouseUp(MouseEvent e) {
               AWorkbench.popup("mouseUp " + e.count);
            }

            @Override
            public void mouseDown(MouseEvent e) {
               AWorkbench.popup("mouseDown " + e.count);
            }

            @Override
            public void mouseDoubleClick(MouseEvent e) {
               AWorkbench.popup("mouseDoubleClick " + e.count);
            }
         });

         new XRadioButtonTest(sel.getStyledText().getParent(), SWT.BORDER);

      }
   }

}