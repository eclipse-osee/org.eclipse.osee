/*
 * Created on Jun 25, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.client.workdef;

import java.util.logging.Level;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.workdef.api.IAtsWidgetDefinition;
import org.eclipse.osee.ats.workdef.api.IAttributeResolver;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.utility.AttributeTypeToXWidgetName;

public class WorkDefAttributeResolver implements IAttributeResolver {

   @Override
   public boolean isAttributeNamed(String attributeName) {
      return getAttributeType(attributeName) != null;
   }

   @Override
   public String getUnqualifiedName(String attributeName) {
      return getAttributeType(attributeName).getUnqualifiedName();
   }

   @Override
   public void setXWidgetNameBasedOnAttributeName(String attributeName, IAtsWidgetDefinition widgetDef) {
      try {
         if (!Strings.isValid(widgetDef.getXWidgetName())) {
            widgetDef.setXWidgetName(AttributeTypeToXWidgetName.getXWidgetName(getAttributeType(attributeName)));
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public String getDescription(String attributeName) {
      return getAttributeType(attributeName).getDescription();
   }

   public AttributeType getAttributeType(String attributeName) {
      try {
         return AttributeTypeManager.getType(attributeName);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }
}
