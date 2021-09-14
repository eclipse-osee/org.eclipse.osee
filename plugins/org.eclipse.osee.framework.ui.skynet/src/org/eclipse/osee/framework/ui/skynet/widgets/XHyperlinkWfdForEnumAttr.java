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
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author Donald G. Dunne
 */
public class XHyperlinkWfdForEnumAttr extends XHyperlinkWithFilteredDialog<String> implements AttributeTypeWidget {

   private AttributeTypeToken attributeTypeToken = AttributeTypeToken.SENTINEL;

   public XHyperlinkWfdForEnumAttr() {
      super("unknown");
   }

   @Override
   public Collection<String> getSelectable() {
      return attributeTypeToken.toEnum().getEnumStrValues();
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeTypeToken;
   }

   @Override
   public void setAttributeType(AttributeTypeToken attributeTypeToken) {
      this.attributeTypeToken = attributeTypeToken;
      if (attributeTypeToken.isValid()) {
         this.label = this.attributeTypeToken.getUnqualifiedName();
      }
   }

}
