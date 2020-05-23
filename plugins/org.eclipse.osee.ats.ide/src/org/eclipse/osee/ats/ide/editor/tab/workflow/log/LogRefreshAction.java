/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.ide.editor.tab.workflow.log;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;

/**
 * @author Donald G. Dunne
 */
public final class LogRefreshAction extends Action {

   private final XLogViewer xLogViewer;

   public LogRefreshAction(String text, int style, XLogViewer xLogViewer) {
      super(text, style);
      this.xLogViewer = xLogViewer;
      setImageDescriptor(ImageManager.getImageDescriptor(PluginUiImage.REFRESH));
      setToolTipText("Refresh Log");
   }

   @Override
   public void run() {
      if (Widgets.isAccessible(xLogViewer.getXViewer().getTree())) {
         xLogViewer.getXViewer().reload();
      }
   }
}
