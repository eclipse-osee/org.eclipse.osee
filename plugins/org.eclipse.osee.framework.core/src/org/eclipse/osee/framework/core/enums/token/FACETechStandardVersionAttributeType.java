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
import org.eclipse.osee.framework.core.enums.token.FACETechStandardVersionAttributeType.FTSVersionEnum;

/**
 * @author David W. Miller
 */
public class FACETechStandardVersionAttributeType extends AttributeTypeEnum<FTSVersionEnum> {

   public final FTSVersionEnum Unspecified = new FTSVersionEnum(0, "Unspecified");
   public final FTSVersionEnum Ver2_1 = new FTSVersionEnum(1, "FACE 2.1");
   public final FTSVersionEnum Ver3_0 = new FTSVersionEnum(2, "FACE 3.0");
   public final FTSVersionEnum Ver3_1 = new FTSVersionEnum(3, "FACE 3.1");

   public FACETechStandardVersionAttributeType(NamespaceToken namespace, int enumCount) {
      super(6532873813893818450L, namespace, "FACE Technical Standard Version", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public FACETechStandardVersionAttributeType() {
      this(NamespaceToken.OSEE, 4);
   }

   public class FTSVersionEnum extends EnumToken {
      public FTSVersionEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}