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
import java.util.TreeSet;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
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

public class EmbeddedEnumAttributeEditor implements IEmbeddedAttributeEditor {
   private static final String PROMPT = "Please select a value from the combo box";
   private static final String ERROR_PROMPT =
         "All artifacts must be of the same type when edited in an enumeration editor.";
   protected String attributeName;
   protected String displayName;
   protected Collection<?> attributeHolder;
   protected boolean persist;
   protected EmbeddedEnumEditor editor;

   public EmbeddedEnumAttributeEditor(String arg, Collection<?> attributeHolder, String displayName, String attributeName, boolean persist) {
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
                  AWorkbench.popup("ERROR", ERROR_PROMPT);
                  return false;
               }
            } else
               return false;
         }
      }
      editor = new EmbeddedEnumEditor(PROMPT);
      editor.createEditor(composite);
      TreeSet<String> options = new TreeSet<String>();
      try {
         if (obj instanceof Artifact) {
            options =
                  new TreeSet<String>(ConfigurationPersistenceManager.getValidEnumerationAttributeValues(attributeName,
                        ((Artifact) obj).getBranch()));
         }
         if (obj instanceof AttributeConflict) {
            options = ((AttributeConflict) obj).getEnumerationAttributeValues();
         }
         for (String string : options) {
            editor.addSelectionChoice(string);
         }
      } catch (Exception ex) {
         OSEELog.logException(EmbeddedEnumAttributeEditor.class, ex, true);
      }
      if (obj instanceof Artifact) {
         try {
            editor.setSelected(((Artifact) obj).getSoleAttributeValue(attributeName).toString());
         } catch (Exception ex) {
            OSEELog.logException(EmbeddedEnumAttributeEditor.class, ex, true);
         }
      } else if (obj instanceof AttributeConflict) {
         try {
            if (((AttributeConflict) obj).getMergeObject() != null) {
               editor.setSelected(((AttributeConflict) obj).getMergeObject().toString());
            }
         } catch (Exception ex) {
            OSEELog.logException(EmbeddedEnumAttributeEditor.class, ex, true);
         }
      }
      return true;
   }

   public void update(Object value) {
      editor.setSelected(value.toString());
   }

   public boolean commit() {
      String selection = editor.getSelected();
      try {
         for (Object object : attributeHolder) {
            if (object instanceof Artifact) {
               ((Artifact) object).setSoleAttributeFromString(attributeName, selection);
               if (persist) ((Artifact) object).persistAttributes();
            }
            if (object instanceof AttributeConflict) {
               if (selection.equals("")) {
                  if (!((AttributeConflict) object).clearValue()) {
                     AWorkbench.popup("Attention", MergeUtility.COMMITED_PROMPT);
                  }
               } else {
                  if (!((AttributeConflict) object).setStringAttributeValue(selection)) {
                     AWorkbench.popup("Attention", MergeUtility.COMMITED_PROMPT);
                  }
               }
            }
         }
         return true;
      } catch (Exception ex) {
         OSEELog.logException(EmbeddedEnumAttributeEditor.class, ex, true);
      }
      return true;
   }

   public boolean canClear() {
      return false;
   }

   public boolean canFinish() {
      return true;
   }

}
