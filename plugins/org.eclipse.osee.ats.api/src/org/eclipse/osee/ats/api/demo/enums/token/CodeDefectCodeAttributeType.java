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

   public final CodeDefectCodeEnum Cl01RequirementsImplementedIncorrectly =
      new CodeDefectCodeEnum(0, "CL01 - Requirements Implemented Incorrectly");
   public final CodeDefectCodeEnum Cl06TimingProblem = new CodeDefectCodeEnum(5, "CL06 - Timing Problem");
   public final CodeDefectCodeEnum Cl07InterfaceError = new CodeDefectCodeEnum(6, "CL07 - Interface Error");
   public final CodeDefectCodeEnum Nc01RequirementError = new CodeDefectCodeEnum(11, "NC01 - Requirement Error");
   public final CodeDefectCodeEnum Nc02TestScriptError = new CodeDefectCodeEnum(12, "NC02 - Test Script Error");
   public final CodeDefectCodeEnum Nc04HardwareError = new CodeDefectCodeEnum(14, "NC04 - Hardware Error");
   public final CodeDefectCodeEnum Te99OtherToolError = new CodeDefectCodeEnum(19, "TE99 - Other Tool Error");

   public CodeDefectCodeAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1152921504606847242L, namespace, "demo.code.Defect Code", mediaType, "", taggerType, 20);
   }

   public class CodeDefectCodeEnum extends EnumToken {
      public CodeDefectCodeEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}