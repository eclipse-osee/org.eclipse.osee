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
package org.eclipse.osee.framework.ui.skynet.widgets.xchange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Jeff C. Phillips
 */
public class DefaultHierSorter {
   private Map<Artifact, Set<Artifact>> childrenMap;
   private ArrayList<Artifact> orderedList;
   private Set<Artifact> orginalList;

   public List<Artifact> sort(Collection<Artifact> artifacts) throws OseeCoreException {
      childrenMap = new HashMap<Artifact, Set<Artifact>>();
      orderedList = new ArrayList<Artifact>();
      orginalList = new HashSet<Artifact>();
      
      if(artifacts.isEmpty()){
         return orderedList;
      }

      for (Artifact artifact : artifacts) {
         createAncestorTree(artifact);
         orginalList.add(artifact);
      }

      orderChildren(OseeSystemArtifacts.getDefaultHierarchyRootArtifact(artifacts.iterator().next().getBranch()));
      addDeletedArtifacts();

      if (artifacts.size() != orderedList.size()) {
         throw new OseeCoreException("Error in sorting artifacts");
      }

      return orderedList;
   }

   private void addDeletedArtifacts() {
      for (Artifact artifact : orderedList) {
         if (orginalList.contains(artifact)) {
            orginalList.remove(artifact);
         }
      }

      orderedList.addAll(orginalList);
   }

   private void orderChildren(Artifact parent) throws OseeCoreException {
      if (orginalList.contains(parent)) {
         orderedList.add(parent);
      }

      if (childrenMap.get(parent) == null) {
         return;
      }

      if (childrenMap.get(parent).size() > 1) {
         for (Artifact child : parent.getChildren(true)) {
            if (childrenMap.get(parent).contains(child)) {
               orderChildren(child);
            }
         }
      } else if ((childrenMap.get(parent).size() == 1)) {
         orderChildren(childrenMap.get(parent).iterator().next());
      }
   }

   private void createAncestorTree(Artifact child) throws OseeCoreException {
      Artifact parent = child.getParent();

      while (parent != null) {
         if (!insertChild(parent, child)) {
            return;
         }

         child = parent;
         parent = child.getParent();

         if (parent != null) {
            createAncestorTree(parent);
         }
      }
   }

   private boolean insertChild(Artifact parent, Artifact child) {
      Set<Artifact> children = childrenMap.get(parent);
      if (children == null) {
         children = new HashSet<Artifact>();
         childrenMap.put(parent, children);
      }
      return children.add(child);
   }

}
