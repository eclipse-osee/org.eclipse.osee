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

package org.eclipse.osee.ats.ide.util.widgets.xx;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.SWT;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
// CodeReview.IgnoreXxWid
@Component(service = XWidget.class, immediate = true)
public class XXBlockWfeWidget extends XXAbstractValueReasonWidget {

   public static final WidgetId ID = WidgetIdAts.XXBlockWfeWidget;
   private static String TOOLTIP = "Block = Unable to work this due to dependency";
   private static String POSITIVE = "Block";
   private static String NEGATIVE = "UnBlock";

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXBlockWfeWidget() {
      super(ID, "", SWT.COLOR_DARK_RED, CoreImage.X_RED, POSITIVE, NEGATIVE);
      setAttributeType(AtsAttributeTypes.BlockedReason);
      setToolTip(TOOLTIP);
   }

}
