/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.demo.enums.token;

import org.eclipse.osee.ats.api.demo.enums.token.CodeDefectCodeAttributeType.CodeDefectCodeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;

/**
 * @author Stephen J. Molaro
 */
public class CodeDefectCodeAttributeType extends AttributeTypeEnum<CodeDefectCodeEnum> {

   // @formatter:off
	public final CodeDefectCodeEnum Cl01RequirementsImplementedIncorrectly = new CodeDefectCodeEnum(0, "CL01 - Requirements Implemented Incorrectly");
	public final CodeDefectCodeEnum Cl02InitializationError = new CodeDefectCodeEnum(1, "CL02 - Initialization Error");
	public final CodeDefectCodeEnum Cl03OutOfRangeError = new CodeDefectCodeEnum(2, "CL03 - Out of Range Error");
	public final CodeDefectCodeEnum Cl04DataConversionError = new CodeDefectCodeEnum(3, "CL04 - Data Conversion Error");
	public final CodeDefectCodeEnum Cl05BufferSizingError = new CodeDefectCodeEnum(4, "CL05 - Buffer Sizing Error");
	public final CodeDefectCodeEnum Cl06TimingProblem = new CodeDefectCodeEnum(5, "CL06 - Timing Problem");
	public final CodeDefectCodeEnum Cl07InterfaceError = new CodeDefectCodeEnum(6, "CL07 - Interface Error");
	public final CodeDefectCodeEnum Cl08LogicControlError = new CodeDefectCodeEnum(7, "CL08 - Logic Control Error");
	public final CodeDefectCodeEnum Cl09Typo = new CodeDefectCodeEnum(8, "CL09 - TYPO");
	public final CodeDefectCodeEnum Cl10MultipleCodeLogicProblems = new CodeDefectCodeEnum(9, "CL10 - Multiple Code Logic Problems");
	public final CodeDefectCodeEnum Cl99OtherCodeLogicProblem = new CodeDefectCodeEnum(10, "CL99 - Other Code Logic Problem");
	public final CodeDefectCodeEnum Nc01RequirementError = new CodeDefectCodeEnum(11, "NC01 - Requirement Error");
	public final CodeDefectCodeEnum Nc02TestScriptError = new CodeDefectCodeEnum(12, "NC02 - Test Script Error");
	public final CodeDefectCodeEnum Nc03CteError = new CodeDefectCodeEnum(13, "NC03 - CTE Error");
	public final CodeDefectCodeEnum Nc04HardwareError = new CodeDefectCodeEnum(14, "NC04 - Hardware Error");
	public final CodeDefectCodeEnum Nc99OtherNonCodeProblem = new CodeDefectCodeEnum(15, "NC99 - Other NON-CODE Problem");
	public final CodeDefectCodeEnum Sv01CodingStandardViolation = new CodeDefectCodeEnum(16, "SV01 - Coding Standard Violation");
	public final CodeDefectCodeEnum Te01CompilerLinkerAssemblerProblem = new CodeDefectCodeEnum(17, "TE01 - Compiler/Linker/Assembler Problem");
	public final CodeDefectCodeEnum Te02ApexProblem = new CodeDefectCodeEnum(18, "TE02 - APEX Problem");
	public final CodeDefectCodeEnum Te99OtherToolError = new CodeDefectCodeEnum(19, "TE99 - Other Tool Error");
	// @formatter:on

   public CodeDefectCodeAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1152921504606847242L, namespace, "Code Defect Code", mediaType, "", taggerType);
   }

   public class CodeDefectCodeEnum extends EnumToken {
      public CodeDefectCodeEnum(int ordinal, String name) {
         super(ordinal, name);
      }
   }
}
