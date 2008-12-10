/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.artifact.editor;

import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
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
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationModType;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.ui.forms.editor.FormEditor;

/**
 * @author Jeff C. Phillips
 */
public abstract class AbstractEventArtifactEditor extends FormEditor implements IDirtiableEditor, IArtifactsPurgedEventListener, IBranchEventListener, IAccessControlEventListener, IArtifactModifiedEventListener, IArtifactsChangeTypeEventListener, IRelationModifiedEventListener, IFrameworkTransactionEventListener {

   private Artifact artifact;

   public AbstractEventArtifactEditor() {
      super();
      OseeEventManager.addListener(this);
   }

   public void setArtifact(Artifact artifact) {
      this.artifact = artifact;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.swt.IDirtiableEditor#onDirtied()
    */
   @Override
   public void onDirtied() {
      abstractOnDirty();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.event.IArtifactsPurgedEventListener#handleArtifactsPurgedEvent(org.eclipse.osee.framework.skynet.core.event.Sender, org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts)
    */
   @Override
   public void handleArtifactsPurgedEvent(Sender sender, LoadedArtifacts loadedArtifacts) throws OseeCoreException {
      try {
         if (loadedArtifacts.getLoadedArtifacts().contains(artifact)) {
            closeEditor();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.event.IBranchEventListener#handleBranchEvent(org.eclipse.osee.framework.skynet.core.event.Sender, org.eclipse.osee.framework.skynet.core.event.BranchEventType, int)
    */
   @Override
   public void handleBranchEvent(Sender sender, BranchEventType branchModType, int branchId) {
      if (branchModType == BranchEventType.Committed) {
         try {
            changeToArtifact(ArtifactQuery.getArtifactFromId(artifact.getGuid(), BranchManager.getDefaultBranch()));
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            closeEditor();
         }
      }
      if (branchModType == BranchEventType.DefaultBranchChanged) {
         try {
            if (artifact.getBranch().equals(BranchManager.getDefaultBranch()) != true && !artifact.isReadOnly()) {
               try {
                  changeToArtifact(ArtifactQuery.getArtifactFromId(artifact.getGuid(), BranchManager.getDefaultBranch()));
               } catch (ArtifactDoesNotExist ex) {
                  System.err.println("Attention: Artifact " + artifact.getArtId() + " does not exist on new default branch. Closing the editor.");
                  closeEditor();
               }
            }
            checkEnabledTooltems();
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.event.IBranchEventListener#handleLocalBranchToArtifactCacheUpdateEvent(org.eclipse.osee.framework.skynet.core.event.Sender)
    */
   @Override
   public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.event.IAccessControlEventListener#handleAccessControlArtifactsEvent(org.eclipse.osee.framework.skynet.core.event.Sender, org.eclipse.osee.framework.skynet.core.event.AccessControlEventType, org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts)
    */
   @Override
   public void handleAccessControlArtifactsEvent(Sender sender, AccessControlEventType accessControlEventType, LoadedArtifacts loadedArtifacts) {
      try {
         if (accessControlEventType == AccessControlEventType.ArtifactsLocked || accessControlEventType == AccessControlEventType.ArtifactsLocked) {
            if (loadedArtifacts.getLoadedArtifacts().contains(artifact)) {
               Displays.ensureInDisplayThread(new Runnable() {
                  /* (non-Javadoc)
                   * @see java.lang.Runnable#run()
                   */
                  @Override
                  public void run() {
                     setTitleImage(artifact.getImage());
                  }
               });
            }
         }
      } catch (Exception ex) {
         // do nothing
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.event.IArtifactModifiedEventListener#handleArtifactModifiedEvent(org.eclipse.osee.framework.skynet.core.event.Sender, org.eclipse.osee.framework.skynet.core.artifact.ArtifactModType, org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   @Override
   public void handleArtifactModifiedEvent(Sender sender, ArtifactModType artifactModType, Artifact artifact) {
      if (!this.artifact.equals(artifact)) return;
      if (artifactModType == ArtifactModType.Added || artifactModType == ArtifactModType.Changed || artifactModType == ArtifactModType.Reverted) {
         refreshDirtyArtifact();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.event.IArtifactsChangeTypeEventListener#handleArtifactsChangeTypeEvent(org.eclipse.osee.framework.skynet.core.event.Sender, int, org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts)
    */
   @Override
   public void handleArtifactsChangeTypeEvent(Sender sender, int toArtifactTypeId, LoadedArtifacts loadedArtifacts) {
      try {
         if (loadedArtifacts.getLoadedArtifacts().contains(artifact)) {
            closeEditor();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.event.IRelationModifiedEventListener#handleRelationModifiedEvent(org.eclipse.osee.framework.skynet.core.event.Sender, org.eclipse.osee.framework.skynet.core.relation.RelationModType, org.eclipse.osee.framework.skynet.core.relation.RelationLink, org.eclipse.osee.framework.skynet.core.artifact.Branch, java.lang.String)
    */
   @Override
   public void handleRelationModifiedEvent(Sender sender, RelationModType relationModType, RelationLink link, Branch branch, String relationType) {
      try {
         if (link.getArtifactA().equals(artifact) || link.getArtifactB().equals(artifact)) {
            refreshRelationsComposite();
            onDirtied();
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener#handleFrameworkTransactionEvent(org.eclipse.osee.framework.skynet.core.event.Sender, org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData)
    */
   @Override
   public void handleFrameworkTransactionEvent(Sender sender, FrameworkTransactionData transData) throws OseeCoreException {
      if (!transData.isHasEvent(artifact)) {
         return;
      }
      if (transData.isDeleted(artifact)) {
         closeEditor();
      }
      if (transData.isRelAddedChangedDeleted(artifact)) {
         refreshRelationsComposite();
      }
      if (transData.isChanged(artifact)) {
         refreshDirtyArtifact();
      }
      onDirtied();

   }

   protected abstract void abstractOnDirty();

   protected abstract void checkEnabledTooltems();

   protected abstract void changeToArtifact(final Artifact artifact);

   protected abstract void refreshDirtyArtifact();

   protected abstract void closeEditor();

   protected abstract void refreshRelationsComposite();
}
