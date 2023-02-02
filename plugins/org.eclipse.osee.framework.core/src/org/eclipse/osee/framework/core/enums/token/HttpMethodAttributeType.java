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
import org.eclipse.osee.framework.core.enums.token.HttpMethodAttributeType.HttpMethodEnum;

/**
 * @author Ryan Baldwin
 */

public class HttpMethodAttributeType extends AttributeTypeEnum<HttpMethodEnum> {

   public final HttpMethodEnum Get = new HttpMethodEnum(0, "GET");
   public final HttpMethodEnum Post = new HttpMethodEnum(1, "POST");
   public final HttpMethodEnum Put = new HttpMethodEnum(2, "PUT");
   public final HttpMethodEnum Patch = new HttpMethodEnum(3, "PATCH");
   public final HttpMethodEnum Delete = new HttpMethodEnum(4, "DELETE");

   public HttpMethodAttributeType(NamespaceToken namespace, int enumCount) {
      super(2412383418964323219L, namespace, "Http Method", MediaType.TEXT_PLAIN, "", TaggerTypeToken.PlainTextTagger,
         enumCount);
   }

   public HttpMethodAttributeType() {
      this(NamespaceToken.OSEE, 5);
   }

   public class HttpMethodEnum extends EnumToken {
      public HttpMethodEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}