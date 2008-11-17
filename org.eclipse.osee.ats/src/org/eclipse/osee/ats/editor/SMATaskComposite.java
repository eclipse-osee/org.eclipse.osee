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

import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.task.IXTaskViewer;
import org.eclipse.osee.ats.task.TaskComposite;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

/**
 * @author Donald G. Dunne
 */
public class SMATaskComposite extends Composite {

   private static String HELP_CONTEXT_ID = "atsWorkflowEditorTaskTab";
   private final TaskComposite taskComposite;

   public SMATaskComposite(IXTaskViewer iXTaskViewer, Composite parent, int style, ToolBar toolBar) throws OseeCoreException {
      super(parent, style);

      setLayout(new GridLayout(1, true));
      setLayoutData(new GridData(GridData.FILL_BOTH));
      taskComposite = new TaskComposite(iXTaskViewer, this, style, toolBar);
      AtsPlugin.getInstance().setHelp(this, HELP_CONTEXT_ID);
      taskComposite.loadTable();
   }

   @Override
   public String toString() {
      try {
         return "SMATaskComposite for SMA \"" + taskComposite.getIXTaskViewer().getParentSmaMgr().getSma() + "\"";
      } catch (Exception ex) {
         return "SMATaskComposite " + ex.getLocalizedMessage();
      }
   }

   public void disposeTaskComposite() {
      taskComposite.dispose();
   }

   public String getHtml() {
      return taskComposite.toHTML(AHTML.LABEL_FONT);
   }

   /**
    * @return the xTask
    */
   public TaskComposite getTaskComposite() {
      return taskComposite;
   }

}
