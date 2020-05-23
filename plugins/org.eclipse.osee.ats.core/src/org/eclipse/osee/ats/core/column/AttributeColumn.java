/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.core.column;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Donald G. Dunne
 */
public class AttributeColumn extends AbstractServicesColumn {

   private final AttributeTypeToken attrType;

   public AttributeColumn(AtsApi atsApi, AttributeTypeToken attrType) {
      super(atsApi);
      this.attrType = attrType;
   }

   @Override
   public String getText(IAtsObject atsObject) {
      return Collections.toString("; ", atsApi.getAttributeResolver().getAttributesToStringList(atsObject, attrType));
   }

   @Override
   public String toString() {
      return "AttributeColumn [attrType=" + attrType + "]";
   }

}
