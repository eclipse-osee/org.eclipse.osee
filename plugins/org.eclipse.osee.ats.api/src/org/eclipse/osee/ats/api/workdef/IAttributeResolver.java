/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workdef;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.IAttribute;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAttributeResolver {

   boolean isAttributeNamed(String attributeName) throws OseeCoreException;

   String getUnqualifiedName(String attributeName);

   void setXWidgetNameBasedOnAttributeName(String attributeName, IAtsWidgetDefinition widgetDef);

   String getDescription(String attributeName);

   <T> T getSoleAttributeValue(IAtsWorkItem workItem, IAttributeType attributeType, T defaultReturnValue) throws OseeCoreException;

   Collection<String> getAttributesToStringList(IAtsWorkItem workItem, IAttributeType attributeType) throws OseeCoreException;

   boolean isAttributeTypeValid(IAtsWorkItem workItem, IAttributeType attributeType) throws OseeCoreException;

   String getSoleAttributeValueAsString(IAtsWorkItem workItem, IAttributeType attributeType, String defaultReturnValue) throws OseeCoreException;

   void setSoleAttributeValue(IAtsWorkItem workItem, IAttributeType attributeType, Object value) throws OseeCoreException;

   int getAttributeCount(IAtsWorkItem workItem, IAttributeType attributeType) throws OseeCoreException;

   void addAttribute(IAtsWorkItem workItem, IAttributeType attributeType, Object value) throws OseeCoreException;

   <T> Collection<IAttribute<T>> getAttributes(IAtsWorkItem workItem, IAttributeType attributeType) throws OseeCoreException;

}
