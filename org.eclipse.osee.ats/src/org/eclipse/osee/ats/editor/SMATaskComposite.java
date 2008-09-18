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
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.widgets.task.IXTaskViewer;
import org.eclipse.osee.ats.util.widgets.task.TaskContentProvider;
import org.eclipse.osee.ats.util.widgets.task.XTaskViewer;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IArtifactsChangeTypeEventListener;
import org.eclipse.osee.framework.skynet.core.event.IArtifactsPurgedEventListener;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedArtifact;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class SMATaskComposite extends Composite implements IArtifactsPurgedEventListener, IArtifactsChangeTypeEventListener, IFrameworkTransactionEventListener {

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
      OseeEventManager.addListener(this, this);
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
      OseeEventManager.removeListeners(this);
      super.dispose();
   }

   public String getHtml() {
      return xTaskViewer.toHTML(AHTML.LABEL_FONT);
   }

   @Override
   public void handleArtifactsPurgedEvent(Sender sender, final Collection<? extends Artifact> cacheArtifacts, Collection<UnloadedArtifact> unloadedArtifacts) {
      if (cacheArtifacts.size() == 0) return;
      // ContentProvider ensures in display thread
      ((TaskContentProvider) xTaskViewer.getXViewer().getContentProvider()).remove(cacheArtifacts);
   }

   @Override
   public void handleArtifactsChangeTypeEvent(Sender sender, int toArtifactTypeId, final Collection<? extends Artifact> cacheArtifacts, Collection<UnloadedArtifact> unloadedArtifacts) {
      if (cacheArtifacts.size() == 0) return;
      // ContentProvider ensures in display thread
      ((TaskContentProvider) xTaskViewer.getXViewer().getContentProvider()).remove(cacheArtifacts);
   }

   @Override
   public void handleFrameworkTransactionEvent(Sender sender, final FrameworkTransactionData transData) {

      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {

            ((TaskContentProvider) xTaskViewer.getXViewer().getContentProvider()).remove(transData.cacheDeletedArtifacts);
            xTaskViewer.getXViewer().update(transData.cacheChangedArtifacts, null);

            try {
               Artifact parentSma = xTaskViewer.getIXTaskViewer().getParentSmaMgr().getSma();
               if (parentSma != null) {
                  // Add any new tasks related to parent sma
                  Collection<Artifact> artifacts =
                        transData.getRelatedArtifacts(parentSma.getArtId(),
                              AtsRelation.SmaToTask_Task.getRelationType().getRelationTypeId(),
                              AtsPlugin.getAtsBranch().getBranchId(), transData.cacheAddedRelations);
                  if (artifacts.size() > 0) {
                     ((TaskContentProvider) xTaskViewer.getXViewer().getContentProvider()).add(artifacts);
                  }

                  // Remove any tasks related to parent sma
                  artifacts =
                        transData.getRelatedArtifacts(parentSma.getArtId(),
                              AtsRelation.SmaToTask_Task.getRelationType().getRelationTypeId(),
                              AtsPlugin.getAtsBranch().getBranchId(), transData.cacheDeletedRelations);
                  if (artifacts.size() > 0) {
                     ((TaskContentProvider) xTaskViewer.getXViewer().getContentProvider()).remove(artifacts);
                  }
               }
            } catch (Exception ex) {
               OSEELog.logException(AtsPlugin.class, ex, false);
            }
         }
      });
   }

   /**
    * @return the xTask
    */
   public XTaskViewer getXTask() {
      return xTaskViewer;
   }

}
