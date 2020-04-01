/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.enums;

import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.TechStandardVersionAttributeType.FaceVersionEnum;

/**
 * @author David W. Miller
 */
public final class TechStandardVersionAttributeType extends AttributeTypeEnum<FaceVersionEnum> {

   public final FaceVersionEnum Uspecified = new FaceVersionEnum(0, "Unspecified");
   public final FaceVersionEnum Face_2_1 = new FaceVersionEnum(1, "FACE 2.1");
   public final FaceVersionEnum Face_3_0 = new FaceVersionEnum(2, "FACE 3.0");
   public final FaceVersionEnum Face_3_1 = new FaceVersionEnum(3, "FACE 3.1");

   public TechStandardVersionAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(8567252650750079789L, namespace, "Tech Standard Version", mediaType,
         "Enumeration to describe The Tech Standard Version for the FACE UoC", taggerType, 4);
   }

   public class FaceVersionEnum extends EnumToken {
      public FaceVersionEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}