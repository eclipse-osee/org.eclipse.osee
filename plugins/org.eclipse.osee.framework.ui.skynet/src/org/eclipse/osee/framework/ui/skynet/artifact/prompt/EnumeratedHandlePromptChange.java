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
package org.eclipse.osee.framework.ui.skynet.artifact.prompt;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.artifact.EnumSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.artifact.EnumSelectionDialog.Selection;
import org.eclipse.osee.framework.ui.skynet.artifact.EnumSingletonSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Jeff C. Phillips
 */
public class EnumeratedHandlePromptChange implements IHandlePromptChange {
   private EnumSelectionDialog dialog = null;
   private EnumSingletonSelectionDialog singletonDialog = null;
   private final Collection<? extends Artifact> artifacts;
   private final AttributeTypeId attributeType;
   private final boolean persist;
   private boolean isSingletonAttribute = true;

   public EnumeratedHandlePromptChange(Collection<? extends Artifact> artifacts, AttributeTypeId attributeType, String displayName, boolean persist) {
      super();
      this.artifacts = artifacts;
      this.attributeType = attributeType;
      this.persist = persist;

      try {
         if (AttributeTypeManager.getMaxOccurrences(attributeType) != 1) {
            for (Artifact art : artifacts) {
               if (art.getAttributeCount(attributeType) > 1) {
                  this.isSingletonAttribute = false;
                  break;
               }
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      if (isSingletonAttribute) {
         this.singletonDialog = new EnumSingletonSelectionDialog(attributeType, artifacts);
      } else {
         this.dialog = new EnumSelectionDialog(attributeType, artifacts);
      }
   }

   @Override
   public boolean promptOk() {
      if (isSingletonAttribute) {
         return singletonDialog.open() == Window.OK;
      } else {
         return dialog.open() == Window.OK;
      }
   }

   @Override
   public boolean store() {
      if (isSingletonAttribute) {
         return storeSingleton();
      } else {
         return storeNonSingleton();
      }
   }

   private boolean storeSingleton() {
      boolean result = false;
      String selected = singletonDialog.getSelectedOption();
      boolean isRemoveAll = singletonDialog.isRemoveAllSelected();
      if (isRemoveAll || Strings.isValid(selected)) {
         if (artifacts.size() > 0) {
            SkynetTransaction transaction =
               !persist ? null : TransactionManager.createTransaction(artifacts.iterator().next().getBranch(),
                  "Change enumerated attribute");
            for (Artifact artifact : artifacts) {
               if (isRemoveAll) {
                  artifact.deleteAttributes(attributeType);
               } else {
                  artifact.setSoleAttributeValue(attributeType, selected);
               }
               if (persist) {
                  artifact.persist(transaction);
               }
            }
            if (transaction != null && persist) {
               transaction.execute();
            }
         }
         result = true;
      }
      return result;
   }

   private boolean storeNonSingleton() {
      boolean result = false;
      Set<String> selected = new HashSet<>();
      for (Object obj : dialog.getResult()) {
         selected.add((String) obj);
      }
      if (!selected.isEmpty()) {
         if (artifacts.size() > 0) {
            SkynetTransaction transaction =
               !persist ? null : TransactionManager.createTransaction(artifacts.iterator().next().getBranch(),
                  "Change enumerated attribute");
            for (Artifact artifact : artifacts) {
               List<String> current = artifact.getAttributesToStringList(attributeType);
               if (dialog.getSelected() == Selection.AddSelection) {
                  current.addAll(selected);
                  artifact.setAttributeValues(attributeType, current);
               } else if (dialog.getSelected() == Selection.DeleteSelected) {
                  current.removeAll(selected);
                  artifact.setAttributeValues(attributeType, current);
               } else if (dialog.getSelected() == Selection.ReplaceAll) {
                  artifact.setAttributeValues(attributeType, selected);
               } else if (dialog.getSelected() == Selection.RemoveAll) {
                  artifact.deleteAttributes(attributeType);
               } else {
                  AWorkbench.popup("ERROR", "Unhandled selection type => " + dialog.getSelected().name());
                  return false;
               }
               if (persist) {
                  artifact.persist(transaction);
               }
            }
            if (transaction != null && persist) {
               transaction.execute();
            }
            result = true;
         }
      }
      return result;
   }
}