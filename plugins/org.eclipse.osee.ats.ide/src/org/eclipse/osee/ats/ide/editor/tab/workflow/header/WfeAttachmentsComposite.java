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

package org.eclipse.osee.ats.ide.editor.tab.workflow.header;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.AtsOpenOption;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsEditors;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IRelationLink;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class WfeAttachmentsComposite extends Composite {

   private final WorkflowEditor editor;
   private final IAtsWorkItem workItem;
   private final Map<Long, Composite> relIdToComp = new HashMap<>();
   private final Set<Long> existingRels = new HashSet<>();
   private final AtsApi atsApi;

   public WfeAttachmentsComposite(Composite parent, int style, WorkflowEditor editor) {
      super(parent, style);
      this.editor = editor;
      this.workItem = editor.getWorkItem();
      this.atsApi = AtsApiService.get();
   }

   public void create() {
      setLayout(ALayout.getZeroMarginLayout(1, false));
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      setLayoutData(gd);
      editor.getToolkit().adapt(this);

      Label label = new Label(this, SWT.NONE);
      label.setText("Attachments: ");
      label.setBackground(Displays.getSystemColor(SWT.COLOR_WHITE));
      label.setFont(FontManager.getCourierNew12Bold());

      createUpdateAttached();

      refresh();

      layout(true, true);
      getParent().layout(true, true);
      editor.getWorkFlowTab().getManagedForm().reflow(true);
   }

   private void createUpdateAttached() {

      Map<RelationLink, Artifact> supporting = getSupporting();
      List<Long> linkHandled = new ArrayList<>();
      existingRels.addAll(relIdToComp.keySet());

      for (Entry<RelationLink, Artifact> supportEntry : supporting.entrySet()) {
         if (linkHandled.contains(supportEntry.getKey().getId())) {
            continue;
         }
         try {
            RelationLink relation = supportEntry.getKey();
            if (existingRels.contains(relation.getId())) {
               existingRels.remove(relation.getId());
               continue;
            }
            Artifact support = supportEntry.getValue();
            String labelStr = support.toStringWithId();
            if (support instanceof IAtsWorkItem) {
               IAtsWorkItem thatWorkItem = (IAtsWorkItem) support;
               labelStr = String.format("[%s] - %s - [%s]", thatWorkItem.getArtifactTypeName(),
                  thatWorkItem.toStringWithAtsId(), thatWorkItem.getStateMgr().getCurrentStateName());
            }
            Composite lComp = new Composite(this, SWT.NONE);
            relIdToComp.put(relation.getId(), lComp);
            lComp.setLayout(ALayout.getZeroMarginLayout(5, false));
            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            lComp.setLayoutData(gd);
            editor.getToolkit().adapt(lComp);
            lComp.setBackground(Displays.getSystemColor(SWT.COLOR_WHITE));

            editor.getToolkit().createLabel(lComp, "    - " + labelStr);

            Artifact thisArt = (Artifact) workItem.getStoreObject();
            Artifact thatArt = (Artifact) atsApi.getQueryService().getArtifact(support);

            createReadHyperlink(thisArt, thatArt, lComp, editor, "Open");
            createEditHyperlink(thatArt, lComp, editor);
            createDeleteHyperlink(thisArt, thatArt, relation, lComp, editor);

            linkHandled.add(supportEntry.getKey().getId());
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.WARNING, "Error showing link " + supportEntry.getKey(), ex);
         }
      }
      if (!existingRels.isEmpty()) {
         removeRelations(existingRels);
      }

   }

   private Map<RelationLink, Artifact> getSupporting() {
      Set<IRelationLink> supportingLink = new HashSet<>();
      supportingLink.addAll(atsApi.getRelationResolver().getRelations(workItem.getStoreObject(),
         CoreRelationTypes.SupportingInfo_SupportingInfo));
      supportingLink.addAll(atsApi.getRelationResolver().getRelations(workItem.getStoreObject(),
         CoreRelationTypes.SupportingInfo_IsSupportedBy));
      Map<RelationLink, Artifact> supporting = new HashMap<>();
      for (IRelationLink iLink : supportingLink) {
         RelationLink link = (RelationLink) iLink;
         if (workItem.getId().equals(link.getArtifactA().getId())) {
            Artifact otherArt = (Artifact) atsApi.getQueryService().getArtifact(link.getArtifactB());
            supporting.put(link, otherArt);
         } else {
            Artifact otherArt = (Artifact) atsApi.getQueryService().getArtifact(link.getArtifactA());
            supporting.put(link, otherArt);
         }
      }
      return supporting;
   }

   private void removeRelations(final Set<Long> existingRels) {
      final Composite fComp = this;

      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            if (Widgets.isAccessible(fComp) && relIdToComp != null) {
               for (Long relationId : existingRels) {
                  Composite comp = relIdToComp.get(relationId);
                  if (comp != null) {
                     comp.dispose();
                  }
               }
            }
         }
      });
   }

   public void refresh() {
      if (Widgets.isAccessible(this)) {
         createUpdateAttached();
      }
   }

   public static Hyperlink createDeleteHyperlink(Artifact thisArt, final Artifact thatArt, final RelationLink relation, Composite lComp, WorkflowEditor editor) {
      Hyperlink link = editor.getToolkit().createHyperlink(lComp, "Delete", SWT.NONE);
      link.addHyperlinkListener(new HyperlinkAdapter() {
         @Override
         public void linkActivated(HyperlinkEvent e) {
            Artifact delArt = thatArt;
            if (thatArt instanceof IAtsWorkItem) {
               delArt = thisArt;
            }

            Collection<ArtifactToken> related = Arrays.asList(thatArt);
            if (thatArt instanceof IAtsObject) {
               if (MessageDialog.openConfirm(Displays.getActiveShell(), "Delete Related",
                  "You do not have permissions to delete ATS Objects.  Would you Like to Un-Relate?")) {
                  IAtsChangeSet changes = AtsApiService.get().createChangeSet("Delete Relation");
                  changes.deleteRelation(relation);
                  changes.execute();
               }
               return;
            }
            XResultData results =
               AtsApiService.get().getAccessControlService().isDeleteable(related, new XResultData());
            if (results.isErrors()) {
               AWorkbench.popup(results.toString());
               return;
            }
            if (MessageDialog.openConfirm(Displays.getActiveShell(), "Delete Related",
               "Are you sure you want to delete related artifact\n\n" + delArt.toStringWithId() + " ?")) {
               IAtsChangeSet changes = AtsApiService.get().createChangeSet("Delete Related Artifact");
               changes.deleteArtifact(delArt);
               changes.execute();
            }
         }
      });
      return link;
   }

   public static Hyperlink createEditHyperlink(final Artifact thatArt, Composite lComp, WorkflowEditor editor) {
      Hyperlink link = editor.getToolkit().createHyperlink(lComp, "Edit", SWT.NONE);
      link.addHyperlinkListener(new HyperlinkAdapter() {
         @Override
         public void linkActivated(HyperlinkEvent e) {
            if (AtsObjects.isAtsWorkItemOrAction(thatArt)) {
               AtsEditors.openATSAction(thatArt, AtsOpenOption.OpenOneOrPopupSelect);
            } else {
               try {
                  RendererManager.open(thatArt, PresentationType.SPECIALIZED_EDIT);
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
      });
      return link;
   }

   public static Hyperlink createReadHyperlink(Artifact thisArt, final Artifact thatArt, Composite lComp, WorkflowEditor editor, String label) {
      Hyperlink link = editor.getToolkit().createHyperlink(lComp, label, SWT.NONE);
      link.addHyperlinkListener(new HyperlinkAdapter() {
         @Override
         public void linkActivated(HyperlinkEvent e) {
            if (AtsObjects.isAtsWorkItemOrAction(thatArt)) {
               AtsEditors.openATSAction(thatArt, AtsOpenOption.OpenOneOrPopupSelect);
            } else {
               try {
                  RendererManager.open(thatArt, PresentationType.DEFAULT_OPEN);
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
      });
      return link;
   }

}
