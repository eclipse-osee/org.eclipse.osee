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
package org.eclipse.osee.framework.ui.skynet.artifact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.order.IRelationSorter;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderBaseTypes;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderData;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactPasteConfiguration;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactPasteOperation extends AbstractOperation {

   private final List<Artifact> itemsToCopy;
   private final Artifact destination;
   private final ArtifactPasteConfiguration config;
   private final ArtifactNameConflictHandler nameConflictHandler;

   public ArtifactPasteOperation(ArtifactPasteConfiguration config, Artifact destination, List<Artifact> itemsToCopy, ArtifactNameConflictHandler nameConflictHandler) {
      super("Paste Artifact(s)", SkynetGuiPlugin.PLUGIN_ID);
      this.itemsToCopy = itemsToCopy;
      this.destination = destination;
      this.config = config;
      this.nameConflictHandler = nameConflictHandler;
   }

   @Override
   protected void doWork(final IProgressMonitor monitor) throws Exception {
      if (destination == null) {
         throw new OseeArgumentException("Destination Artifact cannot be null.");
      }
      if (!itemsToCopy.isEmpty()) {
         double workAmount = 0.80;
         final Artifact itemToCopy = itemsToCopy.get(0);
         if (itemToCopy.hasParent() && destination.equals(itemToCopy.getParent())) {

            // Prevent Pasting multiples if pasting onto same parent
            if (itemToCopy instanceof User) {
               return;
            }

            Object object = nameConflictHandler.resolve(itemToCopy);
            String changedName = object != null ? object.toString() : null;
            if (!Strings.isValid(changedName) || itemToCopy.getName().equals(changedName)) {
               throw new OperationCanceledException();
            } else {
               Artifact newArtifact = pasteArtifact(monitor, workAmount, config, destination, itemToCopy);
               newArtifact.setName(changedName);
            }
         } else {
            workAmount = workAmount / itemsToCopy.size();
            for (Artifact item : itemsToCopy) {
               pasteArtifact(monitor, workAmount, config, destination, item);
            }
         }
         destination.persist();
         monitor.worked(calculateWork(0.20));
      } else {
         monitor.worked(calculateWork(1.0));
      }
   }

   private void pasteRelationOrder(ArtifactPasteConfiguration config, Artifact source, Artifact newArtifact, List<Artifact> copiedChildren) throws OseeCoreException {
      if (config.isKeepRelationOrderSettings()) {
         IRelationEnumeration relationTypeSide = CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD;
         RelationOrderData data = RelationManager.createRelationOrderData(source);
         String order = data.getCurrentSorterGuid(relationTypeSide.getRelationType(), relationTypeSide.getSide());
         IRelationSorter sorter = RelationManager.getSorterProvider().getRelationOrder(order);
         if (RelationOrderBaseTypes.USER_DEFINED == sorter.getSorterId()) {
            newArtifact.setRelationOrder(relationTypeSide, copiedChildren);
         } else {
            newArtifact.setRelationOrder(relationTypeSide, sorter.getSorterId());
         }
      }
   }

   private Artifact pasteArtifact(IProgressMonitor monitor, double workAmount, ArtifactPasteConfiguration config, Artifact destination, Artifact source) throws OseeCoreException {
      boolean workComplete = true;
      Artifact newArtifact = null;
      // We do not support duplicating user artifacts.
      if (!(source instanceof User)) {
         newArtifact = source.duplicate(destination.getBranch());
         destination.addChild(newArtifact);
         List<Artifact> copiedChildren = new ArrayList<Artifact>();
         if (config.isIncludeChildrenOfCopiedElements()) {
            Collection<Artifact> children = source.getChildren();
            if (!children.isEmpty()) {
               workComplete = false;
               double stepAmount = workAmount / children.size();
               for (Artifact sourceChild : children) {
                  copiedChildren.add(pasteArtifact(monitor, stepAmount, config, newArtifact, sourceChild));
               }
            }
         }

         if (config.isKeepRelationOrderSettings()) {
            pasteRelationOrder(config, source, newArtifact, copiedChildren);
         }
      }
      if (workComplete) {
         monitor.worked(calculateWork(workAmount));
      }
      return newArtifact;
   }
}
