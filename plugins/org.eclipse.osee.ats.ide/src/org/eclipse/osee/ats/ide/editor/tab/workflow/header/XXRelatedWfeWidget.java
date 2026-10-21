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
package org.eclipse.osee.ats.ide.editor.tab.workflow.header;

import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXAbstractBaseXWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.osgi.service.component.annotations.Component;

/**
 * Widget used to display items related to Workflow in header: eg Siblings, Tasks, Reviews
 *
 * @author Donald G. Dunne
 */
// CodeReview.IgnoreXxWid
@Component(service = XWidget.class, immediate = true)
public class XXRelatedWfeWidget extends XXAbstractBaseXWidget<String> {

   public static final WidgetId ID = WidgetIdAts.XXRelatedWfeWidget;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXRelatedWfeWidget() {
      this("", null);
      set(XOption.WRITE);
   }

   public XXRelatedWfeWidget(String label, XModifiedListener listener) {
      super(ID, label);
      if (listener != null) {
         widData.addXModListener(listener);
      }
      set(XOption.VAL_NOT_APPLICABLE);
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
      return true;
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

}
