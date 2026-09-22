/*********************************************************************
 * Copyright (c) 2026 Boeing
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

import java.util.Set;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * Implementation of {@link RequiredIndicatorRefresher} that reads the classification names from the
 * {@code DataRightsFooters} artifact with a {@link DataRightsFootersIndicatorLoader} and promotes
 * the names that are not already valid into the valid enum set of the target
 * {@link AttributeTypeEnum} by calling {@code addDbLoadedValidEnum}.
 * <p>
 * The refresh is additive only: {@code addDbLoadedValidEnum} never removes a value, so the compiled
 * seed set and any previously added values are always retained. It performs no database writes and
 * is idempotent, so it is safe to call repeatedly. A second call with unchanged footer content adds
 * no values and returns zero.
 *
 * @implNote The target attribute type is held as an {@link AttributeTypeEnum} of an unbounded
 * wildcard. Only {@code getEnumStrValues()}, {@code isValidEnum(String)}, and
 * {@code addDbLoadedValidEnum(String)} are used; none of these consume the enum token type, and the
 * token returned by {@code addDbLoadedValidEnum} is ignored, so a wildcard reference compiles
 * cleanly and lets tests inject a fresh attribute type instead of mutating the shared
 * {@link CoreAttributeTypes#DataRightsClassification} singleton.
 * @author David W. Miller
 */

class RequiredIndicatorRefresherImpl implements RequiredIndicatorRefresher {

   /**
    * Reads the classification names from the {@code DataRightsFooters} artifact.
    */

   private final DataRightsFootersIndicatorLoader loader;

   /**
    * The attribute type whose valid enum set is grown with the footer-derived classification names.
    */

   private final AttributeTypeEnum<?> attributeType;

   /**
    * Creates a refresher wired for production use. Reads footer names with a
    * {@link DataRightsFootersIndicatorLoaderImpl} and promotes them into the shared
    * {@link CoreAttributeTypes#DataRightsClassification} attribute type.
    */

   RequiredIndicatorRefresherImpl() {
      this(new DataRightsFootersIndicatorLoaderImpl(), CoreAttributeTypes.DataRightsClassification);
   }

   /**
    * Creates a refresher with the specified loader and target attribute type. This constructor is
    * provided so unit tests can inject a mock loader and a fresh attribute type instead of mutating
    * the shared {@link CoreAttributeTypes#DataRightsClassification} singleton.
    *
    * @param loader the loader used to read the footer classification names.
    * @param attributeType the attribute type whose valid enum set is grown.
    */

   RequiredIndicatorRefresherImpl(DataRightsFootersIndicatorLoader loader, AttributeTypeEnum<?> attributeType) {
      this.loader = loader;
      this.attributeType = attributeType;
   }

   @Override
   public int refresh(QueryBuilder commonBranchQuery) {

      Set<String> classificationNames = this.loader.loadClassificationNames(commonBranchQuery);

      if (classificationNames.isEmpty()) {
         return 0;
      }

      int addedCount = 0;

      /*
       * Ask the live attribute type whether each name is already valid rather than snapshotting the
       * valid set up front. addDbLoadedValidEnum is synchronized and idempotent by name, so under a
       * concurrent refresh the loser sees the name as already valid and does not double-count it.
       */

      for (String classificationName : classificationNames) {

         if (this.attributeType.isValidEnum(classificationName)) {
            continue;
         }

         this.attributeType.addDbLoadedValidEnum(classificationName);
         addedCount++;
      }

      return addedCount;
   }

}
