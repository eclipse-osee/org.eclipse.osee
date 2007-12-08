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
package org.eclipse.osee.framework.ui.skynet.changeReport;

import java.sql.SQLException;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.swt.ITreeNode;

/**
 * @author Ryan D. Brooks
 */
public class ChangeRepolrt2ClickListener implements IDoubleClickListener {
   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
    */
   public void doubleClick(DoubleClickEvent event) {
      Object selectedItem = ((IStructuredSelection) event.getSelection()).getFirstElement();

      if (selectedItem instanceof ITreeNode) {
         Object backingObject = ((ITreeNode) selectedItem).getBackingData();
         if (backingObject instanceof ArtifactChange) {
            try {
               ArtifactEditor.editArtifact(((ArtifactChange) backingObject).getArtifact());
            } catch (SQLException ex) {
               OSEELog.logException(getClass(), ex, true);
            }
         }
      }
   }
}