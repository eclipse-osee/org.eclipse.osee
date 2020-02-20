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
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.MergeUtility;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Theron Virgin
 */

/*
 * All of the instance of checks are needed to support both artifacts and conflicts. The reason to support both is I
 * created the classes for artifacts so all of the work was already done for them. I then realized that I needed to
 * control the setting of values for conflicts and thus had to call the conflict specific methods instead of simply
 * setting the values.
 */

public class EmbeddedStringAttributeEditor implements IEmbeddedAttributeEditor {
   private static final String PROMPT = "Set the Attribute Value";
   private static final String VALIDATION_ERROR =
      "ERROR: You have entered an invalid value." + " This value can not be saved.";
   private static final String TYPE_ERROR = "All the artifacts being edited are not of the same type.";

   protected AttributeTypeGeneric<?> attributeType;
   protected String displayName;
   protected Collection<?> attributeHolders;
   protected boolean persist;
   protected EmbeddedStringEditor editor;
   protected String regExp;

   public EmbeddedStringAttributeEditor(String regExp, Collection<?> attributeHolders, String displayName, AttributeTypeGeneric<?> attributeType, boolean persist) {
      this.regExp = regExp;
      this.attributeType = attributeType;
      this.displayName = displayName;
      this.attributeHolders = attributeHolders;
      this.persist = persist;
   }

   @Override
   public boolean create(Composite composite, GridData gd) {
      if (attributeHolders == null) {
         return false;
      }
      if (attributeHolders.size() < 1) {
         return false;
      }
      Object obj = attributeHolders.iterator().next();
      if (obj instanceof Artifact) {
         String type = ((Artifact) obj).getArtifactTypeName();
         for (Object object : attributeHolders) {
            if (object instanceof Artifact) {
               if (!type.equals(((Artifact) object).getArtifactTypeName())) {
                  AWorkbench.popup("ERROR", TYPE_ERROR);
                  return false;
               }
            } else {
               return false;
            }
         }
      }
      editor = new EmbeddedStringEditor(PROMPT);
      editor.setValidationErrorString(VALIDATION_ERROR);
      editor.createEditor(composite);
      if (obj instanceof Artifact) {
         try {
            editor.setEntry(((Artifact) obj).getSoleAttributeValue(attributeType).toString());
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      } else if (obj instanceof AttributeConflict) {
         try {
            if (((AttributeConflict) obj).getMergeObject() != null) {
               editor.setEntry(((AttributeConflict) obj).getMergeObject().toString());
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      if (regExp != null) {
         editor.setValidationRegularExpression(regExp);
      }
      return true;
   }

   @Override
   public void update(Object value) {
      if (value != null) {
         editor.setEntry(value.toString());
      }
   }

   @Override
   public boolean commit() {
      if (editor != null) {
         try {
            for (Object object : attributeHolders) {
               if (object instanceof Artifact) {
                  ((Artifact) object).setSoleAttributeFromString(attributeType, editor.getEntry());
                  if (persist) {
                     ((Artifact) object).persist(getClass().getSimpleName());
                  }
               }
               if (object instanceof AttributeConflict) {
                  if (!editor.getEntry().equals("")) {
                     try {
                        if (!((AttributeConflict) object).setStringAttributeValue(editor.getEntry())) {
                           AWorkbench.popup("Attention", MergeUtility.COMMITED_PROMPT);
                        }
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
                     }
                  } else {
                     if (!((AttributeConflict) object).clearValue()) {
                        AWorkbench.popup("Attention", MergeUtility.COMMITED_PROMPT);
                     }
                  }
               }
            }
            return true;
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      return false;

   }

   @Override
   public boolean canClear() {
      return true;
   }

   @Override
   public boolean canFinish() {
      if (editor == null) {
         return false;
      }
      return editor.handleModified() || editor.getEntry().equals("");
   }
}
