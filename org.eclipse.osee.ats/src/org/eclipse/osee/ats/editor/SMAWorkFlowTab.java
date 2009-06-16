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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.NoteItem;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.editor.service.ServicesArea;
import org.eclipse.osee.ats.workflow.ATSXWidgetOptionResolver;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.db.connection.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.artifact.annotation.AnnotationComposite;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.parts.MessageSummaryNote;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.HtmlDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPage;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessage;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class SMAWorkFlowTab extends FormPage implements IActionable {
   private final SMAManager smaMgr;
   private final ArrayList<SMAWorkFlowSection> sections = new ArrayList<SMAWorkFlowSection>();
   private final XFormToolkit toolkit;
   private static String ORIGINATOR = "Originator:";
   private Label origLabel;
   private final List<AtsWorkPage> pages = new ArrayList<AtsWorkPage>();
   private AtsWorkPage currentAtsWorkPage;
   private ScrolledForm scrolledForm;
   private final Integer HEADER_COMP_COLUMNS = 4;
   private static Map<String, Integer> guidToScrollLocation = new HashMap<String, Integer>();
   private SMARelationsHyperlinkComposite smaRelationsComposite;
   private IManagedForm managedForm;
   private Composite body;
   private Composite atsBody;
   private SMAActionableItemHeader actionableItemHeader;
   private SMAWorkflowMetricsHeader workflowMetricsHeader;

   public SMAWorkFlowTab(SMAManager smaMgr) {
      super(smaMgr.getEditor(), "overview", "Workflow");
      this.smaMgr = smaMgr;
      toolkit = smaMgr.getEditor().getToolkit();
   }

   private void createBody(Composite body) throws OseeCoreException {
      atsBody = toolkit.createComposite(body);
      atsBody.setLayoutData(new GridData(GridData.FILL_BOTH));
      atsBody.setLayout(new GridLayout(1, false));

      Composite headerComp = toolkit.createComposite(atsBody);
      headerComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      headerComp.setLayout(ALayout.getZeroMarginLayout(1, false));
      // mainComp.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));

      // Display relations
      try {
         createTopLineHeader(headerComp, toolkit);
         createAssigneesLineHeader(headerComp, toolkit);
         createLatestHeader(headerComp, toolkit);
         if (smaMgr.getSma() instanceof TeamWorkFlowArtifact) {
            actionableItemHeader = new SMAActionableItemHeader(headerComp, toolkit, smaMgr);
         }
         workflowMetricsHeader = new SMAWorkflowMetricsHeader(headerComp, toolkit, smaMgr);
         createSMANotesHeader(headerComp, toolkit, smaMgr, HEADER_COMP_COLUMNS);
         createStateNotesHeader(headerComp, toolkit, smaMgr, HEADER_COMP_COLUMNS, null);
         createAnnotationsHeader(headerComp, toolkit);

         sections.clear();
         pages.clear();

         if (SMARelationsHyperlinkComposite.relationExists(smaMgr.getSma())) {
            smaRelationsComposite = new SMARelationsHyperlinkComposite(atsBody, toolkit, SWT.NONE);
            smaRelationsComposite.create(smaMgr);
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }

      // Only display current or past states
      for (WorkPageDefinition workPageDefinition : smaMgr.getSma().getWorkFlowDefinition().getPagesOrdered()) {
         AtsWorkPage atsWorkPage =
               new AtsWorkPage(smaMgr.getWorkFlowDefinition(), workPageDefinition, null,
                     ATSXWidgetOptionResolver.getInstance());
         if (smaMgr.isCurrentState(atsWorkPage.getName())) currentAtsWorkPage = atsWorkPage;
         if (smaMgr.isCurrentState(atsWorkPage.getName()) || smaMgr.getStateMgr().isStateVisited(atsWorkPage.getName())) {
            // Don't show completed or cancelled state if not currently those state
            if (atsWorkPage.isCompletePage() && !smaMgr.isCompleted()) continue;
            if (atsWorkPage.isCancelledPage() && !smaMgr.isCancelled()) continue;
            SMAWorkFlowSection section = new SMAWorkFlowSection(atsBody, toolkit, SWT.NONE, atsWorkPage, smaMgr);
            managedForm.addPart(section);
            control = section.getMainComp();
            sections.add(section);
            pages.add(atsWorkPage);
         }
      }

      if (AtsPlugin.isAtsAdmin()) {
         SMAWorkFlowDebugSection section = new SMAWorkFlowDebugSection(atsBody, toolkit, SWT.NONE, smaMgr);
         managedForm.addPart(section);
         control = section.getMainComp();
         sections.add(section);
      }

      atsBody.setFocus();
      // Jump to scroll location if set
      Integer selection = guidToScrollLocation.get(smaMgr.getSma().getGuid());
      if (selection != null) {
         JumpScrollbarJob job = new JumpScrollbarJob("");
         job.schedule(500);
      }

      managedForm.refresh();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
    */
   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      this.managedForm = managedForm;
      try {
         scrolledForm = managedForm.getForm();
         scrolledForm.addDisposeListener(new DisposeListener() {
            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
             */
            public void widgetDisposed(DisposeEvent e) {
               storeScrollLocation();
            }
         });

         scrolledForm.setText(getEditorInput().getName());
         fillBody(managedForm);
         addMessageDecoration(scrolledForm);

         refreshToolbar();

         if (smaMgr.getSma().getHelpContext() != null) AtsPlugin.getInstance().setHelp(scrolledForm,
               smaMgr.getSma().getHelpContext());

      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private void addMessageDecoration(ScrolledForm form) {
      form.getForm().addMessageHyperlinkListener(new HyperlinkAdapter() {

         @Override
         public void linkActivated(HyperlinkEvent e) {
            String title = e.getLabel();
            Object href = e.getHref();
            if (href instanceof IMessage[]) {
               Point noteLocation = ((Control) e.widget).toDisplay(0, 0);
               noteLocation.x += 10;
               noteLocation.y += 10;

               MessageSummaryNote note = new MessageSummaryNote(getManagedForm(), title, (IMessage[]) href);
               note.setLocation(noteLocation);
               note.open();
            }
         }

      });
   }

   private void refreshToolbar() throws OseeCoreException {
      if (toolbarArea != null) {
         toolbarArea.dispose();
         scrolledForm.getToolBarManager().removeAll();
      }
      toolbarArea = new ServicesArea(smaMgr);
      toolbarArea.createToolbarServices(currentAtsWorkPage, scrolledForm.getToolBarManager());

      OseeAts.addButtonToEditorToolBar(smaMgr.getEditor(), this, AtsPlugin.getInstance(),
            scrolledForm.getToolBarManager(), SMAEditor.EDITOR_ID, "ATS Editor");

      scrolledForm.updateToolBar();
   }

   public Result isXWidgetDirty() throws OseeCoreException {
      for (SMAWorkFlowSection section : sections) {
         Result result = section.isXWidgetDirty();
         if (result.isTrue()) return result;
      }
      return Result.FalseResult;
   }

   public Result isXWidgetSavable() throws OseeCoreException {
      for (SMAWorkFlowSection section : sections) {
         Result result = section.isXWidgetSavable();
         if (result.isFalse()) return result;
      }
      return Result.TrueResult;
   }

   public void saveXWidgetToArtifact() throws OseeCoreException {
      for (SMAWorkFlowSection section : sections) {
         section.saveXWidgetToArtifact();
      }
   }

   @Override
   public void dispose() {
      if (actionableItemHeader != null) {
         actionableItemHeader.dispose();
      }
      if (workflowMetricsHeader != null) {
         workflowMetricsHeader.dispose();
      }
      for (SMAWorkFlowSection section : sections)
         section.dispose();
      if (toolbarArea != null) {
         toolbarArea.dispose();
      }
      if (toolkit != null) {
         toolkit.dispose();
      }
   }

   public String getActionDescription() {
      return "Workflow Tab";
   }

   public final static String normalColor = "#FFFFFF";
   private final static String activeColor = "#EEEEEE";

   public String getHtml() throws OseeCoreException {
      StringBuffer htmlSb = new StringBuffer();
      for (WorkPage wPage : pages) {
         AtsWorkPage page = (AtsWorkPage) wPage;
         StringBuffer notesSb = new StringBuffer();
         for (NoteItem note : smaMgr.getNotes().getNoteItems()) {
            if (note.getState().equals(page.getName())) {
               notesSb.append(note.toHTML() + AHTML.newline());
            }
         }
         if (smaMgr.isCurrentState(page.getName()) || smaMgr.getStateMgr().isStateVisited(page.getName())) {
            htmlSb.append(page.getHtml(smaMgr.isCurrentState(page.getName()) ? activeColor : normalColor,
                  notesSb.toString(), SMAReviewInfoComposite.toHTML(smaMgr, page.getName())));
            htmlSb.append(AHTML.newline());
         }
      }
      return htmlSb.toString();
   }

   private void fillBody(IManagedForm managedForm) throws OseeCoreException {
      body = managedForm.getForm().getBody();
      GridLayout gridLayout = new GridLayout(1, false);
      body.setLayout(gridLayout);
      body.setLayoutData(new GridData(SWT.LEFT, SWT.LEFT, true, false));

      createBody(body);
   }

   private ServicesArea toolbarArea;

   private Control control = null;

   private void storeScrollLocation() {
      if (scrolledForm != null) {
         Integer selection = scrolledForm.getVerticalBar().getSelection();
         // System.out.println("Storing selection => " + selection);
         guidToScrollLocation.put(smaMgr.getSma().getGuid(), selection);
      }
   }

   private class JumpScrollbarJob extends Job {
      public JumpScrollbarJob(String name) {
         super(name);
      }

      @Override
      protected IStatus run(IProgressMonitor monitor) {
         Displays.ensureInDisplayThread(new Runnable() {
            public void run() {
               Integer selection = guidToScrollLocation.get(smaMgr.getSma().getGuid());
               // System.out.println("Restoring selection => " + selection);

               // Find the ScrolledComposite operating on the control.
               ScrolledComposite sComp = null;
               if (control == null || control.isDisposed()) return;
               Composite parent = control.getParent();
               while (parent != null) {
                  if (parent instanceof ScrolledComposite) {
                     sComp = (ScrolledComposite) parent;
                     break;
                  }
                  parent = parent.getParent();
               }

               if (sComp != null) {
                  sComp.setOrigin(0, selection);
               }
            }
         });
         return Status.OK_STATUS;

      }
   }

   private void createAssigneesLineHeader(Composite comp, XFormToolkit toolkit) throws OseeCoreException {
      Composite topLineComp = new Composite(comp, SWT.NONE);
      topLineComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      topLineComp.setLayout(ALayout.getZeroMarginLayout(2, false));
      toolkit.adapt(topLineComp);
      SMAEditor.createLabelValue(toolkit, topLineComp, "Assignee(s)", smaMgr.getStateMgr().getAssigneesStr(150));
   }

   private void createTopLineHeader(Composite comp, XFormToolkit toolkit) {
      Composite topLineComp = new Composite(comp, SWT.NONE);
      topLineComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      topLineComp.setLayout(ALayout.getZeroMarginLayout(13, false));
      toolkit.adapt(topLineComp);
      // mainComp.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));

      try {
         SMAEditor.createLabelValue(toolkit, topLineComp, "Current State", smaMgr.getStateMgr().getCurrentStateName());
         if (smaMgr.getSma() instanceof TeamWorkFlowArtifact) {
            SMAEditor.createLabelValue(toolkit, topLineComp, "Team",
                  ((TeamWorkFlowArtifact) smaMgr.getSma()).getTeamName());
         }
         SMAEditor.createLabelValue(toolkit, topLineComp, "Created", XDate.getDateStr(
               smaMgr.getLog().getCreationDate(), XDate.MMDDYYHHMM));
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }

      try {
         createOriginatorHeader(topLineComp, toolkit);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }

      try {
         SMAEditor.createLabelValue(
               toolkit,
               topLineComp,
               "Action Id",
               smaMgr.getSma().getParentActionArtifact() == null ? "??" : smaMgr.getSma().getParentActionArtifact().getHumanReadableId());
         SMAEditor.createLabelValue(toolkit, topLineComp, smaMgr.getSma().getArtifactSuperTypeName() + " Id",
               smaMgr.getSma().getHumanReadableId());
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }
   }

   private void createLatestHeader(Composite comp, XFormToolkit toolkit) {
      if (smaMgr.isHistoricalVersion()) {
         Label label =
               toolkit.createLabel(
                     comp,
                     "This is a historical version of this " + smaMgr.getSma().getArtifactTypeName() + " and can not be edited; Select \"Open Latest\" to view/edit latest version.");
         label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
      }
   }

   private void createAnnotationsHeader(Composite comp, XFormToolkit toolkit) throws OseeCoreException {
      if (smaMgr.getSma().getAnnotations().size() > 0) {
         new AnnotationComposite(toolkit, comp, SWT.None, smaMgr.getSma());
      }
   }

   public static void createSMANotesHeader(Composite comp, XFormToolkit toolkit, SMAManager smaMgr, int horizontalSpan) throws OseeCoreException {
      // Display SMA Note
      String note = smaMgr.getSma().getSoleAttributeValue(ATSAttributes.SMA_NOTE_ATTRIBUTE.getStoreName(), "");
      if (!note.equals("")) {
         createLabelOrHyperlink(comp, toolkit, horizontalSpan, "Note: " + note, false);
      }
   }

   public static void createStateNotesHeader(Composite comp, XFormToolkit toolkit, SMAManager smaMgr, int horizontalSpan, String forStateName) throws MultipleAttributesExist {
      // Display global Notes
      for (NoteItem noteItem : smaMgr.getNotes().getNoteItems()) {
         if (forStateName == null || noteItem.getState().equals(forStateName)) {
            createLabelOrHyperlink(comp, toolkit, horizontalSpan, noteItem.toString(), false);
         }
      }
   }

   private static void createLabelOrHyperlink(Composite comp, XFormToolkit toolkit, final int horizontalSpan, final String str, boolean onlyState) {
      if (str.length() > 150) {
         Hyperlink label = toolkit.createHyperlink(comp, Strings.truncate(str, 150) + "...", SWT.NONE);
         label.setToolTipText("click to view all");
         label.addListener(SWT.MouseUp, new Listener() {
            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
             */
            public void handleEvent(Event event) {
               new HtmlDialog("Note", null, str).open();
            }
         });
         GridData gd = new GridData(GridData.FILL_HORIZONTAL);
         gd.horizontalSpan = horizontalSpan;
         label.setLayoutData(gd);
      } else {
         Label label = toolkit.createLabel(comp, str);
         GridData gd = new GridData(GridData.FILL_HORIZONTAL);
         gd.horizontalSpan = horizontalSpan;
         label.setLayoutData(gd);
      }
   }

   private void createOriginatorHeader(Composite comp, XFormToolkit toolkit) throws OseeCoreException {
      if (!smaMgr.isCancelled() && !smaMgr.isCompleted()) {
         toolkit.createLabel(comp, "     ");
         Hyperlink link = toolkit.createHyperlink(comp, ORIGINATOR, SWT.NONE);
         link.addHyperlinkListener(new IHyperlinkListener() {

            public void linkEntered(HyperlinkEvent e) {
            }

            public void linkExited(HyperlinkEvent e) {
            }

            public void linkActivated(HyperlinkEvent e) {
               try {
                  if (smaMgr.promptChangeOriginator()) {
                     updateOrigLabel();
                     smaMgr.getEditor().onDirtied();
                  }
               } catch (OseeCoreException ex) {
                  OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });
         if (smaMgr.getOriginator() == null) {
            Label errorLabel = toolkit.createLabel(comp, "Error: No originator identified.");
            errorLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
         } else {
            origLabel = toolkit.createLabel(comp, smaMgr.getOriginator().getName());
            origLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         }
      } else {
         if (smaMgr.getOriginator() == null) {
            Label errorLabel = toolkit.createLabel(comp, "Error: No originator identified.");
            errorLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
         } else {
            Label origLabel = toolkit.createLabel(comp, "     " + ORIGINATOR + smaMgr.getOriginator().getName());
            origLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         }
      }
   }

   public void updateOrigLabel() throws OseeCoreException {
      origLabel.setText(smaMgr.getOriginator().getName());
      origLabel.getParent().layout();
   }

   public void refresh() throws OseeCoreException {
      if (smaMgr.getEditor() != null && !smaMgr.isInTransition()) {
         //         System.out.println("SMAWorkFlowTab refresh...");
         for (SMAWorkFlowSection section : sections) {
            section.dispose();
         }
         atsBody.dispose();
         createBody(body);
         scrolledForm.setText(getEditorInput().getName());
         refreshToolbar();
      }
   }

   public List<AtsWorkPage> getPages() {
      return pages;
   }

   public List<SMAWorkFlowSection> getSections() {
      return sections;
   }

   public SMAWorkFlowSection getSectionForCurrentState() {

      return null;
   }

   public List<XWidget> getXWidgetsFromState(String stateName, Class<?> clazz) {
      for (SMAWorkFlowSection section : sections) {
         if (section.getPage().getName().equals(stateName)) {
            return section.getXWidgets(clazz);
         }
      }
      return Collections.emptyList();
   }

}