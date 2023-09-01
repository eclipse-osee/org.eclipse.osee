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

import java.util.stream.Stream;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.FileExtension;

/**
 * Initializes an enumerated {@link AttributeTypeToken} implementation for file extensions.
 *
 * @author Ryan Baldwin
 * @author Loren K. Ashley
 */

public class FileExtensionAttributeType extends AttributeTypeEnum<FileExtension.FileExtensionEnum> {

   /**
    * The attribute type description.
    */

   private static String description =
      "The file extension used to find the application to view/edit the attribute's main content.";

   /**
    * The attribute type identifier.
    */

   private static long identifier = 3731534343896308858L;

   /**
    * The attribute type display name.
    */

   private static String name = "File Extension";

   /**
    * Creates a new {@link AttributeTypeEnum} {@link AttributeTypeToken} with the {@link NamespaceToken} specified by
    * <code>namespace</code>. The enumeration members are created from the members of the {@link FileExtension}
    * enumeration.
    *
    * @param namespace the {@link NamespaceToken} to create the {@link AttributeTypeToken} with.
    */

   public FileExtensionAttributeType(NamespaceToken namespace) {
      //@formatter:off
      super
         (
            FileExtensionAttributeType.identifier,
            namespace,
            FileExtensionAttributeType.name,
            MediaType.TEXT_PLAIN,
            FileExtensionAttributeType.description,
            TaggerTypeToken.PlainTextTagger,
            FileExtension.values().length
         );

      Stream.of( FileExtension.values() )
         .map( FileExtension::getEnumToken )
         .forEach( this::addEnum );
      //@formatter:on
   }

   /**
    * Creates a new {@link FileExtensionAttributeType} with the {@link NamespaceToken#OSEE}.
    */

   public FileExtensionAttributeType() {
      this(NamespaceToken.OSEE);
   }

}

/* EOF */