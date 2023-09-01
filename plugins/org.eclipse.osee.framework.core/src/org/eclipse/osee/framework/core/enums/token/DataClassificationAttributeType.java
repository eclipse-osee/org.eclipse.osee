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

import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.enums.token.DataClassificationAttributeType.DataClassificationEnum;
import org.eclipse.osee.framework.core.publishing.CuiCategoryIndicator;
import org.eclipse.osee.framework.core.publishing.CuiTypeIndicator;
import org.eclipse.osee.framework.core.util.toggles.CuiNamesConfiguration;
import org.eclipse.osee.framework.core.util.toggles.TogglesFactory;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Initialize enumeration of CUI Category And CUI Type pairs.
 *
 * @author Murshed Alam
 * @author Loren K. Ashley
 */

public class DataClassificationAttributeType extends AttributeTypeEnum<DataClassificationEnum> {

   /**
    * Class for the enumeration members of the {@link DataClassificationEnum}.
    */

   public static class DataClassificationEnum extends EnumToken {

      /**
       * Creates a new {@link DataClassificationEnum} member with the specified <code>ordinal</code> and
       * <code>name</code>.
       *
       * @param ordinal the ordinal value for the enumeration member.
       * @param name the name for the enumeration member.
       */

      public DataClassificationEnum(int ordinal, String name) {
         super(ordinal, name);
      }
   }

   /**
    * The attribute type description for the {@link CuiNamesConfiguration#STANDARD} configuration.
    */

   //@formatter:off
   private static String standardDescription =
      "This attribute is used by the following artifact types:\n"
    + "   * CoreArtifactTypes.Controlled:\n"
    + "        to specify a CUI Category and CUI Type of the data contained in the controlled artifact.\n"
    + "   * CoreArtifactTypes.DataRightsConfiguration:\n"
    + "        to specify an allowed CUI Category and CUI Type for a publish.\n";
   //@formatter:on

   /**
    * The attribute type name for the {@link CuiNamesConfiguration#STANDARD} configuration.
    */

   private static String standardName = "CUI Category And CUI Type Pairs";

   /**
    * The attribute type description for the {@link CuiNamesConfiguration#VERSION_ONE} configuration.
    */

   private static String versionOneDescription = Strings.emptyString();

   /**
    * The attribute type name for the {@link CuiNamesConfiguration#VERSION_ONE} configuration.
    */

   private static String versionOneName = "Data Classification";

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
                         DataClassificationAttributeType.versionOneName,
                         DataClassificationAttributeType.versionOneDescription
                      );
         case STANDARD:
         default:
            return
               new Pair<>
                      (
                         DataClassificationAttributeType.standardName,
                         DataClassificationAttributeType.standardDescription
                      );
      }
      //@formatter:off
   }

   /**
    * Creates a new {@link AttributeTypeEnum} {@link AttributeTypeToken} with the {@link NamespaceToken} specified by
    * <code>namespace</code>. The enumeration members are created from all the permutations of the members of the
    * {@link CuiCategoryIndicator} and {@link CuiTypeIndicator} enumerations.
    *
    * @param the {@link NamespaceToken} to create the {@link AttributeTypeToken} with.
    */

   //@formatter:off
   public DataClassificationAttributeType
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
            CuiCategoryIndicator.values().length * CuiTypeIndicator.values().length
         );

      var cuiCategoryIndicatorValues = CuiCategoryIndicator.values();
      var cuiTypeIndicatorValues = CuiTypeIndicator.values();
      var stringBuilder = new StringBuilder( 512 );

      for (int i = 0; i < cuiCategoryIndicatorValues.length; i++) {
         for (int j = 0; j < cuiTypeIndicatorValues.length; j++) {
            stringBuilder.setLength(0);
            stringBuilder
               .append( cuiCategoryIndicatorValues[i].name() )
               .append( " - " )
               .append( cuiTypeIndicatorValues[j].name() )
               ;
            this.addEnum
               (
                  new DataClassificationEnum( i * j + j, stringBuilder.toString() )
               );
         }
      }
      //@formatter:on
   }
}

/* EOF */
