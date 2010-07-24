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

import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.core.enums.IRelationEnumeration;
import org.eclipse.osee.framework.core.enums.RelationSide;

public class OteRelationTypes extends NamedIdentity implements IRelationEnumeration {
   public static final OteRelationTypes TEST_SCRIPT_TO_RUN_RELATION__TEST_SCRIPT = new OteRelationTypes(
      RelationSide.SIDE_A, "AAMFE+jMyBDK7CV479wA", "Test Case to Run Relation");
   public static final OteRelationTypes TEST_SCRIPT_TO_RUN_RELATION__TEST_RUN = new OteRelationTypes(
      RelationSide.SIDE_B, "AAMFE+jMyBDK7CV479wA", "Test Case to Run Relation");
   private final RelationSide relationSide;

   private OteRelationTypes(RelationSide relationSide, String guid, String name) {
      super(guid, name);
      this.relationSide = relationSide;
   }

   @Override
   public RelationSide getSide() {
      return relationSide;
   }
}