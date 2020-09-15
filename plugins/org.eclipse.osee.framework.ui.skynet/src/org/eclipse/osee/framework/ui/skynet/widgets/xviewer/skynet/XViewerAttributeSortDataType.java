/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet;

import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author Donald G. Dunne
 */
public class XViewerAttributeSortDataType {

   public static SortDataType get(AttributeTypeToken attributeType) {
      SortDataType sortType = SortDataType.String;
      try {
         if (attributeType.isDate()) {
            sortType = SortDataType.Date;
         } else if (attributeType.isDouble()) {
            sortType = SortDataType.Float;
         } else if (attributeType.isInteger()) {
            sortType = SortDataType.Integer;
         } else if (attributeType.isLong()) {
            sortType = SortDataType.Long;
         } else if (attributeType.isBoolean()) {
            sortType = SortDataType.Boolean;
         } else if (attributeType.isString()) {
            sortType = SortDataType.Paragraph_Number;
         }
      } catch (Exception ex) {
         //do nothing
      }
      return sortType;
   }
}
