/*
 * Created on May 20, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;

/**
 * @author Donald G. Dunne
 */
public interface IAttributeXWidgetProvider {

   public DynamicXWidgetLayoutData getDynamicXWidgetLayoutData(AttributeType attributeType);
}
