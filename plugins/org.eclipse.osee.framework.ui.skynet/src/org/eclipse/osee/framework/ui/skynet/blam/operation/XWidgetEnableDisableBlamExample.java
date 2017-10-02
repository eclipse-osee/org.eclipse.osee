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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class XWidgetEnableDisableBlamExample extends AbstractBlam {

   private XCombo combo;

   @Override
   public String getName() {
      return "XWidget Enable/Disable BLAM Example";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      monitor.done();
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets>" + //
      "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Check Me\" />" + //
      "<XWidget xwidgetType=\"XCombo(1,2,3)\" displayName=\"Enable/Disabled Combo\" />" + //
      "</xWidgets>";

   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable)  {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (xWidget.getLabel().equals("Enable/Disabled Combo")) {
         combo = (XCombo) xWidget;
      } else if (xWidget.getLabel().equals("Check Me")) {
         final XCheckBox checkBox = (XCheckBox) xWidget;
         checkBox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               super.widgetSelected(e);
               combo.setEditable(!checkBox.isChecked());
            }

         });
      }
   }

   @Override
   public String getDescriptionUsage() {
      return "This blam is an example to show how an XWidget selection can enable/disable another widget.";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Util");
   }
}