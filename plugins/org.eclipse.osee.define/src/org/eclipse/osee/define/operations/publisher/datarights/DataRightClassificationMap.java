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

package org.eclipse.osee.define.operations.publisher.datarights;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.publishing.DataRight;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * Encapsulates an immutable map of data right footer Word ML by data right classification names. The map entries are
 * read from the data rights artifact on the common branch of the database. When the data rights artifact does not
 * specify a footer for the unspecified key, a default unspecified footer entry is added to the map.
 *
 * @author Loren K. Ashley
 */

class DataRightClassificationMap {

   /**
    * The default {@link DataRight} used when an artifact does not have a specified data right.
    */

   private static final DataRight unspecifiedDataRight =
      new DataRight(DataRightConfiguration.defaultClassification, DataRightConfiguration.unspecifiedValue);

   /**
    * A default data rights classification map with an entry for the unspecified data right.
    */

   private static final Map<String, DataRight> unspecifiedMap =
      Map.of(DataRightConfiguration.defaultClassification, DataRightClassificationMap.unspecifiedDataRight);

   /**
    * A default instance of this class with an entry for the unspecified data right.
    */

   private static final DataRightClassificationMap unspecifiedDataRightClassificationMap =
      new DataRightClassificationMap(DataRightClassificationMap.unspecifiedMap);

   /**
    * Saves an unmodifiable map of the Word ML data right footers by the data right classification names.
    */

   private final Map<String, DataRight> map;

   /**
    * Creates a new {@link DataRightClassificationMap} instance with the specified data rights map.
    *
    * @param map the map of data right footers by data right classification.
    */

   private DataRightClassificationMap(Map<String, DataRight> map) {
      //@formatter:off
      assert
           map.containsKey( DataRightConfiguration.defaultClassification )
         : "DataRightClassificationMap::new, the parameter \"map\" does not contain an entry for the unspecified key.";
      //@formatter:on

      this.map = map;
   }

   /**
    * Gets the {@link #mappingArtifact} from the database base and parses the values of the {@link #mappingAttribute}.
    * The first line of each value is taken as the map key and the remaining lines as the map value ({@link DataRight}).
    * Invalid attribute values are ignored. When the generated map does not contain an entry for the
    * {@link #unspecifiedKey}, an entry will be added to the map with the {@link #unspecifiedKey} and
    * {@link #unspecifiedDataRight}.
    *
    * @param commonBranchQuery a query builder for the common branch.
    * @return an unmodifiable {@link Map} of the data right statements by data right classification names.
    */

   public static DataRightClassificationMap create(QueryBuilder commonBranchQuery) {

      Map<String, DataRight> toReturn = new HashMap<>();
      ArtifactReadable footerMappingArtifact;

      try {
         footerMappingArtifact = commonBranchQuery.andId(DataRightConfiguration.mappingArtifact).asArtifact();
      } catch (Exception e) {
         return DataRightClassificationMap.unspecifiedDataRightClassificationMap;
      }

      //@formatter:off
      footerMappingArtifact.getAttributeValues( DataRightConfiguration.mappingAttribute )
         .forEach
            (
               ( footer ) ->
               {
                  var parts = ((String) footer).split("\\n", 2);

                  if (parts.length == 2) {

                     var classification = parts[0].trim();
                     var content        = parts[1].trim();

                     DataRight dataRight = new DataRight(classification, content);
                     toReturn.put(classification, dataRight);
                  }
               }
            );
      //@formatter:on

      if (toReturn.size() == 0) {
         return DataRightClassificationMap.unspecifiedDataRightClassificationMap;
      }

      if (!toReturn.containsKey(DataRightConfiguration.defaultClassification)) {
         toReturn.put(DataRightConfiguration.defaultClassification, DataRightClassificationMap.unspecifiedDataRight);
      }

      return new DataRightClassificationMap(Collections.unmodifiableMap(toReturn));
   }

   /**
    * Gets the {@link DataRight} for the specified data right classification name. When the specified name is
    * <code>null</code>, the specified name is empty, or the map does not contain an entry for the specified name, the
    * default {@link #unspecifiedDataRight} will be returned.
    *
    * @param classification the data right classification to get the {@link DataRight} for. This parameter maybe
    * <code>null</code> or empty.
    * @return the {@link DataRight} for the specified <code>classification</code>.
    */

   public DataRight get(String classification) {
      //@formatter:off
      var dataRight =
         this.map.get
            (
               Strings.isValid( classification )
                  ? classification
                  : "Unspecified"
            );

      return
         Objects.nonNull( dataRight )
            ? dataRight
            : this.map.get( DataRightConfiguration.defaultClassification );
      //@formatter:on
   }

}

/* EOF */