/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.framework.core.model.change;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.framework.core.enums.OseeEnum;

/**
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
public class ChangeType extends OseeEnum {

   private static final Long ENUM_ID = 2834799904L;

   public static ChangeType Artifact = new ChangeType(111L, "ArtifactChange");
   public static ChangeType Attribute = new ChangeType(222L, "AttributeChange");
   public static ChangeType Relation = new ChangeType(333L, "RelationChange");
   public static ChangeType Tuple = new ChangeType(444L, "TupleChange");
   public static ChangeType Unknown = new ChangeType(555L, "UnknownChange");

   public ChangeType() {
      super(ENUM_ID, -1L, "");
   }

   public ChangeType(String name) {
      super(ENUM_ID, name);
   }

   public ChangeType(long id, String name) {
      super(ENUM_ID, id, name);
   }

   @JsonIgnore
   public boolean isArtifactChange() {
      return this.equals(Artifact);
   }

   @JsonIgnore
   public boolean isAttributeChange() {
      return this.equals(Attribute);
   }

   @JsonIgnore
   public boolean isRelationChange() {
      return this.equals(Relation);
   }

   @JsonIgnore
   public boolean isTupleChange() {
      return this.equals(Tuple);
   }

   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }

   @JsonIgnore
   @Override
   public OseeEnum getDefault() {
      return Unknown;
   }

   public boolean isNotRelationChange() {
      return !isRelationChange();
   }

   public boolean isNotAttributeChange() {
      return !isAttributeChange();
   }
}
