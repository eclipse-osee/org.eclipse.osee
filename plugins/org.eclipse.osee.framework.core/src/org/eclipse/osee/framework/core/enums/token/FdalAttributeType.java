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
import org.eclipse.osee.framework.core.enums.token.FdalAttributeType.FdalEnum;

/**
 * @author Stephen J. Molaro
 */
public class FdalAttributeType extends AttributeTypeEnum<FdalEnum> {

   public final FdalEnum A = new FdalEnum(0, "A");
   public final FdalEnum B = new FdalEnum(1, "B");
   public final FdalEnum C = new FdalEnum(2, "C");
   public final FdalEnum D = new FdalEnum(3, "D");
   public final FdalEnum E = new FdalEnum(4, "E");
   public final FdalEnum Unspecified = new FdalEnum(5, "Unspecified");

   public FdalAttributeType(NamespaceToken namespace, int enumCount) {
      super(8007959514939954596L, namespace, "FDAL", MediaType.TEXT_PLAIN, "Functional Development Assurance Level",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public FdalAttributeType() {
      this(NamespaceToken.OSEE, 6);
   }

   public class FdalEnum extends EnumToken {
      public FdalEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}