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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.osee.ats.core.client.config.AtsBulkLoad;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.world.IWorldViewerEventHandler;
import org.eclipse.osee.ats.world.WorldXViewer;
import org.eclipse.osee.ats.world.WorldXViewerEventManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.skynet.util.LoadingComposite;
import org.eclipse.osee.framework.ui.skynet.util.OseeDictionary;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ExceptionComposite;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Donald G. Dunne
 */
public class SMAMembersTab extends FormPage implements IWorldViewerEventHandler {
   private final AbstractWorkflowArtifact awa;
   private IManagedForm managedForm;
   private Composite bodyComp;
   private SMAGoalMembersSection smaGoalMembersSection;
   private LoadingComposite loadingComposite;
   public final static String ID = "ats.members.tab";
   private final SMAEditor editor;
   private static Map<String, Integer> guidToScrollLocation = new HashMap<String, Integer>();

   public SMAMembersTab(SMAEditor editor, AbstractWorkflowArtifact awa) {
      super(editor, ID, "Members");
      this.editor = editor;
      this.awa = awa;
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      this.managedForm = managedForm;
      try {
         managedForm.getForm().addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
               storeScrollLocation();
            }
         });
         updateTitleBar();

         bodyComp = managedForm.getForm().getBody();
         GridLayout gridLayout = new GridLayout(1, false);
         bodyComp.setLayout(gridLayout);
         GridData gd = new GridData(SWT.LEFT, SWT.LEFT, true, true);
         bodyComp.setLayoutData(gd);

         setLoading(true);
         refreshData();
         WorldXViewerEventManager.add(this);
      } catch (Exception ex) {
         handleException(ex);
      }
   }

   private void updateTitleBar() throws OseeCoreException {
      if (managedForm != null && Widgets.isAccessible(managedForm.getForm())) {
         String titleString = editor.getTitleStr();
         String displayableTitle = Strings.escapeAmpersands(titleString);
         managedForm.getForm().setText(displayableTitle);
         managedForm.getForm().setImage(ArtifactImageManager.getImage(awa));
      }
   }

   @Override
   public void showBusy(boolean busy) {
      super.showBusy(busy);
      if (getManagedForm() != null && Widgets.isAccessible(getManagedForm().getForm())) {
         getManagedForm().getForm().getForm().setBusy(busy);
      }
   }

   public void refreshData() {
      if (Widgets.isAccessible(bodyComp)) {
         Operations.executeAsJob(AtsBulkLoad.getConfigLoadingOperation(), true, Job.LONG, new ReloadJobChangeAdapter(
            editor));
         // Don't put in operation cause doesn't have to be loaded before editor displays
         OseeDictionary.load();
      }
   }
   private final class ReloadJobChangeAdapter extends JobChangeAdapter {

      private final SMAEditor editor;

      private ReloadJobChangeAdapter(SMAEditor editor) {
         this.editor = editor;
         showBusy(true);
      }

      @Override
      public void done(IJobChangeEvent event) {
         super.done(event);
         Job job = new UIJob("Draw Workflow Tab") {

            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
               try {
                  updateTitleBar();
                  setLoading(false);
                  createMembersBody();
                  smaGoalMembersSection.reload();
                  jumptoScrollLocation();
                  FormsUtil.addHeadingGradient(editor.getToolkit(), managedForm.getForm(), true);
                  editor.onDirtied();
               } catch (OseeCoreException ex) {
                  handleException(ex);
               } finally {
                  showBusy(false);
               }
               return Status.OK_STATUS;
            }
         };
         Operations.scheduleJob(job, false, Job.SHORT, null);
      }
   }

   private void handleException(Exception ex) {
      setLoading(false);
      if (Widgets.isAccessible(smaGoalMembersSection)) {
         smaGoalMembersSection.dispose();
      }
      OseeLog.log(Activator.class, Level.SEVERE, ex);
      new ExceptionComposite(bodyComp, ex);
      bodyComp.layout();
   }

   private void setLoading(boolean set) {
      if (set) {
         loadingComposite = new LoadingComposite(bodyComp);
         bodyComp.layout();
      } else {
         if (Widgets.isAccessible(loadingComposite)) {
            loadingComposite.dispose();
         }
      }
      showBusy(set);
   }

   private void createMembersBody() {
      if (!Widgets.isAccessible(smaGoalMembersSection)) {

         smaGoalMembersSection =
            new SMAGoalMembersSection("workflow.edtor.members.tab", editor, bodyComp, SWT.NONE, null);
         smaGoalMembersSection.layout();
         smaGoalMembersSection.setFocus();

         smaGoalMembersSection.getWorldComposite().getWorldXViewer().getTree().addListener(SWT.MouseWheel,
            new Listener() {

               @Override
               public void handleEvent(Event event) {
                  ScrolledComposite sc = managedForm.getForm();
                  Point origin = sc.getOrigin();
                  origin.y -= event.count * 16;
                  sc.setOrigin(origin);
               }
            });
      }
   }

   private void jumptoScrollLocation() {
      // Jump to scroll location if set
      Integer selection = guidToScrollLocation.get(awa.getGuid());
      if (selection != null) {
         JumpScrollbarJob job = new JumpScrollbarJob("");
         job.schedule(500);
      }
   }

   @Override
   public void dispose() {
      if (smaGoalMembersSection != null) {
         smaGoalMembersSection.dispose();
      }
      if (editor.getToolkit() != null) {
         editor.getToolkit().dispose();
      }
   }

   private final Control control = null;

   private void storeScrollLocation() {
      if (managedForm != null && managedForm.getForm() != null) {
         Integer selection = managedForm.getForm().getVerticalBar().getSelection();
         // System.out.println("Storing selection => " + selection);
         guidToScrollLocation.put(awa.getGuid(), selection);
      }
   }

   private class JumpScrollbarJob extends Job {
      public JumpScrollbarJob(String name) {
         super(name);
      }

      @Override
      protected IStatus run(IProgressMonitor monitor) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               Integer selection = guidToScrollLocation.get(awa.getGuid());
               // System.out.println("Restoring selection => " + selection);

               // Find the ScrolledComposite operating on the control.
               ScrolledComposite sComp = null;
               if (control == null || control.isDisposed()) {
                  return;
               }
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

   public void refresh() {
      if (editor != null && !awa.isInTransition()) {
         // add pages back
         refreshData();
      }
   }

   public SMAGoalMembersSection getSmaGoalMembersSection() {
      return smaGoalMembersSection;
   }

   @Override
   public WorldXViewer getWorldXViewer() {
      return smaGoalMembersSection.getWorldComposite().getWorldXViewer();
   }

   @Override
   public void removeItems(Collection<? extends Object> objects) {
      for (Object obj : objects) {
         if (obj instanceof EventBasicGuidArtifact) {
            EventBasicGuidArtifact guidArt = (EventBasicGuidArtifact) obj;
            if (guidArt.getModType() == EventModType.Purged) {
               refresh();
               return;
            }
         }
      }
   }

   @Override
   public void relationsModifed(Collection<Artifact> relModifiedArts) {
      if (relModifiedArts.contains(awa)) {
         refresh();
      }
   }

   @Override
   public boolean isDisposed() {
      return editor.isDisposed();
   }

}