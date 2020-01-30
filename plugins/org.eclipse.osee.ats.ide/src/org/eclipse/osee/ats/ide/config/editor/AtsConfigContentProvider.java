/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.config.editor;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigContentProvider implements ITreeContentProvider {

   protected Collection<IResultsXViewerRow> rootSet = new HashSet<>();
   private static Object[] EMPTY_ARRAY = new Object[0];

   public AtsConfigContentProvider() {
      super();
   }

   @Override
   @SuppressWarnings("rawtypes")
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Object[]) {
         return (Object[]) parentElement;
      }
      if (parentElement instanceof Collection) {
         return ((Collection) parentElement).toArray();
      }
      if (parentElement instanceof IResultsXViewerRow) {
         IResultsXViewerRow row = (IResultsXViewerRow) parentElement;
         Object data = row.getData();
         if (data instanceof IAtsTeamDefinition) {
            return getTeamDefChildren(data);
         }
         if (data instanceof IAtsActionableItem) {
            return getActionableItemChildren(data);
         }
      }
      if (parentElement instanceof IAtsTeamDefinition) {
         return getTeamDefChildren(parentElement);
      }
      if (parentElement instanceof IAtsActionableItem) {
         return getActionableItemChildren(parentElement);
      }

      return EMPTY_ARRAY;
   }

   private Object[] getActionableItemChildren(Object data) {
      IAtsActionableItem aia = (IAtsActionableItem) data;
      List<Object> children = new LinkedList<>();
      children.addAll(aia.getChildrenActionableItems());
      if (aia.getTeamDefinition() != null) {
         children.add(aia.getTeamDefinition());
      }
      return children.toArray();
   }

   private Object[] getTeamDefChildren(Object data) {
      IAtsTeamDefinition teamDef = (IAtsTeamDefinition) data;
      List<Object> children = new LinkedList<>();
      children.addAll(AtsClientService.get().getTeamDefinitionService().getChildrenTeamDefinitions(teamDef));
      children.addAll(AtsClientService.get().getActionableItemService().getActionableItems(teamDef));
      children.addAll(AtsClientService.get().getVersionService().getVersions(teamDef));
      return children.toArray();
   }

   @Override
   public Object getParent(Object element) {
      return null;
   }

   @Override
   public boolean hasChildren(Object element) {
      return getChildren(element).length > 0;
   }

   @Override
   public Object[] getElements(Object inputElement) {
      if (inputElement instanceof String) {
         return new Object[] {inputElement};
      }
      return getChildren(inputElement);
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      // do nothing
   }

}