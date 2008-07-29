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
package org.eclipse.osee.ats.health;

import java.io.File;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.util.AFile;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactNameDescriptorCache;
import org.eclipse.osee.framework.skynet.core.revision.AttributeChange;
import org.eclipse.osee.framework.skynet.core.revision.AttributeSummary;
import org.eclipse.osee.framework.skynet.core.revision.RevisionChange;
import org.eclipse.osee.framework.skynet.core.revision.RevisionManager;
import org.eclipse.osee.framework.skynet.core.revision.TransactionData;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.util.OseeData;
import org.eclipse.osee.framework.ui.skynet.branch.BranchContentProvider;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAutoRunAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class ValidateChangeReports extends XNavigateItemAutoRunAction {
   private static ArtifactNameDescriptorCache artifactNameDescriptorCache = new ArtifactNameDescriptorCache();

   /**
    * @param parent
    */
   public ValidateChangeReports(XNavigateItem parent) {
      super(parent, "Validate Change Reports");
   }

   public ValidateChangeReports() {
      this(null);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run(TableLoadOption... tableLoadOptions) throws SQLException {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) return;
      Jobs.startJob(new Report(getName()), true);
   }

   public class Report extends Job {

      public Report(String name) {
         super(name);
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      protected IStatus run(IProgressMonitor monitor) {
         try {
            final XResultData rd = new XResultData(AtsPlugin.getLogger());
            runIt(monitor, rd);
            rd.report(getName());
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, false);
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
         }
         monitor.done();
         return Status.OK_STATUS;
      }
   }

   // Lba SA11 Req Team Workflow
   // Lba B3 Req Team Workflow
   // Lba V11 REU Req Team Workflow
   // Lba V13 Req Team Workflow
   private void runIt(IProgressMonitor monitor, XResultData xResultData) throws OseeCoreException, SQLException {
      StringBuffer sbFull = new StringBuffer(AHTML.beginMultiColumnTable(100, 1));
      String[] columnHeaders = new String[] {"Team", "Working", "Mod", "New", "Del", "Notes"};
      sbFull.append(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
      //      for (String artifactTypeName : TeamWorkflowExtensions.getInstance().getAllTeamWorkflowArtifactNames()) {
      for (String artifactTypeName : new String[] {"Lba V13 Req Team Workflow"}) {
         sbFull.append(AHTML.addRowSpanMultiColumnTable(artifactTypeName, columnHeaders.length));
         StringBuffer sbByType = new StringBuffer(AHTML.beginMultiColumnTable(100, 1));
         sbByType.append(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
         sbByType.append(AHTML.addRowSpanMultiColumnTable(artifactTypeName, columnHeaders.length));
         boolean foundChangeReport = false;
         try {
            int x = 1;
            Collection<Artifact> artifacts =
                  ArtifactQuery.getArtifactsFromType(artifactTypeName, AtsPlugin.getAtsBranch());
            for (Artifact artifact : artifacts) {
               String result = String.format("Processing %s/%s  - %s", x++, artifacts.size(), artifact);
               OSEELog.logInfo(AtsPlugin.class, result, false);
               //               if (!artifact.getHumanReadableId().equals("1PL1U")) continue;
               TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) artifact;
               try {
                  Collection<Change> changes = null;
                  if (teamArt.getSmaMgr().getBranchMgr().isCommittedBranch()) {
                     changes =
                           RevisionManager.getInstance().getChangesPerTransaction(
                                 teamArt.getSmaMgr().getBranchMgr().getTransactionId());
                     foundChangeReport = true;

                  } else if (teamArt.getSmaMgr().getBranchMgr().isWorkingBranch()) {
                     changes =
                           RevisionManager.getInstance().getChangesPerBranch(
                                 teamArt.getSmaMgr().getBranchMgr().getWorkingBranch());
                     foundChangeReport = true;
                  }
                  if (changes != null) {
                     Map<Integer, Artifact> modArt = new HashMap<Integer, Artifact>();
                     Map<Integer, Artifact> delArt = new HashMap<Integer, Artifact>();
                     Map<Integer, Artifact> newArt = new HashMap<Integer, Artifact>();
                     for (Change change : changes) {
                        if (change.getItemKind().equals("Artifact")) {
                           if (change.getModificationType() == ModificationType.CHANGE) {
                              modArt.put(change.getArtifact().getArtId(), change.getArtifact());
                           }
                           if (change.getModificationType() == ModificationType.DELETED) {
                              delArt.put(change.getArtifact().getArtId(), change.getArtifact());
                           }
                           if (change.getModificationType() == ModificationType.NEW) {
                              newArt.put(change.getArtifact().getArtId(), change.getArtifact());
                           }
                        }
                     }
                     Map<Integer, Artifact> oldModArt = new HashMap<Integer, Artifact>();
                     Map<Integer, Artifact> oldDelArt = new HashMap<Integer, Artifact>();
                     Map<Integer, Artifact> oldNewArt = new HashMap<Integer, Artifact>();
                     for (ArtifactChange artifactChange : teamArt.getSmaMgr().getBranchMgr().getArtifactChanges()) {
                        if (artifactChange.getModType() == ModificationType.CHANGE && (artifactChange.getChangeType() == ChangeType.OUTGOING || artifactChange.getChangeType() == ChangeType.CONFLICTING)) {
                           // If there was at least one attribute changed, count it; don't count relation only changes
                           for (Object obj : BranchContentProvider.summarize(RevisionManager.getInstance().getTransactionChanges(
                                 artifactChange, artifactNameDescriptorCache))) {

                              if (obj instanceof AttributeSummary) {
                                 for (AttributeChange attributeChange : ((AttributeSummary) obj).getChanges()) {
                                    if (attributeChange.getChangeType() == ChangeType.OUTGOING) {
                                       oldModArt.put(artifactChange.getArtifact().getArtId(),
                                             artifactChange.getArtifact());
                                    }
                                 }
                              }

                              if (obj instanceof AttributeChange && ((AttributeChange) obj).getChangeType() == ChangeType.OUTGOING) {
                                 oldModArt.put(artifactChange.getArtifact().getArtId(), artifactChange.getArtifact());
                              }
                           }
                        }

                        boolean newAndDeleted = false;
                        if (artifactChange.getModType() == ModificationType.DELETED) {
                           if (artifactChange.getArtifact().getArtId() == 244865) {
                              int i = 1;
                           }
                           for (TransactionData transactionData : RevisionManager.getInstance().getTransactionsPerArtifact(
                                 artifactChange.getArtifact(), false)) {

                              for (RevisionChange revisionChange : RevisionManager.getInstance().getTransactionChanges(
                                    transactionData)) {
                                 if (teamArt.getSmaMgr().getBranchMgr().isWorkingBranch() && revisionChange instanceof ArtifactChange && revisionChange.getModType() == ModificationType.NEW) {
                                    newAndDeleted = true;
                                    break;
                                 }
                              }
                              if (newAndDeleted) {
                                 break;
                              }
                           }
                           if (!newAndDeleted) {
                              oldDelArt.put(artifactChange.getArtifact().getArtId(), artifactChange.getArtifact());
                           }
                        }
                        if (artifactChange.getModType() == ModificationType.NEW) {
                           oldNewArt.put(artifactChange.getArtifact().getArtId(), artifactChange.getArtifact());
                        }
                     }
                     boolean modMismatch = false;
                     boolean newMismatch = false;
                     boolean delMismatch = false;
                     StringBuffer notes = new StringBuffer();

                     for (Integer modArtifact : modArt.keySet()) {
                        if (!oldModArt.containsKey(modArtifact)) {
                           notes.append(" old mod missing: " + modArt.get(modArtifact).getDescriptiveName() + "<br>");
                        }
                     }

                     for (Integer oldModArtifact : oldModArt.keySet()) {
                        if (!modArt.containsKey(oldModArtifact)) {
                           notes.append(" new mod missing: " + oldModArt.get(oldModArtifact).getDescriptiveName() + "<br>");
                        }
                     }

                     for (Integer newArtifact : newArt.keySet()) {
                        if (!oldNewArt.containsKey(newArtifact)) {
                           notes.append(" old new missing: " + newArt.get(newArtifact).getDescriptiveName() + "<br>");
                        }
                     }

                     for (Integer oldNewArtifact : oldNewArt.keySet()) {
                        if (!newArt.containsKey(oldNewArtifact)) {
                           notes.append(" new new missing: " + oldNewArt.get(oldNewArtifact).getDescriptiveName() + "<br>");
                        }
                     }

                     for (Integer delArtifact : delArt.keySet()) {
                        if (!oldDelArt.containsKey(delArtifact)) {
                           notes.append(" old del missing: " + delArt.get(delArtifact).getInternalDescriptiveName() + "<br>");
                        }
                     }

                     for (Integer oldDelArtifact : oldDelArt.keySet()) {
                        if (!delArt.containsKey(oldDelArtifact)) {
                           notes.append(" new del missing: " + oldDelArt.get(oldDelArtifact).getInternalDescriptiveName() + "<br>");
                        }
                     }

                     if (modArt.size() != oldModArt.size()) modMismatch = true;
                     if (newArt.size() != oldNewArt.size()) newMismatch = true;
                     if (delArt.size() != oldDelArt.size()) delMismatch = true;
                     String str =
                           AHTML.addRowMultiColumnTable(new String[] {
                                 teamArt.getHumanReadableId(),
                                 teamArt.getSmaMgr().getBranchMgr().isWorkingBranch() ? "Working" : "Committed",
                                 String.format("%s%d/%d", (modMismatch ? "Error: " : ""), modArt.size(),
                                       oldModArt.size()),
                                 String.format("%s%d/%d", (newMismatch ? "Error: " : ""), newArt.size(),
                                       oldNewArt.size()),
                                 String.format("%s%d/%d", (delMismatch ? "Error: " : ""), delArt.size(),
                                       oldDelArt.size()), notes.toString()});
                     sbFull.append(str);
                     sbByType.append(str);

                  }
               } catch (Exception ex) {
                  sbFull.append(AHTML.addRowSpanMultiColumnTable(
                        "Artifact " + artifact.getHumanReadableId() + " - Exception: " + ex.getLocalizedMessage(),
                        columnHeaders.length));
                  sbByType.append(AHTML.addRowSpanMultiColumnTable(
                        "Artifact " + artifact.getHumanReadableId() + " - Exception: " + ex.getLocalizedMessage(),
                        columnHeaders.length));
               }
            }

         } catch (Exception ex) {
            sbFull.append(AHTML.addRowSpanMultiColumnTable("Exception: " + ex.getLocalizedMessage(),
                  columnHeaders.length));
         }
         // report results for this artifactTypeName if any change report was detected
         // this gives results as you go instead of waiting for final report
         if (foundChangeReport) {
            sbByType.append(AHTML.endMultiColumnTable());
            XResultData xResultdata = new XResultData(AtsPlugin.getLogger());
            xResultdata.addRaw(sbByType.toString().replaceAll("\n", ""));
            xResultdata.report("Change Report Test for " + artifactTypeName);
            File file = OseeData.getFile(artifactTypeName + ".html");
            AFile.writeFile(file, sbFull.toString());
            System.out.println("Report saved to " + file);
         }
      }
      sbFull.append(AHTML.endMultiColumnTable());
      xResultData.addRaw(sbFull.toString().replaceAll("\n", ""));
   }
}
