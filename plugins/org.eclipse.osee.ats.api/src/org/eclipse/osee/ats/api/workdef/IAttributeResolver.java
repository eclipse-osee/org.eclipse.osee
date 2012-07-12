/*
 * Created on Jun 25, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api.workdef;

/**
 * @author Donald G. Dunne
 */
public interface IAttributeResolver {

   boolean isAttributeNamed(String attributeName);

   String getUnqualifiedName(String attributeName);

   void setXWidgetNameBasedOnAttributeName(String attributeName, IAtsWidgetDefinition widgetDef);

   String getDescription(String attributeName);

}
