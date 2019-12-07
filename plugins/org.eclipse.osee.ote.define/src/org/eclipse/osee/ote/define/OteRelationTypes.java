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
package org.eclipse.osee.ote.define;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.TestCase;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.TestRun;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_A;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_B;
import static org.eclipse.osee.framework.core.enums.RelationSorter.UNORDERED;
import static org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity.ONE_TO_MANY;
import static org.eclipse.osee.ote.define.OteTypeTokenProvider.ote;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;

public interface OteRelationTypes {

   // @formatter:off
   RelationTypeToken TestCaseToRunRelation = ote.add(2305843009213694326L, "Test Case to Run Relation", ONE_TO_MANY, UNORDERED, TestCase, "Test Case", TestRun, "Test Run");
   RelationTypeSide TestCaseToRunRelation_TestCase = RelationTypeSide.create(TestCaseToRunRelation, SIDE_A);
   RelationTypeSide TestCaseToRunRelation_TestRun = RelationTypeSide.create(TestCaseToRunRelation, SIDE_B);
   // @formatter:on
}