/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import org.eclipse.osee.framework.core.publishing.RequiredIndicatorFrequencyIndicator;

/**
 * Initialize enumeration of Required Indicator Frequency
 *
 * @author Md I. Khan
 * @author Loren K. Ashley
 */

public class RequiredIndicatorFrequencyIndicatorAttributeType extends AttributeTypeEnum<RequiredIndicatorFrequencyIndicator.RequiredIndicatorFrequencyIndicatorEnum> {

   /**
    * Description of the attribute's content.
    */

   private static String description =
      "Specifies the location (title page, header, footer) and frequency (every page, containing pages) for a Required Indicator.";

   /**
    * The attribute type identifier.
    */

   private static long identifier = 1722628299042865586L;

   /**
    * Short name of the attribute type.
    */

   private static String name = "Required Indicator Frequency";

   /**
    * Creates a new {@link AttributeTypeEnum} {@link AttributeTypeToken} with the {@link NamespaceToken} specified by
    * <code>namespace</code>. The enumeration members are created from the members of the
    * {@link RequiredIndicatorFrequencyIndicator} enumeration.
    *
    * @param the {@link NamespaceToken} to create the {@link AttributeTypeToken} with.
    */

   //@formatter:off
   public RequiredIndicatorFrequencyIndicatorAttributeType(NamespaceToken namespace) {

      super
         (  RequiredIndicatorFrequencyIndicatorAttributeType.identifier,
            namespace,
            RequiredIndicatorFrequencyIndicatorAttributeType.name,
            MediaType.TEXT_PLAIN,
            RequiredIndicatorFrequencyIndicatorAttributeType.description,
            TaggerTypeToken.PlainTextTagger,
            RequiredIndicatorFrequencyIndicator.values().length
         );

      Stream.of( RequiredIndicatorFrequencyIndicator.values() )
         .map( RequiredIndicatorFrequencyIndicator::getEnumToken )
         .forEach( this::addEnum );
   }
   //@formatter:on

}

/* EOF */
