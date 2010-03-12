/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.compare;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class AttributeCompareItem extends CompareItem {
   private final AttributeConflict attributeConflict;

   public AttributeCompareItem(AttributeConflict attributeConflict, String name, String contents, boolean isEditable, Image image) {
      super(name, contents, System.currentTimeMillis(), isEditable, image);

      this.attributeConflict = attributeConflict;
   }

   @Override
   public void persistContent() {
      try {
         attributeConflict.setAttributeValue(getStringContent());
      } catch (OseeCoreException ex) {
         OseeLog.log(AttributeCompareItem.class, Level.SEVERE, ex);
      }
   }
}
