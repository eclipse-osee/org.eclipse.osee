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
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Jeff C. Phillips
 */
public class DefaultHierarchySorter implements Comparator<Artifact> {

   private List<Artifact> populateParent(Artifact artifact, List<Artifact> parentList) {
      if (artifact != null) {
         parentList.add(artifact);
         parentList = populateParent(artifact.getParent(), parentList);
      }
      return parentList;
   }

   @Override
   public int compare(Artifact firstArtifact, Artifact secondArtifact) {
      List<Artifact> firstArtifactsParentList = new ArrayList<>();
      List<Artifact> secondArtifactParentList = new ArrayList<>();

      try {
         firstArtifactsParentList = populateParent(firstArtifact, firstArtifactsParentList);
         secondArtifactParentList = populateParent(secondArtifact, secondArtifactParentList);

         Artifact commonParent = getCommonParent(firstArtifactsParentList, secondArtifactParentList);

         if (commonParent != null) {

            if (commonParent.equals(firstArtifact)) {
               return -1;
            } else if (commonParent.equals(secondArtifact)) {
               return 1;
            }

            for (Artifact possibleChild : commonParent.getChildren()) {
               int firstIndex = firstArtifactsParentList.indexOf(commonParent);
               if (firstIndex > 0) {
                  firstArtifact = firstArtifactsParentList.get(firstIndex - 1);
               }
               int secondIndex = secondArtifactParentList.indexOf(commonParent);
               if (secondIndex > 0) {
                  secondArtifact = secondArtifactParentList.get(secondIndex - 1);
               }

               if (possibleChild.equals(firstArtifact)) {
                  return -1;
               } else if (possibleChild.equals(secondArtifact)) {
                  return 1;
               }
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return 0;
   }

   /**
    * @return Returns the common parent for both lists.
    */
   private Artifact getCommonParent(List<Artifact> firstArtifactsParentList, List<Artifact> secondArtifactParentList) {
      Artifact toReturn = null;
      for (Artifact commonParent : firstArtifactsParentList) {
         if (secondArtifactParentList.contains(commonParent)) {
            toReturn = commonParent;
            break;
         }
      }
      return toReturn;
   }
}
