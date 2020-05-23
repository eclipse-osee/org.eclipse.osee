/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.ui.skynet.action;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.compare.CompareHandler;
import org.eclipse.osee.framework.ui.skynet.compare.CompareItem;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.BranchEntryEntryDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * Given one or two artifact id (guid or artid) lists, obtain the artifact names and show in compare editor.
 *
 * @author Donald G. Dunne
 */
public class CompareTwoArtifactIdListsAction extends Action {

   public CompareTwoArtifactIdListsAction() {
      setText("Compare Two Artifact Id Lists");
      setToolTipText(
         "Given one or two artifact id (guid or artid) lists, obtain the artifact names and show in compare editor.");
   }

   @Override
   public void run() {
      try {
         final BranchEntryEntryDialog ed = new BranchEntryEntryDialog(getText(),
            "Enter Artifact Ids (guid/artId) to compare", "Artifact Id List 1", "Artifact Id List 2");
         ed.setModeless();
         ed.setFillVertically(true);
         ed.setOkListener(new Listener() {

            @Override
            public void handleEvent(Event event) {
               Conditions.checkNotNull(ed.getBranch(), "Branch");
               CompareHandler compareHandler = new CompareHandler(null,
                  new CompareItem("First", getArtifactList(ed.getBranch(), ed.getEntry()), System.currentTimeMillis(),
                     null),
                  new CompareItem("Second", getArtifactList(ed.getBranch(), ed.getEntry2()), System.currentTimeMillis(),
                     null),
                  null);
               compareHandler.compare();
            }
         });
         ed.open();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private String getArtifactList(BranchId branch, String idString) {
      List<String> guids = new LinkedList<>();
      List<String> artIds = new LinkedList<>();
      String[] idList = idString.split("[\n\r]");
      for (String id : idList) {
         id = id.replaceAll(" ", "");
         if (GUID.isValid(id)) {
            guids.add(id);
         }
         if (Strings.isNumeric(id)) {
            artIds.add(id);
         }
      }
      Map<String, ArtifactToken> idsToTokens = ArtifactQuery.getArtifactTokensFromGuids(branch, guids);
      idsToTokens.putAll(ArtifactQuery.getArtifactTokensFromIds(branch, artIds));
      StringBuilder sb = new StringBuilder();
      for (String id : idList) {
         if (Strings.isValid(id)) {
            sb.append(String.format("%s, %s\n", id, idsToTokens.get(id).getName()));
         }
      }
      return sb.toString();
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.EDIT);
   }

}
