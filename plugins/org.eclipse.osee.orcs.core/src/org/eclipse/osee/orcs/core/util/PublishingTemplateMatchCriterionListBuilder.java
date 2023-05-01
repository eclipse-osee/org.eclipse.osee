/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.orcs.core.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.eclipse.osee.orcs.core.internal.SetupPublishing;

/**
 * A dynamic builder for a list of {@link PublishingTemplateMatchCriterion}. The list build has the following options
 * for appending match criteria:
 * <dl>
 * <dt>Always:</dt>
 * <dd>The provided match criteria is always appended to the list.</dd>
 * <dt>If Not Match By Names:</dt>
 * <dd>The provided match criteria is only appended when the flag {@link SetupPublishing#matchPreviewTemplatesByName} is
 * <code>false</code>.</dd>
 * </dl>
 *
 * @author Loren K. Ashley
 */

public class PublishingTemplateMatchCriterionListBuilder {

   private final boolean matchPreviewTemplatesByName;

   /**
    * Saves the list being built.
    */

   private final LinkedList<PublishingTemplateMatchCriterion> list;

   /**
    * Creates a new empty list of {@link PublishingTemplateMatchCriterion}.
    */

   public PublishingTemplateMatchCriterionListBuilder(boolean matchPreviewTemplatesByName) {
      this.list = new LinkedList<PublishingTemplateMatchCriterion>();
      this.matchPreviewTemplatesByName = matchPreviewTemplatesByName;
   }

   /**
    * Always appends the specified {@link PublishingTemplateMatchCriterion} to the list.
    *
    * @param publishingTemplateMatchCriterion the match criterion to add to the list.
    * @return the {@link PublishingTemplateMatchCriterionListBuilder}.
    * @throws NullPointerException when the parameter <code>publishingTemplateMatchCriterion</code> is
    * <code>null</code>.
    */

   public PublishingTemplateMatchCriterionListBuilder appendAlways(PublishingTemplateMatchCriterion publishingTemplateMatchCriterion) {
      this.list.add(Objects.requireNonNull(publishingTemplateMatchCriterion));
      return this;
   }

   /**
    * Only appends the specified {@link PublishingTemplateMatchCriterion} to the list when the flag
    * {@link SetupPublishing#matchPreviewTemplatesByName} is <code>true</code>.
    *
    * @param publishingTemplateMatchCriterion the match criterion to add to the list.
    * @return the {@link PublishingTemplateMatchCriterionListBuilder}.
    */

   public PublishingTemplateMatchCriterionListBuilder appendIfMatchByNames(PublishingTemplateMatchCriterion publishingTemplateMatchCriterion) {
      if (this.matchPreviewTemplatesByName) {
         this.list.add(publishingTemplateMatchCriterion);
      }
      return this;
   }

   /**
    * Only appends the specified {@link PublishingTemplateMatchCriterion} to the list when the flag
    * {@link SetupPublishing#matchPreviewTemplatesByName} is <code>false</code>.
    *
    * @param publishingTemplateMatchCriterion the match criterion to add to the list.
    * @return the {@link PublishingTemplateMatchCriterionListBuilder}.
    */

   public PublishingTemplateMatchCriterionListBuilder appendIfNotMatchByNames(PublishingTemplateMatchCriterion publishingTemplateMatchCriterion) {
      if (!this.matchPreviewTemplatesByName) {
         this.list.add(publishingTemplateMatchCriterion);
      }
      return this;
   }

   /**
    * Gets the accumulated list of {@link PublishingTemplateMatchCriterion} as an unmodifiable list.
    *
    * @return the list of the match criteria.
    */

   public List<PublishingTemplateMatchCriterion> toList() {
      return Collections.unmodifiableList(this.list);
   }

}

/* EOF */