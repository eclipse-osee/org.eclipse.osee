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

package org.eclipse.osee.ats.ide.search.widget;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.query.AtsSearchUserType;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXAbstractBaseXWidget;
import org.osgi.service.component.annotations.Component;

/**
 * Currently only a selection widget
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXUserTypeWidget extends XXAbstractBaseXWidget<AtsSearchUserType> {

   public static final WidgetId ID = WidgetIdAts.XXUserTypeWidget;
   public static List<AtsSearchUserType> DefaultUserTypes = Arrays.asList(AtsSearchUserType.Assignee,
      AtsSearchUserType.Originated, AtsSearchUserType.Favorites, AtsSearchUserType.Subscribed);

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXUserTypeWidget() {
      super(ID, "User Type");
      setAttributeType(AtsAttributeTypes.ChangeType);
      setSelectable(DefaultUserTypes);
      set(XOption.VAL_SINGLE_SEL);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public AtsSearchUserType getSentinel() {
      return null;
   }

   @Override
   public boolean isOfType(Object obj) {
      return obj instanceof AtsSearchUserType;
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

}
