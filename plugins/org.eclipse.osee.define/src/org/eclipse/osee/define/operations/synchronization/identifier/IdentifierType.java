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

package org.eclipse.osee.define.operations.synchronization.identifier;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.osee.define.operations.synchronization.LinkType;
import org.eclipse.osee.define.operations.synchronization.forest.GroveThing;

/**
 * An enumeration of the parts of a Synchronization Artifact. The members of the enumeration also serve as a factory for
 * creating the objects that represent that part of a Synchronization Artifact. Thread local storage is used to build
 * and track the identifier counts. Multiple threads can build Synchronization Artifacts concurrently. However, only one
 * thread should be used to build a single Synchronization Artifact.
 *
 * @author Loren.K.Ashley
 */

public enum IdentifierType implements LinkType {

   /**
    * The {@link IdentifierType} for Attribute Definition {@link GroveThing} Synchronization Artifact things. Attribute
    * Definitions are members of the following groups:
    * <ul>
    * <li>{@link IdentifierTypeGroup#ATTRIBUTE}</li>
    * <li>{@link IdentifierTypeGroup#SYNCHRONIZATION_ARTIFACT_DOM}</li>
    * <li>{@link IdentifierTypeGroup#PUBLISHING_DOM}</li>
    * </ul>
    */

   //@formatter:off
   ATTRIBUTE_DEFINITION
      (
         "AD",
         IdentifierTypeGroup.ATTRIBUTE,
         IdentifierTypeGroup.SYNCHRONIZATION_ARTIFACT_DOM
      ),
   //@formatter:on

   /**
    * The {@link IdentifierType} for Attribute Value {@link GroveThing} Synchronization Artifact things. Attribute
    * Values are members of the following groups:
    * <ul>
    * <li>{@link IdentifierTypeGroup#ATTRIBUTE}</li>
    * <li>{@link IdentifierTypeGroup#SYNCHRONIZATION_ARTIFACT_DOM}</li>
    * <li>{@link IdentifierTypeGroup#PUBLISHING_DOM}</li>
    * </ul>
    */

   //@formatter:off
   ATTRIBUTE_VALUE
      (
         "AV",
         IdentifierTypeGroup.ATTRIBUTE,
         IdentifierTypeGroup.SYNCHRONIZATION_ARTIFACT_DOM
      ),
   //@formatter:on

   /**
    * The {@link IdentifierType} for Data Type Definition {@link GroveThing} Synchronization Artifact things. Data Type
    * Definitions are members of the following groups:
    * <ul>
    * <li>{@link IdentifierTypeGroup#SYNCHRONIZATION_ARTIFACT_DOM}</li>
    * </ul>
    */

   //@formatter:off
   DATA_TYPE_DEFINITION
      (
         "DD",
         IdentifierTypeGroup.SYNCHRONIZATION_ARTIFACT_DOM
      ),
   //@formatter:on

   /**
    * The {@link IdentifierType} for Enum Value {@link GroveThing} Synchronization Artifact things. Enum Values are
    * members of the following groups:
    * <ul>
    * <li>{@link IdentifierTypeGroup#SYNCHRONIZATION_ARTIFACT_DOM}</li>
    * </ul>
    */

   //@formatter:off
   ENUM_VALUE
      (
         "EV",
         IdentifierTypeGroup.SYNCHRONIZATION_ARTIFACT_DOM
      ),
   //@formatter:on

   /**
    * The {@link IdentifierType} for Header {@link GroveThing} Synchronization Artifact things. Headers are members of
    * the following groups:
    * <ul>
    * <li>{@link IdentifierTypeGroup#SYNCHRONIZATION_ARTIFACT_DOM}</li>
    * </ul>
    */

   //@formatter:off
   HEADER
      (
         "H",
         IdentifierTypeGroup.SYNCHRONIZATION_ARTIFACT_DOM
      ),
   //@formatter:on

   /**
    * The {@link IdentifierType} for Specification {@link GroveThing} Synchronization Artifact things. Specifications
    * are members of the following groups:
    * <ul>
    * <li>{@link IdentifierTypeGroup#SYNCHRONIZATION_ARTIFACT_DOM}</li>
    * <li>{@link IdentifierTypeGroup#PUBLISHING_DOM}</li>
    * <li>{@link IdentifierTypeGroup#OBJECT}</li>
    * </ul>
    */

   //@formatter:off
   SPECIFICATION
      (
         "S",
         IdentifierTypeGroup.SYNCHRONIZATION_ARTIFACT_DOM,
         IdentifierTypeGroup.OBJECT
      ),
   //@formatter:on

   /**
    * The {@link IdentifierType} for Spec Type {@link GroveThing} Synchronization Artifact things. Specification Types
    * are members of the following groups:
    * <ul>
    * <li>{@link IdentifierTypeGroup#SYNCHRONIZATION_ARTIFACT_DOM}</li>
    * <li>{@link IdentifierTypeGroup#TYPE}</li>
    * </ul>
    */

   //@formatter:off
   SPECIFICATION_TYPE
      (
         "ST",
         IdentifierTypeGroup.SYNCHRONIZATION_ARTIFACT_DOM,
         IdentifierTypeGroup.TYPE
      ),
   //@formatter:on

   /**
    * The {@link IdentifierType} for Specter Spec Object {@link GroveThing} Synchronization Artifact things. Specter
    * Spec Objects are members of the groups:
    * <ul>
    * <li>{@link IdentifierTypeGroup#SYNCHRONIZATION_ARTIFACT_DOM}</li>
    * <li>{@link IdentifierTypeGroup#PUBLISHING_DOM}</li>
    * <li>{@link IdentifierTypeGroup#OBJECT},</li>
    * <li>{@link IdentifierTypeGroup#SUBORDINATE_OBJECT}, and</li>
    * <li>{@link IdentifierTypeGroup#RELATABLE_OBJECT}</li>
    * </ul>
    * Specter Spec Objects represent Spec Objects that are not in the Synchronization Artifact that are related to a
    * Spec Object in the Synchronization Artifact.
    */

   //@formatter:off
   SPECTER_SPEC_OBJECT
      (
         "SSO",
         IdentifierTypeGroup.SYNCHRONIZATION_ARTIFACT_DOM,
         IdentifierTypeGroup.OBJECT,
         IdentifierTypeGroup.SUBORDINATE_OBJECT,
         IdentifierTypeGroup.RELATABLE_OBJECT
      ),
   //@formatter:on

   /**
    * The {@link IdentifierType} for Spec Object {@link GroveThing} Synchronization Artifact things. Spec Objects are
    * members of the groups:
    * <ul>
    * <li>{@link IdentifierTypeGroup#SYNCHRONIZATION_ARTIFACT_DOM}</li>
    * <li>{@link IdentifierTypeGroup#PUBLISHING_DOM}</li>
    * <li>{@link IdentifierTypeGroup#OBJECT},</li>
    * <li>{@link IdentifierTypeGroup#SUBORDINATE_OBJECT}, and</li>
    * <li>{@link IdentifierTypeGroup#RELATABLE_OBJECT}</li>
    * </ul>
    */

   //@formatter:off
   SPEC_OBJECT
      (
         "SO",
         IdentifierTypeGroup.SYNCHRONIZATION_ARTIFACT_DOM,
         IdentifierTypeGroup.OBJECT,
         IdentifierTypeGroup.SUBORDINATE_OBJECT,
         IdentifierTypeGroup.RELATABLE_OBJECT
      ),
   //@formatter:on

   /**
    * The {@link IdentifierType} for Spec Object Type {@link GroveThing} Synchronization Artifact things. Spec Object
    * Types are members of the following groups:
    * <ul>
    * <li>{@link IdentifierTypeGroup#SYNCHRONIZATION_ARTIFACT_DOM}</li>
    * <li>{@link IdentifierTypeGroup#TYPE}</li>
    * </ul>
    */

   //@formatter:off
   SPEC_OBJECT_TYPE
      (
         "SOT",
         IdentifierTypeGroup.SYNCHRONIZATION_ARTIFACT_DOM,
         IdentifierTypeGroup.TYPE
      ),
   //@formatter:on

   /**
    * The {@link IdentifierType} for Spec Relation {@link GroveThing} Synchronization Artifact things. Spec Relations
    * are members of the groups:
    * <ul>
    * <li>{@link IdentifierTypeGroup#SYNCHRONIZATION_ARTIFACT_DOM}</li>
    * <li>{@link IdentifierTypeGroup#PUBLISHING_DOM}</li>
    * <li>{@link IdentifierTypeGroup#OBJECT}, and</li>
    * <li>{@link IdentifierTypeGroup#SUBORDINATE_OBJECT}</li>
    * </ul>
    */

   //@formatter:off
   SPEC_RELATION
      (
         "SR",
         IdentifierTypeGroup.SYNCHRONIZATION_ARTIFACT_DOM,
         IdentifierTypeGroup.OBJECT,
         IdentifierTypeGroup.SUBORDINATE_OBJECT
      ),
   //@formatter:on

   /**
    * The {@link IdentifierType} for Spec Relation Type {@link GroveThing} Synchronization Artifact things. Spec
    * Relation Types are members of the following groups:
    * <ul>
    * <li>{@link IdentifierTypeGroup#SYNCHRONIZATION_ARTIFACT_DOM}</li>
    * <li>{@link IdentifierTypeGroup#TYPE}</li>
    * </ul>
    */

   //@formatter:off
   SPEC_RELATION_TYPE
      (
         "SRT",
         IdentifierTypeGroup.SYNCHRONIZATION_ARTIFACT_DOM,
         IdentifierTypeGroup.TYPE
      );
   //@formatter:on

   /**
    * Map of identifier type associations. Each Synchronization Artifact {@link GroveThing} that represents an object
    * has an associated Synchronization Artifact {@link GroveThing} that defines the attributes for that object.
    */

   //@formatter:off
   private static final Map<IdentifierType,IdentifierType> associatedTypeMap =
      Map.of
         (
            /* Type */                          /* Associated Type */
            IdentifierType.SPECIFICATION,       IdentifierType.SPECIFICATION_TYPE,
            IdentifierType.SPECTER_SPEC_OBJECT, IdentifierType.SPEC_OBJECT_TYPE,
            IdentifierType.SPEC_OBJECT,         IdentifierType.SPEC_OBJECT_TYPE,
            IdentifierType.SPEC_RELATION,       IdentifierType.SPEC_RELATION_TYPE
         );
   //@formatter:on

   /**
    * A map of {@link String}s enumerating the {@link IdentifierType} members in each {@link IdentifierTypeGroup}.
    */

   //@formatter:off
   private static final Map<IdentifierTypeGroup,String> identifierTypeGroupMembersMessageMap =
      Stream.of( IdentifierTypeGroup.values() )
         .map
            (
               ( IdentifierTypeGroup identifierTypeGroup ) -> Map.entry
                                             (
                                                identifierTypeGroup,
                                                Stream.of( IdentifierType.values() )
                                                   .filter
                                                      (
                                                        ( identifierType ) -> identifierType.isInGroup( identifierTypeGroup )
                                                      )
                                                   .map( IdentifierType::name )
                                                   .collect( Collectors.joining( ", ", "[ ", " ]" ) )
                                             )
            )
         .collect
            (
               Collectors.toMap
                  (
                     Map.Entry::getKey,
                     Map.Entry::getValue
                  )
             );
   //@formatter:on

   /**
    * The number of members in the {@link IdentifierType} enumeration.
    */

   private static final int size = IdentifierType.values().length;

   /**
    * Thread local storage to track the number of identifiers produced.
    */

   private ThreadLocal<Long> identifierCount;

   /**
    * The string prefix used for identifier of the {@link IdentifierType}.
    */

   private String identifierPrefix;

   /**
    * Saves the {@link IdentifierTypeGroup}s the {@link IdentifierType} is a member of.
    */

   private Set<IdentifierTypeGroup> identifierTypeGroups;

   /**
    * Thread local storage used for building the identifier strings.
    */

   private ThreadLocal<StringBuilder> stringBuilder;

   /**
    * Constructor for the enumeration members.
    *
    * @param identifierPrefix the string prefix used for identifiers of the type being constructed.
    * @param factory a factory method used to produce the Synchronization Artifact thing associated with the type being
    * constructed.
    */

   private IdentifierType(String identifierPrefix, IdentifierTypeGroup... identifierTypeGroups) {
      assert Objects.nonNull(identifierPrefix);

      this.identifierPrefix = identifierPrefix;

      this.identifierTypeGroups = EnumSet.noneOf(IdentifierTypeGroup.class);

      if (Objects.nonNull(identifierTypeGroups)) {
         for (var identifierTypeGroup : identifierTypeGroups) {
            this.identifierTypeGroups.add(identifierTypeGroup);
         }
      }

      this.identifierCount = new ThreadLocal<Long>() {
         @Override
         protected Long initialValue() {
            return 0L;
         }
      };

      this.stringBuilder = new ThreadLocal<StringBuilder>() {
         @Override
         protected StringBuilder initialValue() {
            return new StringBuilder(32);
         }
      };

   }

   /**
    * Gets a {@link String} enumerating the {@link IdentifierType} members that are in the specified
    * {@link IdentifierTypeGroup}.
    *
    * @param identifierTypeGroup the {@link IdentifierTypeGroup} to get a message for.
    * @return a {@link String} describing the {@link IdentifierType} members that are in the specified
    * {@link IdentifierTypeGroup}.
    */

   public static String getIdentifierTypeGroupMembersMessage(IdentifierTypeGroup identifierTypeGroup) {
      return IdentifierType.identifierTypeGroupMembersMessageMap.get(identifierTypeGroup);
   }

   /**
    * The number of members in the {@link IdentifierType} enumeration.
    *
    * @return the number of members in the {@link IdentifierType} enumeration.
    */

   public static int size() {
      return IdentifierType.size;
   }

   public static void resetIdentifierCounts() {

      Arrays.stream(IdentifierType.values()).forEach(IdentifierType::resetIdentifierCount);
   }

   private void resetIdentifierCount() {
      this.identifierCount.set(0L);
   }

   /**
    * For {@link #SPECIFICATION}, {@link #SPEC_OBJECT}, and {@link #SPEC_RELATION} {@link IdentifierTypes} gets the
    * {@link IdentifierType} representing the associated {@link #SPECIFICATION_TYPE}, {@link #SPEC_OBJECT_TYPE}, or
    * {@link #SPEC_RELATION_TYPE} respectively.
    *
    * @return the {@link IdentifierType} associated with this {@link IdentifierType}.
    * @throws RuntimeException when the {@link IdentifierType} represented by this object does not have an associated
    * {@link IdentifierType}.
    */

   public IdentifierType getAssociatedType() {
      var associatedType = IdentifierType.associatedTypeMap.get(this);

      if (Objects.isNull(associatedType)) {
         var message = new StringBuilder(1024);

         message.append("\n").append(
            "Requested the associated type for an IdentifierType other than SPECIFICATION, SPECTER_SPEC_OBJECT, SPEC_OBJECT, or SPEC_RELATION.").append(
               "\n").append("   Identifier Type: ").append(this).append("\n");

         throw new RuntimeException(message.toString());
      }

      return associatedType;
   }

   public String getIdentifierPrefix() {
      return this.identifierPrefix;
   }

   /**
    * Predicate to determine if the {@link IdentifierType} is a part of the specified {@link IdentifierTypeGroup}.
    *
    * @param identifierTypeGroup the identifier type group to check for membership in.
    * @return <code>true</code> when the {@link IdentifierType} is a member of the specified
    * {@link IdentifierTypeGroup}; otherwise, <code>false</code>.
    */

   public boolean isInGroup(IdentifierTypeGroup identifierTypeGroup) {
      return this.identifierTypeGroups.contains(identifierTypeGroup);
   }

   /**
    * Predicate to determine if the {@link IdentifierType} is in all of the specified {@link IdentifierTypeGroup}s.
    *
    * @param identifierTypeGroups the identifier type groups to check for membership in.
    * @return <code>true</code> when the {@link IdentifierType} is a member of all of the specified
    * {@link IdentifierTypeGroup}s; otherwise, <code>false</code>.
    */

   public boolean isInGroupAllOf(IdentifierTypeGroup... identifierTypeGroups) {
      for (var identifierTypeGroup : identifierTypeGroups) {
         if (!this.identifierTypeGroups.contains(identifierTypeGroup)) {
            return false;
         }
      }
      return true;
   }

   /**
    * Gets a {@link List} of the {@link IdentifierTypeGroup} the {@link IdentifierType} is not a member of.
    *
    * @param identifierTypeGroups the identifier type groups to check for membership in.
    * @return a {@link List} of the {@link IdentifierTypeGroup}s in <code>identifierTypeGroups</code> the
    * <code>identifierType</code> is not a member of.
    */

   public List<IdentifierTypeGroup> notInGroups(IdentifierTypeGroup... identifierTypeGroups) {
      //@formatter:off
      return
         Arrays
            .stream(identifierTypeGroups)
            .filter( (identifierTypeGroup) -> !this.isInGroup(identifierTypeGroup))
            .collect(Collectors.toList());
      //@formatter:on
   }

   /**
    * Predicate to determine if the {@link IdentifierType} is in any of the specified {@link IdentifierTypeGroup}s.
    *
    * @param identifierTypeGroups the identifier type groups to check for membership in.
    * @return <code>true</code> when the {@link IdentifierType} is a member of any of the specified
    * {@link IdentifierTypeGroup}s; otherwise, <code>false</code>.
    */

   public boolean isInGroupAnyOf(IdentifierTypeGroup... identifierTypeGroups) {
      for (var identifierTypeGroup : identifierTypeGroups) {
         if (this.identifierTypeGroups.contains(identifierTypeGroup)) {
            return true;
         }
      }
      return false;
   }

}

/* EOF */
