/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.Collection;
import java.util.List;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.DisplayHint;
import org.eclipse.osee.framework.ui.plugin.util.NoOpViewerComparator;

/**
 * @author Donald G. Dunne
 */
public class XHyperlinkWfdForEnumAttr extends XHyperlinkWithFilteredDialog<String> implements AttributeTypeWidget {

   private Collection<String> selectable = null;

   public XHyperlinkWfdForEnumAttr() {
      super("unknown");
   }

   @Override
   public Collection<String> getSelectable() {
      if (selectable != null) {
         return selectable;
      }
      List<String> enumStrValues = attributeType.toEnum().getEnumStrValues();
      return enumStrValues;
   }

   @Override
   protected ViewerComparator getComparator() {
      if (attributeType.hasDisplayHint(DisplayHint.InOrder)) {
         // Do not sort, use enum as supplied to dialogs
         return NoOpViewerComparator.instance;
      }
      return super.getComparator();
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   @Override
   public void setAttributeType(AttributeTypeToken attributeType) {
      this.attributeType = attributeType;
      if (attributeType.isValid()) {
         this.label = this.attributeType.getUnqualifiedName();
      }
   }

   /**
    * Override attributes enum values. If not set, enum type's values will be used.
    */
   public void setSelectable(Collection<String> selectable) {
      this.selectable = selectable;
   }

   @Override
   public void setRequiredEntry(boolean requiredEntry) {
      super.setRequiredEntry(requiredEntry);
      validate();
   }

}
