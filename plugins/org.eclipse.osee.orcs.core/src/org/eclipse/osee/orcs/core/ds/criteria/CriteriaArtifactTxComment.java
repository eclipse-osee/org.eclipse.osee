/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.core.ds.criteria;

import org.eclipse.osee.framework.core.data.AttributeTypeJoin;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author Ryan D. Brooks
 */
public class CriteriaArtifactTxComment extends Criteria {
   private final String commentPattern;
   private final AttributeTypeJoin typeJoin;

   public CriteriaArtifactTxComment(String commentPattern, AttributeTypeJoin typeJoin) {
      this.commentPattern = commentPattern;
      this.typeJoin = typeJoin;
   }

   public String getCommentPattern() {
      return commentPattern;
   }

   public AttributeTypeJoin getTypeJoin() {
      return typeJoin;
   }

   @Override
   public void checkValid(Options options) {
      Conditions.checkNotNull(commentPattern, "commentPattern");
   }

   @Override
   public String toString() {
      return getClass().getSimpleName() + " " + commentPattern;
   }
}