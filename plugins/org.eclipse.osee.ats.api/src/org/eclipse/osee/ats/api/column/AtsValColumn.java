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

import org.eclipse.osee.ats.api.config.ActionRollup;
import org.eclipse.osee.ats.api.config.ColumnAlign;
import org.eclipse.osee.ats.api.config.InheritParent;
import org.eclipse.osee.ats.api.config.MultiEdit;
import org.eclipse.osee.ats.api.config.Show;
import org.eclipse.osee.ats.api.util.ColumnType;

/**
 * @author Donald G. Dunne
 */
public class AtsValColumn extends AtsColumnToken {

   private String columnId;

   public AtsValColumn() {
      // For JaxRs Instantiation
   }

   public AtsValColumn(String columnId, String name, int width, ColumnType columnType, ColumnAlign align, Show show, //
      MultiEdit multi, String description) {
      this(columnId, name, width, columnType, align, show, multi, ActionRollup.No, InheritParent.No, description);
   }

   public AtsValColumn(String columnId, String name, int width, ColumnType columnType, ColumnAlign align, Show show, //
      MultiEdit multi, ActionRollup actionRollup, InheritParent inheritParent, String description) {
      this(columnId, name, width, align.name(), show.yes(), columnType, multi.yes(), description, actionRollup.yes(),
         inheritParent.yes());
   }

   /**
    * Use non-boolean constructors above
    */
   @Deprecated
   public AtsValColumn(String columnId, String name, int width, String align, boolean show, ColumnType sortDataType, boolean multiColumnEditable, String description, Boolean actionRollup, Boolean inheritParent) {
      super(columnId, name, width, align, show, sortDataType, multiColumnEditable, description, actionRollup,
         inheritParent);
      this.columnId = columnId;
   }

   public String getColumnId() {
      return columnId;
   }

}
