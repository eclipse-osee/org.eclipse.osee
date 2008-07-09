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

package org.eclipse.osee.framework.skynet.core.utility;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
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

   public static Artifact getOrCreateArtifact(Branch branch, String artifactTypeName, String name) throws OseeCoreException, SQLException {
      try {
         return ArtifactQuery.getArtifactFromTypeAndName(artifactTypeName, name, branch);
      } catch (ArtifactDoesNotExist ex) {
         return ArtifactTypeManager.addArtifact(artifactTypeName, BranchPersistenceManager.getAtsBranch(), name);
      }
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
         protected void handleTxWork() throws OseeCoreException, SQLException {
            for (Artifact art : artifacts)
               art.persistAttributesAndRelations();
         }

      };
      newActionTx.execute();
   }

   public static void delete(final Collection<? extends Artifact> artifacts) throws Exception {
      AbstractSkynetTxTemplate newActionTx = new AbstractSkynetTxTemplate(artifacts.iterator().next().getBranch()) {

         @Override
         protected void handleTxWork() throws OseeCoreException, SQLException {
            for (Artifact art : artifacts)
               art.delete();
         }

      };
      newActionTx.execute();
   }

   public static void purge(final Collection<? extends Artifact> artifacts) throws Exception {
      AbstractSkynetTxTemplate newActionTx = new AbstractSkynetTxTemplate(artifacts.iterator().next().getBranch()) {

         @Override
         protected void handleTxWork() throws OseeCoreException, SQLException {
            for (Artifact art : artifacts)
               art.purgeFromBranch();
         }

      };
      newActionTx.execute();
   }

   /**
    * Recurses default hierarchy and collections children of parentArtifact that are of type class
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
}