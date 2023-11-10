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
import org.eclipse.osee.ats.api.config.AtsDisplayHint;
import org.eclipse.osee.ats.api.config.ColumnAlign;
import org.eclipse.osee.ats.api.config.InheritParent;
import org.eclipse.osee.ats.api.config.MultiEdit;
import org.eclipse.osee.ats.api.config.Show;
import org.eclipse.osee.ats.api.util.ColumnType;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author Donald G. Dunne
 */
public class AtsCoreCodeColumnToken extends AtsCoreColumnToken {

   private String columnId;
   // Column does not have to have associated attrType (eg: ChangeType would have associated, TotalPercentComplete would not)
   private AttributeTypeToken attrType = AttributeTypeToken.SENTINEL;

   public AtsCoreCodeColumnToken() {
      // For JaxRs Instantiation
   }

   public AtsCoreCodeColumnToken(String columnId, String name, int width, ColumnType columnType, ColumnAlign align, Show show, //
      MultiEdit multi, String description) {
      this(columnId, name, width, columnType, align, show, multi, ActionRollup.No, InheritParent.No, description);
   }

   public AtsCoreCodeColumnToken(String columnId, String name, int width, ColumnType columnType, ColumnAlign align, Show show, //
      MultiEdit multi, ActionRollup actionRollup, InheritParent inheritParent, String description) {
      this(columnId, name, width, align.name(), show.yes(), columnType, multi.yes(), description, actionRollup.yes(),
         inheritParent.yes());
   }

   /**
    * Use non-boolean constructors above
    */
   @Deprecated
   public AtsCoreCodeColumnToken(String columnId, String name, int width, String align, boolean show, ColumnType columnType, boolean multiColumnEditable, String description, Boolean actionRollup, Boolean inheritParent) {
      super(columnId, name, width, align, show, columnType, multiColumnEditable, description, actionRollup,
         inheritParent);
      this.columnId = columnId;
   }

   public AtsCoreCodeColumnToken(String columnId, AttributeTypeToken attrType) {
      this(columnId, attrType.getUnqualifiedName(), AtsColumnUtil.getColumnWidth(attrType),
         AtsColumnUtil.getColumnType(attrType), AtsColumnUtil.getColumnAlign(attrType), Show.No,
         (attrType.hasDisplayHint(AtsDisplayHint.Edit) ? MultiEdit.Yes : MultiEdit.No), ActionRollup.No,
         InheritParent.No, attrType.getDescription());
      this.attrType = attrType;
   }

   public AtsCoreCodeColumnToken(String columnId, String name, AttributeTypeToken attrType) {
      this(columnId, name, AtsColumnUtil.getColumnWidth(attrType), AtsColumnUtil.getColumnType(attrType),
         AtsColumnUtil.getColumnAlign(attrType), Show.No,
         (attrType.hasDisplayHint(AtsDisplayHint.Edit) ? MultiEdit.Yes : MultiEdit.No), ActionRollup.No,
         InheritParent.No, attrType.getDescription());
      this.attrType = attrType;
   }

   public AtsCoreCodeColumnToken(String columnId, String name, int width, AttributeTypeToken attrType) {
      this(columnId, name, width, AtsColumnUtil.getColumnType(attrType), AtsColumnUtil.getColumnAlign(attrType),
         Show.No, (attrType.hasDisplayHint(AtsDisplayHint.Edit) ? MultiEdit.Yes : MultiEdit.No), ActionRollup.No,
         InheritParent.No, attrType.getDescription());
      this.attrType = attrType;
   }

   public String getColumnId() {
      return columnId;
   }

   public AttributeTypeToken getAttrType() {
      return attrType;
   }

   public void setAttrType(AttributeTypeToken attrType) {
      this.attrType = attrType;
   }

}
