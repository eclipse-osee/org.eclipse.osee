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

import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.RelationSide;

public final class OteRelationTypes {

   // @formatter:off
   public static final RelationTypeSide TestCaseToRunRelation_TestCase = RelationTypeSide.create(RelationSide.SIDE_A, 2305843009213694326L, "Test Case to Run Relation");
   public static final RelationTypeSide TestCaseToRunRelation_TestRun = TestCaseToRunRelation_TestCase.getOpposite();
   // @formatter:off

}