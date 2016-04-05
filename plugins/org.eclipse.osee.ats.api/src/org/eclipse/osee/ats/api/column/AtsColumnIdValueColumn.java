/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.column;

import org.eclipse.osee.ats.api.util.ColumnType;

/**
 * @author Donald G. Dunne
 */
public class AtsColumnIdValueColumn extends AtsValueColumn {

   private IAtsColumnId columnId;

   public AtsColumnIdValueColumn() {
      // For JaxRs Instantitaion
   }

   public AtsColumnIdValueColumn(IAtsColumnId columnId, String name, int width, String align, boolean show, ColumnType sortDataType, boolean multiColumnEditable, String description, boolean actionRollup) {
      super(columnId.getId(), name, width, align, show, sortDataType, multiColumnEditable, description, actionRollup);
      this.columnId = columnId;
   }

   public AtsColumnIdValueColumn(IAtsColumnId columnId, String name, int width, String align, boolean show, ColumnType sortDataType, boolean multiColumnEditable, String description) {
      super(columnId.getId(), name, width, align, show, sortDataType, multiColumnEditable, description);
      this.columnId = columnId;
   }

   public IAtsColumnId getColumnId() {
      return columnId;
   }

}
