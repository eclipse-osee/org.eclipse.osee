/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.framework.core.enums;

import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.TechStandardVersionAttributeType.FaceVersionEnum;

/**
 * @author David W. Miller
 */
public final class TechStandardVersionAttributeType extends AttributeTypeEnum<FaceVersionEnum> {

   public final FaceVersionEnum Unspecified = new FaceVersionEnum(0, "Unspecified");
   public final FaceVersionEnum Face_2_1 = new FaceVersionEnum(1, "FACE 2.1");
   public final FaceVersionEnum Face_3_0 = new FaceVersionEnum(2, "FACE 3.0");
   public final FaceVersionEnum Face_3_1 = new FaceVersionEnum(3, "FACE 3.1");

   public TechStandardVersionAttributeType(NamespaceToken namespace, int enumCount) {
      super(8567252650750079789L, namespace, "Tech Standard Version", MediaType.TEXT_PLAIN,
         "Enumeration to describe The Tech Standard Version for the FACE UoC", TaggerTypeToken.PlainTextTagger,
         enumCount);
   }

   public TechStandardVersionAttributeType() {
      this(CoreTypeTokenProvider.FACE, 4);
   }

   public class FaceVersionEnum extends EnumToken {
      public FaceVersionEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}