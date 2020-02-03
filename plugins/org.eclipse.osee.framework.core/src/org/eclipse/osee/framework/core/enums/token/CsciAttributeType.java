/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.enums.token;

import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.enums.token.CsciAttributeType.CsciEnum;

/**
 * @author Roberto E. Escobar
 */

public class CsciAttributeType extends AttributeTypeEnum<CsciEnum> {

   public final CsciEnum CoreUnit = new CsciEnum(0, "CoreUnit");
   public final CsciEnum Framework = new CsciEnum(1, "Framework");
   public final CsciEnum Interface = new CsciEnum(2, "Interface");
   public final CsciEnum Navigation = new CsciEnum(3, "Navigation");
   public final CsciEnum Unspecified = new CsciEnum(4, "Unspecified");
   public final CsciEnum Visual = new CsciEnum(5, "Visual");

   public CsciAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1152921504606847136L, namespace, "CSCI", mediaType, "", taggerType, 6);
   }

   public class CsciEnum extends EnumToken {
      public CsciEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}