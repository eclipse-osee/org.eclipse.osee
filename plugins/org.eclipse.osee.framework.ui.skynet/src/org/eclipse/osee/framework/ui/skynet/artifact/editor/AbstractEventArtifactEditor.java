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

import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.AccessControlEventType;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IAccessControlEventListener;
import org.eclipse.osee.framework.skynet.core.event.IArtifactsChangeTypeEventListener;
import org.eclipse.osee.framework.skynet.core.event.IArtifactsPurgedEventListener;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event2.AccessControlEvent;
import org.eclipse.osee.framework.skynet.core.event2.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event2.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public abstract class AbstractEventArtifactEditor extends AbstractArtifactEditor implements IArtifactEditorEventHandler {

   private final InternalEventHandler internalEventHandler;

   public AbstractEventArtifactEditor() {
      super();
      if (OseeEventManager.getPreferences().isOldEvents()) {
         internalEventHandler = new InternalEventHandler();
         OseeEventManager.addListener(internalEventHandler);
      } else {
         internalEventHandler = null;
         ArtifactEditorEventManager.add(this);
      }
   }

   @Override
   protected void addPages() {
      // do nothing
   }

   @Override
   public boolean isDirty() {
      boolean wasDirty = false;
      Artifact artifact = getArtifactFromEditorInput();
      if (artifact != null) {
         if (!artifact.isDeleted()) {
            wasDirty = super.isDirty() || artifact.isDirty();
         }
      }
      return wasDirty;
   }

   protected abstract void checkEnabledTooltems();

   @Override
   public abstract void refreshDirtyArtifact();

   @Override
   public abstract void closeEditor();

   @Override
   public abstract void refreshRelations();

   @Override
   public void setMainImage(Image titleImage) {
      super.setTitleImage(titleImage);
   }

   @Override
   public void dispose() {
      super.dispose();
      if (OseeEventManager.getPreferences().isOldEvents()) {
         OseeEventManager.removeListener(internalEventHandler);
      }
   }

   @Override
   public AbstractEventArtifactEditor getEditor() {
      return this;
   }

   /**
    * <REM2> this entire listener can be removed cause handled through ArtifactEditorEventManager
    * 
    * @author Donald G. Dunne
    */
   private final class InternalEventHandler implements IArtifactsPurgedEventListener, IBranchEventListener, IAccessControlEventListener, IArtifactsChangeTypeEventListener, IFrameworkTransactionEventListener {

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
      public void handleFrameworkTransactionEvent(Sender sender, final FrameworkTransactionData transData) {
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
      public void handleArtifactsPurgedEvent(Sender sender, final LoadedArtifacts loadedArtifacts) {
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
      public void handleBranchEventREM1(Sender sender, final BranchEventType branchModType, final int branchId) {
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
      public void handleAccessControlArtifactsEvent(Sender sender, AccessControlEvent accessControlEvent) {
         try {
            if (accessControlEvent.getEventType() == AccessControlEventType.ArtifactsLocked || accessControlEvent.getEventType() == AccessControlEventType.ArtifactsUnlocked) {
               if (accessControlEvent.getArtifacts().contains(getArtifactFromEditorInput())) {
                  Displays.ensureInDisplayThread(new Runnable() {
                     @Override
                     public void run() {
                        setTitleImage(ArtifactImageManager.getImage(getArtifactFromEditorInput()));
                     }
                  });
               }
            }
         } catch (Exception ex) {
            // do nothing
         }
      }

      @Override
      public void handleBranchEvent(Sender sender, final BranchEvent branchEvent) {
         // Handled by ArtifactEditorEventManager
      }

      @Override
      public List<? extends IEventFilter> getEventFilters() {
         Artifact artifact = getArtifactFromEditorInput();
         if (artifact != null) {
            return OseeEventManager.getEventFiltersForBranch(artifact.getBranch());
         }
         return null;
      }
   }
}
