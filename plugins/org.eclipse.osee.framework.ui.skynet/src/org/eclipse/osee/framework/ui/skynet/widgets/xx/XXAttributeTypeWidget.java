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

import java.util.Collection;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXAttributeTypeWidget extends XXAbstractBaseXWidget<AttributeTypeToken> {

   public static final WidgetId ID = WidgetId.XXAttributeTypeWidget;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXAttributeTypeWidget() {
      this(ID, "Attribute Type");
   }

   public XXAttributeTypeWidget(WidgetId widgetId, String label) {
      super(widgetId, label);
      set(XOption.VAL_SINGLE_SEL);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public Collection<AttributeTypeToken> getSelectable() {
      return Collections.castAll(OseeApiService.tokenSvc().getAttributeTypes());
   }

   @Override
   public AttributeTypeToken getSentinel() {
      return AttributeTypeToken.SENTINEL;
   }

   @Override
   public boolean isOfType(Object obj) {
      return obj instanceof AttributeTypeToken;
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

}
