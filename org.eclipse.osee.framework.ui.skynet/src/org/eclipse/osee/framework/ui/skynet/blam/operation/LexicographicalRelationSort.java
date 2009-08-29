/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactNameComparator;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Ryan D. Brooks
 */
public class LexicographicalRelationSort extends AbstractBlam {
   @Override
   public String getName() {
      return "Lexicographical Relation Sort";
   }

   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      List<Artifact> artifacts = variableMap.getArtifacts("Artifacts");

      ArtifactNameComparator nameComparator = new ArtifactNameComparator();
      SkynetTransaction transaction = new SkynetTransaction(artifacts.get(0).getBranch());

      for (Artifact parent : artifacts) {
//         RelationManager.sortRelatedArtifacts(parent, CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD,
//               nameComparator);
//         parent.persistAttributesAndRelations(transaction);
      }
      transaction.execute();
      /*  OseeEventManager.kickRelationModifiedEvent(RelationManager.class, RelationModType.ReOrdered, relation,
              relation.getABranch(), relationType.getName());*/
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XListDropViewer\" displayName=\"Artifacts\" /></xWidgets>";
   }

   @Override
   public String getDescriptionUsage() {
      return "Alphebetically sorts children of the given artifacts";
   }

   public Collection<String> getCategories() {
      return Arrays.asList("Util");
   }
}