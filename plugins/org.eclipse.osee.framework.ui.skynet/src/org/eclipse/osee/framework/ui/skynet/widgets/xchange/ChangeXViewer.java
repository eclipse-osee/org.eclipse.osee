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
package org.eclipse.osee.framework.ui.skynet.widgets.xchange;

import java.util.ArrayList;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerTextFilter;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.change.ViewWordChangeReportHandler;
import org.eclipse.osee.framework.ui.skynet.preferences.EditorsPreferencePage;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 * @author Jeff C. Phillips
 */
public class ChangeXViewer extends XViewer {

   private XChangeTextFilter xChangeTextFilter;

   public ChangeXViewer(Composite parent, int style, IXViewerFactory factory) {
      super(parent, style, factory);
      getTree().addKeyListener(new KeySelectedListener());
   }

   @Override
   public void handleDoubleClick() {
      try {
         if (getSelectedChanges().isEmpty()) {
            return;
         }

         Change change = getSelectedChanges().iterator().next();
         Artifact artifact = (Artifact) ((IAdaptable) change).getAdapter(Artifact.class);

         if (artifact != null) {
            ArrayList<Artifact> artifacts = new ArrayList<Artifact>(1);
            artifacts.add(artifact);

            if (EditorsPreferencePage.isPreviewOnDoubleClickForWordArtifacts()) {
               RendererManager.previewInJob(artifacts);
            } else {
               RendererManager.openInJob(artifacts, PresentationType.GENERALIZED_EDIT);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private class KeySelectedListener implements KeyListener {
      public void keyPressed(KeyEvent e) {
      }

      public void keyReleased(KeyEvent e) {
         if (e.keyCode == SWT.F5) {
            System.out.println("pressed");
            ViewWordChangeReportHandler handler = (new ViewWordChangeReportHandler());
            if (handler.isEnabled()) {
               handler.execute(null);
            }
         }
      }
   }

   public ArrayList<Change> getSelectedChanges() {
      ArrayList<Change> arts = new ArrayList<Change>();
      TreeItem items[] = getTree().getSelection();

      if (items.length > 0) {
         for (TreeItem item : items) {
            arts.add((Change) item.getData());
         }
      }
      return arts;
   }

   @Override
   public void dispose() {
      getLabelProvider().dispose();
   }

   @Override
   public String getStatusString() {
      if (isShowDocumentOrderFilter()) {
         return "[Show Document Order]";
      }
      return "";
   }

   @Override
   public XViewerTextFilter getXViewerTextFilter() {
      if (xChangeTextFilter == null) {
         xChangeTextFilter = new XChangeTextFilter(this);
      }
      return xChangeTextFilter;
   }

   public boolean isShowDocumentOrderFilter() {
      if (xChangeTextFilter == null) {
         xChangeTextFilter = new XChangeTextFilter(this);
      }
      return xChangeTextFilter.isShowDocumentOrderFilter();
   }

   public void setShowDocumentOrderFilter(boolean showDocumentOrderFilter) {
      if (xChangeTextFilter == null) {
         xChangeTextFilter = new XChangeTextFilter(this);
      }
      xChangeTextFilter.setShowDocumentOrderFilter(showDocumentOrderFilter);
   }

   public boolean isRemoveItemsMenuOptionEnabled() {
      return false;
   }

}
