/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.framework.core.enums.token;

import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.enums.token.QualificationMethodAttributeType.QualificationMethodEnum;

/**
 * @author Stephen J. Molaro
 */
public class QualificationMethodAttributeType extends AttributeTypeEnum<QualificationMethodEnum> {

   public final QualificationMethodEnum Demonstration = new QualificationMethodEnum(0, "Demonstration");
   public final QualificationMethodEnum Test = new QualificationMethodEnum(1, "Test");
   public final QualificationMethodEnum Analysis = new QualificationMethodEnum(2, "Analysis");
   public final QualificationMethodEnum Inspection = new QualificationMethodEnum(3, "Inspection");
   public final QualificationMethodEnum Similarity = new QualificationMethodEnum(4, "Similarity");
   public final QualificationMethodEnum PassThru = new QualificationMethodEnum(5, "Pass Thru");
   public final QualificationMethodEnum SpecialQualification = new QualificationMethodEnum(6, "Special Qualification");
   public final QualificationMethodEnum Legacy = new QualificationMethodEnum(7, "Legacy");
   public final QualificationMethodEnum DesignConstraint = new QualificationMethodEnum(8, "Design Constraint");
   public final QualificationMethodEnum Info = new QualificationMethodEnum(9, "Info");
   public final QualificationMethodEnum Exception = new QualificationMethodEnum(10, "Exception");
   public final QualificationMethodEnum Unspecified = new QualificationMethodEnum(11, "Unspecified");

   public QualificationMethodAttributeType(NamespaceToken namespace, int enumCount) {
      super(1152921504606847113L, namespace, "Qualification Method", MediaType.TEXT_PLAIN,
         "Demonstration:  The operation of the CSCI, or a part of the CSCI, that relies on observable functional operation not requiring the use of instrumentation, special test equipment, or subsequent analysis.\n\nTest:  The operation of the CSCI, or a part of the CSCI, using instrumentation or other special test equipment to collect data for later analysis.\n\nAnalysis:  The processing of accumulated data obtained from other qualification methods.  Examples are reduction, interpretation, or extrapolation of test results.\n\nInspection:  The visual examination of CSCI code, documentation, etc.\n\nSpecial Qualification Methods:  Any special qualification methods for the CSCI, such as special tools, techniques, procedures, facilities, and acceptance limits.\n\nLegacy:  Requirement, design, or implementation has not changed since last qualification (use sparingly - Not to be used with functions implemented in internal software).\n\nUnspecified:  The qualification method has yet to be set.",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public QualificationMethodAttributeType() {
      this(NamespaceToken.OSEE, 12);
   }

   public class QualificationMethodEnum extends EnumToken {
      public QualificationMethodEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}