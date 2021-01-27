/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.widgets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.workflow.header.WfeEditorAddSupportingFiles;
import org.eclipse.osee.ats.ide.editor.tab.workflow.header.WfeRelationsHyperlinkComposite;
import org.eclipse.osee.ats.ide.editor.tab.workflow.header.WfeRelationsHyperlinkComposite.OpenType;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.widgets.Composite;

/**
 * This is an example of how to implement and use the XAttachmentCombo.
 *
 * @author Bhawana Mishra
 */

public abstract class XAttachmentCombo extends XCombo implements IArtifactWidget {

   protected IAtsWorkItem workItem;
   HashMap<String, String> locationMap = new HashMap<>();
   private final String configKey;

   public XAttachmentCombo(String displayLabel, String configKey) {
      super(displayLabel);
      this.configKey = configKey;
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);
      fillCombo();
      Pair<Artifact, RelationLink> entry = getSelectedChecklist();
      Artifact checklistArt = entry.getFirst();
      if (checklistArt != null) {
         WorkflowEditor editor = WorkflowEditor.getWorkflowEditor(workItem);
         WfeRelationsHyperlinkComposite.createReadHyperlink((AbstractWorkflowArtifact) workItem, checklistArt, parent,
            editor, OpenType.Read.name());
         WfeRelationsHyperlinkComposite.createEditHyperlink(checklistArt, parent, editor);
         WfeRelationsHyperlinkComposite.createDeleteHyperlink((AbstractWorkflowArtifact) workItem, checklistArt,
            entry.getSecond(), parent, editor);
         // Show selected checklist in combo box
         set(checklistArt.toString());
      }
   }

   private Pair<Artifact, RelationLink> getSelectedChecklist() {
      Artifact workflowArt = (AbstractWorkflowArtifact) workItem;
      Artifact thatArt = null;
      RelationLink relation = null;
      for (final RelationLink rel : workflowArt.getRelations(CoreRelationTypes.SupportingInfo_SupportingInfo)) {
         if (rel.getArtifactB().getTags().contains(getAttachmentStaticId())) {
            thatArt = rel.getArtifactB();
            relation = rel;
         }
      }
      return new Pair<Artifact, RelationLink>(thatArt, relation);
   }

   private boolean isChecklistAttached() {
      Pair<Artifact, RelationLink> artRelPair = getSelectedChecklist();
      return artRelPair.getFirst() != null && artRelPair.getSecond() != null;
   }

   /**
    * This id is added to a checklist when it's copy and attached to the workflow. This Id has to be different for every
    * different types of checklist.
    */
   public abstract String getAttachmentStaticId();

   /**
    * @return single string of \"name;fullfilename\name;fullfilename\" from the fileDirectory and single string of
    * \"fileName;osee:570:file\n" from OSEE
    */
   protected String getFileListString() {
      return AtsApiService.get().getConfigValue(configKey);
   }

   public static void getUrlToFile(String urlStr, File outFile) throws IOException {
      URL url = new URL(urlStr);
      InputStream inputStream = null;
      try {
         inputStream = new BufferedInputStream(url.openStream());
         Lib.inputStreamToFile(inputStream, outFile);
      } finally {
         Lib.close(inputStream);
      }
   }

   protected Map<String, String> getNameToLocationMap() {
      String locationList = getFileListString();
      for (String nameAndLocation : locationList.split("\n")) {
         String nameLocation[] = nameAndLocation.split(";");
         locationMap.put(nameLocation[0], nameLocation[1]);
      }
      return locationMap;
   }

   private void fillCombo() {

      getNameToLocationMap();

      // add to fileNames
      setDataStrings(locationMap.keySet().toArray(new String[locationMap.size()]));

      addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            // Determine if file is already attached and ask if want to replace
            if (isChecklistAttached()) {
               if (!MessageDialog.openConfirm(Displays.getActiveShell(), "Attach Selected",
                  "There is already one checklist attached, do you want to delete and replace with the new one?")) {
                  return;
               }
               IAtsChangeSet changes = AtsApiService.get().createChangeSet("Delete Related Artifact");
               Pair<Artifact, RelationLink> entry = getSelectedChecklist();
               changes.deleteArtifact(entry.getFirst());
               changes.execute();
            }
            String location = locationMap.get(getData());

            //check if this files are stored in OSEE
            if (location.startsWith("osee")) {
               String[] values = location.split(":");
               String branchIdStr = values[1];
               String checklistName = values[2];
               checklistName = checklistName.replaceAll("\r", "");
               BranchId branch = null;
               if (Strings.isNumeric(branchIdStr)) {
                  branch = BranchId.valueOf(branchIdStr);
               } else {
                  AWorkbench.popup("Error Attaching Checklist",
                     String.format("Invalid Branch Id [%s].  Aborting.", branchIdStr));
                  return;
               }
               Artifact art =
                  ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.GeneralDocument, checklistName, branch);
               Artifact duplicateArt = art.duplicate((CoreBranches.COMMON));
               SkynetTransaction transaction = TransactionManager.createTransaction(CoreBranches.COMMON, "file");
               ((Artifact) workItem.getStoreObject()).addRelation(CoreRelationTypes.SupportingInfo_SupportingInfo,
                  duplicateArt);
               duplicateArt.addAttribute(CoreAttributeTypes.StaticId, getAttachmentStaticId());
               transaction.addArtifact(duplicateArt);
               transaction.execute();

            } else {
               File file = new File(location);
               if (!file.exists()) {
                  AWorkbench.popup("Error Attaching Checklist",
                     String.format("Select file [%s] does not exist.  Aborting.", file.getAbsolutePath()));
                  return;
               }
               List<File> selectedFile = new ArrayList<>();
               selectedFile.add(file);

               Jobs.startJob(new WfeEditorAddSupportingFiles(workItem, selectedFile, getAttachmentStaticId()), true);
            }
         }
      });

   }

   @Override
   public void saveToArtifact() {
      // do nothing
   }

   @Override
   public void revert() {
      // do nothing
   }

   @Override
   public Result isDirty() {
      // do nothing
      return Result.FalseResult;
   }

   @Override
   public void setArtifact(Artifact artifact) {
      if (artifact instanceof IAtsWorkItem) {
         workItem = (IAtsWorkItem) artifact;
      }
   }

   @Override
   public Artifact getArtifact() {
      return (Artifact) workItem.getStoreObject();
   }

}
