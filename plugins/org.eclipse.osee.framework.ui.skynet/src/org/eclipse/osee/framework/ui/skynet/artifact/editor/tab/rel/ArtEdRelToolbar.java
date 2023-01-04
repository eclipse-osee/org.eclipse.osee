/*********************************************************************
 * Copyright (c) 2023 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.rel;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction.IRefreshActionHandler;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.action.OpenHistoryAction;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class ArtEdRelToolbar {

   private final ScrolledForm scrolledForm;
   private final IRefreshActionHandler refreshActionHandler;
   private final Artifact artifact;

   public ArtEdRelToolbar(ScrolledForm scrolledForm, Artifact artifact, IRefreshActionHandler refreshActionHandler) {
      this.scrolledForm = scrolledForm;
      this.artifact = artifact;
      this.refreshActionHandler = refreshActionHandler;
   }

   public void build() {
      IToolBarManager toolBarMgr = scrolledForm.getToolBarManager();
      toolBarMgr.removeAll();
      toolBarMgr.add(new OpenHistoryAction(artifact));
      toolBarMgr.add(new Separator());
      toolBarMgr.add(new RefreshAction(refreshActionHandler));
      scrolledForm.updateToolBar();
   }

}
