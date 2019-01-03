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
package org.eclipse.osee.ats.ide.world;

import java.util.List;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.ats.ide.search.AtsSearchWorkflowSearchItem;
import org.eclipse.osee.ats.ide.world.search.AbstractWorkItemSearchItem;
import org.eclipse.osee.ats.ide.world.search.AtsSearchGoalSearchItem;
import org.eclipse.osee.ats.ide.world.search.AtsSearchReviewSearchItem;
import org.eclipse.osee.ats.ide.world.search.AtsSearchTaskSearchItem;
import org.eclipse.osee.ats.ide.world.search.AtsSearchTeamWorkflowSearchItem;
import org.eclipse.osee.ats.ide.world.search.AtsSearchWorkPackageSearchItem;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

/**
 * The factory which is capable of recreating class file editor inputs stored in a memento.
 *
 * @author Donald G. Dunne
 */
public class WorldEditorInputFactory implements IElementFactory {

   public final static String ID = "org.eclipse.osee.ats.ide.WorldEditorInputFactory"; //$NON-NLS-1$
   public final static String ART_IDS = "org.eclipse.osee.ats.ide.WorldEditorInputFactory.artIds"; //$NON-NLS-1$
   public final static String BRANCH_KEY = "org.eclipse.osee.ats.ide.WorldEditorInputFactory.branchId"; //$NON-NLS-1$
   public final static String TITLE = "org.eclipse.osee.ats.ide.WorldEditorInputFactory.title"; //$NON-NLS-1$
   public final static String ATS_SEARCH_ID = "org.eclipse.osee.ats.ide.WorldEditorInputFactory.atsSearchId"; //$NON-NLS-1$
   private static final String ATS_SEARCH_NAMESPACE =
      "org.eclipse.osee.ats.ide.WorldEditorInputFactory.atsSearchNamespace"; //$NON-NLS-1$;

   @Override
   public IAdaptable createElement(IMemento memento) {
      long atsSearchId = 0L;
      BranchId branch = BranchId.SENTINEL;
      String title = memento.getString(TITLE);
      if (Strings.isValid(memento.getString(BRANCH_KEY))) {
         branch = BranchId.valueOf(memento.getString(BRANCH_KEY));
      }
      List<ArtifactId> artIds = Collections.fromString(memento.getString(ART_IDS), ArtifactId::valueOf);
      String atsSearchIdStr = memento.getString(ATS_SEARCH_ID);
      if (Strings.isNumeric(atsSearchIdStr)) {
         atsSearchId = Long.valueOf(atsSearchIdStr);
      }
      try {
         if (atsSearchId > 0L) {
            String namespace = memento.getString(ATS_SEARCH_NAMESPACE);
            if (Strings.isValid(namespace)) {
               if (AtsSearchTeamWorkflowSearchItem.NAMESPACE.equals(namespace)) {
                  AbstractWorkItemSearchItem searchItem = new AtsSearchTeamWorkflowSearchItem();
                  searchItem.setRestoreId(atsSearchId);
                  return new WorldEditorInput(new WorldEditorParameterSearchItemProvider(searchItem, null));
               }
               if (AtsSearchTaskSearchItem.NAMESPACE.equals(namespace)) {
                  AbstractWorkItemSearchItem searchItem = new AtsSearchTaskSearchItem();
                  searchItem.setRestoreId(atsSearchId);
                  return new WorldEditorInput(new WorldEditorParameterSearchItemProvider(searchItem, null));
               }
               if (AtsSearchGoalSearchItem.NAMESPACE.equals(namespace)) {
                  AbstractWorkItemSearchItem searchItem = new AtsSearchGoalSearchItem();
                  searchItem.setRestoreId(atsSearchId);
                  return new WorldEditorInput(new WorldEditorParameterSearchItemProvider(searchItem, null));
               }
               if (AtsSearchReviewSearchItem.NAMESPACE.equals(namespace)) {
                  AtsSearchReviewSearchItem searchItem = new AtsSearchReviewSearchItem();
                  searchItem.setRestoreId(atsSearchId);
                  return new WorldEditorInput(new WorldEditorParameterSearchItemProvider(searchItem, null));
               }
               if (AtsSearchWorkPackageSearchItem.NAMESPACE.equals(namespace)) {
                  AtsSearchWorkPackageSearchItem searchItem = new AtsSearchWorkPackageSearchItem();
                  searchItem.setRestoreId(atsSearchId);
                  return new WorldEditorInput(new WorldEditorParameterSearchItemProvider(searchItem, null));
               }
               for (IAtsWorldEditorItem item : AtsWorldEditorItems.getItems()) {
                  if (item.isWorldEditorSearchProviderNamespaceMatch(namespace)) {
                     return item.getNewWorldEditorInputFromNamespace(namespace, atsSearchId);
                  }
               }
            }
            AtsSearchWorkflowSearchItem searchItem = new AtsSearchWorkflowSearchItem();
            searchItem.setRestoreId(atsSearchId);
            return new WorldEditorInput(new WorldEditorParameterSearchItemProvider(searchItem, null));
         }
      } catch (Exception ex) {
         // do nothing
      }
      return new WorldEditorInput(new WorldEditorReloadProvider(title, branch, artIds));
   }

   public static void saveState(IMemento memento, WorldEditorInput input) {
      String title = input.getName();
      String artIds = input.getIdString();
      BranchId branch = input.getBranch();

      if (Strings.isValid(artIds) && branch.isValid() && Strings.isValid(title)) {
         memento.putString(BRANCH_KEY, branch.getIdString());
         memento.putString(ART_IDS, artIds);
         memento.putString(TITLE, title);
      }
      if (input.getAtsSearchId() > 0L) {
         memento.putString(ATS_SEARCH_ID, String.valueOf(input.getAtsSearchId()));
         memento.putString(ATS_SEARCH_NAMESPACE, String.valueOf(input.getAtsSearchNamespace()));
      }
   }

}
