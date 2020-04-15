/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

/**
 * @author Donald G. Dunne
 */
public class XWidgetsExampleDialog extends XWidgetsDialog {

   public XWidgetsExampleDialog(String dialogTitle, String dialogMessage) {
      super(dialogTitle, dialogMessage);
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<XWidgets>");
      builder.append("<XWidget xwidgetType=\"XText\" displayName=\"Action Title\" id=\"title\"/>");
      builder.append(
         "<XWidget xwidgetType=\"XText\" displayName=\"Enter Detailed Description\" id=\"desc\" fill=\"Vertically\" />");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"Is This Valid\" id=\"valid\" />");
      builder.append(
         "<XWidget xwidgetType=\"XCombo(one,two,three)\" displayName=\"Select number to create\" id=\"number\" horizontalLabel=\"true\"/>");
      builder.append("</XWidgets>");
      return builder.toString();
   }

}
