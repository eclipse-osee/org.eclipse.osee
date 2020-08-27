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

package org.eclipse.osee.ats.api.demo.enums.token;

import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.data.AtsTypeTokenProvider;
import org.eclipse.osee.ats.api.demo.enums.token.CodeReqDocAttributeType.CodeReqDocEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;

/**
 * @author Stephen J. Molaro
 */
public class CodeReqDocAttributeType extends AttributeTypeEnum<CodeReqDocEnum> {

   public final CodeReqDocEnum Unknown = new CodeReqDocEnum(0, "Unknown");
   public final CodeReqDocEnum SRS = new CodeReqDocEnum(3, "SRS");
   public final CodeReqDocEnum Other = new CodeReqDocEnum(4, "Other");

   public CodeReqDocAttributeType(NamespaceToken namespace, int enumCount) {
      super(1740569308658341L, namespace, "demo.code.Req Doc", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public CodeReqDocAttributeType() {
      this(AtsTypeTokenProvider.ATSDEMO, 5);
   }

   public class CodeReqDocEnum extends EnumToken {
      public CodeReqDocEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}