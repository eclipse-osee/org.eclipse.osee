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
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.svn.VersionControl;
import org.eclipse.osee.framework.ui.plugin.util.AJavaProject;
import org.eclipse.osee.framework.ui.plugin.util.AWorkspace;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ResourceTransfer;

/**
 * @author Donald G. Dunne
 */
public class XTextResourceDropDam extends XTextDam {

   /**
    * @param displayLabel
    */
   public XTextResourceDropDam(String displayLabel) {
      super(displayLabel);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XTextDam#createWidgets(org.eclipse.swt.widgets.Composite,
    *      int, boolean)
    */
   @Override
   public void createWidgets(Composite parent, int horizontalSpan, boolean fillText) {
      super.createWidgets(parent, horizontalSpan, fillText);
      setupDragAndDropSupport();
   }

   private void setupDragAndDropSupport() {

      // Do not allow drop if default branch is not same as artifacts that reside in this table
      DropTarget target = new DropTarget(getStyledText(), DND.DROP_COPY);
      target.setTransfer(new Transfer[] {ResourceTransfer.getInstance(), FileTransfer.getInstance(),
            TextTransfer.getInstance(), ArtifactTransfer.getInstance()});
      target.addDropListener(new DropTargetAdapter() {

         public void drop(DropTargetEvent event) {
            performDrop(event);
         }

         public void dragOver(DropTargetEvent event) {
            event.detail = DND.DROP_COPY;
         }

         public void dropAccept(DropTargetEvent event) {
         }
      });
   }

   private void performDrop(DropTargetEvent e) {
      Set<String> strs = new HashSet<String>();
      if (e.data instanceof String) {
         strs.add((String) e.data);
      } else if (e.data instanceof String[]) {
         for (String str : (String[]) e.data)
            strs.add(str);
      } else if (e.data instanceof IResource[]) {
         IResource res[] = (IResource[]) e.data;
         for (Object obj : res) {
            StringBuffer sb = new StringBuffer();
            if (obj instanceof IFile) {
               IFile iFile = (IFile) obj;
               if (iFile != null) {
                  File file = AWorkspace.iFileToFile(iFile);
                  try {
                     String javaPkg = AJavaProject.getJavaPackage(file);
                     if (javaPkg != null && !javaPkg.equals("")) sb.append(javaPkg + " - ");
                  } catch (Exception ex) {
                     // do nothing
                  }
                  sb.append(iFile.getName());
                  String ver = VersionControl.getInstance().getRepositoryEntry(file).getVersion();
                  if (ver != null) {
                     if (ver.equals("unknown")) ver = "enter version here";
                     sb.append(" (" + ver + ")");
                  }
               }
            }
            if (!sb.toString().equals("")) strs.add(sb.toString());
         }
      }
      for (String str : strs) {
         append(str + "\n");
      }
      refresh();
   }

}
