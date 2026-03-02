/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.attr;

import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.ui.skynet.widgets.XLabelValueWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XArtEdAttrViewerWidget extends XLabelValueWidget implements IOseeTreeReportProvider {

   public static WidgetId ID = WidgetId.XArtEdAttrViewerWidget;

   public final static String normalColor = "#EEEEEE";

   public XArtEdAttrViewerWidget() {
      super(ID, "Attributes", "");
   }

   @Override
   public String getEditorTitle() {
      try {
         return String.format("Attributes for %s", getArtifact().toStringWithId());
      } catch (Exception ex) {
         // do nothing
      }
      return "Table Report - Defects";
   }

   @Override
   public String getReportTitle() {
      return getEditorTitle();
   }

}
