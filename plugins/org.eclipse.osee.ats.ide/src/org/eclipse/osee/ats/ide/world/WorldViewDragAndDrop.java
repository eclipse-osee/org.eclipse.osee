/*******************************************************************************
 /*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/eplv10.html
 *
 * Contributors:
 *     Boeing  initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.world.search.GroupWorldSearchItem;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.ui.skynet.util.SkynetDragAndDrop;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;

/**
 * @author Donald G. Dunne
 */
public class WorldViewDragAndDrop extends SkynetDragAndDrop {

   private final WorldComposite worldComposite;

   public WorldViewDragAndDrop(WorldComposite worldComposite, String viewId) {
      super(worldComposite.getXViewer().getTree(), viewId);
      this.worldComposite = worldComposite;
   }

   @Override
   public Artifact[] getArtifacts() {
      IStructuredSelection selection = (IStructuredSelection) worldComposite.getXViewer().getSelection();
      Iterator<?> i = selection.iterator();
      List<Artifact> artifacts = new ArrayList<>();
      while (i.hasNext()) {
         Object object = i.next();
         if (object instanceof Artifact) {
            artifacts.add(AtsClientService.get().getQueryServiceClient().getArtifact(object));
         }
      }
      return artifacts.toArray(new Artifact[artifacts.size()]);
   }

   protected boolean isValidForArtifactDrop(DropTargetEvent event) {
      boolean validForDrop = false;
      if (ArtifactTransfer.getInstance().isSupportedType(event.currentDataType)) {
         ArtifactData artData = ArtifactTransfer.getInstance().nativeToJava(event.currentDataType);

         if (artData != null) {
            Artifact[] artifacts = artData.getArtifacts();
            for (Artifact art : artifacts) {
               if (AtsObjects.isAtsWorkItemOrAction(art) || art.isOfType(CoreArtifactTypes.UniversalGroup)) {
                  validForDrop = true;
                  break;
               }
            }
         }
      }
      return validForDrop;
   }

   @Override
   public void performDragOver(DropTargetEvent event) {
      if (isValidForArtifactDrop(event)) {
         event.detail = DND.DROP_COPY;
      }
   }

   @Override
   public void performDrop(final DropTargetEvent event) {
      if (ArtifactTransfer.getInstance().isSupportedType(event.currentDataType)) {
         final ArtifactData artData = ArtifactTransfer.getInstance().nativeToJava(event.currentDataType);
         Job job = new Job("Loading WorldView...") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
               try {
                  String name = "Dropped Artifacts";
                  Set<Artifact> arts = new HashSet<>();
                  if (artData != null) {
                     Artifact[] artifacts = artData.getArtifacts();
                     if (artifacts.length == 1) {
                        Artifact art = artifacts[0];
                        if (AtsObjects.isAtsWorkItemOrAction(art)) {
                           name = art.getName();
                        } else if (art.isOfType(CoreArtifactTypes.UniversalGroup)) {
                           GroupWorldSearchItem groupWorldSearchItem = new GroupWorldSearchItem(art.getBranch());
                           groupWorldSearchItem.setSelectedGroup(art);
                           WorldEditor.open(
                              new WorldEditorUISearchItemProvider(groupWorldSearchItem, null, TableLoadOption.NoUI));
                           return Status.OK_STATUS;
                        }
                     }
                     for (Artifact art : artifacts) {
                        if (AtsObjects.isAtsWorkItemOrAction(art)) {
                           arts.add(art);
                        } else if (art.isOfType(CoreArtifactTypes.UniversalGroup)) {
                           for (Artifact relArt : art.getRelatedArtifacts(
                              CoreRelationTypes.UniversalGrouping_Members)) {
                              if (AtsObjects.isAtsWorkItemOrAction(relArt)) {
                                 arts.add(relArt);
                              }
                           }
                        }
                     }
                  }
                  if (arts.size() > 0) {
                     WorldEditor.open(new WorldEditorSimpleProvider(name, arts));
                  }
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
                  return new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getLocalizedMessage(), ex);
               }
               return Status.OK_STATUS;
            }
         };
         Jobs.startJob(job);
      }
   }

}