/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.skynet.core.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;

/**
 * Utility methods for common tasks performed on Artifact's.
 * 
 * @author Robert A. Fisher
 */
public final class Artifacts {

   private Artifacts() {
      // This constructor is private because there is no reason to instantiate this class
   }

   public static String commaArts(Collection<? extends Artifact> artifacts) {
      return toTextList(artifacts, ", ");
   }

   public static String toTextList(Collection<? extends Artifact> artifacts, String separator) {
      StringBuilder sb = new StringBuilder();
      for (Artifact art : artifacts) {
         sb.append(art.getDescriptiveName());
         sb.append(separator);
      }
      if (sb.length() > separator.length()) {
         return sb.substring(0, sb.length() - separator.length());
      }
      return "";
   }

   public static Collection<String> artNames(Collection<? extends Artifact> arts) {
      ArrayList<String> names = new ArrayList<String>();
      for (Artifact art : arts)
         names.add(art.getDescriptiveName());
      return names;
   }

   public static void persist(final Collection<? extends Artifact> artifacts, final boolean recurse) throws Exception {
      AbstractSkynetTxTemplate newActionTx = new AbstractSkynetTxTemplate(artifacts.iterator().next().getBranch()) {

         @Override
         protected void handleTxWork() throws Exception {
            for (Artifact art : artifacts)
               art.persist(recurse);
         }

      };
      newActionTx.execute();
   }

   public static void delete(final Collection<? extends Artifact> artifacts) throws Exception {
      AbstractSkynetTxTemplate newActionTx = new AbstractSkynetTxTemplate(artifacts.iterator().next().getBranch()) {

         @Override
         protected void handleTxWork() throws Exception {
            for (Artifact art : artifacts)
               art.delete();
         }

      };
      newActionTx.execute();
   }

   public static void purge(final Collection<? extends Artifact> artifacts) throws Exception {
      AbstractSkynetTxTemplate newActionTx = new AbstractSkynetTxTemplate(artifacts.iterator().next().getBranch()) {

         @Override
         protected void handleTxWork() throws Exception {
            for (Artifact art : artifacts)
               art.purge();
         }

      };
      newActionTx.execute();
   }

   /**
    * Calculates the artifacts from the provided list that are not a descendant of any of the other artifacts in the
    * collection. The provided RelationSide should either produce no artifact or a single artifact from any artifact in
    * the collection, and should not produce any cycle.<br/> <br/> <b>Example</b><br/> <i>Given:</i> A Skynet system
    * storing family trees with an artifact modeling each person with a unique name and with a gender of male or female
    * and a family link with sides parent and child.<br/> <br/> <i>Problem:</i> Find all the founding males of each
    * family tree<br/> <br/> <i>Code:</i><br/> <code>
    * Collection<Artifact> males = ArtifactPersistenceManager.getInstance().getArtifacts(new AttributeValueSearch("Gender", "Male", Operator.EQUAL), Branch.getDefaultBranch());<br/>
    * <br/> 
    * Collection<Artifact> foundingMales = getRoots(males, RelationSide.Family_Parent);
    * </code>
    * 
    * @param artifacts A list of artifacts to analyze.
    * @param parentSide The relation side that is the link that defines the parent
    * @throws IllegalArgumentException if any parameter is null
    * @throws IllegalArgumentException if any artifact is on a different branch than another
    * @throws IllegalStateException if following the parent side links causes a cycle
    * @throws IllegalStateException if following the parent side link results in more than one artifact from a given
    *            point
    */
   public static Collection<Artifact> getRoots(Collection<Artifact> artifacts, RelationSide parentSide) {
      if (artifacts == null) throw new IllegalArgumentException("artifacts can not be null");
      if (parentSide == null) throw new IllegalArgumentException("side can not be null");

      if (artifacts.isEmpty()) {
         return new ArrayList<Artifact>(0);
      }

      Branch branch = artifacts.iterator().next().getBranch();
      for (Artifact artifact : artifacts) {
         if (artifact.getBranch() != branch) throw new IllegalArgumentException(
               "All artifacts must be on same branch, first artifact is on " + branch + " but another artifact is on " + artifact.getBranch());
      }

      // Since artifacts may be provided with overriden hashcode/equals or may be no, we will use
      // serial ids for identification
      HashSet<Integer> artifactSerialIds = new HashSet<Integer>((int) (artifacts.size() / .75f) + 1, .75f);
      for (Artifact artifact : artifacts) {
         artifactSerialIds.add(artifact.aaaSerialId);
      }

      Collection<Artifact> rootArtifacts = new LinkedList<Artifact>();
      try {
         // Get all of the artifacts who's parent is not in the provided collection
         HashSet<Integer> visitedIds = new HashSet<Integer>();
         for (Artifact artifact : artifacts) {
            // Restart the cycle detector
            visitedIds.clear();

            Artifact parentCursor = artifact;
            Collection<Artifact> candidates;
            for (;;) {
               candidates = parentCursor.getArtifacts(parentSide);
               if (candidates.isEmpty()) {
                  rootArtifacts.add(artifact);
                  break;
               } else if (candidates.size() == 1) {
                  parentCursor = candidates.iterator().next();
                  if (!visitedIds.contains(parentCursor.aaaSerialId)) {
                     if (artifactSerialIds.contains(parentCursor.aaaSerialId)) {
                        // This is someone's child, so ignore it
                        break;
                     }
                     visitedIds.add(parentCursor.aaaSerialId);
                  } else {
                     throw new IllegalArgumentException("parentSide value produced a cycle");
                  }
               } else {
                  throw new IllegalArgumentException("parentSide value produced multiple artifacts");
               }
            }
         }
      } catch (SQLException ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }

      return rootArtifacts;
   }

   /**
    * Recurses default hierarchy and collections childrent of parentArtifact that are of type class
    * 
    * @param <A>
    * @param parentArtifact
    * @param children
    * @param clazz
    * @param recurse TODO
    * @throws SQLException
    */
   @SuppressWarnings("unchecked")
   public static <A extends Artifact> void getChildrenOfType(Artifact parentArtifact, Collection<A> children, Class<A> clazz, boolean recurse) throws SQLException {
      for (Artifact child : parentArtifact.getChildren()) {
         if (child.getClass().equals(clazz)) {
            children.add((A) child);
            if (recurse) getChildrenOfType(child, children, clazz, recurse);
         }
      }
   }

   @SuppressWarnings("unchecked")
   public static <A extends Artifact> Set<A> getChildrenOfTypeSet(Artifact parentArtifact, Class<A> clazz, boolean recurse) throws SQLException {
      Set<A> children = new HashSet<A>();
      for (Artifact child : parentArtifact.getChildren()) {
         if (child.getClass().equals(clazz)) {
            children.add((A) child);
            if (recurse) getChildrenOfType(child, children, clazz, recurse);
         }
      }
      return children;
   }

   /**
    * Returns all descendants of the parent artifact from the set of candidates. Descendants are considered from the
    * tree developed from tracing the parentSide links.<br/> <br/> <b>Example</b><br/> <i>Given:</i> A Skynet system
    * storing family trees with an artifact modeling each person with a unique name and with a gender of male or female
    * and a family link with sides parent and child.<br/> <br/> <i>Problem:</i> Find all male descendants of Joe Java<br/>
    * <br/> <i>Code:</i><br/> <code>
    * Artifact joeJava = ArtifactPersistenceManager.getInstance().getArtifacts(new AttributeValueSearch("Name", "Joe Java", Operator.EQUAL), Branch.getDefaultBranch()).iterator().next();<br/>
    * Collection<Artifact> males = ArtifactPersistenceManager.getInstance().getArtifacts(new AttributeValueSearch("Gender", "Male", Operator.EQUAL), Branch.getDefaultBranch());<br/>
    * <br/> 
    * Collection<Artifact> joesMaleDescendants = getDescendants(joeJava, males, RelationSide.Family_Parent);
    * </code>
    * 
    * @throws IllegalArgumentException if any parameter is null
    * @throws IllegalArgumentException if any artifact is on a different branch than another
    * @throws IllegalStateException if following the parent side links causes a cycle
    * @throws IllegalStateException if following the parent side link results in more than one artifact from a given
    *            point
    */
   public static Collection<Artifact> getDescendants(Artifact parent, Collection<Artifact> candidates, RelationSide parentSide) {
      if (parent == null) throw new IllegalArgumentException("parent can not be null");
      if (candidates == null) throw new IllegalArgumentException("candidates can not be null");
      if (parentSide == null) throw new IllegalArgumentException("side can not be null");

      Collection<Artifact> descendants = new LinkedList<Artifact>();

      try {
         HashSet<Integer> visitedIds = new HashSet<Integer>();
         for (Artifact artifact : candidates) {
            // Restart the cycle detector
            visitedIds.clear();

            Artifact parentCursor = artifact;
            Collection<Artifact> parentCandidates;
            for (;;) {
               parentCandidates = parentCursor.getArtifacts(parentSide);
               if (parentCandidates.isEmpty()) {
                  // Not a descendant, so ignore it
                  break;
               } else if (parentCandidates.size() == 1) {
                  parentCursor = parentCandidates.iterator().next();
                  if (!visitedIds.contains(parentCursor.aaaSerialId)) {
                     if (parent.aaaSerialId == parentCursor.aaaSerialId) {
                        descendants.add(artifact);
                        break;
                     }
                     visitedIds.add(parentCursor.aaaSerialId);
                  } else {
                     throw new IllegalArgumentException("parentSide value produced a cycle");
                  }
               } else {
                  throw new IllegalArgumentException("parentSide value produced multiple artifacts");
               }
            }
         }
      } catch (SQLException ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }

      return descendants;
   }

   /**
    * Orders a collection of artifacts based on a particular type of link. The provided RelationSide should either
    * produce no artifact or a single artifact from any artifact in the collection, and should not produce any cycle.
    * 
    * @param artifacts The artifacts to order
    * @param side The side to base ordering on
    * @throws IllegalArgumentException if any parameter is null
    * @throws IllegalArgumentException if any artifact is on a different branch than another
    * @throws IllegalArgumentException if the artifacts are in disjoint sets from the provided RelationSide
    * @throws IllegalStateException if following the parent side links causes a cycle
    * @throws IllegalStateException if following the parent side link results in more than one artifact from a given
    *            point
    */
   public static List<Artifact> orderByRelation(Collection<Artifact> artifacts, RelationSide side) {
      throw new UnsupportedOperationException("not implemented yet");
   }

   /**
    * Returns all of the n level deep artifacts in the direction of a particular link side. Level 0 will simply return a
    * collection that has the root artifact in it.
    * 
    * @param root The artifact to start looking from
    * @param childSide The relationSide to follow to find children
    * @param level The level from the root artifact to capture descendants
    * @throws IllegalArgumentException if any parameter is null
    * @throws IllegalArgumentException if level is less than zero
    * @throws SQLException See
    *            {@link Artifact#getArtifacts(org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration)}
    */
   public static Collection<Artifact> getDescendantsAtLevel(Artifact root, RelationSide childSide, int level) throws SQLException {
      if (root == null) throw new IllegalArgumentException("root can not be null");
      if (childSide == null) throw new IllegalArgumentException("childSide can not be null");
      if (level < 0) throw new IllegalArgumentException("level can not be less than 0");

      Collection<Artifact> descendants = new LinkedList<Artifact>();

      addAtLevelZero(descendants, root, childSide, level);

      return descendants;
   }

   private static void addAtLevelZero(Collection<Artifact> levelArtifacts, Artifact ancestor, RelationSide childSide, int level) throws SQLException {
      if (level == 0) {
         levelArtifacts.add(ancestor);
      } else {
         for (Artifact artifact : ancestor.getArtifacts(childSide)) {
            addAtLevelZero(levelArtifacts, artifact, childSide, level - 1);
         }
      }
   }
}
