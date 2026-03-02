/*********************************************************************
 * Copyright (c) 2010 Boeing
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

import java.util.Collection;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.osgi.service.component.annotations.Component;

/**
 * Multi selection of attribute types with checkbox dialog and filtering
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XAttributeTypeSelectionWidget extends XAbstractSelectFromDialogWidget<AttributeTypeToken> {

   public static WidgetId ID = WidgetId.XAttributeTypeSelectionWidget;;

   public XAttributeTypeSelectionWidget() {
      super(ID, "Select Attribute Type(s)");
   }

   @Override
   public Collection<AttributeTypeToken> getSelectableItems() {
      return Collections.castAll(ServiceUtil.getTokenService().getAttributeTypes());
   }

}