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
package org.eclipse.osee.ats.editor.stateItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.editor.service.WorkPageService;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
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

   public Result committing(SMAManager smaMgr) throws OseeCoreException {
      return Result.TrueResult;
   }

   public String getBranchShortName(SMAManager smaMgr) throws OseeCoreException {
      return null;
   }

   protected String getId() {
      return null;
   }

   public Collection<String> getIds() throws OseeCoreException {
      if (getId() == null) return EMPTY_STRING;
      return Arrays.asList(getId());
   }

   public Collection<User> getOverrideTransitionToAssignees(SMAWorkFlowSection section) throws OseeCoreException {
      return null;
   }

   public String getOverrideTransitionToStateName(SMAWorkFlowSection section) throws OseeCoreException {
      return null;
   }

   public List<WorkPageService> getSidebarServices(SMAManager smaMgr) throws OseeCoreException {
      return EMPTY_SERVICES;
   }

   public List<WorkPageService> getToolbarServices(SMAManager smaMgr) throws OseeCoreException {
      return EMPTY_SERVICES;
   }

   public Result pageCreated(FormToolkit toolkit, AtsWorkPage page, SMAManager smaMgr, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException {
      return Result.TrueResult;
   }

   public void transitioned(SMAManager smaMgr, String fromState, String toState, Collection<User> toAssignees, SkynetTransaction transaction) throws OseeCoreException {
   }

   public Result transitioning(SMAManager smaMgr, String fromState, String toState, Collection<User> toAssignees) throws OseeCoreException {
      return Result.TrueResult;
   }

   public void widgetModified(SMAWorkFlowSection section, XWidget xWidget) throws OseeCoreException {
   }

   public void xWidgetCreated(XWidget xWidget, FormToolkit toolkit, AtsWorkPage page, Artifact art, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException {
   }

   public Result xWidgetCreating(XWidget xWidget, FormToolkit toolkit, AtsWorkPage page, Artifact art, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException {
      return Result.TrueResult;
   }

   public boolean isAccessControlViaAssigneesEnabledForBranching() throws OseeCoreException {
      return false;
   }
}