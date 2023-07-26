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

package org.eclipse.osee.orcs.core.ds;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.RelationTypeSide;

/**
 * @author Ryan T. Baldwin
 */
public class FollowRelation {

   private final boolean isFork;
   private RelationTypeSide followRelation;
   private List<FollowRelation> children;

   public static FollowRelation follow(RelationTypeSide followRelation) {
      return new FollowRelation(followRelation, false);
   }

   public static FollowRelation fork(RelationTypeSide followRelation) {
      return new FollowRelation(followRelation, true);
   }

   public static FollowRelation fork(RelationTypeSide followRelation, FollowRelation... followRelations) {
      FollowRelation rel = new FollowRelation(followRelation, true);
      for (FollowRelation follow : followRelations) {
         rel.getChildren().add(follow);
      }
      return rel;
   }

   public static FollowRelation fork(RelationTypeSide followRelation, List<FollowRelation> followRelations) {
      FollowRelation rel = new FollowRelation(followRelation, true);
      for (FollowRelation follow : followRelations) {
         rel.getChildren().add(follow);
      }
      return rel;
   }

   public static List<FollowRelation> followList(RelationTypeSide... followRelations) {
      List<FollowRelation> rels = new LinkedList<>();
      for (RelationTypeSide rel : followRelations) {
         rels.add(FollowRelation.follow(rel));
      }
      return rels;
   }

   public FollowRelation(RelationTypeSide followRelation, boolean isFork) {
      setFollowRelation(followRelation);
      this.children = new LinkedList<>();
      this.isFork = isFork;
   }

   public boolean isFork() {
      return isFork;
   }

   public RelationTypeSide getFollowRelation() {
      return followRelation;
   }

   public void setFollowRelation(RelationTypeSide followRelation) {
      this.followRelation = followRelation;
   }

   public List<FollowRelation> getChildren() {
      return children;
   }

   public void setChildren(List<FollowRelation> children) {
      this.children = children;
   }

}