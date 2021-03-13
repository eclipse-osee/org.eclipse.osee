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
import java.util.Map.Entry;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.AtsAttachment;
import org.eclipse.osee.ats.api.workflow.AtsAttachments;
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
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.ArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * This is an example of how to implement and use the XAttachmentCombo.
 *
 * @author Bhawana Mishra
 */

public abstract class XAttachmentCombo extends XCombo implements ArtifactWidget {

   protected IAtsWorkItem workItem;
   HashMap<String, BranchId> nameBranchMap = new HashMap<>();
   private final String configKey;
   boolean inOsee = false;
   private String location;
   private Composite parent;
   private Hyperlink readHyperlink;
   private Hyperlink editHyperlink;
   private Hyperlink deleteHyperlink;
   private boolean listenerEnabled = false;

   public XAttachmentCombo(String displayLabel, String configKey) {
      super(displayLabel);
      this.configKey = configKey;
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      this.parent = parent;
      super.createControls(parent, horizontalSpan);
      fillCombo();
   }

   private Pair<Artifact, RelationLink> getSelectedAttachment() {
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

   private boolean isAttachmentAttached() {
      Pair<Artifact, RelationLink> artRelPair = getSelectedAttachment();
      return artRelPair.getFirst() != null && artRelPair.getSecond() != null;
   }

   /**
    * This id is added to a attachment when it's copy and attached to the workflow. This Id has to be different for
    * every different types of attachment.
    */
   public abstract String getAttachmentStaticId();

   /**
    * @return json representation of Attachements/Attachment to be used.
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

   protected Map<String, BranchId> getNameToLocationMap() {
      String locationList = getFileListString();

      AtsAttachments allAttachments = AtsApiService.get().jaxRsApi().readValue(locationList, AtsAttachments.class);
      for (AtsAttachment attachment : allAttachments.getAttachments()) {
         nameBranchMap.put(attachment.getName(), attachment.getBranch());
         location = attachment.getlocation();
         if (location.equals("osee")) {
            inOsee = true;
         }
      }
      return nameBranchMap;
   }

   private void deletePreviouslySelectedArtifact() {
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Delete Related Artifact");
      Pair<Artifact, RelationLink> entry = getSelectedAttachment();
      changes.deleteArtifact(entry.getFirst());
      changes.execute();
   }

   private void fillCombo() {

      getNameToLocationMap();

      // Add to fileNames
      setDataStrings(nameBranchMap.keySet().toArray(new String[nameBranchMap.size()]));

      addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {

            if (!listenerEnabled) {
               return;
            }
            // Determine if file is already attached and ask if want to replace
            if (isAttachmentAttached()) {
               if (getData().equals("--select--")) {
                  if (!MessageDialog.openConfirm(Displays.getActiveShell(), "Attach Selected",
                     "You didn't select the right attachment, do you want to delete previous attachment and attach nothing?")) {
                     return;
                  }
               } else if (!MessageDialog.openConfirm(Displays.getActiveShell(), "Attach Selected",
                  "There is already one attachment attached, do you want to delete and replace with the new one?")) {
                  return;
               }
               deletePreviouslySelectedArtifact();
            }

            // Check if these files are stored in OSEE
            if (inOsee) {
               String selection = (String) getData();
               for (Entry<String, BranchId> entry : nameBranchMap.entrySet()) {
                  if (entry.getKey().equals(selection)) {
                     String attachmentName = entry.getKey();
                     BranchId branchId = entry.getValue();
                     try {
                        Artifact art = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.GeneralDocument,
                           attachmentName, branchId);
                        Artifact duplicateArt = art.duplicate((CoreBranches.COMMON));
                        SkynetTransaction transaction =
                           TransactionManager.createTransaction(CoreBranches.COMMON, "Duplicate and store attachment");
                        ((Artifact) workItem.getStoreObject()).addRelation(
                           CoreRelationTypes.SupportingInfo_SupportingInfo, duplicateArt);
                        duplicateArt.addAttribute(CoreAttributeTypes.StaticId, getAttachmentStaticId());
                        transaction.addArtifact(duplicateArt);
                        transaction.execute();
                     } catch (ArtifactDoesNotExist ex) {
                        AWorkbench.popup("Selected attachment does not exist");
                     }
                     return;
                  }
               }

            } else {
               File file = new File(location);
               if (!file.exists()) {
                  AWorkbench.popup("Error Attaching Attachment",
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

   @Override
   public void refresh() {
      super.refresh();
      listenerEnabled = false;
      Pair<Artifact, RelationLink> entry = getSelectedAttachment();
      Artifact attachmentArt = entry.getFirst();
      if (attachmentArt != null && !Widgets.isAccessible(readHyperlink)) {
         WorkflowEditor editor = WorkflowEditor.getWorkflowEditor(workItem);
         readHyperlink = WfeRelationsHyperlinkComposite.createReadHyperlink((AbstractWorkflowArtifact) workItem,
            attachmentArt, parent, editor, OpenType.Read.name());
         editHyperlink = WfeRelationsHyperlinkComposite.createEditHyperlink(attachmentArt, parent, editor);
         deleteHyperlink = WfeRelationsHyperlinkComposite.createDeleteHyperlink((AbstractWorkflowArtifact) workItem,
            attachmentArt, entry.getSecond(), parent, editor);
         set(attachmentArt.toString());
      } else {
         if (Widgets.isAccessible(readHyperlink)) {
            readHyperlink.dispose();
            editHyperlink.dispose();
            deleteHyperlink.dispose();
         }
         set("");
      }
      listenerEnabled = true;
   }

}
