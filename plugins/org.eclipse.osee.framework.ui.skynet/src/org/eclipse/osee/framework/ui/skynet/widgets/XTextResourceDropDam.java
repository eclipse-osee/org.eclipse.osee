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

package org.eclipse.osee.framework.ui.skynet.widgets;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.ui.ws.AJavaProject;
import org.eclipse.osee.framework.ui.ws.AWorkspace;
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

   public XTextResourceDropDam(String displayLabel) {
      super(displayLabel);
   }

   @Override
   public void createControls(Composite parent, int horizontalSpan, boolean fillText) {
      super.createControls(parent, horizontalSpan, fillText);
      setupDragAndDropSupport();
   }

   private void setupDragAndDropSupport() {

      // Do not allow drop if default branch is not same as artifacts that reside in this table
      DropTarget target = new DropTarget(getStyledText(), DND.DROP_COPY);
      target.setTransfer(new Transfer[] {
         ResourceTransfer.getInstance(),
         FileTransfer.getInstance(),
         TextTransfer.getInstance(),
         ArtifactTransfer.getInstance()});
      target.addDropListener(new DropTargetAdapter() {

         @Override
         public void drop(DropTargetEvent event) {
            performDrop(event);
         }

         @Override
         public void dragOver(DropTargetEvent event) {
            event.detail = DND.DROP_COPY;
         }

         @Override
         public void dropAccept(DropTargetEvent event) {
            // do nothing
         }
      });
   }

   private void performDrop(DropTargetEvent e) {
      Set<String> strs = new HashSet<>();
      if (e.data instanceof String) {
         strs.add((String) e.data);
      } else if (e.data instanceof String[]) {
         for (String str : (String[]) e.data) {
            strs.add(str);
         }
      } else if (e.data instanceof IResource[]) {
         IResource res[] = (IResource[]) e.data;
         for (Object obj : res) {
            StringBuffer sb = new StringBuffer();
            if (obj instanceof IFile) {
               IFile iFile = (IFile) obj;
               File file = AWorkspace.iFileToFile(iFile);
               try {
                  String javaPkg = AJavaProject.getJavaPackage(file);
                  if (Strings.isValid(javaPkg)) {
                     sb.append(javaPkg);
                     sb.append(" - ");
                  }
               } catch (Exception ex) {
                  // do nothing
               }
               sb.append(iFile.getName());
               String ver = "unknown"; //TODO properly abstract out version control (team providers?) so that we can get the version - VersionControl.getInstance().getRepositoryEntry(file).getVersion();
               if (ver.equals("unknown")) {
                  ver = "enter version here";
               }
               sb.append(" (");
               sb.append(ver);
               sb.append(")");
            }
            if (!sb.toString().equals("")) {
               strs.add(sb.toString());
            }
         }
      }
      for (String str : strs) {
         append(str + "\n");
      }
      refresh();
   }

}
