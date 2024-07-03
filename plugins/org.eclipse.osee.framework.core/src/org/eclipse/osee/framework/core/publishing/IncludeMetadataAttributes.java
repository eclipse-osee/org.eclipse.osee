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
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * Enumeration used to indicate whether metadata attributes are to be included in a publish.
 *
 * @author Loren K. Ashley
 */

@JsonSerialize(using = IncludeMetadataAttributesSerializer.class)
@JsonDeserialize(using = IncludeMetadataAttributesDeserializer.class)
public enum IncludeMetadataAttributes {

   /**
    * Metadata attributes are to be processed for the publish.
    */

   ALWAYS("Always"),

   /**
    * Metadata attributes are not to be included in the publish.
    */

   NEVER("Never"),

   /**
    * Metadata attributes are included for non-heading artifacts.
    */

   NOT_FOR_HEADINGS("NotForHeadings"),

   /**
    * Metadata attributes are only to be included when the artifact has main content.
    */

   ONLY_WITH_MAIN_CONTENT("OnlyWithMainContent"),

   /**
    * Metadata attributes are only to be included for artifacts of the types or derived from the types
    * {@link CoreArtifactTypes#Requirement} or {@link CoreArtifactTypes#DesignMsWord}.
    *
    * @implNote There is not a format agnostic design artifact type.
    */

   ONLY_WITH_REQUIREMENT_OR_DESIGN_MSWORD("OnlyWithRequirementOrDesignMsWord");

   /**
    * Map of the enumeration members by the format names used in publishing template and data rights configuration
    * artifacts.
    */

   private static final @NonNull Map<@NonNull String, @NonNull IncludeMetadataAttributes> membersByOptionName;

   /**
    * The JSON field name used for serializations of {@link FormatIndicator} objects.
    */

   static final String jsonObjectName = "includeMetadataAttributes";

   static {

      membersByOptionName = new HashMap<>();

      for (final var member : IncludeMetadataAttributes.values()) {
         membersByOptionName.put(member.getOptionName(), member);
      }
   }

   /**
    * Gets the {@link IncludeMetadataAttributes} enumeration member associated with the JSON option name.
    *
    * @param optionName the JSON option name for an enumeration member.
    * @return when a {@link IncludeMetadataAttributes} enumeration member is defined for the <code>optionName</code> an
    * {@link Optional} containing the associated {@link IncludeMetadataAttributes} enumeration member; otherwise, an
    * empty {@link Optional}.
    */

   public static Optional<IncludeMetadataAttributes> ofOptionName(@Nullable String optionName) {
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

   private IncludeMetadataAttributes(@NonNull String optionName) {
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

   /**
    * Predicate to determine if the member is {@link #NOT_FOR_HEADINGS}.
    *
    * @return <code>true</code> when the member is {@link #NOT_FOR_HEADINGS}; otherwise <code>false</code>.
    */

   public boolean isNotForHeadings() {
      return this == NOT_FOR_HEADINGS;
   }

   /**
    * Predicate to determine if the member is {@link #ONLY_WITH_MAIN_CONTENT}.
    *
    * @return <code>true</code> when the member is {@link #ONLY_WITH_MAIN_CONTENT}; otherwise <code>false</code>.
    */

   public boolean isOnlyWithMainContent() {
      return this == ONLY_WITH_MAIN_CONTENT;
   }

   /**
    * Predicate to determine if the member is {@link #ONLY_WITH_REQUIREMENT_OR_DESIGN_MSWORD}.
    *
    * @return <code>true</code> when the member is {@link #ONLY_WITH_REQUIREMENT_OR_DESIGN_MSWORD}; otherwise
    * <code>false</code>.
    */

   public boolean isOnlyWithRequirementOrDesignMsWord() {
      return this == ONLY_WITH_REQUIREMENT_OR_DESIGN_MSWORD;
   }

}

/* EOF */
