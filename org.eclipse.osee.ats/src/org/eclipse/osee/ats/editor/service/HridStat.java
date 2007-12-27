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
package org.eclipse.osee.ats.editor.service;

import java.sql.SQLException;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.ui.plugin.util.ALayout;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 * @author Donald G. Dunne
 */
public class HridStat extends WorkPageService {

   public HridStat(SMAManager smaMgr, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section) {
      super("", smaMgr, page, toolkit, section, ServicesArea.STATISTIC_CATEGORY, Location.CurrentState);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.statistic.WorkPageStatistic#create()
    */
   @Override
   public void create(Group workComp) {

      Composite comp = new Composite(workComp, SWT.NONE);
      comp.setLayout(ALayout.getZeroMarginLayout(2, false));
      comp.setLayoutData(new GridData());
      toolkit.adapt(comp);
      toolkit.createLabel(comp, smaMgr.getSma().getArtifactSuperTypeName() + ":");

      Text text = new Text(comp, SWT.NONE);
      toolkit.adapt(text, true, true);
      text.setText(smaMgr.getSma().getHumanReadableId());

      try {
         if (smaMgr.getSma().getParentActionArtifact() != null) {
            toolkit.createLabel(comp, "Action:");
            text = new Text(comp, SWT.NONE);
            toolkit.adapt(text, true, true);
            text.setText(smaMgr.getSma().getParentActionArtifact().getHumanReadableId());
         }
      } catch (SQLException ex) {
         // Do nothing
      }

   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.statistic.WorkPageStatistic#refresh()
    */
   @Override
   public void refresh() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#dispose()
    */
   @Override
   public void dispose() {
   }
}
