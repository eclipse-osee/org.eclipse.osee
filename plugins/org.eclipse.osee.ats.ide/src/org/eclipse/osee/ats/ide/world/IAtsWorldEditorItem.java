/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.world;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.ide.search.AtsSearchWorkflowSearchItem;
import org.eclipse.osee.ats.ide.workflow.task.TaskXViewer;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorldEditorItem {

   public List<XViewerColumn> getXViewerColumns();

   public boolean isXColumnProvider(XViewerColumn xCol);

   public String getColumnText(Object element, XViewerColumn xCol, int columnIndex);

   public Color getForeground(Object element, XViewerColumn xCol, int columnIndex);

   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex);

   /**
    * Return menu item objects to add to the World Editor pull-down menu only if applicable for the given
    * worldSearchItem
    */
   public List<? extends Action> getWorldEditorMenuActions(IWorldEditorProvider worldEditorProvider, WorldEditor worldEditor);

   public void updateTaskEditMenuActions(TaskXViewer taskXViewer);

   public default boolean isWorldEditorSearchProviderNamespaceMatch(String namespace) {
      return false;
   }

   public default IAdaptable getNewWorldEditorInputFromNamespace(String namespace, long atsSearchId) {
      return null;
   }

   public List<AtsSearchWorkflowSearchItem> getSearchWorkflowSearchItems();

   default public Collection<IUserGroupArtifactToken> getUserGroups() {
      return Collections.singleton(CoreUserGroups.Everyone);
   }
}
