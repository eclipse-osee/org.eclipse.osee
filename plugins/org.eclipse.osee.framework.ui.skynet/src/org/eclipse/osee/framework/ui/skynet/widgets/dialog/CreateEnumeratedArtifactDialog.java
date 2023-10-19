/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

/**
 * @author Vaibhav Patel
 */
public class CreateEnumeratedArtifactDialog extends XWidgetsDialog {

   public CreateEnumeratedArtifactDialog(String dialogTitle, String dialogMessage) {
      super(dialogTitle, dialogMessage);
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<XWidgets>");
      builder.append("<XWidget xwidgetType=\"XText\" displayName=\"Name\" id=\"name\"/>");
      builder.append("<XWidget xwidgetType=\"XText\" displayName=\"Value(s)\" id=\"value\" fill=\"Vertically\" />");
      builder.append("</XWidgets>");
      return builder.toString();
   }

}
