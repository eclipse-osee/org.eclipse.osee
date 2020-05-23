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

import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.enums.token.VerificationEventAttributeType.VerificationEventEnum;

/**
 * @author Stephen J. Molaro
 */
public class VerificationEventAttributeType extends AttributeTypeEnum<VerificationEventEnum> {

   public final VerificationEventEnum FlightTest = new VerificationEventEnum(0, "Flight Test");
   public final VerificationEventEnum GroundTest = new VerificationEventEnum(1, "Ground Test");
   public final VerificationEventEnum LabTest = new VerificationEventEnum(2, "Lab Test");
   public final VerificationEventEnum SimulationTest = new VerificationEventEnum(3, "Simulation Test");
   public final VerificationEventEnum SubsystemTest = new VerificationEventEnum(4, "Subsystem Test");
   public final VerificationEventEnum ComponentTest = new VerificationEventEnum(5, "Component Test");
   public final VerificationEventEnum Unspecified = new VerificationEventEnum(6, "Unspecified");

   public VerificationEventAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1152921504606847124L, namespace, "Verification Event", mediaType, "", taggerType, 7);
   }

   public class VerificationEventEnum extends EnumToken {
      public VerificationEventEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}