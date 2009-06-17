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
package org.eclipse.osee.ote.ui.define.utilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.osee.ote.define.artifacts.TestRunOperator;
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

   public TestRunOperator getSelection(StructuredViewer viewer) {
      TestRunOperator toReturn = null;
      if (viewer != null) {
         IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
         if (selection != null && selection.size() == 1) {
            Object object = selection.getFirstElement();
            TestRunOperator operator = asTestRunOperator(object);
            if (operator != null && operator.hasValidArtifact() != false) {
               toReturn = operator;
            }
         }
      }
      return toReturn;
   }

   public TestRunOperator asTestRunOperator(Object object) {
      TestRunOperator operator = null;
      if (object instanceof ArtifactItem) {
         ArtifactItem artItem = (ArtifactItem) object;
         operator = artItem.getOperator();
      }
      return operator;
   }

   public List<TestRunOperator> getSelections(StructuredViewer viewer) {
      List<TestRunOperator> toReturn = new ArrayList<TestRunOperator>();
      if (viewer != null) {
         IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
         if (selection != null && selection.isEmpty() != true) {
            Iterator<?> iterator = selection.iterator();
            while (iterator.hasNext()) {
               Object object = iterator.next();
               TestRunOperator operator = asTestRunOperator(object);
               if (operator != null && operator.hasValidArtifact() != false) {
                  toReturn.add(operator);
               }
            }
         }
      }
      return toReturn;
   }
}
