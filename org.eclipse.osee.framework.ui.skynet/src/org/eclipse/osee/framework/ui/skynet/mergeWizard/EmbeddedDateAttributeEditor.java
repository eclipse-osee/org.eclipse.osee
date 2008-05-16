/*
 * Created on Mar 7, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.mergeWizard;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
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
      try {
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

      } catch (SQLException ex) {
         OSEELog.logException(EmbeddedDateAttributeEditor.class, ex, true);
      }
      Date date = new Date();
      if (obj instanceof Artifact) {
         try {
            Object object = ((Artifact) obj).getSoleAttributeValue(attributeName);
            if (object instanceof Date) date = (Date) object;
         } catch (Exception ex) {
            OSEELog.logException(EmbeddedDateAttributeEditor.class, ex, true);
         }
      }
      if (obj instanceof AttributeConflict) try {
         Object object = ((AttributeConflict) obj).getMergeObject();
         if (object instanceof Date)
            date = (Date) object;
         else
            OSEELog.logException(EmbeddedDateAttributeEditor.class, new Exception(
                  "Date editor did not receive a date value"), true);

      } catch (Exception ex) {
         OSEELog.logException(EmbeddedDateAttributeEditor.class, ex, true);
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
                  ((Artifact) object).setSoleXAttributeValue(attributeName, "");
               else
                  ((Artifact) object).setSoleXAttributeValue(attributeName, selected.getTime() + "");
               if (persist) ((Artifact) object).persistAttributes();
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
         OSEELog.logException(EmbeddedDateAttributeEditor.class, ex, true);
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
