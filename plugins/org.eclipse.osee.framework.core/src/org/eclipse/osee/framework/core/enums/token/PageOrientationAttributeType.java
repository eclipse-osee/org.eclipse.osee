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

package org.eclipse.osee.framework.core.enums.token;

import java.util.stream.Stream;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.publishing.WordCoreUtil;
import org.eclipse.osee.framework.core.publishing.WordCoreUtil.pageType.PageOrientationEnum;

/**
 * Defines an enumerated {@link AttributeTypeToken} implementation class for publishing page orientations. The attribute
 * type enumeration members are defined by the class {@link WordCoreUtil.pageType.PageOrientationEnum}.
 *
 * @author Stephen J. Molaro
 * @author Loren K. Ashley
 */

public class PageOrientationAttributeType extends AttributeTypeEnum<WordCoreUtil.pageType.PageOrientationEnum> {

   /**
    * Description of the attribute's content.
    */

   private static String description = "Page Orientation: Landscape/Portrait";

   /**
    * The attribute type identifier.
    */

   private static long identifier = 1152921504606847091L;

   /**
    * Short name of the attribute type.
    */

   private static String name = "Page Orientation";

   /**
    * Gets the {@link PageOrientationEnum} member for the {@link WordCoreUtil.pageType}.
    *
    * @param pageType the {@link WordCoreUtil.pageType}.
    * @return the {@link PageOrientationEnum} member associated with the {@link WordCoreUtil.pageType} enumeration
    * member.
    */

   public PageOrientationEnum getPageOrientationEnum(WordCoreUtil.pageType pageType) {
      return pageType.getEnumToken();
   }

   /**
    * Creates a new {@link AttributeTypeEnum} {@link AttributeTypeToken} with the {@link NamespaceToken} specified by
    * <code>namespace</code>. The enumeration members are created from the members of the {@link WordCoreUtil.pageType}
    * enumeration.
    *
    * @param the {@link NamespaceToken} to create the {@link AttributeTypeToken} with.
    */

   public PageOrientationAttributeType(NamespaceToken namespace) {
      //@formatter:off
      super
         (
            PageOrientationAttributeType.identifier,
            namespace,
            PageOrientationAttributeType.name,
            MediaType.TEXT_PLAIN,
            PageOrientationAttributeType.description,
            TaggerTypeToken.PlainTextTagger,
            WordCoreUtil.pageType.values().length
         );

      Stream.of( WordCoreUtil.pageType.values() )
         .map( WordCoreUtil.pageType::getEnumToken )
         .forEach( this::addEnum );
      //@formatter:on
   }

   /**
    * Creates a new {@link AttributeTypeToken} with the default {@link NameSpaceToken#OSEE}.
    */

   public PageOrientationAttributeType() {
      this(NamespaceToken.OSEE);
   }

}

/* EOF */
