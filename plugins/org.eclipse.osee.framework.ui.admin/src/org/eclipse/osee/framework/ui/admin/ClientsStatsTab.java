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
package org.eclipse.osee.framework.ui.admin;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class ClientsStatsTab {

   private Text reportText;

   public ClientsStatsTab(TabFolder tabFolder) throws OseeCoreException {
      super();
      createControl(tabFolder);
   }

   private void createControl(TabFolder tabFolder) throws OseeCoreException {
      Composite mainComposite = new Composite(tabFolder, SWT.NONE);
      mainComposite.setLayout(new GridLayout(2, false));
      mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      TabItem tab = new TabItem(tabFolder, SWT.NONE);
      tab.setControl(mainComposite);
      tab.setText("Client Stats");

      Button reportButton = new Button(mainComposite, SWT.PUSH);
      reportButton.setText("Ping");
      reportButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            refreshReport();
         }
      });
      Button clear = new Button(mainComposite, SWT.PUSH);
      clear.setText("clear");
      clear.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            reportText.setText("");
         }
      });
      reportText = new Text(mainComposite, SWT.WRAP | SWT.BORDER);
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.horizontalSpan = 2;
      reportText.setLayoutData(gd);
      refreshReport();

   }

   private void refreshReport() {
      try {
         reportText.append(ArtifactCache.report() + "\n");
      } catch (OseeCoreException ex) {
         OseeLog.log(AdminPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }

   }

}
