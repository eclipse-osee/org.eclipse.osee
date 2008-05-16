/*
 * Created on Mar 7, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.mergeWizard;

import java.sql.SQLException;
import java.util.Collection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.utils.AttributeObjectConverter;
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

public class EmbeddedStringAttributeEditor implements IEmbeddedAttributeEditor {
   private static final String PROMPT = "Set the Attribute Value";
   private static final String VALIDATION_ERROR =
         "ERROR: You have entered an invalid value." + " This value can not be saved.";
   private static final String TYPE_ERROR = "All the artifacts being edited are not of the same type.";
   private static final String SAVE_ERROR = "Could not store the string attribute";

   protected String attributeName;
   protected String displayName;
   protected Collection<?> attributeHolders;
   protected boolean persist;
   protected EmbeddedStringEditor editor;
   protected String regExp;

   public EmbeddedStringAttributeEditor(String regExp, Collection<?> attributeHolders, String displayName, String attributeName, boolean persist) {
      this.regExp = regExp;
      this.attributeName = attributeName;
      this.displayName = displayName;
      this.attributeHolders = attributeHolders;
      this.persist = persist;
   }

   public boolean create(Composite composite, GridData gd) {
      if (attributeHolders == null) return false;
      if (attributeHolders.size() < 1) return false;
      Object obj = attributeHolders.iterator().next();
      try {
         if (obj instanceof Artifact) {
            String type = ((Artifact) obj).getArtifactTypeName();
            for (Object object : attributeHolders) {
               if (object instanceof Artifact) {
                  if (!type.equals(((Artifact) object).getArtifactTypeName())) {
                     AWorkbench.popup("ERROR", TYPE_ERROR);
                     return false;
                  }
               } else
                  return false;
            }
         }
      } catch (SQLException ex) {
         OSEELog.logException(EmbeddedStringAttributeEditor.class, ex, true);
      }
      editor = new EmbeddedStringEditor(PROMPT);
      editor.setValidationErrorString(VALIDATION_ERROR);
      editor.createEditor(composite);
      if (obj instanceof Artifact) {
         try {
            editor.setEntry(((Artifact) obj).getSoleAttributeValue(attributeName).toString());
         } catch (Exception ex) {
            OSEELog.logException(EmbeddedStringAttributeEditor.class, ex, true);
         }
      } else if (obj instanceof AttributeConflict) {
         try {
            editor.setEntry(((AttributeConflict) obj).getMergeObject().toString());
         } catch (Exception ex) {
            OSEELog.logException(EmbeddedStringAttributeEditor.class, ex, true);
         }
      }
      if (regExp != null) editor.setValidationRegularExpression(regExp);
      return true;
   }

   public void update(Object value) {
      editor.setEntry(value.toString());
   }

   public boolean commit() {
      if (editor != null) {
         try {
            for (Object object : attributeHolders) {
               if (object instanceof Artifact) {
                  ((Artifact) object).setSoleXAttributeValue(attributeName, editor.getEntry());
                  if (persist) ((Artifact) object).persistAttributes();
               }
               if (object instanceof AttributeConflict) {
                  if (!editor.getEntry().equals("")) {
                     try {
                        Object obj =
                              AttributeObjectConverter.stringToObject(((AttributeConflict) object).getAttribute(),
                                    editor.getEntry());
                        if (!((AttributeConflict) object).setAttributeValue(obj)) {
                           AWorkbench.popup("Attention", MergeUtility.COMMITED_PROMPT);
                        }
                     } catch (Exception ex) {
                        OSEELog.logException(EmbeddedStringAttributeEditor.class, ex, true);
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
            OSEELog.logException(EmbeddedStringAttributeEditor.class, ex, true);
         }
      }
      return false;

   }

   public boolean canClear() {
      return true;
   }

   public boolean canFinish() {
      if (editor == null) return false;
      return (editor.handleModified() || editor.getEntry().equals(""));
   }
}
