/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.IXWidgetInputAddable;

/**
 * Typically used to parse GUIDs copied from another application via text-clipboard. Operation extracts input from
 * <code>guidData</code> argument and attempts to find artifacts referenced by GUIDs on argument branch. The result list
 * of artifacts is then passed to IXWidgetInputAddable-type widget.
 *
 * @author Karol M. Wilk
 */
public class StringGuidsToArtifactListOperation extends AbstractOperation {

   private final String rawGuidsData;
   private final BranchId branch;
   private final IXWidgetInputAddable widget;

   public final static String splitRegex = "\\s+";

   private final static String taskName = "Mapping GUIDs to Artifacts...";
   private final static String subTaskName = "Retrieving Artifacts from cache and/or database...";

   /**
    * @param guidData string data of form <code>GUID1\\s+GUID2\\s+...GUIDN\\s+</code> separated by <code>\\s+</code>
    * @param branch on which the artifacts live on
    * @param widget accepting input by implementing <code>IXWidgetInputAddable</code> interface
    */
   public StringGuidsToArtifactListOperation(OperationLogger logger, String guidData, BranchId branch, IXWidgetInputAddable widget) {
      super(taskName, Activator.PLUGIN_ID, logger);
      this.rawGuidsData = guidData;
      this.branch = branch;
      this.widget = widget;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      if (Strings.isValid(rawGuidsData) && branch != null && widget != null) {
         //Arbitrary number accounting for querying the cache and db retrieval
         int costOfArtifactRetrieval = 5;

         String[] guids = this.rawGuidsData.split(splitRegex);
         monitor.beginTask(taskName, guids.length + costOfArtifactRetrieval);

         List<String> validGuids = new ArrayList<>(guids.length);
         final Collection<Object> artifacts = new ArrayList<>(guids.length); //widget accepts Collection<Object>

         for (int guidIndex = 0; !monitor.isCanceled() && guidIndex < guids.length; guidIndex++) {
            if (GUID.isValid(guids[guidIndex])) {
               validGuids.add(guids[guidIndex]);
            }
            monitor.worked(guidIndex + 1);
         }

         try {
            //written to minimize calls to db VS individuals gets (+cost of overhead)
            if (!monitor.isCanceled()) {
               monitor.subTask(subTaskName);
               monitor.beginTask(subTaskName, costOfArtifactRetrieval);
               artifacts.addAll(ArtifactQuery.getArtifactListFromIds(validGuids, this.branch));
               monitor.done();
               monitor.worked(costOfArtifactRetrieval);
            }
         } catch (Exception ex) {
            getLogger().log(ex);
         }

         monitor.done();

         widget.addToInput(artifacts);
      } else {
         getLogger().logf("Problem with arguments for this operation: %s",
            Strings.buildStatment(Arrays.asList(new String[] {"rawGuidsData", "branch", "widget"})), " or ");
         monitor.setCanceled(true);
         monitor.done();
      }
   }
}
