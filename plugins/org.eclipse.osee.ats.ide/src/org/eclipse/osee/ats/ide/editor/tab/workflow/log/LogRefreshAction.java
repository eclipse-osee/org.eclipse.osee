/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
