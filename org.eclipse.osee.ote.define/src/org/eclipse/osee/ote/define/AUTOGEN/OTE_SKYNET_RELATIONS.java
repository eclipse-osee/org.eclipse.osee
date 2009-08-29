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

import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;

public enum OTE_SKYNET_RELATIONS implements IRelationEnumeration {
   TEST_SCRIPT_TO_RUN_RELATION__TEST_SCRIPT(RelationSide.SIDE_A, "Test Script to Run Relation"),
   TEST_SCRIPT_TO_RUN_RELATION__TEST_RUN(RelationSide.SIDE_B, "Test Script to Run Relation");
   private final RelationSide relationSide;

   private String typeName;

   private OTE_SKYNET_RELATIONS(RelationSide relationSide, String typeName) {
      this.relationSide = relationSide;
      this.typeName = typeName;
   }

   /**
    * @return Returns the sideName.
    */
   public boolean isSideA() {
      return relationSide.isSideA();
   }

   public String getSideName() throws OseeCoreException {
      return getRelationType().getSideName(relationSide);
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

   public boolean isThisType(RelationLink link) {
      return link.getRelationType().getName().equals(typeName);
   }

   @Override
   public RelationSide getSide() {
      return relationSide;
   }
}