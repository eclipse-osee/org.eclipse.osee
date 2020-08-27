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
import org.eclipse.osee.framework.core.enums.OSS_ProfileAttributeType.FaceProfileEnum;

/**
 * @author David W. Miller
 */
public final class OSS_ProfileAttributeType extends AttributeTypeEnum<FaceProfileEnum> {

   public final FaceProfileEnum Uspecified = new FaceProfileEnum(0, "Unspecified");
   public final FaceProfileEnum Security = new FaceProfileEnum(1, "Security");
   public final FaceProfileEnum Safety_Base = new FaceProfileEnum(2, "Safety - Base");
   public final FaceProfileEnum Safety_Extended = new FaceProfileEnum(3, "Safety - Extended");
   public final FaceProfileEnum General_Purpose = new FaceProfileEnum(4, "General_Purpose");

   public OSS_ProfileAttributeType(NamespaceToken namespace, int enumCount) {
      super(2499150216801464545L, namespace, "OSS Profile", MediaType.TEXT_PLAIN,
         "Enumeration to describe The OSS Profile for the FACE UoC", TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public OSS_ProfileAttributeType() {
      this(CoreTypeTokenProvider.FACE, 5);
   }

   public class FaceProfileEnum extends EnumToken {
      public FaceProfileEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}