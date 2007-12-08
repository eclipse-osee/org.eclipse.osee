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

import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.forms.events.IHyperlinkListener;

/**
 * @author Donald G. Dunne
 */
public abstract class WorkPageService {

   protected final AtsWorkPage page;
   protected final XFormToolkit toolkit;
   protected final String name;
   protected final SMAManager smaMgr;
   protected final SMAWorkFlowSection section;
   // Global - Shows at the first state of the workflow. Used for "global" services (eg: HRID)
   // AllState - Show for each state (eg: Admin flag)
   // AllNonCompleteState - Shown for each state exception Completed/Cancelled (eg: Hours Spent)
   // CurrentState - Shown only on current state
   // NonCurrentState - Shown only on current state except Completed/Cancelled (eg: State Percent
   // Complete)
   // SpecifiedPageId - Created if isSpecifiedPageId() returns true
   public static enum Location {
      Global, AllState, AllNonCompleteState, CurrentState, NonCompleteCurrentState, SpecifiedPageId
   };
   protected final String serviceType;
   protected final Location location;

   /**
    * @param category TODO
    * @param location TODO
    */
   public WorkPageService(String name, SMAManager smaMgr, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section, String category, Location location) {
      super();
      this.name = name;
      this.smaMgr = smaMgr;
      this.page = page;
      this.toolkit = toolkit;
      this.section = section;
      this.serviceType = category;
      this.location = location;
   }

   public boolean isSpecifiedPageId(String pageId) {
      return false;
   }

   public abstract void create(Group workGroup);

   public boolean displayService() {
      return true;
   }

   public abstract void dispose();

   public abstract void refresh();

   /**
    * @return Returns the location.
    */
   public Location getLocation() {
      return location;
   }

   /**
    * @return Returns the serviceType.
    */
   public String getCategory() {
      return serviceType;
   }

   protected IHyperlinkListener readOnlyHyperlinkListener = new IHyperlinkListener() {
      public void linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent e) {
         if (smaMgr.isHistoricalVersion())
            AWorkbench.popup(
                  "Historical Error",
                  "You can not change a historical version of " + smaMgr.getSma().getArtifactTypeName() + ":\n\n" + smaMgr.getSma());

         else
            AWorkbench.popup(
                  "Authentication Error",
                  "You do not have permissions to edit " + smaMgr.getSma().getArtifactTypeName() + ":" + smaMgr.getSma());
      };

      public void linkEntered(org.eclipse.ui.forms.events.HyperlinkEvent e) {
      };

      public void linkExited(org.eclipse.ui.forms.events.HyperlinkEvent e) {
      };
   };
}
