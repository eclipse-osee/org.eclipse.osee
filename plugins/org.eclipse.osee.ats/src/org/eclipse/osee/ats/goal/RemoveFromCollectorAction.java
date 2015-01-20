/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.goal;

import java.util.Collection;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.core.client.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.core.client.artifact.CollectorArtifact;
import org.eclipse.osee.ats.editor.IMemberProvider;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class RemoveFromCollectorAction extends Action {

   private final CollectorArtifact collectorArt;
   private final ISelectedAtsArtifacts selectedAtsArtifacts;
   private final RemovedFromCollectorHandler handler;
   private final IMemberProvider memberProvider;

   public static interface RemovedFromCollectorHandler {
      void removedFromCollector(Collection<? extends Artifact> removed);
   }

   public RemoveFromCollectorAction(IMemberProvider memberProvider, CollectorArtifact collectorArt, ISelectedAtsArtifacts selectedAtsArtifacts, RemovedFromCollectorHandler handler) {
      super(String.format("Remove from %s", memberProvider.getCollectorName()));
      this.memberProvider = memberProvider;
      this.collectorArt = collectorArt;
      this.selectedAtsArtifacts = selectedAtsArtifacts;
      this.handler = handler;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.REMOVE);
   }

   @Override
   public void run() {
      try {
         Collection<? extends Artifact> selected = selectedAtsArtifacts.getSelectedAtsArtifacts();
         if (selected.isEmpty()) {
            AWorkbench.popup("No items selected");
            return;
         }
         if (MessageDialog.openConfirm(Displays.getActiveShell(), "Remove from " + memberProvider.getCollectorName(),
            String.format("Remove the selected %d artifact%s from the %s [%s]?", selected.size(),
               selected.size() > 1 ? "s" : "", memberProvider.getCollectorName(), collectorArt))) {
            for (Artifact art : selected) {
               collectorArt.deleteRelation(memberProvider.getMemberRelationTypeSide(), art);
            }
            collectorArt.persist("Remove from " + memberProvider.getCollectorName());
            handler.removedFromCollector(selected);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
