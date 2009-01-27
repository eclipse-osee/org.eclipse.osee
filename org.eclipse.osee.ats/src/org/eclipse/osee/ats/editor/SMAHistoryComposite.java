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

import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultsComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

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
   public SMAHistoryComposite(SMAManager smaMgr, Composite parent, int style) throws OseeCoreException {
      super(parent, style);
      this.smaMgr = smaMgr;

      xResultsComp = new XResultsComposite(this, SWT.BORDER);
      xResultsComp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));
      GridData gd = new GridData(GridData.FILL_BOTH);
      gd.heightHint = 500;
      xResultsComp.setLayoutData(gd);
      xResultsComp.setHtmlText(smaMgr.getLog().getHtml(true), smaMgr.getSma().getArtifactTypeName() + " History");

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

   public void refresh() throws OseeCoreException {
      if (xResultsComp != null && !xResultsComp.isDisposed()) {
         xResultsComp.setHtmlText(smaMgr.getLog().getHtml(true), smaMgr.getSma().getArtifactTypeName() + " History");
      }
   }

}
