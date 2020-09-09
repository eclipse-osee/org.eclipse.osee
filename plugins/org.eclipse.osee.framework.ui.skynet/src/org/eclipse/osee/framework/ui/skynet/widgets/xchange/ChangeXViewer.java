/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.widgets.xchange;

import java.util.ArrayList;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.skynet.ArtifactDoubleClick;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.change.ViewContextChangeReportHandler;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.change.ViewWordChangeReportHandler;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
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

   public ChangeXViewer(Composite parent, int style, IXViewerFactory factory) {
      super(parent, style, factory);
      getTree().addKeyListener(new KeySelectedListener());
   }

   @Override
   public void handleDoubleClick() {
      if (getSelectedChanges().isEmpty()) {
         return;
      }
      Change change = getSelectedChanges().iterator().next();
      Artifact artifact = ((IAdaptable) change).getAdapter(Artifact.class);
      ArtifactDoubleClick.open(artifact);
   }

   private class KeySelectedListener implements KeyListener {
      @Override
      public void keyPressed(KeyEvent e) {
         // do nothing
      }

      @Override
      public void keyReleased(KeyEvent e) {
         if (e.keyCode == SWT.F5) {
            ViewWordChangeReportHandler handler = new ViewWordChangeReportHandler();
            if (handler.isEnabled()) {
               try {
                  handler.execute(null);
               } catch (ExecutionException ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         } else if (e.keyCode == SWT.F6) {
            ViewContextChangeReportHandler handler = new ViewContextChangeReportHandler();
            if (handler.isEnabled()) {
               try {
                  handler.execute(null);
               } catch (ExecutionException ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
      }
   }

   private ArrayList<Change> getSelectedChanges() {
      ArrayList<Change> arts = new ArrayList<>();
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
   public boolean isRemoveItemsMenuOptionEnabled() {
      return false;
   }
}