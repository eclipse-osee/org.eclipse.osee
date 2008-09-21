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
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.util.widgets.task.IXTaskViewer;
import org.eclipse.osee.ats.util.widgets.task.XTaskViewer;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
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
   }

   @Override
   public String toString() {
      try {
         return "SMATaskComposite for SMA \"" + xTaskViewer.getIXTaskViewer().getParentSmaMgr().getSma() + "\"";
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

}
