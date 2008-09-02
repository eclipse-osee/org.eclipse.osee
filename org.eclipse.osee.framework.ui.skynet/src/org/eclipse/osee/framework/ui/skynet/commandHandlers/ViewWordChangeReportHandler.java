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
package org.eclipse.osee.framework.ui.skynet.commandHandlers;

import static org.eclipse.osee.framework.skynet.core.change.ModificationType.DELETED;
import static org.eclipse.osee.framework.skynet.core.change.ModificationType.NEW;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.WholeDocumentRenderer;
import org.eclipse.osee.framework.ui.skynet.render.WordRenderer;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.ui.PlatformUI;

/**
 * @author Paul K. Waldfogel
 */
public class ViewWordChangeReportHandler extends AbstractHandler {
   private static final AccessControlManager accessControlManager = AccessControlManager.getInstance();
   private static final ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();
   private static final String DIFF_ARTIFACT = "DIFF_ARTIFACT";
   private Map<Integer, ArtifactChange> artifactChangeMap = new HashMap<Integer, ArtifactChange>();

   public ViewWordChangeReportHandler() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      ArrayList<Artifact> baseArtifacts = new ArrayList<Artifact>(artifactChangeMap.size());
      ArrayList<Artifact> newerArtifacts = new ArrayList<Artifact>(artifactChangeMap.size());

      for (ArtifactChange artifactChange : artifactChangeMap.values()) {
         try {
            Artifact baseArtifact =
                  artifactChange.getModType() == NEW ? null : artifactManager.getArtifactFromId(
                        artifactChange.getArtifact().getArtId(), artifactChange.getBaselineTransactionId());
            Artifact newerArtifact =
                  artifactChange.getModType() == DELETED ? null : artifactManager.getArtifactFromId(
                        artifactChange.getArtifact().getArtId(), artifactChange.getToTransactionId());

            baseArtifacts.add(baseArtifact);
            newerArtifacts.add(newerArtifact);
         } catch (Exception e1) {
            OSEELog.logException(getClass(), e1, true);
         }
      }

      if (newerArtifacts.size() == 0 || (baseArtifacts.size() != newerArtifacts.size())) {
         throw new IllegalArgumentException(
               "base artifacts size: " + baseArtifacts.size() + " must match newer artifacts size: " + newerArtifacts.size() + ".");
      } else {
         IRenderer myIRenderer =
               RendererManager.getInstance().getBestRenderer(PresentationType.DIFF,
                     baseArtifacts.get(0) == null ? newerArtifacts.get(0) : baseArtifacts.get(0));
         if (myIRenderer instanceof WholeDocumentRenderer) {
            JobFamily aFamilyMember =
                  new JobFamily(baseArtifacts.get(0), newerArtifacts.get(newerArtifacts.size() - 1), DIFF_ARTIFACT,
                        "Diff", newerArtifacts.get(newerArtifacts.size() - 1).getDescriptiveName());
            aFamilyMember.schedule();
         } else {
            //All other artifacts types can be rendered by the wordRenderer so the are displayed in the word change report.
            WordRenderer renderer =
                  (WordRenderer) RendererManager.getInstance().getRendererById(WordRenderer.WORD_RENDERER_EXTENSION);
            try {
	               renderer.compareArtifacts(baseArtifacts,newerArtifacts,DIFF_ARTIFACT,null,
	                       baseArtifacts.get(0) == null ? newerArtifacts.get(0).getBranch() : baseArtifacts.get(0).getBranch());  
            } catch (CoreException ex) {
               OSEELog.logException(getClass(), ex, true);
            } catch (Exception ex) {
               OSEELog.logException(getClass(), ex, true);
            }
         }
      }
      return null;
   }

   @Override
   public boolean isEnabled() {
      if (PlatformUI.getWorkbench().isClosing()) {
         return false;
      }

      artifactChangeMap.clear();
      List<Artifact> artifacts = new LinkedList<Artifact>();
      boolean isEnabled = false;

      try {
         ISelectionProvider selectionProvider =
               AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider();

         if (selectionProvider != null && selectionProvider.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
            List<ArtifactChange> artifactChanges =
                  Handlers.getArtifactChangesFromStructuredSelection(structuredSelection);

            for (ArtifactChange artifactChange : artifactChanges) {
               artifacts.add(artifactChange.getArtifact());
               artifactChangeMap.put(artifactChange.getArtifact().getArtId(), artifactChange);
            }
            isEnabled = accessControlManager.checkObjectListPermission(artifacts, PermissionEnum.READ);
         }
      } catch (Exception ex) {
         OSEELog.logException(getClass(), ex, true);
      }

      return isEnabled;
   }
   public class FamilyMember extends Job {
      private String lastName;
      private Artifact firstArtifact;
      private Artifact secondArtifact;;
      private String diffOption;
      private IProgressMonitor monitor;
      private List<String> JournalList = new ArrayList<String>();

      public FamilyMember(Artifact firstArtifact, Artifact secondArtifact, String diffOption, String firstName, String lastName) {
         super(firstName + " " + lastName);
         this.lastName = lastName;
         this.firstArtifact = firstArtifact;
         this.secondArtifact = secondArtifact;
         this.diffOption = diffOption;
         this.diffOption = diffOption;
      }

      protected IStatus run(IProgressMonitor monitor) {
         this.monitor = monitor;
         try {
            IRenderer myIRenderer =
                  RendererManager.getInstance().getBestRenderer(PresentationType.DIFF,
                        firstArtifact != null ? firstArtifact : secondArtifact);
            myIRenderer.compare(firstArtifact, secondArtifact, diffOption, monitor, null, PresentationType.DIFF);
         } catch (Exception e) {
            JournalList.add(e.getMessage());
         }
         return Status.OK_STATUS;
      }

      public IProgressMonitor getIProgressMonitor() {
         return monitor;
      }

      public boolean belongsTo(Object family) {
         return lastName.equals(family);
      }

      public List<String> getJounalList() {
         return JournalList;
      }

   }
   public class JobFamily extends Job {
      private String lastName;
      private Artifact firstArtifact;
      private Artifact secondArtifact;;
      private String diffOption;
      private IProgressMonitor monitor;
      private List<String> JournalList = new ArrayList<String>();

      public JobFamily(Artifact firstArtifact, Artifact secondArtifact, String diffOption, String firstName, String lastName) {
         super(firstName + " " + lastName);
         this.lastName = lastName;
         this.firstArtifact = firstArtifact;
         this.secondArtifact = secondArtifact;
         this.diffOption = diffOption;
      }

      protected IStatus run(IProgressMonitor monitor) {
         this.monitor = monitor;
         try {
            IRenderer myIRenderer =
                  RendererManager.getInstance().getBestRenderer(PresentationType.DIFF,
                        firstArtifact != null ? firstArtifact : secondArtifact);
            myIRenderer.compare(firstArtifact, secondArtifact, diffOption, monitor, null, PresentationType.DIFF);
         } catch (Exception ex) {
            OSEELog.logException(getClass(), ex, true);
            JournalList.add(ex.getMessage());
         }
         return Status.OK_STATUS;
      }

      public IProgressMonitor getIProgressMonitor() {
         return monitor;
      }

      public boolean belongsTo(Object family) {
         return lastName.equals(family);
      }

      public List<String> getJounalList() {
         return JournalList;
      }

   }

}
