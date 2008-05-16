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
package org.eclipse.osee.framework.ui.skynet.export;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.ResourceNode;
import org.eclipse.compare.ZipFileStructureCreator;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.DiffTreeViewer;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.compare.structuremergeviewer.IDiffContainer;
import org.eclipse.compare.structuremergeviewer.IStructureComparator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.exportImport.BranchExporter;
import org.eclipse.osee.framework.ui.plugin.util.OseeData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class ImportBranchVerification {
   private static Logger logger = ConfigUtil.getConfigFactory().getLogger(ImportBranchVerification.class);

   private final File sourceFile;
   private final Branch branch;
   private final boolean includeMainLevelBranch;
   private final boolean includeDescendantBranches;

   public ImportBranchVerification(File sourceFile, Branch branch, boolean includeMainLevelBranch, boolean includeDescendantBranches) {
      this.sourceFile = sourceFile;
      this.branch = branch;
      this.includeDescendantBranches = includeDescendantBranches;
      this.includeMainLevelBranch = includeMainLevelBranch;
   }

   public void execute(final IProgressMonitor monitor) throws Exception {
      final File verificationFile = getVerificationFile();
      try {
         // Export database to a temporary file
         boolean descendantsOnly = (includeMainLevelBranch == false && includeDescendantBranches == true);
         BranchExporter branchExporter =
               new BranchExporter(monitor, verificationFile, branch, new Timestamp(0),
                     GlobalTime.GreenwichMeanTimestamp(), descendantsOnly);
         branchExporter.export();

         // Run Compare
         Display.getDefault().syncExec(new Runnable() {
            public void run() {
               try {
                  processVerification(monitor, verificationFile, sourceFile);
               } catch (Exception ex) {
                  logger.log(Level.SEVERE, ex.toString(), ex);
               }
            }
         });
      } finally {
         verificationFile.delete();
      }
   }

   private void processVerification(IProgressMonitor monitor, File actual, File expected) throws Exception {
      final IFile sourceIfile = OseeData.getIFile("actualTemp.zip", new FileInputStream(actual), true);
      final IFile verIfile = OseeData.getIFile("expectedTemp.zip", new FileInputStream(expected), true);
      try {
         CompareConfiguration cc = new CompareConfiguration();
         ResourceCompareInput resourceCompareInput = new ResourceCompareInput(cc);
         resourceCompareInput.setInput(sourceIfile, verIfile,
               PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
         resourceCompareInput.initializeCompareConfiguration();
         resourceCompareInput.run(monitor);
         Object object = resourceCompareInput.getCompareResult();
         if (object != null) {
            CompareUI.openCompareEditorOnPage(resourceCompareInput,
                  PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage());
         } else {
            MessageDialog.openInformation(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
                  "Importing Onto Branch", "Branch Import Verification: Passed");
         }
      } finally {
         if (sourceIfile != null) {
            sourceIfile.delete(true, new NullProgressMonitor());
         }
         if (verIfile != null) {
            verIfile.delete(true, new NullProgressMonitor());
         }
      }
   }

   private File getVerificationFile() {
      String filePath = sourceFile.getAbsolutePath();
      String extension = Lib.getExtension(filePath);
      filePath = filePath.replaceAll("\\." + extension, "\\.verify\\." + extension);
      return new File(filePath);
   }

   private class ResourceCompareInput extends CompareEditorInput {
      private Object fRoot;
      private IStructureComparator fLeft;
      private IStructureComparator fRight;
      private IResource fLeftResource;
      private IResource fRightResource;
      private DiffTreeViewer fDiffViewer;
      private IAction fOpenAction;

      class MyDiffNode extends DiffNode {

         private boolean fDirty = false;
         private ITypedElement fLastId;
         private String fLastName;

         public MyDiffNode(IDiffContainer parent, int description, ITypedElement ancestor, ITypedElement left, ITypedElement right) {
            super(parent, description, ancestor, left, right);
         }

         public void fireChange() {
            super.fireChange();
            setDirty(true);
            fDirty = true;
            if (fDiffViewer != null) fDiffViewer.refresh(this);
         }

         void clearDirty() {
            fDirty = false;
         }

         public String getName() {
            if (fLastName == null) fLastName = super.getName();
            if (fDirty) return '<' + fLastName + '>';
            return fLastName;
         }

         public ITypedElement getId() {
            ITypedElement id = super.getId();
            if (id == null) return fLastId;
            fLastId = id;
            return id;
         }
      }

      public ResourceCompareInput(CompareConfiguration config) {
         super(config);
      }

      public Viewer createDiffViewer(Composite parent) {
         fDiffViewer = new DiffTreeViewer(parent, getCompareConfiguration()) {
            protected void fillContextMenu(IMenuManager manager) {

               if (fOpenAction == null) {
                  fOpenAction = new Action() {
                     public void run() {
                        handleOpen(null);
                     }
                  };
               }
               boolean enable = false;
               ISelection selection = getSelection();
               if (selection instanceof IStructuredSelection) {
                  IStructuredSelection ss = (IStructuredSelection) selection;
                  if (ss.size() == 1) {
                     Object element = ss.getFirstElement();
                     if (element instanceof MyDiffNode) {
                        ITypedElement te = ((MyDiffNode) element).getId();
                        if (te != null) enable = !ITypedElement.FOLDER_TYPE.equals(te.getType());
                     } else
                        enable = true;
                  }
               }
               fOpenAction.setEnabled(enable);
               manager.add(fOpenAction);

               super.fillContextMenu(manager);
            }
         };
         return fDiffViewer;
      }

      public boolean setInput(IResource resource1, IResource resource2, Shell shell) {
         fLeftResource = resource1;
         fRightResource = resource2;

         fLeft = new ZipFileStructureCreator().getStructure(new ResourceNode(fLeftResource));
         fRight = new ZipFileStructureCreator().getStructure(new ResourceNode(fRightResource));
         return true;
      }

      /**
       * Initializes the images in the compare configuration.
       */
      void initializeCompareConfiguration() {
         CompareConfiguration cc = getCompareConfiguration();
         if (fLeftResource != null) {
            cc.setLeftLabel(buildLabel(fLeftResource));
            cc.setLeftImage(CompareUI.getImage(fLeftResource));
         }
         if (fRightResource != null) {
            cc.setRightLabel(buildLabel(fRightResource));
            cc.setRightImage(CompareUI.getImage(fRightResource));
         }
      }

      public Object prepareInput(IProgressMonitor pm) throws InvocationTargetException {

         try {
            fLeftResource.refreshLocal(IResource.DEPTH_INFINITE, pm);
            fRightResource.refreshLocal(IResource.DEPTH_INFINITE, pm);

            pm.beginTask("Operation in Progress...", IProgressMonitor.UNKNOWN);

            String leftLabel = fLeftResource.getName();
            String rightLabel = fRightResource.getName();
            setTitle(String.format("Two-way compare of [%s] with [%s]", leftLabel, rightLabel));

            Differencer d = new Differencer() {
               protected Object visit(Object parent, int description, Object ancestor, Object left, Object right) {
                  return new MyDiffNode((IDiffContainer) parent, description, (ITypedElement) ancestor,
                        (ITypedElement) left, (ITypedElement) right);
               }
            };

            fRoot = d.findDifferences(false, pm, null, null, fLeft, fRight);
            return fRoot;

         } catch (CoreException ex) {
            throw new InvocationTargetException(ex);
         } finally {
            pm.done();
         }
      }

      public String getToolTipText() {
         if (fLeftResource != null && fRightResource != null) {
            String leftLabel = fLeftResource.getFullPath().makeRelative().toString();
            String rightLabel = fRightResource.getFullPath().makeRelative().toString();
            return String.format("Two-way compare of [%s] with [%s]", leftLabel, rightLabel);
         }
         return super.getToolTipText();
      }

      private String buildLabel(IResource r) {
         String n = r.getFullPath().toString();
         if (n.charAt(0) == IPath.SEPARATOR) return n.substring(1);
         return n;
      }

      public boolean canRunAsJob() {
         return true;
      }
   }
}
