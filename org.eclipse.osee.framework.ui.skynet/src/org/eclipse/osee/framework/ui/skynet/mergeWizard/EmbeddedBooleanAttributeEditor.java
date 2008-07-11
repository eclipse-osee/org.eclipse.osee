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
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
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
public class EmbeddedBooleanAttributeEditor implements IEmbeddedAttributeEditor {

   protected String attributeName;
   protected String displayName;
   protected Collection<?> attributeHolder;
   protected boolean persist;
   protected EmbeddedBooleanEditor editor;

   public EmbeddedBooleanAttributeEditor(String prompt, Collection<?> attributeHolder, String displayName, String attributeName, boolean persist) {
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
                        "All artifacts must be of the same type when " + "edited in a boolean editor.");
                  return false;
               }
            } else
               return false;
         }
      }
      editor = new EmbeddedBooleanEditor("Select a value for the " + attributeName);
      editor.createEditor(composite, gd);

      try {
         if (obj instanceof Artifact) {
            try {
               Object object = ((Artifact) obj).getSoleAttributeValue(attributeName);
               if (object instanceof Boolean)
                  editor.setEntry(((Boolean) object).booleanValue());
               else
                  OSEELog.logException(EmbeddedBooleanAttributeEditor.class, new Exception(
                        "Boolean editor did not receive a boolean value"), true);
            } catch (Exception ex) {
               OSEELog.logException(EmbeddedBooleanAttributeEditor.class, ex, true);
            }
         }
         if (obj instanceof AttributeConflict) {
            Object object = ((AttributeConflict) obj).getMergeObject();
            if (object instanceof Boolean) {
               editor.setEntry(((Boolean) object).booleanValue());
            }
         }
      } catch (Exception ex) {
         OSEELog.logException(EmbeddedBooleanAttributeEditor.class, ex, true);
      }
      return true;
   }

   public void update(Object value) {
      if (editor != null) editor.setEntry(((Boolean) value).booleanValue());
   }

   public boolean commit() {
      if (editor != null) {
         boolean value = editor.getEntry();
         try {
            for (Object obj : attributeHolder) {
               if (obj instanceof Artifact) {
                  ((Artifact) obj).setSoleAttributeValue(attributeName, new Boolean(value));
                  if (persist) ((Artifact) obj).persistAttributes();
               }
               if (obj instanceof AttributeConflict) {
                  if (!((AttributeConflict) obj).setAttributeValue(new Boolean(value))) {
                     AWorkbench.popup("Attention", MergeUtility.COMMITED_PROMPT);
                  }
               }
            }
            return true;
         } catch (Exception ex) {
            OSEELog.logException(EmbeddedBooleanAttributeEditor.class, ex, true);
         }
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
