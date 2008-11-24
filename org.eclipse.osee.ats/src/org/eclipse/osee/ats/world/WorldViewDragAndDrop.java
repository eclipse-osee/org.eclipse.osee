/*
 * Created on Oct 7, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.world;

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
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.world.search.GroupWorldSearchItem;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.skynet.core.artifact.UniversalGroup;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.util.SkynetDragAndDrop;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
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
      List<Artifact> artifacts = new ArrayList<Artifact>();
      while (i.hasNext()) {
         Object object = i.next();
         if (object instanceof Artifact) artifacts.add((Artifact) object);
      }
      return artifacts.toArray(new Artifact[artifacts.size()]);
   }

   private boolean isValidForArtifactDrop(DropTargetEvent event) {
      if (ArtifactTransfer.getInstance().isSupportedType(event.currentDataType)) {
         ArtifactData artData = ArtifactTransfer.getInstance().nativeToJava(event.currentDataType);

         if (artData != null) {
            Artifact[] artifacts = artData.getArtifacts();
            for (Artifact art : artifacts) {
               if ((art instanceof IWorldViewArtifact) || art.getArtifactTypeName().equals(
                     UniversalGroup.ARTIFACT_TYPE_NAME)) {
                  return true;
               }
            }
         }
      }
      return false;
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
            /* (non-Javadoc)
             * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
             */
            @Override
            protected IStatus run(IProgressMonitor monitor) {
               try {
                  String name = "Dropped Artifacts";
                  Set<Artifact> arts = new HashSet<Artifact>();
                  if (artData != null) {
                     Artifact[] artifacts = artData.getArtifacts();
                     if (artifacts.length == 1) {
                        Artifact art = artifacts[0];
                        if (art instanceof IWorldViewArtifact) {
                           name = art.getDescriptiveName();
                        } else if (art.getArtifactTypeName().equals(UniversalGroup.ARTIFACT_TYPE_NAME)) {
                           GroupWorldSearchItem groupWorldSearchItem = new GroupWorldSearchItem();
                           groupWorldSearchItem.setSelectedGroup(art);
                           WorldEditor.open(new WorldEditorUISearchItemProvider(groupWorldSearchItem, null,
                                 TableLoadOption.NoUI));
                           return Status.OK_STATUS;
                        }
                     }
                     for (Artifact art : artifacts) {
                        if (art instanceof IWorldViewArtifact) {
                           arts.add(art);
                        } else if (art.getArtifactTypeName().equals(UniversalGroup.ARTIFACT_TYPE_NAME)) {
                           for (Artifact relArt : art.getRelatedArtifacts(CoreRelationEnumeration.UNIVERSAL_GROUPING__MEMBERS)) {
                              if (relArt instanceof IWorldViewArtifact) {
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
                  OSEELog.logException(AtsPlugin.class, ex, true);
                  return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, ex.getLocalizedMessage(), ex);
               }
               return Status.OK_STATUS;
            }
         };
         Jobs.startJob(job);
      }
   }

}
