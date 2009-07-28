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
package org.eclipse.osee.framework.ui.skynet.revert;

import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.swt.widgets.Combo;

/**
 * @author Theron Virgin
 */
public class RevertDeletionCheck {

   public static boolean relationWillBeReverted(Artifact artifact) throws OseeCoreException {
      RelationLink link;
      boolean linkToDelete = false;
      List<RelationLink> childLinks = artifact.getRelations(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__PARENT);
      if (childLinks != null && !childLinks.isEmpty()) {
         link = childLinks.get(0);
         linkToDelete = ArtifactPersistenceManager.isRelationNewOnBranch(link, artifact.getBranch());
      }
      return linkToDelete;
   }

   public static boolean isRootArtifact(Artifact artifact, Combo selBox, List<List<Artifact>> artifacts) {
      int index = selBox.getSelectionIndex() == -1 ? 0 : selBox.getSelectionIndex();
      Artifact HeadArtifact = artifacts.get(index).get(0);
      boolean rootArtifact = false;
      if (artifact.equals(HeadArtifact)) {
         rootArtifact = true;
      }
      return rootArtifact;
   }

}
