/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.attr;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction.IRefreshActionHandler;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.action.OpenHistoryAction;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.attr.action.AddAttributeAction;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.attr.action.DeleteAttributeAction;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.attr.action.EditAttributeAction;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class ArtEdAttrToolbar {

   private final ScrolledForm scrolledForm;
   private final ArtEdAttrXViewer attrXViewer;
   private final IRefreshActionHandler refreshActionHandler;

   public ArtEdAttrToolbar(ScrolledForm scrolledForm, ArtEdAttrXViewer attrXViewer, IRefreshActionHandler refreshActionHandler) {
      this.scrolledForm = scrolledForm;
      this.attrXViewer = attrXViewer;
      this.refreshActionHandler = refreshActionHandler;
   }

   public void build() {
      IToolBarManager toolBarMgr = scrolledForm.getToolBarManager();
      toolBarMgr.removeAll();
      toolBarMgr.add(new AddAttributeAction(attrXViewer));
      toolBarMgr.add(new EditAttributeAction(attrXViewer));
      toolBarMgr.add(new DeleteAttributeAction(attrXViewer));
      toolBarMgr.add(new Separator());
      toolBarMgr.add(attrXViewer.getCustomizeAction());
      toolBarMgr.add(new Separator());
      toolBarMgr.add(new OpenHistoryAction(attrXViewer.getArtifact()));
      toolBarMgr.add(new Separator());
      toolBarMgr.add(new RefreshAction(refreshActionHandler));
      scrolledForm.updateToolBar();
   }

}
