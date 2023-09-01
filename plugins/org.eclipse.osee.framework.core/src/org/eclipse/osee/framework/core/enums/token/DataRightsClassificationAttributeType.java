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
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.publishing.RequiredIndicator;
import org.eclipse.osee.framework.core.util.toggles.CuiNamesConfiguration;
import org.eclipse.osee.framework.core.util.toggles.TogglesFactory;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * Initialize enumeration of Required Indicators.
 *
 * @author Stephen J. Molaro
 * @author Loren K. Ashley
 */

public class DataRightsClassificationAttributeType extends AttributeTypeEnum<RequiredIndicator.RequiredIndicatorEnum> {

   /**
    * The attribute type description for the {@link CuiNamesConfiguration#STANDARD} configuration.
    */

   //@formatter:off
   private static String standardDescription =
      "Specifies statements that must be published with artifact.";
   //@formatter:on

   /**
    * The attribute type name for the {@link CuiNamesConfiguration#STANDARD} configuration.
    */

   private static String standardName = "Required Indicators";

   /**
    * The attribute type description for the {@link CuiNamesConfiguration#VERSION_ONE} configuration.
    */

   //@formatter:off
   private static String versionOneDescription =
        "Restricted Rights:  Rights are retained by the company\n"
      + "\n"
      + "Restricted Rights Mixed:  contains some Restricted Rights that need separation of content with other rights\n"
      + "\n"
      + "Other:  does not contain content with Restricted Rights\n\nUnspecified: not yet specified";
   //@formatter:on

   /**
    * The attribute type name for the {@link CuiNamesConfiguration#VERSION_ONE} configuration.
    */

   private static String versionOneName = "Data Rights Classification";

   /**
    * Reads the "CuiNamesConfiguration" toggle from the Manifest Unloaded Configuration map and returns the configured
    * name and description.
    *
    * @return a {@link Pair} containing first the name and second the description.
    */

   public static Pair<String, String> getConfiguredNameAndDescription() {
      var toggles = TogglesFactory.create("CuiNamesConfiguration", CuiNamesConfiguration::convert,
         TogglesFactory.ToggleSource.BUNDLE_MANIFEST);
      var cuiNamesConfiguration = toggles.get();
      //@formatter:off
      switch (cuiNamesConfiguration) {
         case VERSION_ONE:
            return
               new Pair<>
                      (
                         DataRightsClassificationAttributeType.versionOneName,
                         DataRightsClassificationAttributeType.versionOneDescription
                      );
         case STANDARD:
         default:
            return
               new Pair<>
                      (
                         DataRightsClassificationAttributeType.standardName,
                         DataRightsClassificationAttributeType.standardDescription
                      );
      }
      //@formatter:off
   }

   /**
    * Creates a new {@link AttributeTypeEnum} {@link AttributeTypeToken} with the {@link NamespaceToken} specified by
    * <code>namespace</code>. The enumeration members are created from the members of the
    * {@link RequiredIndicator} enumeration.
    *
    * @param the {@link NamespaceToken} to create the {@link AttributeTypeToken} with.
    */

   //@formatter:off
   public DataRightsClassificationAttributeType
             (
                Long            identifier,
                String          name,
                String          description,
                TaggerTypeToken taggerTypeToken,
                String          mediaType,
                NamespaceToken  namespace
   ) {
      super
         (
            identifier,
            namespace,
            name,
            mediaType,
            description,
            taggerTypeToken,
            RequiredIndicator.values().length
         );

      Stream.of( RequiredIndicator.values() )
         .map( RequiredIndicator::getEnumToken )
         .forEach( this::addEnum );
   }
   //@formatter:on

}
