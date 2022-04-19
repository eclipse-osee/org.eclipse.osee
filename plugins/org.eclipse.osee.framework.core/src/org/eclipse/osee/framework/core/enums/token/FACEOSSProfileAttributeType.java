/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import org.eclipse.osee.framework.core.enums.token.FACEOSSProfileAttributeType.FOSSProfileEnum;

/**
 * @author David W. Miller
 */
public class FACEOSSProfileAttributeType extends AttributeTypeEnum<FOSSProfileEnum> {

   public final FOSSProfileEnum Unspecified = new FOSSProfileEnum(0, "Unspecified");
   public final FOSSProfileEnum Security = new FOSSProfileEnum(1, "Security");
   public final FOSSProfileEnum BaseSafety = new FOSSProfileEnum(2, "Safety - Base");
   public final FOSSProfileEnum ExtendedSafety = new FOSSProfileEnum(3, "Safety - Extended");
   public final FOSSProfileEnum GeneralPurpose = new FOSSProfileEnum(4, "General Purpose");

   public FACEOSSProfileAttributeType(NamespaceToken namespace, int enumCount) {
      super(4322813713633818578L, namespace, "FACE OSS Profile", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public FACEOSSProfileAttributeType() {
      this(NamespaceToken.OSEE, 5);
   }

   public class FOSSProfileEnum extends EnumToken {
      public FOSSProfileEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}