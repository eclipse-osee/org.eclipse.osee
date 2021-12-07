/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.ats.ide.util.widgets;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.XAbstractSignDateAndByButton;

/**
 * @author Donald G. Dunne
 */
public class XReviewedWidget extends XAbstractSignDateAndByButton {

   public XReviewedWidget() {
      super("Reviewed By", "Sign or clear changes", AtsAttributeTypes.ReviewedByDate, AtsAttributeTypes.ReviewedBy,
         FrameworkImage.RUN_EXC, true);
   }

}
