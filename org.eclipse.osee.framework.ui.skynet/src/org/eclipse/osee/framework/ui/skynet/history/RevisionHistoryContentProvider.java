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
package org.eclipse.osee.framework.ui.skynet.history;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactChangeListener;
import org.eclipse.osee.framework.skynet.core.revision.RevisionChange;
import org.eclipse.osee.framework.skynet.core.revision.RevisionManager;
import org.eclipse.osee.framework.skynet.core.revision.TransactionData;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Jeff C. Phillips
 */
public class RevisionHistoryContentProvider implements ITreeContentProvider, ArtifactChangeListener {
   private static Object[] EMPTY_ARRAY = new Object[0];

   public RevisionHistoryContentProvider() {
      super();
   }

   /**
    * returns the children of the parent element or null if there are none.
    */
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Artifact) {
         Artifact parentItem = (Artifact) parentElement;

         try {
            Collection<TransactionData> transactions =
                  RevisionManager.getInstance().getTransactionsPerArtifact(parentItem, true);
            if (transactions != null) {
               return transactions.toArray();
            }
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      } else if (parentElement instanceof TransactionData) {
         TransactionData parentItem = (TransactionData) parentElement;

         Collection<RevisionChange> changes = null;
         try {
            changes = RevisionManager.getInstance().getTransactionChanges(parentItem);
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
         if (changes != null) {
            return changes.toArray();
         }
      }
      return EMPTY_ARRAY;
   }

   /**
    * get the parent element
    */
   public Object getParent(Object element) {
      return null;
   }

   public boolean hasChildren(Object element) {

      if (element instanceof TransactionData)
         return true;
      else
         return false;
   }

   /**
    * returns the elements
    */
   public Object[] getElements(Object inputElement) {
      return getChildren(inputElement);
   }

   public void dispose() {

   }

   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

   }
}
