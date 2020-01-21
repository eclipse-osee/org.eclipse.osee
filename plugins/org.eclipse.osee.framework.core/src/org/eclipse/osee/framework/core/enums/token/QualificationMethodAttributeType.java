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
package org.eclipse.osee.framework.core.enums.token;

import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.enums.token.QualificationMethodAttributeType.QualificationMethodEnum;

/**
 * @author Stephen J. Molaro
 */
public class QualificationMethodAttributeType extends AttributeTypeEnum<QualificationMethodEnum> {

   // @formatter:off
	public final QualificationMethodEnum Demonstration = new QualificationMethodEnum(0, "Demonstration");
	public final QualificationMethodEnum Test = new QualificationMethodEnum(1, "Test");
	public final QualificationMethodEnum Analysis = new QualificationMethodEnum(2, "Analysis");
	public final QualificationMethodEnum Inspection = new QualificationMethodEnum(3, "Inspection");
	public final QualificationMethodEnum Similarity = new QualificationMethodEnum(4, "Similarity");
	public final QualificationMethodEnum PassThru = new QualificationMethodEnum(5, "Pass Thru");
	public final QualificationMethodEnum SpecialQualification = new QualificationMethodEnum(6, "Special Qualification");
	public final QualificationMethodEnum Legacy = new QualificationMethodEnum(7, "Legacy");
	public final QualificationMethodEnum Unspecified = new QualificationMethodEnum(8, "Unspecified");
	// @formatter:on

   public QualificationMethodAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1152921504606847113L, namespace, "Qualification Method", mediaType,
         "Demonstration:  The operation of the CSCI, or a part of the CSCI, that relies on observable functional operation not requiring the use of instrumentation, special test equipment, or subsequent analysis.\n\nTest:  The operation of the CSCI, or a part of the CSCI, using instrumentation or other special test equipment to collect data for later analysis.\n\nAnalysis:  The processing of accumulated data obtained from other qualification methods.  Examples are reduction, interpretation, or extrapolation of test results.\n\nInspection:  The visual examination of CSCI code, documentation, etc.\n\nSpecial Qualification Methods:  Any special qualification methods for the CSCI, such as special tools, techniques, procedures, facilities, and acceptance limits.\n\nLegacy:  Requirement, design, or implementation has not changed since last qualification (use sparingly - Not to be used with functions implemented in internal software).\n\nUnspecified:  The qualification method has yet to be set.",
         taggerType);
   }

   public class QualificationMethodEnum extends EnumToken {
      public QualificationMethodEnum(int ordinal, String name) {
         super(ordinal, name);
      }
   }
}
