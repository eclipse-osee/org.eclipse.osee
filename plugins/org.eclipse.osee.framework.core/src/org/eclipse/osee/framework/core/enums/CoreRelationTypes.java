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
package org.eclipse.osee.framework.core.enums;

import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_A;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_B;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.NamedIdentity;

public class CoreRelationTypes extends NamedIdentity implements IRelationEnumeration, IRelationType {
   public static final CoreRelationTypes Allocation__Requirement = new CoreRelationTypes(SIDE_A,
      "AAMFE+RS8gRV2BXaCtQA", "Allocation");
   public static final CoreRelationTypes Allocation__Component = new CoreRelationTypes(SIDE_B, "AAMFE+RS8gRV2BXaCtQA",
      "Allocation");
   public static final CoreRelationTypes CodeRequirement_Requirement = new CoreRelationTypes(SIDE_B,
      "AAMFE+TNKxG5ytcGHqgA", "Code-Requirement");
   public static final CoreRelationTypes CodeRequirement_CodeUnit = new CoreRelationTypes(SIDE_A,
      "AAMFE+TNKxG5ytcGHqgA", "Code-Requirement");
   public static final CoreRelationTypes Default_Hierarchical__Parent = new CoreRelationTypes(SIDE_A,
      "AAMFE9skWhPMi6I1WdAA", "Default Hierarchical");
   public static final CoreRelationTypes Default_Hierarchical__Child = new CoreRelationTypes(SIDE_B,
      "AAMFE9skWhPMi6I1WdAA", "Default Hierarchical");
   public static final CoreRelationTypes Dependency__Dependency = new CoreRelationTypes(SIDE_B, "AAMFE9uek34jqArVRuAA",
      "Dependency");
   public static final CoreRelationTypes Dependency__Artifact = new CoreRelationTypes(SIDE_A, "AAMFE9uek34jqArVRuAA",
      "Dependency");
   public static final CoreRelationTypes Design__Design = new CoreRelationTypes(SIDE_B, "AAMFE+XjcSaqnXfOXVQA",
      "Design");
   public static final CoreRelationTypes Design__Requirement = new CoreRelationTypes(SIDE_A, "AAMFE+XjcSaqnXfOXVQA",
      "Design");
   public static final CoreRelationTypes Executes__Test_Plan_Element = new CoreRelationTypes(SIDE_A,
      "AWo0+Hkh+2qNMeAlUwQA", "Executes");
   public static final CoreRelationTypes Executes__Test_Procedure = new CoreRelationTypes(SIDE_B,
      "AWo0+Hkh+2qNMeAlUwQA", "Executes");
   public static final CoreRelationTypes Requirement_Trace__Higher_Level = new CoreRelationTypes(SIDE_A,
      "AAMFE+dRBVFN6fAPXdwA", "Requirement Trace");
   public static final CoreRelationTypes Requirement_Trace__Lower_Level = new CoreRelationTypes(SIDE_B,
      "AAMFE+dRBVFN6fAPXdwA", "Requirement Trace");
   public static final CoreRelationTypes Supercedes_Supercedes = new CoreRelationTypes(SIDE_A, "AAMFE9qJZC_qegaByjgA",
      "Supercedes");
   public static final CoreRelationTypes Supercedes_Superceded = new CoreRelationTypes(SIDE_B, "AAMFE9qJZC_qegaByjgA",
      "Supercedes");
   public static final CoreRelationTypes SupportingInfo_SupportedBy = new CoreRelationTypes(SIDE_A,
      "AAMFE9wWnVhFW0wtOPAA", "Supporting Info");
   public static final CoreRelationTypes SupportingInfo_SupportingInfo = new CoreRelationTypes(SIDE_B,
      "AAMFE9wWnVhFW0wtOPAA", "Supporting Info");
   public static final CoreRelationTypes TeamMember_Team = new CoreRelationTypes(SIDE_A, "AAMFE92A6gCO9WJ2ijQA",
      "TeamMember");
   public static final CoreRelationTypes TeamMember_Member = new CoreRelationTypes(SIDE_B, "AAMFE92A6gCO9WJ2ijQA",
      "TeamMember");
   public static final CoreRelationTypes Test_Unit_Result__Test_Unit = new CoreRelationTypes(SIDE_A,
      "AQm9ouX6JwBD29WrFoAA", "Results Data");
   public static final CoreRelationTypes Test_Unit_Result__Test_Result = new CoreRelationTypes(SIDE_B,
      "AQm9ouX6JwBD29WrFoAA", "Results Data");
   public static final CoreRelationTypes Universal_Grouping__Members = new CoreRelationTypes(SIDE_B,
      "AAMFE9yOp2hIrXwZ13wA", "Universal Grouping");
   public static final CoreRelationTypes Universal_Grouping__Group = new CoreRelationTypes(SIDE_A,
      "AAMFE9yOp2hIrXwZ13wA", "Universal Grouping");
   public static final CoreRelationTypes Users_Artifact =
      new CoreRelationTypes(SIDE_A, "AAMFE9nibXSCqcoXkMAA", "Users");
   public static final CoreRelationTypes Users_User = new CoreRelationTypes(SIDE_B, "AAMFE9nibXSCqcoXkMAA", "Users");
   public static final CoreRelationTypes Uses__Requirement = new CoreRelationTypes(SIDE_A, "AAMFE_GV43rvba32QqwA",
      "Uses");
   public static final CoreRelationTypes Uses__TestUnit = new CoreRelationTypes(SIDE_B, "AAMFE_GV43rvba32QqwA", "Uses");
   public static final CoreRelationTypes Validation__Requirement = new CoreRelationTypes(SIDE_A,
      "AAMFE+fMVRM8f9BUD0AA", "Validation");
   public static final CoreRelationTypes Validation__Validator = new CoreRelationTypes(SIDE_B, "AAMFE+fMVRM8f9BUD0AA",
      "Validation");
   public static final CoreRelationTypes Verification__Requirement = new CoreRelationTypes(SIDE_A,
      "AAMFE+ZdqgB+D5zP5+gA", "Verification");
   public static final CoreRelationTypes Verification__Verifier = new CoreRelationTypes(SIDE_B, "AAMFE+ZdqgB+D5zP5+gA",
      "Verification");
   public static final CoreRelationTypes Verification_Plan__Requirement = new CoreRelationTypes(SIDE_A,
      "ATmJsVc28wU57zFAi7AA", "Verification Plan");
   public static final CoreRelationTypes Verification_Plan__Test_Plan_Element = new CoreRelationTypes(SIDE_B,
      "ATmJsVc28wU57zFAi7AA", "Verification Plan");
   public static final CoreRelationTypes WorkItem__Parent = new CoreRelationTypes(SIDE_A, "AAMFE9jvEzcbzwAfjzwA",
      "Work Item");
   public static final CoreRelationTypes WorkItem__Child = new CoreRelationTypes(SIDE_B, "AAMFE9jvEzcbzwAfjzwA",
      "Work Item");

   private final RelationSide relationSide;

   private CoreRelationTypes(RelationSide side, String guid, String name) {
      super(guid, name);
      this.relationSide = side;
   }

   @Override
   public RelationSide getSide() {
      return relationSide;
   }
}