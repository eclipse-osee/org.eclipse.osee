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

package org.eclipse.osee.ats.navigate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.world.search.MultipleHridSearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.ArtifactDecorator;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.ArtifactViewerSorter;
import org.eclipse.osee.framework.ui.skynet.util.filteredTree.SimpleCheckFilteredTreeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class OpenChangeReportByIdItem extends XNavigateItemAction {

   /**
    * @param parent
    * @param teamDefHoldingVersions Team Definition Artifact that is related to versions or null for popup selection
    */
   public OpenChangeReportByIdItem(XNavigateItem parent) {
      super(parent, "Open Change Report by ID", AtsImage.OPEN_BY_ID);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException {
      try {
         final MultipleHridSearchItem srch = new MultipleHridSearchItem(getName());
         srch.performUI(SearchType.Search);
         if (srch.isCancelled()) return;
         String name = "Open Change Report by ID: \"" + srch.getEnteredIds() + "\"";
         Job openJob = new Job(name) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
               try {
                  Collection<Artifact> artifacts = srch.performSearchGetResults(false);
                  final Set<Artifact> addedArts = new HashSet<Artifact>();
                  for (Artifact artifact : artifacts) {
                     if (artifact instanceof ActionArtifact) {
                        for (TeamWorkFlowArtifact team : ((ActionArtifact) artifact).getTeamWorkFlowArtifacts()) {
                           if (team.getSmaMgr().getBranchMgr().isCommittedBranchExists() || team.getSmaMgr().getBranchMgr().isWorkingBranchInWork()) {
                              addedArts.add(team);
                           }
                        }
                     }
                  }
                  if (addedArts.size() == 1) {
                     Displays.ensureInDisplayThread(new Runnable() {
                        @Override
                        public void run() {
                           for (Artifact art : addedArts) {
                              ((StateMachineArtifact) art).getSmaMgr().getBranchMgr().showChangeReport();
                           }
                        }
                     });
                  } else if (addedArts.size() > 0) {
                     Displays.ensureInDisplayThread(new Runnable() {
                        @Override
                        public void run() {
                           ArtifactDecorator artDecorator = new ArtifactDecorator("");
                           artDecorator.addActions(null, null);
                           artDecorator.setShowArtBranch(true);
                           artDecorator.setShowArtType(true);
                           SimpleCheckFilteredTreeDialog dialog =
                                 new SimpleCheckFilteredTreeDialog("Select Available Change Reports",
                                       "Select available Change Reports to run.", new ArrayTreeContentProvider(),
                                       new ArtifactLabelProvider(artDecorator), new ArtifactViewerSorter(), 0,
                                       Integer.MAX_VALUE);
                           dialog.setInput(addedArts);
                           if (dialog.open() == 0) {
                              if (dialog.getResult().length == 0) {
                                 return;
                              }
                              for (Object obj : dialog.getResult()) {
                                 ((StateMachineArtifact) obj).getSmaMgr().getBranchMgr().showChangeReport();
                              }
                           }
                        }
                     });
                  } else {
                     Displays.ensureInDisplayThread(new Runnable() {
                        @Override
                        public void run() {
                           MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Open Change Reports",
                                 "No change report exists for " + srch.getEnteredIds());
                        }
                     });
                  }
               } catch (Exception ex) {
                  OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
                  return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
               }
               return Status.OK_STATUS;
            }
         };
         Jobs.startJob(openJob);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }

   }
}
