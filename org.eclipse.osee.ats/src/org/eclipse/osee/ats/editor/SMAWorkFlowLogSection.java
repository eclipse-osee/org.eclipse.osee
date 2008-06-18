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
import org.eclipse.osee.ats.editor.stateItem.AtsLogWorkPage;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultsComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Donald G. Dunne
 */
public class SMAWorkFlowLogSection extends SMAWorkFlowSection {

   private Composite workComp;
   private XFormToolkit toolkit;
   private XResultsComposite xResultsComp;
   private String title = "";

   /**
    * @param parent
    * @param toolkit
    * @param style
    * @param page
    * @param smaMgr
    * @throws Exception
    */
   public SMAWorkFlowLogSection(Composite parent, XFormToolkit toolkit, int style, SMAManager smaMgr)throws OseeCoreException, SQLException{
      super(parent, toolkit, style, new AtsLogWorkPage(
            smaMgr.getSma().getArtifactTypeName() + " History"), smaMgr);
   }

   @Override
   protected Composite createWorkArea(Composite comp, AtsWorkPage page, XFormToolkit toolkit)throws OseeCoreException, SQLException{
      this.toolkit = toolkit;
      workComp = super.createWorkArea(comp, page, toolkit);

      xResultsComp = new XResultsComposite(workComp, SWT.BORDER);
      xResultsComp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));
      GridData gd = new GridData(GridData.FILL_BOTH);
      gd.heightHint = 500;
      xResultsComp.setLayoutData(gd);
      xResultsComp.setHtmlText(smaMgr.getSma().getLog().getHtml(false), title);

      return workComp;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.SMAWorkFlowSection#createPage(org.eclipse.swt.widgets.Composite)
    */
   @Override
   protected Section createPage(Composite comp)throws OseeCoreException, SQLException{
      Section section = super.createPage(comp);
      return section;
   }

   public void addDebug(String str) {
      toolkit.createText(workComp, str, SWT.MULTI | SWT.WRAP);
      workComp.layout();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.SMAWorkFlowSection#refresh()
    */
   @Override
   public void refresh() {
      super.refresh();
      xResultsComp.setHtmlText(smaMgr.getSma().getLog().getHtml(false), title);
   }

}
