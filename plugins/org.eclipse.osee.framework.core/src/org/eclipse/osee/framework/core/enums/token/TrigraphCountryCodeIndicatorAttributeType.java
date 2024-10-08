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
import org.eclipse.osee.framework.core.publishing.TrigraphCountryCodeIndicator;

/**
 * Initialize enumeration with the values from Tri-graph Country Code Indicator
 *
 * @author Md I. Khan
 * @author Loren K. Ashley
 */

public class TrigraphCountryCodeIndicatorAttributeType extends AttributeTypeEnum<TrigraphCountryCodeIndicator.TrigraphCountryCodeIndicatorEnum> {

   /**
    * Description of the attribute's content.
    */

   //@formatter:off
   private static String description =
      "This attribute is used by the following artifact types:\n"
      + "   * CoreArtifactTypes.Controlled:\n"
      + "       Specifies the countries the data in the controlled artifact may be released to."
      + "   * CoreArtifactTypes.DataRightsConfiguration:\n"
      + "        Specifies the countries a publish is intended to be distributed to.";

   /**
    * The attribute type identifier.
    */

   private static long identifier = 8003115831873458510L;

   /**
    * Short name of the attribute type.
    */

   private static String name = "CUI Release List";

   /**
    * Creates a new {@link AttributeTypeEnum} {@link AttributeTypeToken} with the {@link NamespaceToken} specified by
    * <code>namespace</code>. The enumeration members are created from the members of the
    * {@link TrigraphCountryCodeIndicator} enumeration.
    *
    * @param the {@link NamespaceToken} to create the {@link AttributeTypeToken} with.
    */

   public TrigraphCountryCodeIndicatorAttributeType(NamespaceToken namespace) {
      //@formatter:off
      super
         (
            TrigraphCountryCodeIndicatorAttributeType.identifier,
            namespace,
            TrigraphCountryCodeIndicatorAttributeType.name,
            MediaType.TEXT_PLAIN,
            TrigraphCountryCodeIndicatorAttributeType.description,
            TaggerTypeToken.PlainTextTagger,
            TrigraphCountryCodeIndicator.values().length
         );

      Stream.of( TrigraphCountryCodeIndicator.values() )
         .map( TrigraphCountryCodeIndicator::getEnumToken )
         .forEach( this::addEnum );
      //@formatter:on
   }

   /**
    * Creates a new {@link AttributeTypeToken} with the default {@link NameSpaceToken#OSEE}.
    */

   public TrigraphCountryCodeIndicatorAttributeType() {
      this(NamespaceToken.OSEE);
   }

}

/* EOF */
