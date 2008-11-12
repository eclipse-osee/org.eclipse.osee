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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.ILoggerListener;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.util.OseeData;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.XBranchSelectWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationOrderAnalysisOnBranch extends AbstractBlam {
   private static Matcher getIds = Pattern.compile(".*?artId\\[(\\d+?)\\].*?relTypeId\\[(\\d+?)\\].*").matcher("");
   final List<String> messages = new ArrayList<String>();
   private Map<Branch, Map<Integer, ArtifactOrder>> branchToArtifactsToSort =
         new HashMap<Branch, Map<Integer, ArtifactOrder>>();
   private Branch currentBranch;

   private class ArtifactOrder {
      Set<Integer> unsortedRelTypeIds = new HashSet<Integer>();
   }

   private ILoggerListener listener = new ILoggerListener() {

      @Override
      public void log(String loggerName, Level level, String message, Throwable th) {
         if (loggerName.equals(RelationManager.class.getName())) {
            messages.add(message);
            getIds.reset(message);
            if (getIds.matches()) {
               int artId = Integer.parseInt(getIds.group(1));
               int relTypeId = Integer.parseInt(getIds.group(2));
               ArtifactOrder artOrder = branchToArtifactsToSort.get(currentBranch).get(artId);
               if (artOrder == null) {
                  artOrder = new ArtifactOrder();
                  branchToArtifactsToSort.get(currentBranch).put(artId, artOrder);
               }
               artOrder.unsortedRelTypeIds.add(relTypeId);
            }
         }
      }

   };

   /*
    * (non-Javadoc)
    * 
    * @seeorg.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#
    * runOperation(org.eclipse.osee.framework.ui.skynet.blam.VariableMap,
    * org.eclipse.osee.framework.skynet.core.artifact.Branch,
    * org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      appendResultLine("\nCurrent Status:\n\n");
      for (Branch branch : branchToArtifactsToSort.keySet()) {
         SkynetTransaction transaction = new SkynetTransaction(branch);
         appendResultLine(String.format("We have %d artifacts that have unsorted relations on branch %s.",
               branchToArtifactsToSort.get(branch).size(), branch.getBranchName()));

         Map<Integer, ArtifactOrder> artifactOrder = branchToArtifactsToSort.get(branch);
         for (Integer artId : artifactOrder.keySet()) {
            ArtifactOrder relTypes = artifactOrder.get(artId);
            Artifact art = ArtifactCache.getActive(artId, branch);
            if (art != null) {
               for (Integer relTypeId : relTypes.unsortedRelTypeIds) {
                  List<RelationLink> relations = art.getRelations(RelationTypeManager.getType(relTypeId));
                  int lastArtId = -1;
                  for (RelationLink link : relations) {
                     if (!link.isDeleted()) {
                        System.out.println(link.getOrder(link.getSide(art).oppositeSide()));
                        if (link.getOrder(link.getSide(art).oppositeSide()) != lastArtId) {
                           link.setOrder(link.getSide(art).oppositeSide(), lastArtId);
                        }
                        lastArtId = link.getArtifactId(link.getSide(art).oppositeSide());
                     }
                  }
               }
               art.persistRelations(transaction);
            }
         }
         System.out.println("stop");
         transaction.execute();
      }
   }

   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XText\" displayName=\"Branch List\" /><XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Branch\" /></xWidgets>";
   }

   /*
    * (non-Javadoc)
    * 
    * @seeorg.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#
    * getDescriptionUsage()
    */
   @Override
   public String getDescriptionUsage() {
      return "This will analyze the selected branch for unsorted artifacts and save that report to .osee.data/relationOrder_<branch_name>.txt.  It also allows you to save all of the relations that are unsorted based on their current order in memory.";
   }

   /*
    * (non-Javadoc)
    * 
    * @seeorg.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#
    * widgetCreated(org.eclipse.osee.framework.ui.skynet.widgets.XWidget,
    * org.eclipse.ui.forms.widgets.FormToolkit,
    * org.eclipse.osee.framework.skynet.core.artifact.Artifact,
    * org.eclipse.osee
    * .framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout,
    * org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener, boolean)
    */
   @Override
   public void widgetCreated(XWidget widget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
      super.widgetCreated(widget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (widget.getLabel().equals("Branch")) {
         final XBranchSelectWidget branchSelection = (XBranchSelectWidget) widget;

         branchSelection.addListener(new Listener() {

            @Override
            public void handleEvent(Event event) {
               final Branch branch = branchSelection.getData();
               currentBranch = branch;
               branchToArtifactsToSort.clear();//do this so we can only do one branch - it makes the txs easier
               Map<Integer, ArtifactOrder> artifactsToSort = branchToArtifactsToSort.get(currentBranch);
               if (artifactsToSort == null) {
                  artifactsToSort = new HashMap<Integer, ArtifactOrder>();
                  branchToArtifactsToSort.put(currentBranch, artifactsToSort);
               } else {
                  artifactsToSort.clear();
               }

               Jobs.startJob(new Job(String.format("Analyizing %s for unsorted relations.", branch.getBranchName())) {

                  @Override
                  protected IStatus run(IProgressMonitor monitor) {
                     try {

                        OseeLog.registerLoggerListener(listener);
                        ArtifactQuery.reloadArtifactsFromBranch(branch, false);
                        OseeLog.unregisterLoggerListener(listener);

                        File branchReport =
                              OseeData.getFile(String.format("relationOrder_%s.txt", branch.getBranchShortName()));
                        FileOutputStream fos = new FileOutputStream(branchReport);
                        for (String msg : messages) {
                           fos.write(msg.getBytes());
                           fos.write("\n".getBytes());
                        }
                        fos.close();
                        messages.clear();
                     } catch (Throwable th) {
                        OseeLog.log(RelationOrderAnalysisOnBranch.class, Level.SEVERE, th);
                        return Status.CANCEL_STATUS;
                     }
                     return Status.OK_STATUS;
                  }
               });
            }
         });
      }
   }

}