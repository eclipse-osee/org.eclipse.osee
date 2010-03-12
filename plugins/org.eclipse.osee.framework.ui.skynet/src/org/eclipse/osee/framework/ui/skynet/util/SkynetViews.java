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
package org.eclipse.osee.framework.ui.skynet.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.database.core.OseeInfo;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jeff C. Phillips
 */
public class SkynetViews {

   private static final String MEMENTO_SOURCE_GUID = "sourceDbGuid";

   public static boolean isSourceValid(IMemento memento) {
      boolean result = false;
      if (memento != null) {
         String dbId = memento.getString(MEMENTO_SOURCE_GUID);
         if (Strings.isValid(dbId)) {
            String currentDbId = null;
            try {
               currentDbId = OseeInfo.getDatabaseGuid();
            } catch (OseeDataStoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.WARNING, "Unable to set memento source db guid");
            }
            if (dbId.equals(currentDbId)) {
               result = true;
            }
         }
      }
      return result;
   }

   public static void addDatabaseSourceId(IMemento memento) {
      if (memento != null) {
         try {
            memento.putString(MEMENTO_SOURCE_GUID, OseeInfo.getDatabaseGuid());
         } catch (OseeDataStoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.WARNING, "Unable to set memento source db guid");
         }
      }
   }

   public static void closeView(final String viewId, final String secondaryId) {
      if (Strings.isValid(viewId)) {
         Display.getDefault().asyncExec(new Runnable() {
            public void run() {
               IWorkbench workbench = PlatformUI.getWorkbench();
               if (workbench != null) {
                  IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
                  if (workbenchWindow != null) {
                     IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
                     if (workbenchPage != null) {
                        workbenchPage.hideView(workbenchPage.findViewReference(viewId, secondaryId));
                     }
                  }
               }
            }
         });
      }
   }

   /**
    * @param memento
    * @return Returns a collection of <code>DynamicAttributeDescriptor</code> stored in a memento.
    * @throws OseeDataStoreException
    */
   public static List<AttributeType> loadAttrTypesFromPreferenceStore(String preferenceKey, Branch branch) throws OseeCoreException {
      List<AttributeType> attributeDescriptors = new LinkedList<AttributeType>();
      Collection<AttributeType> descriptors = AttributeTypeManager.getValidAttributeTypes(branch);

      IPreferenceStore preferenceStore = SkynetGuiPlugin.getInstance().getPreferenceStore();
      for (String attributeType : preferenceStore.getString(preferenceKey).split("\\|")) {
         for (AttributeType descriptor : descriptors) {
            if (attributeType.equals(descriptor.getName())) {
               attributeDescriptors.add(descriptor);
               break;
            }
         }
      }

      return attributeDescriptors;
   }
}