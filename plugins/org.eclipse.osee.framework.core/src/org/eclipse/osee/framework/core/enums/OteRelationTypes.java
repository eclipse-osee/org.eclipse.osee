/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.enums;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.TestCase;
import static org.eclipse.osee.framework.core.enums.CoreTypeTokenProvider.ote;
import static org.eclipse.osee.framework.core.enums.OteArtifactTypes.TestRun;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_A;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_B;
import static org.eclipse.osee.framework.core.enums.RelationSorter.UNORDERED;
import static org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity.ONE_TO_MANY;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;

public interface OteRelationTypes {

   // @formatter:off
   RelationTypeToken TestCaseToRunRelation = ote.add(2305843009213694326L, "Test Case to Run Relation", ONE_TO_MANY, UNORDERED, TestCase, "Test Case", TestRun, "Test Run");
   RelationTypeSide TestCaseToRunRelation_TestCase = RelationTypeSide.create(TestCaseToRunRelation, SIDE_A);
   RelationTypeSide TestCaseToRunRelation_TestRun = RelationTypeSide.create(TestCaseToRunRelation, SIDE_B);
   // @formatter:on
}