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
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.order.IRelationSorter;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderBaseTypes;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderData;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactPasteConfiguration;
import org.eclipse.swt.widgets.Display;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactPasteOperation extends AbstractOperation {

   private final List<Artifact> itemsToCopy;
   private final Artifact destination;
   private final ArtifactPasteConfiguration config;
   private String newName;

   public ArtifactPasteOperation(ArtifactPasteConfiguration config, Artifact destination, List<Artifact> itemsToCopy) {
      super("Paste Artifact(s)", SkynetGuiPlugin.PLUGIN_ID);
      this.itemsToCopy = itemsToCopy;
      this.destination = destination;
      this.config = config;
      this.newName = null;
   }

   @Override
   protected void doWork(final IProgressMonitor monitor) throws Exception {
      if (destination == null) {
         throw new OseeArgumentException("Destination Artifact cannot be null.");
      }
      if (!itemsToCopy.isEmpty()) {
         final Artifact source = itemsToCopy.get(0);
         if (source.hasParent() && destination.equals(source.getParent())) {

            // Prevent Pasting multiples if pasting onto same parent
            if (source instanceof User) {
               return;
            }

            final MutableBoolean isPasteAllowed = new MutableBoolean(false);
            Display.getDefault().syncExec(new Runnable() {
               public void run() {
                  InputDialog dialog =
                        new InputDialog(Display.getCurrent().getActiveShell(), "Name Artifact", "Enter artifact name",
                              source.getName(), new NonBlankAndNotSameAsStartingValidator(source.getName()));
                  int result = dialog.open();
                  isPasteAllowed.setValue(result == Window.OK);
                  newName = dialog.getValue();
               }
            });
            if (isPasteAllowed.getValue()) {
               double workAmount = 0.80;
               Artifact newArtifact = pasteArtifact(monitor, workAmount, config, destination, source);
               newArtifact.setName(newName);
            }
         } else {
            double workAmount = 0.80 / itemsToCopy.size();
            for (Artifact copiedItem : itemsToCopy) {
               pasteArtifact(monitor, workAmount, config, destination, copiedItem);
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
         RelationOrderData data = RelationManager.createRelationOrderData(source);
         String order =
               data.getCurrentSorterGuid(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD.getRelationType(),
                     CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD.getSide());
         IRelationSorter sorter = RelationManager.getSorterProvider().getRelationOrder(order);
         if (RelationOrderBaseTypes.USER_DEFINED == sorter.getSorterId()) {
            newArtifact.setRelationOrder(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, copiedChildren);
         } else {
            newArtifact.setRelationOrder(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, sorter.getSorterId());
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
   private final static class NonBlankAndNotSameAsStartingValidator implements IInputValidator {
      private final String startingName;

      public NonBlankAndNotSameAsStartingValidator(String startingName) {
         this.startingName = startingName;
      }

      public String isValid(String newText) {
         String errorMessage = null;
         if (!Strings.isValid(newText)) {
            errorMessage = "The new name cannot be blank";
         } else if (Strings.isValid(startingName) && startingName.equals(newText)) {
            errorMessage = "The new name must be different";
         }
         return errorMessage;
      }
   }
}
