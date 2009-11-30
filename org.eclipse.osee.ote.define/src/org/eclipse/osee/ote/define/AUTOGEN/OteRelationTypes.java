/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.define.AUTOGEN;

import org.eclipse.osee.framework.core.enums.IRelationEnumeration;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;

public enum OteRelationTypes implements IRelationEnumeration {
   TEST_SCRIPT_TO_RUN_RELATION__TEST_SCRIPT(RelationSide.SIDE_A, "Test Case to Run Relation", "AAMFE+jMyBDK7CV479wA"),
   TEST_SCRIPT_TO_RUN_RELATION__TEST_RUN(RelationSide.SIDE_B, "Test Case to Run Relation", "AAMFE+jMyBDK7CV479wA");
   private final RelationSide relationSide;
   private final String guid;
   private String typeName;

   private OteRelationTypes(RelationSide relationSide, String typeName, String guid) {
      this.relationSide = relationSide;
      this.typeName = typeName;
      this.guid = guid;
   }

   /**
    * @return Returns the sideName.
    */
   public boolean isSideA() {
      return relationSide.isSideA();
   }

   /**
    * @return Returns the typeName.
    */
   public String getName() {
      return typeName;
   }

   public RelationType getRelationType() throws OseeCoreException {
      return RelationTypeManager.getType(typeName);
   }

   @Override
   public RelationSide getSide() {
      return relationSide;
   }

   @Override
   public String getGuid() {
      return guid;
   }
}