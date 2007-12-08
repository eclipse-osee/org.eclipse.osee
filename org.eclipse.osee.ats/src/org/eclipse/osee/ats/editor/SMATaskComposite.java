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
import org.eclipse.osee.ats.util.widgets.task.IXTaskViewer;
import org.eclipse.osee.ats.util.widgets.task.XTaskViewer;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class SMATaskComposite extends Composite {

   private static String HELP_CONTEXT_ID = "atsWorkflowEditorTaskTab";
   private XTaskViewer xTaskViewer;

   /**
    * @param parent
    * @param style
    */
   public SMATaskComposite(Composite parent, int style) {
      super(parent, style);
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

   public void create(IXTaskViewer iXTaskViewer) {
      setLayout(new GridLayout(1, true));
      setLayoutData(new GridData(GridData.FILL_BOTH));

      xTaskViewer = new XTaskViewer(iXTaskViewer);
      xTaskViewer.createWidgets(this, 1);
      // xTask.addXModifiedListener(xModListener);

      AtsPlugin.getInstance().setHelp(this, HELP_CONTEXT_ID);

      xTaskViewer.loadTable();
   }

   /**
    * @return the xTask
    */
   public XTaskViewer getXTask() {
      return xTaskViewer;
   }

}
