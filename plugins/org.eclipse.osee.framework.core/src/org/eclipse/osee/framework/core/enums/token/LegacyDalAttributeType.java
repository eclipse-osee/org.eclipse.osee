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
import org.eclipse.osee.framework.core.enums.token.LegacyDalAttributeType.LegacyDalEnum;

/**
 * @author Stephen J. Molaro
 */
public class LegacyDalAttributeType extends AttributeTypeEnum<LegacyDalEnum> {

   public final LegacyDalEnum A = new LegacyDalEnum(0, "A");
   public final LegacyDalEnum B = new LegacyDalEnum(1, "B");
   public final LegacyDalEnum C = new LegacyDalEnum(2, "C");
   public final LegacyDalEnum D = new LegacyDalEnum(3, "D");
   public final LegacyDalEnum E = new LegacyDalEnum(4, "E");
   public final LegacyDalEnum Unspecified = new LegacyDalEnum(5, "Unspecified");

   public LegacyDalAttributeType(NamespaceToken namespace, int enumCount) {
      super(1152921504606847120L, namespace, "Legacy DAL", MediaType.TEXT_PLAIN,
         "Legacy Development Assurance Level (original DAL)", TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public LegacyDalAttributeType() {
      this(NamespaceToken.OSEE, 6);
   }

   public class LegacyDalEnum extends EnumToken {
      public LegacyDalEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}