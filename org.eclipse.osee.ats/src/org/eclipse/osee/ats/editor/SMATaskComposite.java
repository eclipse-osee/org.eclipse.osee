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
package org.eclipse.osee.ats.editor;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.widgets.task.IXTaskViewer;
import org.eclipse.osee.ats.util.widgets.task.XTaskViewer;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.LocalTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.event.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.eventx.IArtifactsChangeTypeEventListener;
import org.eclipse.osee.framework.skynet.core.eventx.IArtifactsPurgedEventListener;
import org.eclipse.osee.framework.skynet.core.eventx.XEventManager;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.relation.RelationModifiedEvent;
import org.eclipse.osee.framework.skynet.core.relation.RelationModifiedEvent.RelationModType;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.plugin.event.Sender;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedArtifact;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class SMATaskComposite extends Composite implements IEventReceiver {

   private static String HELP_CONTEXT_ID = "atsWorkflowEditorTaskTab";
   private final XTaskViewer xTaskViewer;
   private final IXTaskViewer iXTaskViewer;

   /**
    * @param parent
    * @param style
    */
   public SMATaskComposite(IXTaskViewer iXTaskViewer, Composite parent, int style) throws OseeCoreException, SQLException {
      super(parent, style);
      this.iXTaskViewer = iXTaskViewer;
      setLayout(new GridLayout(1, true));
      setLayoutData(new GridData(GridData.FILL_BOTH));

      xTaskViewer = new XTaskViewer(iXTaskViewer);
      xTaskViewer.createWidgets(this, 1);
      // xTask.addXModifiedListener(xModListener);

      AtsPlugin.getInstance().setHelp(this, HELP_CONTEXT_ID);

      xTaskViewer.loadTable();

      SkynetEventManager.getInstance().register(RemoteTransactionEvent.class, this);
      SkynetEventManager.getInstance().register(LocalTransactionEvent.class, this);

      registerEvents();
   }

   private void registerEvents() {
      XEventManager.addListener(this, new IArtifactsPurgedEventListener() {

         @Override
         public void handleArtifactsPurgedEvent(Sender sender, Collection<? extends Artifact> cacheArtifacts, Collection<UnloadedArtifact> unloadedArtifacts) {
            xTaskViewer.getXViewer().remove(cacheArtifacts.toArray());
         }
      });
      XEventManager.addListener(this, new IArtifactsChangeTypeEventListener() {

         @Override
         public void handleArtifactsChangeTypeEvent(Sender sender, int toArtifactTypeId, Collection<? extends Artifact> cacheArtifacts, Collection<UnloadedArtifact> unloadedArtifacts) {
            xTaskViewer.getXViewer().remove(cacheArtifacts.toArray());
         }

      });
   }

   /* (non-Javadoc)
    * @see org.eclipse.swt.widgets.Widget#dispose()
    */
   @Override
   public void dispose() {
      xTaskViewer.dispose();
      SkynetEventManager.getInstance().unRegisterAll(this);
      XEventManager.removeListeners(this);
      super.dispose();
   }

   public String getHtml() {
      return xTaskViewer.toHTML(AHTML.LABEL_FONT);
   }

   /**
    * @return the xTask
    */
   public XTaskViewer getXTask() {
      return xTaskViewer;
   }

   public void onEvent(final Event event) {
      try {
         if (xTaskViewer == null || xTaskViewer.getXViewer().getTree().isDisposed()) return;

         Set<Artifact> addTasks = new HashSet<Artifact>();
         Set<Artifact> removeTasks = new HashSet<Artifact>();
         Set<Artifact> updateTasks = new HashSet<Artifact>();
         if (event instanceof TransactionEvent) {
            for (Event localEvent : ((TransactionEvent) event).getLocalEvents()) {
               if (localEvent instanceof ArtifactModifiedEvent) {
                  Artifact artifact = ((ArtifactModifiedEvent) localEvent).getArtifact();
                  if (artifact instanceof TaskArtifact) {
                     // Since removes, purge and updates affect all composites that contain the object, send in all
                     // the objects and let the viewer decided if the objects are in the composite
                     if (((ArtifactModifiedEvent) localEvent).getType() == ArtifactModifiedEvent.ArtifactModType.Deleted) {
                        removeTasks.add(artifact);
                     } else {
                        updateTasks.add(artifact);
                     }
                  }
               } else if (localEvent instanceof RelationModifiedEvent) {
                  // Make sure this is a relation that applies to this task composite
                  RelationModType modType = iXTaskViewer.getRelationChangeAction((RelationModifiedEvent) localEvent);
                  if (modType == null) continue;
                  Artifact taskArt =
                        ((RelationModifiedEvent) localEvent).getLink().getArtifact(AtsRelation.SmaToTask_Task.getSide());
                  if (taskArt instanceof TaskArtifact) {
                     if (modType == RelationModType.Added) {
                        addTasks.add(taskArt);
                     } else if (modType == RelationModType.Deleted) {
                        removeTasks.add(taskArt);
                     }
                  }
               }
            }
            if (addTasks.size() > 0) {
               xTaskViewer.getXViewer().add(addTasks);
               //               System.out.println("Adding to \"" + xTaskViewer.getIXTaskViewer().getParentSmaMgr().getSma().getDescriptiveName() + "\" tasks " + addTasks);
            }
            if (removeTasks.size() > 0) {
               xTaskViewer.getXViewer().remove(removeTasks);
               //               System.out.println("Removing to \"" + xTaskViewer.getIXTaskViewer().getParentSmaMgr().getSma().getDescriptiveName() + "\" tasks " + removeTasks);
            }
            if (updateTasks.size() > 0) {
               xTaskViewer.getXViewer().update(updateTasks, null);
               //               System.out.println("Updating to \"" + xTaskViewer.getIXTaskViewer().getParentSmaMgr().getSma().getDescriptiveName() + "\" tasks " + updateTasks);
            }
         } else
            OSEELog.logException(AtsPlugin.class, "Unexpected event => " + event, null, false);
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jdk.core.event.IEventReceiver#runOnEventInDisplayThread()
    */
   public boolean runOnEventInDisplayThread() {
      return true;
   }

}
