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

package org.eclipse.osee.ats.api.column;

import org.eclipse.osee.ats.api.util.ColumnType;

/**
 * @author Donald G. Dunne
 */
public class AtsColumnIdValueColumn extends AtsValueColumn {

   private IAtsColumnId columnId;

   public AtsColumnIdValueColumn() {
      // For JaxRs Instantiation
   }

   public AtsColumnIdValueColumn(IAtsColumnId columnId, String name, int width, String align, boolean show, ColumnType sortDataType, boolean multiColumnEditable, String description, Boolean actionRollup, Boolean inheritParent) {
      super(columnId.getId(), name, width, align, show, sortDataType, multiColumnEditable, description, actionRollup,
         inheritParent);
      this.columnId = columnId;
   }

   public IAtsColumnId getColumnId() {
      return columnId;
   }

}
