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
import java.util.List;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.DisplayHint;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.ui.plugin.util.NoOpViewerComparator;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.osgi.service.component.annotations.Component;

/**
 * Widget to handle a list of strings to choose from in a filtered dialog. Also handles the default Enumerated Attribute
 * where selectable items are provided by default enums. This should NOT need to be extended for different cases.
 * Options can be provided through WidgetBuilder or default XWidget methods.
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXStringsWidget extends XXAbstractBaseXWidget<String> {

   public static final WidgetId ID = WidgetId.XXStringsWidget;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXStringsWidget() {
      this(ID, "");
   }

   public XXStringsWidget(String label) {
      this(ID, label);
   }

   public XXStringsWidget(WidgetId widgetId, String label) {
      this(widgetId, label, AttributeTypeToken.SENTINEL);
   }

   public XXStringsWidget(WidgetId widgetId, AttributeTypeToken attrType) {
      this(widgetId, attrType.getUnqualifiedName(), AttributeTypeToken.SENTINEL);
   }

   public XXStringsWidget(WidgetId widgetId, String label, AttributeTypeToken attrType) {
      super(widgetId, label);
      setAttributeType(attrType);
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
   public Collection<String> getSelectable() {
      if (isSelectable()) {
         if (super.getSelectable().isEmpty() && getAttributeType().isEnumerated()) {
            List<String> enumStrValues = getAttributeType().toEnum().getEnumStrValues();
            return enumStrValues;
         }
         return super.getSelectable();
      }
      return super.getSelectable();
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
   protected ViewerComparator getComparator() {
      if (getAttributeType().isValid() && getAttributeType().hasDisplayHint(DisplayHint.InOrder)) {
         // Do not sort, use enum as supplied to dialogs
         return NoOpViewerComparator.instance;
      }
      return super.getComparator();
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   protected void initializeSelected() {
      if (xxArt.isArtifact() && getAttributeType().isValid()) {
         setSelected(getArtifact().getAttributesToStringList(getAttributeType()), false);
      }
      if (getSelectable().isEmpty() && !widData.getSelectable().isEmpty()) {
         setSelectable(widData.getSelectable());
      }
   }

}
