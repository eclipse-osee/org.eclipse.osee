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
import org.eclipse.osee.framework.core.enums.token.VerificationLevelAttributeType.VerificationLevelEnum;

/**
 * @author Stephen J. Molaro
 */
public class VerificationLevelAttributeType extends AttributeTypeEnum<VerificationLevelEnum> {

   public final VerificationLevelEnum System = new VerificationLevelEnum(0, "System");
   public final VerificationLevelEnum Subsystem = new VerificationLevelEnum(1, "Subsystem");
   public final VerificationLevelEnum Component = new VerificationLevelEnum(2, "Component");
   public final VerificationLevelEnum Unspecified = new VerificationLevelEnum(3, "Unspecified");
   public final VerificationLevelEnum NA = new VerificationLevelEnum(4, "N/A");

   public VerificationLevelAttributeType(NamespaceToken namespace, int enumCount) {
      super(1152921504606847115L, namespace, "Verification Level", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public VerificationLevelAttributeType() {
      this(NamespaceToken.OSEE, 5);
   }

   public class VerificationLevelEnum extends EnumToken {
      public VerificationLevelEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}