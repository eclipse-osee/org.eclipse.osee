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
public interface IAtsStateItem {

   public Result pageCreated(FormToolkit toolkit, AtsWorkPage page, SMAManager smaMgr, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException, SQLException;

   public Result xWidgetCreating(XWidget xWidget, FormToolkit toolkit, AtsWorkPage page, Artifact art, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException, SQLException;

   public void xWidgetCreated(XWidget xWidget, FormToolkit toolkit, AtsWorkPage page, Artifact art, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException, SQLException;

   public void widgetModified(SMAWorkFlowSection section, XWidget xWidget) throws OseeCoreException, SQLException;

   public String getOverrideTransitionToStateName(SMAWorkFlowSection section) throws OseeCoreException, SQLException;

   public Collection<User> getOverrideTransitionToAssignees(SMAWorkFlowSection section) throws OseeCoreException, SQLException;

   public String getDescription() throws OseeCoreException, SQLException;

   public String getBranchShortName(SMAManager smaMgr) throws OseeCoreException, SQLException;

   public boolean isAccessControlViaAssigneesEnabledForBranching() throws OseeCoreException, SQLException;

   public Collection<String> getIds() throws OseeCoreException, OseeCoreException, SQLException;

   public List<WorkPageService> getSidebarServices(SMAManager smaMgr) throws OseeCoreException, SQLException;

   public List<WorkPageService> getToolbarServices(SMAManager smaMgr) throws OseeCoreException, SQLException;

   /**
    * @param smaMgr
    * @param fromState
    * @param toState
    * @param toAssignees
    * @return Result of operation. If Result.isFalse(), transition will not continue and Result.popup will occur.
    * @throws Exception TODO
    */
   public Result transitioning(SMAManager smaMgr, String fromState, String toState, Collection<User> toAssignees) throws OseeCoreException, SQLException;

   public void transitioned(SMAManager smaMgr, String fromState, String toState, Collection<User> toAssignees) throws OseeCoreException, SQLException;

   /**
    * @param smaMgr TODO
    * @return Result of operation. If Result.isFalse(), commit will not continue and Result.popup will occur.
    * @throws Exception TODO
    */
   public Result committing(SMAManager smaMgr) throws OseeCoreException, SQLException;

}
