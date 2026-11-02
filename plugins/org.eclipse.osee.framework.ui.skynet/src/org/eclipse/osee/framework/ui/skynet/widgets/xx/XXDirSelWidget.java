/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.framework.ui.skynet.widgets.xx;

import java.io.File;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXDirSelWidget extends XXAbstractBaseXWidget<String> {

   public static final WidgetId ID = WidgetId.XXDirSelWidget;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXDirSelWidget() {
      this(ID, "Select Folder");
   }

   public XXDirSelWidget(WidgetId widgetId, String label) {
      super(widgetId, label);
      set(XOption.VAL_SINGLE_SEL, XOption.CLEAR);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public String getSentinel() {
      return "";
   }

   @Override
   protected boolean handleSelection() {
      DirectoryDialog dialog = new DirectoryDialog(Displays.getActiveShell(), SWT.OPEN);
      String current = getSelectedFirst();
      if (Strings.isValid(current)) {
         File dir = new File(current);
         if (dir.isDirectory()) {
            dialog.setFilterPath(current);
         }
      }
      String result = dialog.open();
      if (Strings.isValid(result)) {
         setSelected(result, false);
         handleSelectedPersist();
         return true;
      }
      return false;
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

}
