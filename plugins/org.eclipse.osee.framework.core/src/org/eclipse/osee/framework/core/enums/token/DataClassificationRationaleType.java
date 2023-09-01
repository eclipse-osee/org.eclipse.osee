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

import org.eclipse.osee.framework.core.data.OrcsTypeTokens;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.toggles.CuiNamesConfiguration;
import org.eclipse.osee.framework.core.util.toggles.TogglesFactory;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Implementation of the {@link OrcsTypeTokens#AttributeNameAndDescriptionSupplier} interface for the
 * {@link CoreAttributeTypes#DataClassificationRationale} attribute.
 *
 * @author Loren K Ashley
 */

public class DataClassificationRationaleType {

   /**
    * The attribute type description for the {@link CuiNamesConfiguration#STANDARD} configuration.
    */

   //@formatter:off
   private static String standardDescription =
      "The rationale for the selected CUI Category CUI Type pairs.";
   //@formatter:on

   /**
    * The attribute type name for the {@link CuiNamesConfiguration#STANDARD} configuration.
    */

   private static String standardName = "CUI Category CUI Type Rationale";

   /**
    * The attribute type description for the {@link CuiNamesConfiguration#VERSION_ONE} configuration.
    */

   private static String versionOneDescription = Strings.emptyString();

   /**
    * The attribute type name for the {@link CuiNamesConfiguration#VERSION_ONE} configuration.
    */

   private static String versionOneName = "Data Classification Rationale";

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
                         DataClassificationRationaleType.versionOneName,
                         DataClassificationRationaleType.versionOneDescription
                      );
         case STANDARD:
         default:
            return
               new Pair<>
                      (
                         DataClassificationRationaleType.standardName,
                         DataClassificationRationaleType.standardDescription
                      );
      }
      //@formatter:off
   }

}

/* EOF */
