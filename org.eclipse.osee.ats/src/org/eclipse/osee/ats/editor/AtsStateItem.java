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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.editor.service.WorkPageService;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public abstract class AtsStateItem implements IAtsStateItem {

   public static String ALL_STATE_IDS = "ALL";
   private static final ArrayList<WorkPageService> EMPTY_SERVICES = new ArrayList<WorkPageService>();
   private static final Collection<String> EMPTY_STRING = new ArrayList<String>();

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#committing(org.eclipse.osee.ats.editor.SMAManager)
    */
   public Result committing(SMAManager smaMgr) throws OseeCoreException, SQLException {
      return Result.TrueResult;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#getBranchShortName(org.eclipse.osee.ats.editor.SMAManager)
    */
   public String getBranchShortName(SMAManager smaMgr) throws OseeCoreException, SQLException {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#getId()
    */
   protected String getId() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#getIds()
    */
   public Collection<String> getIds() throws OseeCoreException, SQLException {
      if (getId() == null) return EMPTY_STRING;
      return Arrays.asList(getId());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#getOverrideTransitionToAssignees(org.eclipse.osee.ats.editor.SMAWorkFlowSection)
    */
   public Collection<User> getOverrideTransitionToAssignees(SMAWorkFlowSection section) throws OseeCoreException, SQLException {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#getOverrideTransitionToStateName(org.eclipse.osee.ats.editor.SMAWorkFlowSection)
    */
   public String getOverrideTransitionToStateName(SMAWorkFlowSection section) throws OseeCoreException, SQLException {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#getSidebarServices(org.eclipse.osee.ats.editor.SMAManager, org.eclipse.osee.ats.workflow.AtsWorkPage, org.eclipse.osee.framework.ui.skynet.XFormToolkit, org.eclipse.osee.ats.editor.SMAWorkFlowSection)
    */
   public List<WorkPageService> getSidebarServices(SMAManager smaMgr) throws OseeCoreException, SQLException {
      return EMPTY_SERVICES;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#getToolbarServices(org.eclipse.osee.ats.editor.SMAManager, org.eclipse.osee.ats.workflow.AtsWorkPage, org.eclipse.osee.framework.ui.skynet.XFormToolkit, org.eclipse.osee.ats.editor.SMAWorkFlowSection)
    */
   public List<WorkPageService> getToolbarServices(SMAManager smaMgr) throws OseeCoreException, SQLException {
      return EMPTY_SERVICES;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#pageCreated(org.eclipse.ui.forms.widgets.FormToolkit, org.eclipse.osee.ats.workflow.AtsWorkPage, org.eclipse.osee.ats.editor.SMAManager, org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener, boolean)
    */
   public Result pageCreated(FormToolkit toolkit, AtsWorkPage page, SMAManager smaMgr, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException, SQLException {
      return Result.TrueResult;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#transitioned(org.eclipse.osee.ats.editor.SMAManager, java.lang.String, java.lang.String, java.util.Collection)
    */
   public void transitioned(SMAManager smaMgr, String fromState, String toState, Collection<User> toAssignees) throws OseeCoreException, SQLException {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#transitioning(org.eclipse.osee.ats.editor.SMAManager, java.lang.String, java.lang.String, java.util.Collection)
    */
   public Result transitioning(SMAManager smaMgr, String fromState, String toState, Collection<User> toAssignees) throws OseeCoreException, SQLException {
      return Result.TrueResult;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#widgetModified(org.eclipse.osee.ats.editor.SMAWorkFlowSection, org.eclipse.osee.framework.ui.skynet.widgets.XWidget)
    */
   public void widgetModified(SMAWorkFlowSection section, XWidget xWidget) throws OseeCoreException, SQLException {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#xWidgetCreated(org.eclipse.osee.framework.ui.skynet.widgets.XWidget, org.eclipse.ui.forms.widgets.FormToolkit, org.eclipse.osee.ats.workflow.AtsWorkPage, org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener, boolean)
    */
   public void xWidgetCreated(XWidget xWidget, FormToolkit toolkit, AtsWorkPage page, Artifact art, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException, SQLException {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#xWidgetCreating(org.eclipse.osee.framework.ui.skynet.widgets.XWidget, org.eclipse.ui.forms.widgets.FormToolkit, org.eclipse.osee.ats.workflow.AtsWorkPage, org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener, boolean)
    */
   public Result xWidgetCreating(XWidget xWidget, FormToolkit toolkit, AtsWorkPage page, Artifact art, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException, SQLException {
      return Result.TrueResult;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#isAccessControlViaAssigneesEnabledForBranching()
    */
   public boolean isAccessControlViaAssigneesEnabledForBranching() throws OseeCoreException, SQLException {
      return false;
   }

}
