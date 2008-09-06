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
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.history.RevisionHistoryView;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultsComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author Donald G. Dunne
 */
public class SMAHistoryComposite extends Composite {

   private final XResultsComposite xResultsComp;
   private final SMAManager smaMgr;

   /**
    * @param parent
    * @param style
    */
   public SMAHistoryComposite(SMAManager smaMgr, Composite parent, int style) throws OseeCoreException, SQLException {
      super(parent, style);
      this.smaMgr = smaMgr;
      createTaskActionBar();

      xResultsComp = new XResultsComposite(this, SWT.BORDER);
      xResultsComp.setLayoutData(new GridData(
            GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));
      GridData gd = new GridData(GridData.FILL_BOTH);
      gd.heightHint = 500;
      xResultsComp.setLayoutData(gd);
      xResultsComp.setHtmlText(smaMgr.getLog().getHtml(true),
            smaMgr.getSma().getArtifactTypeName() + " History");

      Label button = new Label(this, SWT.NONE);
      button.setText("    ");
      final SMAManager fSmaMgr = smaMgr;
      button.addListener(SWT.MouseUp, new Listener() {
         /* (non-Javadoc)
          * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
          */
         @Override
         public void handleEvent(Event event) {
            ArtifactEditor.editArtifact(fSmaMgr.getSma());
         }
      });
   }

   public void refresh() throws OseeCoreException, SQLException {
      if (xResultsComp != null && !xResultsComp.isDisposed()) {
         xResultsComp.setHtmlText(smaMgr.getLog().getHtml(true),
               smaMgr.getSma().getArtifactTypeName() + " History");
      }
   }

   public void createTaskActionBar() throws OseeCoreException, SQLException {

      // Button composite for state transitions, etc
      Composite bComp = new Composite(this, SWT.NONE);
      // bComp.setBackground(mainSComp.getDisplay().getSystemColor(SWT.COLOR_CYAN));
      bComp.setLayout(new GridLayout(2, false));
      bComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      Composite leftComp = new Composite(bComp, SWT.NONE);
      leftComp.setLayout(new GridLayout());
      leftComp.setLayoutData(new GridData(GridData.BEGINNING | GridData.FILL_HORIZONTAL));

      Composite rightComp = new Composite(bComp, SWT.NONE);
      rightComp.setLayout(new GridLayout());
      rightComp.setLayoutData(new GridData(GridData.END));

      ToolBar toolBar = new ToolBar(rightComp, SWT.FLAT | SWT.RIGHT);
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      toolBar.setLayoutData(gd);

      ToolItem item = new ToolItem(toolBar, SWT.CHECK);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("edit.gif"));
      item.setToolTipText("View Full History");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            RevisionHistoryView.open(smaMgr.getSma());
         }
      });
   }

}
