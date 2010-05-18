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
package org.eclipse.osee.framework.skynet.core.event;

import java.rmi.RemoteException;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.data.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.res.AttributeEventModificationType;
import org.eclipse.osee.framework.messaging.event.res.IFrameworkEventListener;
import org.eclipse.osee.framework.messaging.event.res.RemoteEvent;
import org.eclipse.osee.framework.messaging.event.res.ResEventManager;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteTransactionEvent1;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.event.msgs.AttributeChange;
import org.eclipse.osee.framework.skynet.core.event2.FrameworkEventUtil;
import org.eclipse.osee.framework.skynet.core.event2.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventModType;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventModifiedBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;

/**
 * Manages remote events from the SkynetEventService.
 * 
 * @author Donald G Dunne
 */
public class RemoteEventManager2 implements IFrameworkEventListener {
   private static final RemoteEventManager2 instance = new RemoteEventManager2();

   private RemoteEventManager2() {
      super();
   }

   public static RemoteEventManager2 getInstance() {
      return instance;
   }

   @Override
   public void onEvent(final RemoteEvent remoteEvent) throws RemoteException {
      Job job =
            new Job(String.format("[%s] - receiving [%s]", getClass().getSimpleName(),
                  remoteEvent.getClass().getSimpleName())) {

               @Override
               protected IStatus run(IProgressMonitor monitor) {

                  Sender sender = null;
                  try {
                     sender = new Sender(remoteEvent.getNetworkSender());
                     // If the sender's sessionId is the same as this client, then this event was
                     // created in this client and returned by remote event manager; ignore and continue
                     if (sender.isLocal()) {
                        return Status.OK_STATUS;
                     }
                  } catch (OseeAuthenticationRequiredException ex1) {
                     OseeLog.log(Activator.class, Level.SEVERE, ex1);
                     new Status(Status.ERROR, Activator.PLUGIN_ID, -1, ex1.getLocalizedMessage(), ex1);
                  }
                  // Handles TransactionEvents, ArtifactChangeTypeEvents, ArtifactPurgeEvents
                  if (remoteEvent instanceof RemoteTransactionEvent1) {
                     try {
                        RemoteTransactionEvent1 event1 = (RemoteTransactionEvent1) remoteEvent;
                        TransactionEvent transEvent = FrameworkEventUtil.getTransactionEvent(event1);
                        updateArtifacts(sender, transEvent);
                        updateRelations(sender, transEvent);
                        InternalEventManager2.kickTransactionEvent(sender, transEvent);
                        // TODO process transaction event by updating artifact/relation caches
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  }
                  monitor.done();
                  return Status.OK_STATUS;
               }
            };
      job.setSystem(true);
      job.setUser(false);
      job.schedule();
   }

   /**
    * Updates local cache
    **/
   private static void updateArtifacts(Sender sender, TransactionEvent transEvent) {
      // Don't crash on any one artifact update problem (no update method throughs exceptions)
      for (EventBasicGuidArtifact guidArt : transEvent.getArtifacts()) {
         System.out.println("UpdateArtifacts -> " + guidArt);
         // Handle Added Artifacts
         // Nothing to do for added cause they're not in cache yet.  Apps will load if they need them.
         if (guidArt.getModType() == EventModType.Added) {
            System.out.println("UpdateArtifacts -> added " + guidArt);
         }
         // Handle Deleted Artifacts
         else if (guidArt.getModType() == EventModType.Deleted || guidArt.getModType() == EventModType.Purged) {
            updateDeletedArtifact(guidArt);
         }
         // Handle Modified Artifacts
         else if (guidArt.getModType() == EventModType.Modified) {
            updateModifiedArtifact((EventModifiedBasicGuidArtifact) guidArt);
         } else {
            OseeLog.log(Activator.class, Level.SEVERE, String.format("Unhandled mod type [%s]", guidArt.getModType()));
         }
      }
   }

   private static void updateRelations(Sender sender, TransactionEvent transEvent) {
      for (EventBasicGuidRelation guidArt : transEvent.getRelations()) {
         // Don't crash on any one relation update problem
         try {
            System.out.println("UpdateRelation -> " + guidArt);
            RelationEventType eventType = guidArt.getModType();
            String branchGuid = guidArt.getBranchGuid();
            Branch branch = BranchManager.getBranchByGuid(branchGuid);
            RelationType relationType = RelationTypeManager.getTypeByGuid(guidArt.getRelTypeGuid());
            Artifact aArtifact = ArtifactCache.getActive(guidArt.getArtA().getGuid(), branch);
            Artifact bArtifact = ArtifactCache.getActive(guidArt.getArtB().getGuid(), branch);
            // Nothing in cache, ignore
            if (aArtifact == null && bArtifact == null) {
               return;
            }
            boolean aArtifactLoaded = aArtifact != null;
            boolean bArtifactLoaded = bArtifact != null;

            if (aArtifactLoaded || bArtifactLoaded) {
               if (eventType == RelationEventType.Added) {
                  RelationLink relation =
                        RelationManager.getLoadedRelationById(guidArt.getRelationId(), guidArt.getArtAId(),
                              guidArt.getArtBId(), branch, branch);

                  if (relation == null || relation.getModificationType() == ModificationType.DELETED || relation.getModificationType() == ModificationType.ARTIFACT_DELETED) {
                     relation =
                           RelationLink.getOrCreate(guidArt.getArtAId(), guidArt.getArtBId(), branch, branch,
                                 relationType, guidArt.getRelationId(), guidArt.getGammaId(), guidArt.getRationale(),
                                 ModificationType.NEW);

                  }
               } else if (eventType == RelationEventType.Deleted || eventType == RelationEventType.Purged) {
                  RelationLink relation =
                        RelationManager.getLoadedRelationById(guidArt.getRelationId(), guidArt.getArtAId(),
                              guidArt.getArtBId(), branch, branch);
                  if (relation != null) {
                     relation.internalRemoteEventDelete();
                  }
               } else if (eventType == RelationEventType.ModifiedRationale) {
                  RelationLink relation =
                        RelationManager.getLoadedRelationById(guidArt.getRelationId(), guidArt.getArtAId(),
                              guidArt.getArtBId(), branch, branch);
                  if (relation != null) {
                     relation.internalSetRationale(guidArt.getRationale());
                  }
               } else if (eventType == RelationEventType.Undeleted) {
                  RelationLink relation =
                        RelationManager.getLoadedRelationById(guidArt.getRelationId(), guidArt.getArtAId(),
                              guidArt.getArtBId(), branch, branch);
                  if (relation != null) {
                     relation.undelete();
                  }
               } else if (eventType == RelationEventType.ReOrdered) {
                  // TODO Handle this
               } else {
                  OseeLog.log(Activator.class, Level.SEVERE, String.format("Unhandled mod type [%s]", eventType));
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   private static void updateDeletedArtifact(DefaultBasicGuidArtifact guidArt) {
      try {
         String branchGuid = guidArt.getBranchGuid();
         Branch branch = BranchManager.getBranchByGuid(branchGuid);
         Artifact artifact = ArtifactCache.getActive(guidArt.getGuid(), branch);
         if (artifact == null) {
            // do nothing, artifact not in cache, so don't need to update
         } else if (!artifact.isHistorical()) {
            RemoteEventManager.internalHandleRemoteArtifactDeleted(artifact);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private static void updateModifiedArtifact(EventModifiedBasicGuidArtifact guidArt) {
      try {
         String branchGuid = guidArt.getBranchGuid();
         Branch branch = BranchManager.getBranchByGuid(branchGuid);
         Artifact artifact = ArtifactCache.getActive(guidArt.getGuid(), branch);
         if (artifact == null) {
            // do nothing, artifact not in cache, so don't need to update
         } else if (!artifact.isHistorical()) {
            for (AttributeChange attrChange : guidArt.getAttributeChanges()) {
               if (!InternalEventManager.isEnableRemoteEventLoopback()) {
                  ModificationType modificationType =
                        AttributeEventModificationType.getType(attrChange.getModTypeGuid()).getModificationType();
                  AttributeType attributeType = AttributeTypeManager.getTypeByGuid(attrChange.getAttrTypeGuid());
                  try {
                     Attribute<?> attribute = artifact.getAttributeById(attrChange.getAttributeId(), true);
                     // Attribute already exists (but may be deleted), process update
                     // Process MODIFIED / DELETED attribute
                     if (attribute != null) {
                        if (attribute.isDirty()) {
                           OseeLog.log(Activator.class, Level.INFO, String.format(
                                 "%s's attribute %d [/n%s/n] has been overwritten.", artifact.getSafeName(),
                                 attribute.getId(), attribute.toString()));
                        }
                        try {
                           if (modificationType == null) {
                              OseeLog.log(Activator.class, Level.SEVERE, String.format(
                                    "MOD1: Can't get mod type for %s's attribute %d.", artifact.getArtifactTypeName(),
                                    attrChange.getAttributeId()));
                              continue;
                           }
                           if (modificationType.isDeleted()) {
                              attribute.internalSetModificationType(modificationType);
                           } else {
                              attribute.getAttributeDataProvider().loadData(
                                    attrChange.getData().toArray(new Object[attrChange.getData().size()]));
                           }
                           attribute.internalSetGammaId(attrChange.getGammaId());
                           attribute.setNotDirty();
                        } catch (OseeCoreException ex) {
                           OseeLog.log(Activator.class, Level.INFO, String.format(
                                 "Exception updating %s's attribute %d [/n%s/n].", artifact.getSafeName(),
                                 attribute.getId(), attribute.toString()), ex);
                        }
                     }
                     // Otherwise, attribute needs creation
                     // Process NEW attribute
                     else {
                        if (modificationType == null) {
                           OseeLog.log(Activator.class, Level.SEVERE, String.format(
                                 "MOD2: Can't get mod type for %s's attribute %d.", artifact.getArtifactTypeName(),
                                 attrChange.getAttributeId()));
                           continue;
                        }
                        artifact.internalInitializeAttribute(attributeType, attrChange.getAttributeId(),
                              attrChange.getGammaId(), modificationType, false, attrChange.getData().toArray(
                                    new Object[attrChange.getData().size()]));
                     }
                  } catch (OseeCoreException ex) {
                     OseeLog.log(Activator.class, Level.INFO, String.format(
                           "Exception updating %s's attribute change for attributeTypeId %d.", artifact.getSafeName(),
                           attributeType.getId()), ex);
                  }
               }
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public void deregisterForRemoteEvents() throws OseeCoreException {
      ResEventManager.getInstance().stop();
   }

   public void registerForRemoteEvents() throws OseeCoreException {
      if (OseeProperties.isNewEvents()) {
         ResEventManager.getInstance().start(this);
         OseeLog.log(Activator.class, Level.INFO, "REM2 Enabled");
      } else {
         OseeLog.log(Activator.class, Level.INFO, "REM2 Disabled");
      }
   }

   public boolean isConnected() {
      return OseeProperties.isNewEvents() && ResEventManager.getInstance().isConnected();
   }

   /**
    * InternalEventManager.enableRemoteEventLoopback will enable a testing loopback that will take the kicked remote
    * events and loop them back as if they came from an external client. It will allow for the testing of the OEM -> REM
    * -> OEM processing. In addition, this onEvent is put in a non-display thread which will test that all handling by
    * applications is properly handled by doing all processing and then kicking off display-thread when need to update
    * ui. SessionId needs to be modified so this client doesn't think the events came from itself.
    */
   public void kick(final RemoteEvent remoteEvent) {
      if (OseeProperties.isNewEvents() && isConnected()) {
         Job job =
               new Job(String.format("[%s] - sending [%s]", getClass().getSimpleName(),
                     remoteEvent.getClass().getSimpleName())) {
                  @Override
                  protected IStatus run(IProgressMonitor monitor) {
                     try {
                        ResEventManager.getInstance().kick(remoteEvent);
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                        return new Status(Status.ERROR, Activator.PLUGIN_ID, -1, ex.getLocalizedMessage(), ex);
                     }
                     return Status.OK_STATUS;
                  }
               };

         job.schedule();
      }

      if (InternalEventManager2.isEnableRemoteEventLoopback()) {
         OseeLog.log(Activator.class, Level.INFO, "REM2: Loopback enabled - Returning events as Remote event.");
         Thread thread = new Thread() {
            @Override
            public void run() {
               try {
                  String newSessionId = GUID.create();
                  remoteEvent.getNetworkSender().setSessionId(newSessionId);
                  instance.onEvent(remoteEvent);
               } catch (RemoteException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);

               }
            }
         };
         thread.start();
      }
   }

}
