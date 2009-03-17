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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.search.ui.text.Match;

/**
 * This is a utility class for OSEE handlers
 * 
 * @author Jeff C. Phillips
 */
public class Handlers {

   /**
    * Populates a list of ArtifactChange from a IStructuredSelection. Returns an empty list if no ArtifactChange were
    * found.
    * 
    * @param selection
    */
   public static List<ArtifactChange> getArtifactChangesFromStructuredSelection(IStructuredSelection structuredSelection) {
      return processSelectionObjects(ArtifactChange.class, structuredSelection);
   }
   
   /**
    * Populates a list of TransactionIds from a IStructuredSelection. Returns an empty list if no TransactionIds were
    * found.
    * 
    * @param selection
    */
   public static List<TransactionId> getTransactionsFromStructuredSelection(IStructuredSelection structuredSelection) {
      return processSelectionObjects(TransactionId.class, structuredSelection);
   }

   /**
    * Populates a list of branches from a IStructuredSelection. Returns an empty list if no branches were found.
    * 
    * @param selection
    */
   public static List<Branch> getBranchesFromStructuredSelection(IStructuredSelection structuredSelection) {
      return processSelectionObjects(Branch.class, structuredSelection);
   }

   /**
    * Populates a list of artifacts from a IStructuredSelection. Returns an empty list if no artifacts were found.
    * 
    * @param selection
    */
   public static List<Artifact> getArtifactsFromStructuredSelection(IStructuredSelection structuredSelection) {
      return processSelectionObjects(Artifact.class, structuredSelection);
   }

   /**
    * @param clazz
    * @param structuredSelection
    * @return Returns a list of objects from the sturctruedSelection that are an instance of the Class
    */
   public static <E> List<E> processSelectionObjects(Class<E> clazz, IStructuredSelection structuredSelection) {
      List<E> objects = new LinkedList<E>();
      Iterator<?> iterator = structuredSelection.iterator();

      while (iterator.hasNext()) {
         Object object = iterator.next();
         Object targetObject = null;

         if (object instanceof IAdaptable) {
            targetObject = ((IAdaptable) object).getAdapter(clazz);
         } else if (object instanceof Match) {
            targetObject = ((Match) object).getElement();
         }

         if (clazz.isInstance(targetObject)) {
            objects.add(clazz.cast(targetObject));
         }
      }
      return objects;
   }
}