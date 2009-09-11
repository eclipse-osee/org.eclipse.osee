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

package org.eclipse.osee.framework.ui.skynet.mergeWizard;

import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.MergeUtility;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Theron Virgin
 */

/* 
 * All of the instance of checks are needed to support both artifacts and 
 * conflicts.  The reason to support both is I created the classes for
 * artifacts so all of the work was already done for them.  I then realized 
 * that I needed to control the setting of values for conflicts and thus had to call
 * the conflict specific methods instead of simply setting the values.
 */
public class EmbeddedDateAttributeEditor implements IEmbeddedAttributeEditor {

   protected String attributeName;
   protected String displayName;
   protected Collection<?> attributeHolder;
   protected boolean persist;
   protected EmbeddedDateEditor editor;

   public EmbeddedDateAttributeEditor(String notUsed, Collection<?> attributeHolder, String displayName, String attributeName, boolean persist) {
      this.attributeName = attributeName;
      this.displayName = displayName;
      this.attributeHolder = attributeHolder;
      this.persist = persist;
   }

   public boolean create(Composite composite, GridData gd) {
      if (attributeHolder == null) return false;
      if (attributeHolder.size() < 1) return false;
      Object obj = attributeHolder.iterator().next();
      if (obj instanceof Artifact) {
         String type = ((Artifact) obj).getArtifactTypeName();
         for (Object object : attributeHolder) {
            if (object instanceof Artifact) {
               if (!type.equals(((Artifact) object).getArtifactTypeName())) {
                  AWorkbench.popup("ERROR",
                        "All artifacts must be of the same " + "type when edited in an date editor.");
                  return false;
               }
            } else
               return false;
         }
      }
      Date date = new Date();
      if (obj instanceof Artifact) {
         try {
            Object object = ((Artifact) obj).getSoleAttributeValue(attributeName);
            if (object instanceof Date) date = (Date) object;
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      if (obj instanceof AttributeConflict) try {
         Object object = ((AttributeConflict) obj).getMergeObject();
         if (object instanceof Date) {
            date = (Date) object;
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }

      editor = new EmbeddedDateEditor("Edit " + displayName, date);
      editor.createEditor(composite);
      return true;
   }

   public void update(Object value) {
      editor.setSelectedDate((Date) value);
   }

   public boolean commit() {
      Date selected = editor.getSelectedDate();
      try {
         for (Object object : attributeHolder) {
            if (object instanceof Artifact) {
               if (selected == null)
                  ((Artifact) object).setSoleAttributeValue(attributeName, "");
               else
                  ((Artifact) object).setSoleAttributeValue(attributeName, selected.getTime() + "");
               if (persist) ((Artifact) object).persist();
            }
            if (object instanceof AttributeConflict) {
               if (selected == null) {
                  if (!((AttributeConflict) object).clearValue()) {
                     AWorkbench.popup("Attention", MergeUtility.COMMITED_PROMPT);
                  }
               } else {
                  if (!((AttributeConflict) object).setAttributeValue(selected)) {
                     AWorkbench.popup("Attention", MergeUtility.COMMITED_PROMPT);
                  }
               }
            }
         }
         return true;
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      AWorkbench.popup("ERROR", "Could not store the attribute");
      return false;
   }

   public boolean canClear() {
      return false;
   }

   public boolean canFinish() {
      return true;
   }
}
