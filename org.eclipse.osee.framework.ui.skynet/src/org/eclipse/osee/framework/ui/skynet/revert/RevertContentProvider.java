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
package org.eclipse.osee.framework.ui.skynet.revert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactContentProvider;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.widgets.Combo;

/**
 * @author Theron Virgin
 */
public class RevertContentProvider extends ArtifactContentProvider {
   private static Object[] EMPTY_ARRAY = new Object[0];
   private Combo artifactSelectionBox = null;
   private List<List<Artifact>> artifacts = null;

   public RevertContentProvider(Combo artifactSelectionBox, List<List<Artifact>> artifacts) {
      super();
      this.artifactSelectionBox = artifactSelectionBox;
      this.artifacts = artifacts;
   }

   @Override
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Artifact) {
         Artifact parentItem = (Artifact) parentElement;
         if (!RevertDeletionCheck.isRootArtifact(parentItem, artifactSelectionBox, artifacts)) {
            return EMPTY_ARRAY;
         }
         try {
            if (AccessControlManager.hasPermission(parentItem, PermissionEnum.READ)) {
               Collection<Artifact> children = parentItem.getChildren();
               if (children != null) {
                  List<Artifact> childs = new ArrayList<Artifact>();
                  for (Artifact artifact : children) {
                     if (RevertDeletionCheck.relationWillBeReverted(artifact)) {
                        childs.add(artifact);
                     }
                  }
                  return childs.toArray();
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      } else if (parentElement instanceof Collection) {
         return ((Collection<?>) parentElement).toArray();
      }

      return EMPTY_ARRAY;
   }

   @Override
   public boolean hasChildren(Object element) {
      return getChildren(element).length > 0;
   }

}
