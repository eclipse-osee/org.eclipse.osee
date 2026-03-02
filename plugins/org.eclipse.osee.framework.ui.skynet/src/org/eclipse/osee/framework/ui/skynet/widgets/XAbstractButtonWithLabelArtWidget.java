/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public abstract class XAbstractButtonWithLabelArtWidget extends XButtonWidget {

   protected Label resultsLabelWidget;

   public XAbstractButtonWithLabelArtWidget(WidgetId widgetId, String displayLabel, String toolTip, OseeImage oseeImage) {
      super(widgetId, displayLabel);
      setOseeImage(oseeImage);
      setToolTip(toolTip);
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      numColumns = 3;
      super.createControls(parent, horizontalSpan);
      resultsLabelWidget = new Label(comp, SWT.NONE);
      refresh();
   }

   @Override
   public void refresh() {
      if (Widgets.isAccessible(labelWidget)) {

         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               resultsLabelWidget.setText(getResultsText());
               resultsLabelWidget.getParent().layout(true);
               resultsLabelWidget.getParent().getParent().layout(true);
               validate();
            }
         });
      }

   }

   protected abstract String getResultsText();

}
