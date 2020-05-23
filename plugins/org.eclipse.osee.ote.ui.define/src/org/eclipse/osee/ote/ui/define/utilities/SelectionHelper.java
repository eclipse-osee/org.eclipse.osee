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

package org.eclipse.osee.ote.ui.define.utilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.osee.ote.define.artifacts.ArtifactTestRunOperator;
import org.eclipse.osee.ote.ui.define.viewers.data.ArtifactItem;

/**
 * @author Roberto E. Escobar
 */
public class SelectionHelper {

   private static SelectionHelper instance = null;

   private SelectionHelper() {
   }

   public static SelectionHelper getInstance() {
      if (instance == null) {
         instance = new SelectionHelper();
      }
      return instance;
   }

   public ArtifactTestRunOperator getSelection(StructuredViewer viewer) {
      ArtifactTestRunOperator toReturn = null;
      if (viewer != null) {
         IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
         if (selection != null && selection.size() == 1) {
            Object object = selection.getFirstElement();
            ArtifactTestRunOperator operator = asTestRunOperator(object);
            if (operator != null && operator.hasValidArtifact() != false) {
               toReturn = operator;
            }
         }
      }
      return toReturn;
   }

   public ArtifactTestRunOperator asTestRunOperator(Object object) {
      ArtifactTestRunOperator operator = null;
      if (object instanceof ArtifactItem) {
         ArtifactItem artItem = (ArtifactItem) object;
         operator = artItem.getOperator();
      }
      return operator;
   }

   public List<ArtifactTestRunOperator> getSelections(StructuredViewer viewer) {
      List<ArtifactTestRunOperator> toReturn = new ArrayList<>();
      if (viewer != null) {
         IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
         if (selection != null && selection.isEmpty() != true) {
            Iterator<?> iterator = selection.iterator();
            while (iterator.hasNext()) {
               Object object = iterator.next();
               ArtifactTestRunOperator operator = asTestRunOperator(object);
               if (operator != null && operator.hasValidArtifact() != false) {
                  toReturn.add(operator);
               }
            }
         }
      }
      return toReturn;
   }
}
