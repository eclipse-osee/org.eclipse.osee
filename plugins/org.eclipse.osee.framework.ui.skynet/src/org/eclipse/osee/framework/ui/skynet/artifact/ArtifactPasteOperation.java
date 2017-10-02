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

import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderData;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
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
      super("Paste Artifact(s)", Activator.PLUGIN_ID);
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
         destination.persist(getClass().getSimpleName());
         monitor.worked(calculateWork(0.20));
      } else {
         monitor.worked(calculateWork(1.0));
      }
   }

   private void pasteRelationOrder(ArtifactPasteConfiguration config, Artifact source, Artifact newArtifact, List<Artifact> copiedChildren)  {
      if (config.isKeepRelationOrderSettings()) {
         RelationTypeSide relationTypeSide = CoreRelationTypes.Default_Hierarchical__Child;
         RelationOrderData data = RelationManager.createRelationOrderData(source);
         RelationSorter order =
            data.getCurrentSorterGuid(RelationTypeManager.getType(relationTypeSide), relationTypeSide.getSide());
         if (USER_DEFINED == order) {
            newArtifact.setRelationOrder(relationTypeSide, copiedChildren);
         } else {
            newArtifact.setRelationOrder(relationTypeSide, order);
         }
      }
   }

   private Artifact pasteArtifact(IProgressMonitor monitor, double workAmount, ArtifactPasteConfiguration config, Artifact destination, Artifact source)  {
      boolean workComplete = true;
      Artifact newArtifact = null;
      // We do not support duplicating user artifacts.
      if (!(source instanceof User)) {
         newArtifact = source.duplicate(destination.getBranch());
         destination.addChild(newArtifact);
         List<Artifact> copiedChildren = new ArrayList<>();
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
