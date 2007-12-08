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
package org.eclipse.osee.framework.ui.skynet.artifact.massEditor;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.SkynetDebug;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.LocalTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.event.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.TransactionEvent.EventData;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class MassArtifactItem implements IEventReceiver {

   private Artifact artifact;
   private final MassXViewer xViewer;
   protected List<MassArtifactItem> artifactList;
   protected MassArtifactItem parentItem;
   protected static final SkynetEventManager eventManager = SkynetEventManager.getInstance();
   private SkynetDebug debug = new SkynetDebug(false, "WorldArtifactItem");

   public MassArtifactItem(MassXViewer xViewer, Artifact artifact, MassArtifactItem parentItem) {
      this.xViewer = xViewer;
      this.artifact = artifact;
      this.parentItem = parentItem;
      artifactList = new ArrayList<MassArtifactItem>();
      if (artifact != null) {
         eventManager.register(RemoteTransactionEvent.class, this);
         eventManager.register(LocalTransactionEvent.class, this);
      }
   }

   public Artifact getArtifact() {
      return artifact;
   }

   public List<MassArtifactItem> getArtifactItems() {
      return artifactList;
   }

   public MassArtifactItem getParentItem() {
      return parentItem;
   }

   protected void addArtifactItem(MassArtifactItem artifactItem) {
      artifactList.add(artifactItem);
      artifactItem.parentItem = this;
   }

   protected void removeArtifactItem(MassArtifactItem artifactItem) {
      artifactList.remove(artifactItem);
      fireRemove(artifactItem);
   }

   protected void fireRemove(Object removed) {
      eventManager.unRegisterAll(this);
   }

   @SuppressWarnings("unchecked")
   public Object[] getChildren() {
      debug.report("getChildren()");
      return null;
   }

   public void dispose() {
      eventManager.unRegisterAll(this);
   }

   public void onEvent(final Event event) {
      if (artifact.isDeleted() || xViewer.getTree().isDisposed()) {
         if (!xViewer.getTree().isDisposed()) xViewer.remove(this);
         dispose();
         return;
      }
      final MassArtifactItem wai = this;

      if (event instanceof TransactionEvent) {
         EventData ed = ((TransactionEvent) event).getEventData(artifact);
         if (ed.isRemoved()) {
            xViewer.remove(wai);
         } else if (ed.getAvie() != null && ed.getAvie().getOldVersion().equals(artifact)) {
            artifact = (Artifact) ed.getAvie().getNewVersion();
            xViewer.update(wai, null);
         } else if (ed.isModified() || ed.isRelChange()) {
            xViewer.update(wai, null);
         }
      } else
         OSEELog.logSevere(SkynetGuiPlugin.class, "Unexpected event => " + event, true);
   }

   public boolean runOnEventInDisplayThread() {
      return true;
   }

}
