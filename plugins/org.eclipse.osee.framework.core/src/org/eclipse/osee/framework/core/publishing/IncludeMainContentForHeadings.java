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
 * Enumeration used to indicate whether main content is displayed for header.
 *
 * @author Jaden W. Puckett
 */

@JsonSerialize(using = IncludeMainContentForHeadingsSerializer.class)
@JsonDeserialize(using = IncludeMainContentForHeadingsDeserializer.class)
public enum IncludeMainContentForHeadings {

   /**
    * Metadata attributes are to be processed for the publish.
    */

   ALWAYS("Always"),

   /**
    * Metadata attributes are not to be included in the publish.
    */

   NEVER("Never");

   /**
    * Map of the enumeration members by the format names used in publishing template and data rights configuration
    * artifacts.
    */

   private static final @NonNull Map<@NonNull String, @NonNull IncludeMainContentForHeadings> membersByOptionName;

   /**
    * The JSON field name used for serializations of {@link FormatIndicator} objects.
    */

   static final String jsonObjectName = "IncludeMainContentForHeadings";

   static {

      membersByOptionName = new HashMap<>();

      for (final var member : IncludeMainContentForHeadings.values()) {
         membersByOptionName.put(member.getOptionName(), member);
      }
   }

   /**
    * Gets the {@link IncludeMainContentForHeadings} enumeration member associated with the JSON option name.
    *
    * @param optionName the JSON option name for an enumeration member.
    * @return when a {@link IncludeMainContentForHeadings} enumeration member is defined for the <code>optionName</code>
    * an {@link Optional} containing the associated {@link IncludeMainContentForHeadings} enumeration member; otherwise,
    * an empty {@link Optional}.
    */

   public static Optional<IncludeMainContentForHeadings> ofOptionName(@Nullable String optionName) {
      var includeMetadataAttributes = Conditions.applyWhenNonNull(optionName, membersByOptionName::get);
      return Optional.ofNullable(includeMetadataAttributes);
   }

   /*
    * Saves the publishing option value strings used in the publishing template JSON.
    */

   private @NonNull String optionName;

   /**
    * Creates a {@link IncludeMetadataAttribute} enumeration member.
    *
    * @param optionName the option string used in the publishing template JSON.
    * @throws NullPointerException when <code>optionName</code> is <code>null</code>.
    */

   private IncludeMainContentForHeadings(@NonNull String optionName) {
      this.optionName = Conditions.requireNonNull(optionName, "optionName");
   }

   /**
    * Gets the JSON option name associated with the enumeration member.
    *
    * @return the JSON option name.
    */

   public @NonNull String getOptionName() {
      return this.optionName;
   }

   /**
    * Predicate to determine if the member is {@link #ALWAYS}.
    *
    * @return <code>true</code> when the member is {@link #ALWAYS}; otherwise <code>false</code>.
    */

   public boolean isAlways() {
      return this == ALWAYS;
   }

   /**
    * Predicate to determine if the member is {@link #NEVER}.
    *
    * @return <code>true</code> when the member is {@link #NEVER}; otherwise <code>false</code>.
    */

   public boolean isNever() {
      return this == NEVER;
   }

}

/* EOF */
