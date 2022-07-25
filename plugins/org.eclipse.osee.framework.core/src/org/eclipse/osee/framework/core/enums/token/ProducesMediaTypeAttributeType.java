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
import org.eclipse.osee.framework.core.enums.token.ProducesMediaTypeAttributeType.ProducesMediaTypeEnum;

/**
 * @author Ryan Baldwin
 */

public class ProducesMediaTypeAttributeType extends AttributeTypeEnum<ProducesMediaTypeEnum> {

   public final ProducesMediaTypeEnum JSON = new ProducesMediaTypeEnum(0, "application/json");
   public final ProducesMediaTypeEnum XML = new ProducesMediaTypeEnum(1, "application/xml");
   public final ProducesMediaTypeEnum ZIP = new ProducesMediaTypeEnum(2, "application/zip");
   public final ProducesMediaTypeEnum OCTET_STREAM = new ProducesMediaTypeEnum(3, "application/octet-stream");

   public ProducesMediaTypeAttributeType(NamespaceToken namespace, int enumCount) {
      super(2428747355642260466L, namespace, "Produces Media Type", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public ProducesMediaTypeAttributeType() {
      this(NamespaceToken.OSEE, 4);
   }

   public class ProducesMediaTypeEnum extends EnumToken {
      public ProducesMediaTypeEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}