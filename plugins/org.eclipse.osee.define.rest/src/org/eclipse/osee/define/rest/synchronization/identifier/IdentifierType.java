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

package org.eclipse.osee.define.rest.synchronization.identifier;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.osee.define.rest.synchronization.LinkType;
import org.eclipse.osee.define.rest.synchronization.forest.GroveThing;

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
    * The {@link IdentifierType} for Attribute Definition {@link GroveThing} Synchronization Artifact things.
    */

   ATTRIBUTE_DEFINITION("AD"),

   /**
    * The {@link IdentifierType} for Attribute Value {@link GroveThing} Synchronization Artifact things.
    */

   ATTRIBUTE_VALUE("AV"),

   /**
    * The {@link IdentifierType} for Data Type Definition {@link GroveThing} Synchronization Artifact things.
    */

   DATA_TYPE_DEFINITION("DD"),

   /**
    * The {@link IdentifierType} for Enum Value {@link GroveThing} Synchronization Artifact things.
    */

   ENUM_VALUE("EV"),

   /**
    * The {@link IdentifierType} for Header {@link GroveThing} Synchronization Artifact things.
    */

   HEADER("H"),

   /**
    * The {@link IdentifierType} for Specification {@link GroveThing} Synchronization Artifact things. Specifications
    * are members of the {@link IdentifierTypeGroup#OBJECT} group.
    */

   SPECIFICATION("S", IdentifierTypeGroup.OBJECT),

   /**
    * The {@link IdentifierType} for Spec Type {@link GroveThing} Synchronization Artifact things. Specification Types
    * are members of the {@link IdentifierTypeGroup#TYPE} group.
    */

   SPECIFICATION_TYPE("ST", IdentifierTypeGroup.TYPE),

   /**
    * The {@link IdentifierType} for Specter Spec Object {@link GroveThing} Synchronization Artifact things. Specter
    * Spec Objects are members of the {@link IdentifierTypeGroup#OBJECT} and
    * {@link IdentifierTypeGroup#RELATABLE_OBJECT} groups. Specter Spec Objects represent Spec Objects that are not in
    * the Synchronization Artifact that are related to a Spec Object in the Synchronization Artifact.
    */

   SPECTER_SPEC_OBJECT("SSO", IdentifierTypeGroup.OBJECT, IdentifierTypeGroup.RELATABLE_OBJECT),

   /**
    * The {@link IdentifierType} for Spec Object {@link GroveThing} Synchronization Artifact things. Spec Objects are
    * members of the {@link IdentifierTypeGroup#OBJECT} and {@link IdentifierTypeGroup#RELATABLE_OBJECT} groups.
    */

   SPEC_OBJECT("SO", IdentifierTypeGroup.OBJECT, IdentifierTypeGroup.RELATABLE_OBJECT),

   /**
    * The {@link IdentifierType} for Spec Object Type {@link GroveThing} Synchronization Artifact things. Spec Object
    * Types are members of the {@link IdentifierTypeGroup#TYPE} group.
    */

   SPEC_OBJECT_TYPE("SOT", IdentifierTypeGroup.TYPE),

   /**
    * The {@link IdentifierType} for Spec Relation {@link GroveThing} Synchronization Artifact things. Spec Relations
    * are members of the {@link IdentifierTypeGroup#OBJECT} group.
    */

   SPEC_RELATION("SR", IdentifierTypeGroup.OBJECT),

   /**
    * The {@link IdentifierType} for Spec Relation Type {@link GroveThing} Synchronization Artifact things. Spec
    * Relation Types are members of the {@link IdentifierTypeGroup#TYPE} group.
    */

   SPEC_RELATION_TYPE("SRT", IdentifierTypeGroup.TYPE);

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
}

/* EOF */
