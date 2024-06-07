/******************************************************************************
 *
 * Copyright (c) 2024 Boeing
 *
 * DISTRIBUTION STATEMENT D - Distribution authorized to DoD and U.S. DoD
 *   contractors only (Critical Technology), 11 June 2014. Other requests for
 *   this document will be referred to COMNAVAIRSYSCOM (PEO(T) PMA-265)
 *
 * WARNING - This document contains technical data whose export is restricted by
 *   the Arms Export Control Act (Title 22, U.S.C., Sec. 2751 et seq.) or
 *   Executive Order 12470. Violators of these export laws are subject to
 *   severe criminal penalties.
 *
 * EXPORT CONTROLLED - This technical data or software is subject to the U.S.
 *   International Traffic in Arms Regulations (ITAR), (22 C.F.R. Parts 120-130).
 *   Export, re-export, retransfer, or access is prohibited without
 *   authorization from the U.S. Department of State.
 *
 * DESTRUCTION NOTICE - For classified documents, follow the procedures in
 *   DOD 5220.22-M, Industrial Security Manual, Chapter 5, Section 7 Disposition
 *   and Retention, or DOD 5200.1-R, Information Security Program Regulation,
 *   Chapter 6, paragraph C6.7 Disposition and Destruction of Classified Material.
 *   For unclassified, limited documents destroy by any method that will prevent
 *   disclosure of contents or reconstruction of the document.
 *
 *****************************************************************************/

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

public interface ShadowOteRelationTypes {

   // @formatter:off
   RelationTypeToken TestCaseToRunRelationRel = ote.addNewRelationType(4400405161969600885L, "Test Case to Run Relation", ONE_TO_MANY, UNORDERED, TestCase, "Test Case", TestRun, "Test Run", OteRelationTypes.TestCaseToRunRelation);
   RelationTypeSide TestCaseToRunRelationRel_TestCase = RelationTypeSide.create(TestCaseToRunRelationRel, SIDE_A);
   RelationTypeSide TestCaseToRunRelationRel_TestRun = RelationTypeSide.create(TestCaseToRunRelationRel, SIDE_B);
   // @formatter:on
}