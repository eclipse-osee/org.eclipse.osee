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
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Ryan D. Brooks
 */
public class XWidgetPopulateExample extends AbstractBlam {

   @Override
   public String getName() {
      return "XWidget Populate Example";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      //      AWorkbench.popup("Execute", "Blam is an example only.  Nothing done.");
      monitor.done();
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XCombo(1,2,3)\" displayName=\"Select an Option\" /></xWidgets>";
   }

   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable)  {
      super.widgetCreating(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (xWidget.getLabel().equals("Select an Option")) {
         XCombo combo = (XCombo) xWidget;
         combo.setDataStrings(new String[] {"A", "B", "C"});
      }
   }

   @Override
   public String getDescriptionUsage() {
      return "This blam is an example to show how an XWidget can populate it's values ( or perform other"
      //
      + " operations on the XWidget ) during it's creation.  This is used when the options or default" +
      //
      " selected value may come from another dynamic source such as a database query.";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Util");
   }
}