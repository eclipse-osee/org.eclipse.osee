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

import java.util.function.Consumer;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * Class used to indicate the outline heading result by the method {@link WordRenderUtil#processOutlining}.
 *
 * @author Loren K. Ashley
 */

public class OutlineSectionResult {

   /**
    * Private enumeration to save the outlining section result status.
    */

   private static enum SectionStatus {

      /**
       * An outlining section was started.
       */

      STARTED,

      /**
       * The artifact is excluded from the publish.
       */

      EXCLUDED,

      /**
       * The artifact is included in the publish but is not a heading.
       */

      NOT_A_HEADING;
   }

   /**
    * Saves the outlining section result status. @see {@link OutlineSectionResult#SectionStatus}.
    */

   private final @NonNull SectionStatus sectionStatus;

   /**
    * When the {@link #sectionStatus} member is {@link SectionStatus#STARTED} this member will contain the publishing
    * content for the heading.
    */

   private final @Nullable CharSequence sectionText;

   /**
    * Private constructor used by the static creation methods {@link #of}, {@link #excluded}, and {@link #notAHeading}.
    *
    * @param sectionStatus the {@link SectionStatus} to be set.
    * @param sectionText the publishing content for the heading when a heading was generated for the artifact;
    * otherwise, <code>null</code>.
    */

   private OutlineSectionResult(@NonNull SectionStatus sectionStatus, @Nullable CharSequence sectionText) {

      this.sectionStatus = sectionStatus;
      this.sectionText = sectionText;
   }

   /**
    * Creates an {@link OutlineSectionResult} with a section started status and the publishing content for the heading.
    *
    * @param sectionText the publishing content for the heading.
    * @return an {@link OutlineSectionResult} with a section started status and the publishing content for the heading.
    * @throws NullPointerException when the parameter <code>sectionText</code> is <code>null</code>.
    */

   static @NonNull OutlineSectionResult of(@NonNull CharSequence sectionText) {
      final var safeSectionText = Conditions.requireNonNull(sectionText);
      return new OutlineSectionResult(SectionStatus.STARTED, safeSectionText);
   }

   /**
    * Creates an {@link OutlineSectionResult} with a status indicating the artifact is to be excluded from the publish.
    *
    * @return an {@link OutlineSectionResult} with an exclude artifact status.
    */

   static @NonNull OutlineSectionResult excluded() {
      return new OutlineSectionResult(SectionStatus.EXCLUDED, null);
   };

   /**
    * Creates an {@link OutlineSectionResult} with a status indicating the artifact is included in the publish but is
    * not a heading.
    *
    * @return an {@link OutlineSectionResult} with a status indicating the artifact is included in the publish but is
    * not a heading.
    */

   static @NonNull OutlineSectionResult notAHeading() {
      return new OutlineSectionResult(SectionStatus.NOT_A_HEADING, null);
   };

   /**
    * Predicate to indicate whether the {@link OutlineSectionResult} indicates the artifact is a heading.
    *
    * @return <code>true</code> when the processed artifact is a heading; otherwise, <code>false</code>.
    */

   public boolean isStarted() {
      return this.sectionStatus == SectionStatus.STARTED;
   }

   /**
    * Invokes the provided {@link Consumer} when then {@link OutlineSectionResult} contains heading text.
    *
    * @param consumer the {@link Consumer} invoked with the heading text when the {@link OutlineSectionResult} contains
    * heading text.
    * @throws NullPointerException when <code>consumer</code> is <code>null</code>.
    */

   public void ifStarted(@NonNull Consumer<@NonNull CharSequence> consumer) {
      if (this.sectionStatus == SectionStatus.STARTED) {
         final var safeConsumer = Conditions.requireNonNull(consumer, "consumer");
         Conditions.requireNonNull(this.sectionText, Conditions.ValueType.MEMBER, "sectionText");
         safeConsumer.accept(this.sectionText);
      }
   }

   /**
    * Invokes the provided {@link Consumer} when then {@link OutlineSectionResult} contains heading text; otherwise,
    * runs the {@link Runnable}.
    *
    * @param consumer the {@link Consumer} invoked with the heading text when the {@link OutlineSectionResult} contains
    * heading text.
    * @param runnable the {@link Runnable} invoked when the {@link OutlineSectionResult} does not contain heading text.
    * @throws NullPointerException when <code>consumer</code> or <code>runnable</code> is <code>null</code>.
    */

   public void ifStartedOrElse(@NonNull Consumer<@NonNull CharSequence> consumer, @NonNull Runnable runnable) {
      if (this.sectionStatus == SectionStatus.STARTED) {
         final var safeConsumer = Conditions.requireNonNull(consumer, "consumer");
         Conditions.requireNonNull(this.sectionText, Conditions.ValueType.MEMBER, "sectionText");
         safeConsumer.accept(this.sectionText);
      } else {
         final var safeRunnable = Conditions.requireNonNull(runnable, "runnable");
         safeRunnable.run();
      }
   }

   /**
    * Predicate to indicate whether the {@link OutlineSectionResult} indicates the artifact is to be excluded from the
    * publish.
    *
    * @return <code>true</code> when the processed artifact is to be excluded from the publish; otherwise,
    * <code>false</code>.
    */

   public boolean isExcluded() {
      return this.sectionStatus == SectionStatus.EXCLUDED;
   }

   /**
    * Predicate to indicate whether the {@link OutlineSectionResult} indicates the artfact is to be included in the
    * publish but is not a heading.
    *
    * @return <code>true</code> when the processed artifact is to be included in the publish but is not a heading;
    * otherwise, <code>false</code>.
    */

   public boolean isNotAHeading() {
      return this.sectionStatus == SectionStatus.NOT_A_HEADING;
   }

   /**
    * Gets the publish heading text from the {@link OutlineSectionResult}.
    *
    * @return the publishing content for the heading.
    * @throws NullPointerException when the {@link OutlineSectionResult} does not contain heading text.
    */

   public @NonNull CharSequence getHeadingText() {
      return Conditions.requireNonNull(this.sectionText, Conditions.ValueType.MEMBER, "sectionText");
   }

}

/* EOF */
