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
package org.eclipse.osee.framework.ui.skynet.artifact.editor;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModType;
import org.eclipse.osee.framework.skynet.core.event.AccessControlEventType;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IAccessControlEventListener;
import org.eclipse.osee.framework.skynet.core.event.IArtifactModifiedEventListener;
import org.eclipse.osee.framework.skynet.core.event.IArtifactsChangeTypeEventListener;
import org.eclipse.osee.framework.skynet.core.event.IArtifactsPurgedEventListener;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.IRelationModifiedEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Jeff C. Phillips
 */
public abstract class AbstractEventArtifactEditor extends AbstractArtifactEditor {

   private final InternalEventHandler internalEventHandler;

   public AbstractEventArtifactEditor() {
      super();
      internalEventHandler = new InternalEventHandler();
      OseeEventManager.addListener(internalEventHandler);
   }

   @Override
   protected void addPages() {
   }

   @Override
   public boolean isDirty() {
      boolean wasDirty = false;
      Artifact artifact = getArtifactFromEditorInput();
      if (artifact != null) {
         if (!artifact.isDeleted() && !artifact.isReadOnly()) {
            wasDirty = super.isDirty() || artifact.isDirty();
         }
      }
      return wasDirty;
   }

   protected abstract void checkEnabledTooltems();

   protected abstract void refreshDirtyArtifact();

   protected abstract void closeEditor();

   protected abstract void refreshRelations();

   @Override
   public void dispose() {
      super.dispose();
      OseeEventManager.removeListener(internalEventHandler);
   }

   private final class InternalEventHandler implements IArtifactsPurgedEventListener, IBranchEventListener, IAccessControlEventListener, IArtifactModifiedEventListener, IArtifactsChangeTypeEventListener, IRelationModifiedEventListener, IFrameworkTransactionEventListener {
      @Override
      public void handleArtifactModifiedEvent(Sender sender, final ArtifactModType artifactModType, final Artifact artifact) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               if (getArtifactFromEditorInput() == null || !getArtifactFromEditorInput().equals(artifact)) {
                  return;
               }
               if (artifactModType == ArtifactModType.Added || artifactModType == ArtifactModType.Changed || artifactModType == ArtifactModType.Reverted) {
                  refreshDirtyArtifact();
               }
            }
         });
      }

      @Override
      public void handleArtifactsChangeTypeEvent(Sender sender, int toArtifactTypeId, final LoadedArtifacts loadedArtifacts) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               try {
                  Artifact localArtifact = getArtifactFromEditorInput();
                  if (loadedArtifacts.getLoadedArtifacts().contains(localArtifact)) {
                     closeEditor();
                  }
               } catch (OseeCoreException ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               }
            }
         });
      }

      @Override
      public void handleRelationModifiedEvent(Sender sender, RelationEventType relationEventType, final RelationLink link, Branch branch, String relationType) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               try {
                  Artifact localArtifact = getArtifactFromEditorInput();
                  if (link.getArtifactA().equals(localArtifact) || link.getArtifactB().equals(localArtifact)) {
                     refreshRelations();
                     onDirtied();
                  }
               } catch (Exception ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               }
            }
         });
      }

      @Override
      public void handleFrameworkTransactionEvent(Sender sender, final FrameworkTransactionData transData) throws OseeCoreException {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               Artifact localArtifact = getArtifactFromEditorInput();
               if (!transData.isHasEvent(localArtifact)) {
                  return;
               }
               if (transData.isDeleted(localArtifact)) {
                  closeEditor();
               }
               if (transData.isRelAddedChangedDeleted(localArtifact)) {
                  refreshRelations();
               }
               if (transData.isChanged(localArtifact)) {
                  refreshDirtyArtifact();
               }
               onDirtied();
            }
         });
      }

      @Override
      public void handleArtifactsPurgedEvent(Sender sender, final LoadedArtifacts loadedArtifacts) throws OseeCoreException {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               try {
                  if (loadedArtifacts.getLoadedArtifacts().contains(getArtifactFromEditorInput())) {
                     closeEditor();
                  }
               } catch (OseeCoreException ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               }
            }
         });
      }

      @Override
      public void handleBranchEvent(Sender sender, final BranchEventType branchModType, final int branchId) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               if (branchModType == BranchEventType.Committed) {
                  if (getArtifactFromEditorInput().getBranch().getId() == branchId) {
                     closeEditor();
                  }
               }
            }
         });
      }

      @Override
      public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) {
      }

      @Override
      public void handleAccessControlArtifactsEvent(Sender sender, AccessControlEventType accessControlEventType, LoadedArtifacts loadedArtifacts) {
         try {
            if (accessControlEventType == AccessControlEventType.ArtifactsLocked || accessControlEventType == AccessControlEventType.ArtifactsUnlocked) {
               if (loadedArtifacts.getLoadedArtifacts().contains(getArtifactFromEditorInput())) {
                  Displays.ensureInDisplayThread(new Runnable() {
                     @Override
                     public void run() {
                        setTitleImage(ImageManager.getImage(getArtifactFromEditorInput()));
                     }
                  });
               }
            }
         } catch (Exception ex) {
            // do nothing
         }
      }
   }
}
