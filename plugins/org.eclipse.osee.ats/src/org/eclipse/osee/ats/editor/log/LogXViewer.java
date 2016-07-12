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
package org.eclipse.osee.ats.editor.log;

import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class LogXViewer extends XViewer {

   private final XLogViewer xLogViewer;
   private final AbstractWorkflowArtifact awa;

   public LogXViewer(AbstractWorkflowArtifact awa, Composite parent, int style, XLogViewer xLogViewer) {
      super(parent, style, new LogXViewerFactory());
      this.awa = awa;
      this.xLogViewer = xLogViewer;
   }

   @Override
   public void dispose() {
      // Dispose of the table objects is done through separate dispose listener off tree
      // Tell the label provider to release its resources
      getLabelProvider().dispose();
   }

   public void reload() {
      try {
         xLogViewer.getXViewer().setInput(awa.getLog().getLogItems());
         xLogViewer.refresh();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

}
