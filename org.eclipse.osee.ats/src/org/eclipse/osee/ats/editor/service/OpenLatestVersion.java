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
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.ats.AtsOpenOption;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class OpenLatestVersion extends WorkPageService {

   private final SMAManager smaMgr;
   private Hyperlink link;

   public OpenLatestVersion(SMAManager smaMgr, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section) {
      super("Open Latest", smaMgr, page, toolkit, section, ServicesArea.OPERATION_CATEGORY, Location.Global);
      this.smaMgr = smaMgr;
   }

   @Override
   public void create(Group workComp) {
      if (smaMgr.getSma().getPersistenceMemo().getTransactionId().isHead()) return;
      link = toolkit.createHyperlink(workComp, name, SWT.NONE);
      link.addHyperlinkListener(new IHyperlinkListener() {

         public void linkEntered(HyperlinkEvent e) {
         }

         public void linkExited(HyperlinkEvent e) {
         }

         public void linkActivated(HyperlinkEvent e) {
            try {
               Artifact art = ArtifactPersistenceManager.getInstance().getArtifact(smaMgr.getSma().getGuid(),
                     smaMgr.getSma().getBranch());
               if (art == null) {
                  AWorkbench.popup("ERROR", "Can't retrieve latest version of artifact; may have been deleted");
                  return;
               }
               AtsLib.openAtsAction(art, AtsOpenOption.OpenOneOrPopupSelect);
            } catch (SQLException ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         }
      });
      refresh();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.operation.WorkPageService#refresh()
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
