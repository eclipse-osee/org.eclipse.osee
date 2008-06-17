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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class XMultiXWidgetTextDam extends XMultiXWidgetDam {

   /**
    * @param label
    */
   public XMultiXWidgetTextDam(String label) {
      super(label);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XMultiXWidgetDam#addXWidgetDam()
    */
   @Override
   public XWidget addXWidgetDam() {
      return addXWidget("", null);
   }

   public XWidget addXWidget(String label, String defaultValue) {
      XText xTextWidget = new XText(label);
      xTextWidget.addXTextSpellModifyDictionary(new SkynetSpellModifyDictionary());
      if (defaultValue != null && !defaultValue.equals("")) xTextWidget.setText(defaultValue);
      xTextWidget.setFillHorizontally(true);
      if (!xWidgets.contains(xTextWidget)) xWidgets.add(xTextWidget);
      xTextWidget.addXModifiedListener(xModifiedListener);
      return xTextWidget;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XMultiXWidgetDam#isDirty()
    */
   @Override
   public Result isDirty() throws OseeCoreException, SQLException {
      List<String> enteredValues = getEnteredValues();
      List<String> storedValues = artifact.getAttributesToStringList(attributeTypeName);
      if (!Collections.isEqual(enteredValues, storedValues)) {
         return new Result(true, attributeTypeName + " is dirty");
      }
      return Result.FalseResult;
   }

   public List<String> getEnteredValues() {
      List<String> values = new ArrayList<String>();
      for (XWidget xWidget : xWidgets) {
         values.add(((XText) xWidget).get());
      }
      return values;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XMultiXWidgetDam#saveToArtifact()
    */
   @Override
   public void saveToArtifact() throws OseeCoreException, SQLException {
      artifact.setAttributeValues(attributeTypeName, getEnteredValues());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XMultiXWidgetDam#createXWidgets()
    */
   @Override
   public void createXWidgets() throws Exception {
      xWidgets.clear();
      for (String value : artifact.getAttributesToStringList(attributeTypeName)) {
         addXWidget("", value);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XMultiXWidgetDam#handleUndo()
    */
   @Override
   public void handleUndo() throws Exception {
      Composite parent = group.getParent();
      group.dispose();
      System.err.println("dispose all widgets");
      createXWidgets();
      createWidgets(toolkit, parent, horizontalSpan);
   }

}
