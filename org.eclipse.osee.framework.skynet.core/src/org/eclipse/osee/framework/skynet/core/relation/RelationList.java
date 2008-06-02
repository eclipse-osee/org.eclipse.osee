/*
 * Created on May 31, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.relation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author b1528444
 */
public class RelationList {

   private static final int LINKED_LIST_KEY = -1;
   List<RelationLink> relations = new ArrayList<RelationLink>();
   boolean modified = true;
   List<SortedNode> sortedARelations = Collections.synchronizedList(new ArrayList<SortedNode>(4));
   List<SortedNode> sortedBRelations = Collections.synchronizedList(new ArrayList<SortedNode>(4));

   public void insert(Artifact artifact, RelationSide side, RelationLink relation) {
      modified = true;
      List<SortedNode> sorted = side == RelationSide.SIDE_A ? sortedARelations : sortedBRelations;
      boolean sortNodes = false;
      SortedNode modifiedNode = null;
      for (SortedNode node : sorted) {
         if (node.add(side, relation)) {
            sortNodes = true;
            modifiedNode = node;
            break;
         }
      }
      if (sortNodes) {
         for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i).add(modifiedNode)) {
               SortedNode newModifiedNode = sorted.get(i);
               sorted.remove(modifiedNode);
               modifiedNode = newModifiedNode;
               i = -1;
            }
         }
      } else {
         SortedNode node = new SortedNode(side, relation);
         sorted.add(node);
      }
   }

   private class SortedNode {
      private List<RelationLink> sorted = new ArrayList<RelationLink>();
      private boolean isHead;
      private int leftSide;
      private int rightSide;

      /**
       * @param relation
       */
      public SortedNode(RelationSide side, RelationLink relation) {
         if (relation.getOrder(side) == LINKED_LIST_KEY) {
            isHead = true;
            leftSide = LINKED_LIST_KEY;
         } else {
            leftSide = relation.getOrder(side);
         }
         rightSide = relation.getArtifactId(side);
         sorted.add(relation);
      }

      public boolean add(RelationSide side, RelationLink relation) {
         if (!isHead && leftSide == relation.getArtifactId(side)) {
            sorted.add(0, relation);
            leftSide = relation.getOrder(side);
            if (leftSide == -1) {
               isHead = true;
            }
            return true;
         } else if (rightSide == relation.getOrder(side)) {
            sorted.add(relation);
            rightSide = relation.getArtifactId(side);
            return true;
         }
         return false;
      }

      public boolean add(SortedNode node) {
         if (node == this) {
            return false;
         }
         if (leftSide == node.rightSide) {
            sorted.addAll(0, node.sorted);
            leftSide = node.leftSide;
            return true;
         } else if (rightSide == node.leftSide) {
            sorted.addAll(node.sorted);
            rightSide = node.rightSide;
            return true;
         }
         return false;
      }

      /**
       * @return
       */
      public List<RelationLink> getRelations() {
         return sorted;
      }
   }

   /**
    * @return
    */
   public List<RelationLink> getList() {
      if (modified) {
         relations.clear();
         for (SortedNode node : this.sortedARelations) {
            relations.addAll(node.getRelations());
         }
         for (SortedNode node : this.sortedBRelations) {
            relations.addAll(node.getRelations());
         }
         modified = false;
      }
      if (relations.size() == 0) {
         return null;
      }
      return relations;
   }

   /**
    * @param relationToModify
    * @param targetLink
    * @param infront
    */
   public void move(RelationSide side, RelationLink relationToModify, RelationLink targetLink, boolean infront) {
      modified = true;
      List<SortedNode> sorted = side == RelationSide.SIDE_A ? sortedARelations : sortedBRelations;
      SortedNode node = sorted.get(0);
      ;
      if (sorted.size() > 1) {
         for (int i = 1; i < sorted.size(); i++) {
            node.getRelations().addAll(sorted.get(i).getRelations());
         }
         sorted.clear();
         sorted.add(node);
      }
      node.getRelations().remove(relationToModify);
      node.getRelations().add(
            infront ? node.getRelations().indexOf(targetLink) : node.getRelations().indexOf(targetLink) + 1,
            relationToModify);

      int lastArtId = LINKED_LIST_KEY;
      for (RelationLink link : node.getRelations()) {
         if (!link.isDeleted()) {
            if (link.getOrder(side) != lastArtId) {
               link.setOrder(side, lastArtId);
            }
            lastArtId = link.getArtifactId(side);
         }
      }
      System.out.println("test");
   }
}
