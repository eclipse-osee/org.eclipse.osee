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
import org.eclipse.osee.framework.core.enums.token.FileExtensionAttributeType.FileExtensionEnum;

/**
 * @author Ryan Baldwin
 */

public class FileExtensionAttributeType extends AttributeTypeEnum<FileExtensionEnum> {

   public final FileExtensionEnum Xml = new FileExtensionEnum(0, "xml");
   public final FileExtensionEnum Zip = new FileExtensionEnum(1, "zip");
   public final FileExtensionEnum Csv = new FileExtensionEnum(2, "csv");
   public final FileExtensionEnum Json = new FileExtensionEnum(3, "json");

   public FileExtensionAttributeType(NamespaceToken namespace, int enumCount) {
      super(3731534343896308858L, namespace, "File Extension", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public FileExtensionAttributeType() {
      this(NamespaceToken.OSEE, 4);
   }

   public class FileExtensionEnum extends EnumToken {
      public FileExtensionEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}