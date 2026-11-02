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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.ComputedCharacteristicToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.osgi.service.component.annotations.Component;

/**
 * @author Stephen J Molaro
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXComputedCharacteristicWidget extends XXAbstractBaseXWidget<String> {

   public static final WidgetId ID = WidgetId.XXComputedCharacteristicWidget;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXComputedCharacteristicWidget() {
      super(ID, "");
      set(XOption.READ);
      set(XOption.VAL_SINGLE_SEL);
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
      return false;
   }

   @Override
   public String getDisplayValue() {
      if (getArtifact() == null || getComputedCharacteristic() == null) {
         return "";
      }
      if (getAttributeValues().isEmpty()) {
         return "";
      }
      return getArtifact().getComputedCharacteristicValue(getComputedCharacteristic()).toString();
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

   //////////////////////////////////////////
   // Refresh / Lifecycle
   //////////////////////////////////////////

   @Override
   public void refresh() {
      if (!Widgets.isAccessible(comp)) {
         return;
      }
      super.refresh();
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   private ComputedCharacteristicToken<?> getComputedCharacteristic() {
      return widData.getComputedCharacteristic();
   }

   @SuppressWarnings("unchecked")
   private <T> List<T> getAttributeValues() {
      List<T> values = new ArrayList<>();
      ComputedCharacteristicToken<?> cc = getComputedCharacteristic();
      if (cc != null && getArtifact() != null) {
         for (AttributeTypeToken attrType : cc.getAttributeTypesToCompute()) {
            for (Attribute<?> attr : getArtifact().getAttributes(attrType)) {
               values.add((T) attr.getValue());
            }
         }
      }
      return values;
   }

}
