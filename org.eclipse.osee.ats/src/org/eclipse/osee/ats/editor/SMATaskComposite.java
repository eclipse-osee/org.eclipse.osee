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
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.widgets.task.IXTaskViewer;
import org.eclipse.osee.ats.util.widgets.task.TaskContentProvider;
import org.eclipse.osee.ats.util.widgets.task.XTaskViewer;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.eventx.FrameworkTransactionEvent;
import org.eclipse.osee.framework.skynet.core.eventx.IArtifactsChangeTypeEventListener;
import org.eclipse.osee.framework.skynet.core.eventx.IArtifactsPurgedEventListener;
import org.eclipse.osee.framework.skynet.core.eventx.LoadedRelation;
import org.eclipse.osee.framework.skynet.core.eventx.XEventManager;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.event.Sender;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedArtifact;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedRelation;
import org.eclipse.osee.framework.ui.plugin.event.Sender.Source;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class SMATaskComposite extends Composite {

   private static String HELP_CONTEXT_ID = "atsWorkflowEditorTaskTab";
   private final XTaskViewer xTaskViewer;

   /**
    * @param parent
    * @param style
    */
   public SMATaskComposite(IXTaskViewer iXTaskViewer, Composite parent, int style) throws OseeCoreException, SQLException {
      super(parent, style);
      setLayout(new GridLayout(1, true));
      setLayoutData(new GridData(GridData.FILL_BOTH));

      xTaskViewer = new XTaskViewer(iXTaskViewer);
      xTaskViewer.createWidgets(this, 1);
      // xTask.addXModifiedListener(xModListener);

      AtsPlugin.getInstance().setHelp(this, HELP_CONTEXT_ID);

      xTaskViewer.loadTable();

      registerEvents();
   }

   @Override
   public String toString() {
      try {
         return "SMATaskComposite: " + xTaskViewer.getIXTaskViewer().getParentSmaMgr().getSma();
      } catch (Exception ex) {
         return "SMATaskComposite " + ex.getLocalizedMessage();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.swt.widgets.Widget#dispose()
    */
   @Override
   public void dispose() {
      xTaskViewer.dispose();
      XEventManager.removeListeners(this);
      super.dispose();
   }

   public String getHtml() {
      return xTaskViewer.toHTML(AHTML.LABEL_FONT);
   }

   private void registerEvents() {
      XEventManager.addListener(this, new IArtifactsPurgedEventListener() {

         @Override
         public void handleArtifactsPurgedEvent(Sender sender, final Collection<? extends Artifact> cacheArtifacts, Collection<UnloadedArtifact> unloadedArtifacts) {
            if (cacheArtifacts.size() == 0) return;
            // ContentProvider ensures in display thread
            ((TaskContentProvider) xTaskViewer.getXViewer().getContentProvider()).remove(cacheArtifacts);
         }
      });
      XEventManager.addListener(this, new IArtifactsChangeTypeEventListener() {

         @Override
         public void handleArtifactsChangeTypeEvent(Sender sender, int toArtifactTypeId, final Collection<? extends Artifact> cacheArtifacts, Collection<UnloadedArtifact> unloadedArtifacts) {
            if (cacheArtifacts.size() == 0) return;
            // ContentProvider ensures in display thread
            ((TaskContentProvider) xTaskViewer.getXViewer().getContentProvider()).remove(cacheArtifacts);
         }

      });
      XEventManager.addListener(this, new FrameworkTransactionEvent() {

         /* (non-Javadoc)
          * @see org.eclipse.osee.framework.skynet.core.eventx.IFrameworkTransactionEvent#handleArtifactsChanged(org.eclipse.osee.framework.ui.plugin.event.Sender.Source, java.util.Collection, java.util.Collection)
          */
         @Override
         public void handleArtifactsChanged(Source source, final Collection<? extends Artifact> cacheArtifacts, Collection<UnloadedArtifact> unloadedArtifacts) {
            if (cacheArtifacts.size() == 0) return;
            Displays.ensureInDisplayThread(new Runnable() {
               /* (non-Javadoc)
                * @see java.lang.Runnable#run()
                */
               @Override
               public void run() {
                  xTaskViewer.getXViewer().update(cacheArtifacts, null);
               }
            });
         }

         /* (non-Javadoc)
          * @see org.eclipse.osee.framework.skynet.core.eventx.IFrameworkTransactionEvent#handleArtifactsDeleted(org.eclipse.osee.framework.ui.plugin.event.Sender.Source, java.util.Collection, java.util.Collection)
          */
         @Override
         public void handleArtifactsDeleted(Source source, final Collection<? extends Artifact> cacheArtifacts, Collection<UnloadedArtifact> unloadedArtifacts) {
            if (cacheArtifacts.size() == 0) return;
            // ContentProvider ensures in display thread
            ((TaskContentProvider) xTaskViewer.getXViewer().getContentProvider()).remove(cacheArtifacts);
         }

         /* (non-Javadoc)
          * @see org.eclipse.osee.framework.skynet.core.eventx.IFrameworkTransactionEvent#handleRelationsAdded(org.eclipse.osee.framework.ui.plugin.event.Sender.Source, java.util.Collection, java.util.Collection, java.util.Collection)
          */
         @Override
         public void handleRelationsAdded(Source source, Collection<? extends Artifact> cacheArtifacts, Collection<LoadedRelation> cacheRelations, Collection<UnloadedRelation> unloadedRelation) {
            final Collection<Artifact> artifacts = getRelatedTasks(cacheRelations);
            if (artifacts.size() == 0) return;
            // ContentProvider ensures in display thread
            ((TaskContentProvider) xTaskViewer.getXViewer().getContentProvider()).add(artifacts);
         }

         /* (non-Javadoc)
          * @see org.eclipse.osee.framework.skynet.core.eventx.IFrameworkTransactionEvent#handleRelationsDeleted(org.eclipse.osee.framework.ui.plugin.event.Sender.Source, java.util.Collection, java.util.Collection, java.util.Collection)
          */
         @Override
         public void handleRelationsDeleted(Source source, Collection<? extends Artifact> cacheArtifacts, Collection<LoadedRelation> cacheRelations, Collection<UnloadedRelation> unloadedRelation) {
            final Collection<Artifact> artifacts = getRelatedTasks(cacheRelations);
            if (artifacts.size() == 0) return;
            // ContentProvider ensures in display thread
            ((TaskContentProvider) xTaskViewer.getXViewer().getContentProvider()).remove(artifacts);
         }

      });
   }

   /**
    * Return tasks related to this artifact from event service cacheRelations
    * 
    * @param cacheRelations
    * @return
    */
   private Collection<Artifact> getRelatedTasks(Collection<LoadedRelation> cacheRelations) {
      Set<Artifact> artifacts = new HashSet<Artifact>();
      try {
         Set<Integer> artifactIds = new HashSet<Integer>();
         for (LoadedRelation loadedRelation : cacheRelations) {
            try {
               if (loadedRelation.getArtifactA() != null && loadedRelation.getArtifactA().equals(
                     xTaskViewer.getIXTaskViewer().getParentSmaMgr().getSma())) {
                  if (loadedRelation.getRelationType().equals(AtsRelation.SmaToTask_Task.getRelationType())) {
                     if (loadedRelation.getArtifactB() != null) {
                        artifacts.add(loadedRelation.getArtifactB());
                     } else {
                        artifactIds.add(loadedRelation.getUnloadedRelation().getArtifactBId());
                     }
                  }
               }
            } catch (Exception ex) {
               // do nothing
            }
         }
         if (artifactIds.size() > 0) {
            artifacts.addAll(ArtifactQuery.getArtifactsFromIds(artifactIds, AtsPlugin.getAtsBranch(), false));
         }
      } catch (Exception ex) {
         // do nothing
      }
      return artifacts;
   }

   /**
    * @return the xTask
    */
   public XTaskViewer getXTask() {
      return xTaskViewer;
   }

}
