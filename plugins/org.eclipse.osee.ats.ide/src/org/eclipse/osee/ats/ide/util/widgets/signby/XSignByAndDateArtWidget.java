/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.ats.ide.util.widgets.signby;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.osgi.service.component.annotations.Component;

/**
 * This class should not be overridden for Work Definitions but instead use SignbyWidgetDefinition.
 *
 * @author Doanld G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XSignByAndDateArtWidget extends XAbstractSignByAndDateButtonArtWidget {

   public static WidgetId ID = WidgetIdAts.XSignByAndDateArtWidget;

   public XSignByAndDateArtWidget() {
      super(ID, AtsAttributeTypes.SignedOffBy, AtsAttributeTypes.SignedOffByDate);
   }

}
