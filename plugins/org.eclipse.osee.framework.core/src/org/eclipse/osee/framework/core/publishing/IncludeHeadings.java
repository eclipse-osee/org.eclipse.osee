/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.framework.core.publishing;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * Enumeration used to indicate which artifact types can be used for outline headings during a publish.
 *
 * @author Loren K. Ashley
 */

@JsonSerialize(using = IncludeHeadingsSerializer.class)
@JsonDeserialize(using = IncludeHeadingsDeserializer.class)
public enum IncludeHeadings {

   /**
    * This member is used to indicate that all headings will be included in the publish.
    */

   ALWAYS {

      /**
       * {@inheritDoc}
       */

      @Override
      public String getOutliningOptionName() {
         return "Always";
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public boolean populateEmptyHeaders() {
         return false;
      }
   },

   /**
    * This member is used to indicate that no headings will be included in the publish.
    */

   NEVER {

      /**
       * {@inheritDoc}
       */

      @Override
      public String getOutliningOptionName() {
         return "Never";
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public boolean populateEmptyHeaders() {
         return false;
      }

   },

   /**
    * This member is used to indicate that only headings that have a non-heading artifact hierarchically below will be
    * included in the publish.
    */

   ONLY_WITH_NON_HEADING_DESCENDANTS {

      /**
       * {@inheritDoc}
       */

      @Override
      public String getOutliningOptionName() {
         return "OnlyWithNonHeadingDescandants";
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public boolean populateEmptyHeaders() {
         return true;
      }

   },

   /**
    * This member is used to indicate that only headings that contain main content or have an artifact hierarchically
    * below with main content will be included in the publish.
    */

   ONLY_WITH_MAIN_CONTENT {

      /**
       * {@inheritDoc}
       */

      @Override
      public String getOutliningOptionName() {
         return "OnlyWithMainContent";
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public boolean populateEmptyHeaders() {
         return true;
      }

   };

   /**
    * The JSON field name used for serializations of {@link FormatIndicator} objects.
    */

   static final String jsonObjectName = "includeHeading";

   /**
    * A {@link Map} of the {@link OutliningOptions} names associated with each enumeration member.
    */

   private static final @NonNull HashMap<@NonNull String, @NonNull IncludeHeadings> members;

   static {
      members = new HashMap<>();

      for (var includeHeading : IncludeHeadings.values()) {
         members.put(includeHeading.getOutliningOptionName(), includeHeading);
      }
   }

   /**
    * Gets the enumeration member associated with the {@link OutliningOptions} value name.
    *
    * @param outliningOptionName the {@link OutliningOptions} value name to test.
    * @return when the <code>outliningOptionName</code> is the associated name of an {@link IncludeHeadings} member an
    * {@link Optional} with the associated member; otherwise, an empty {@link Optional}.
    */

   public static Optional<IncludeHeadings> ofOutliningOptionName(@Nullable String outliningOptionName) {
      final var includeHeadings = Conditions.applyWhenNonNull(outliningOptionName, IncludeHeadings.members::get);
      return Optional.ofNullable(includeHeadings);
   }

   /**
    * Gets the {@link OutliningOptions} value name associated with the enumeration members.
    *
    * @return gets the {@link OutliningOptions} value name for the enumeration member.
    */

   public abstract String getOutliningOptionName();

   /**
    * Predicate used to determine if an empty headings analysis needs to be completed for the selected
    * {@link IncludeHeadings} member.
    *
    * @return <code>true</code> when an empty headings analysis needs to be completed; otherwise, <code>false</code>.
    */

   public abstract boolean populateEmptyHeaders();

   /**
    * Predicate used to determine if the enumeration member is {@link IncludeHeadings#NEVER}.
    *
    * @return <code>true</code> when the enumeration member is {@link IncludeHeadings#NEVER}; otherwise,
    * <code>false</code>.
    */

   public boolean isNever() {
      return this == NEVER;
   }

}

/* EOF */
