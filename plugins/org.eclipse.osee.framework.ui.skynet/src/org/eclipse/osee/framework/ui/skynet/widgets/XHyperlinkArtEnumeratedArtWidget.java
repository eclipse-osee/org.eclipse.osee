/*******************************************************************************
 * Copyright (c) 2023 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.osgi.service.component.annotations.Component;

/**
 * @author Vaibhav Patel
 */
@Component(service = XWidget.class, immediate = true)
public class XHyperlinkArtEnumeratedArtWidget extends XHyperlinkArtEnumeratedWidget {

   public static final WidgetId ID = WidgetId.XHyperlinkArtEnumeratedArtWidget;

   public XHyperlinkArtEnumeratedArtWidget() {
      this(ID, "");
   }

   public XHyperlinkArtEnumeratedArtWidget(WidgetId widgetId, String label) {
      super(widgetId, label);
   }

   @Override
   public boolean handleSelection() {
      try {
         if (super.handleSelection()) {
            if (getAttributeType().isInvalid()) {
               AWorkbench.popup("Attribute Type is Invalid");
               return false;
            }
            if (checked.isEmpty()) {
               getArtifact().deleteAttributes(getAttributeType());
            } else {
               getArtifact().setAttributeValues(getAttributeType(), checked);
            }
            getArtifact().persistInThread("Set Value(s)");
            return true;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return false;
   }

   @Override
   public String getCurrentValue() {
      String value = Widgets.NOT_SET;
      if (getAttributeType().isValid()) {
         List<String> values = getArtifact().getAttributesToStringList(getAttributeType());
         if (values.size() > 0) {
            value = org.eclipse.osee.framework.jdk.core.util.Collections.toString(", ", values);
         }
      }
      return value;
   }

   @Override
   public List<String> getCurrentSelected() {
      return getArtifact().getAttributesToStringList(getAttributeType());
   }

}
