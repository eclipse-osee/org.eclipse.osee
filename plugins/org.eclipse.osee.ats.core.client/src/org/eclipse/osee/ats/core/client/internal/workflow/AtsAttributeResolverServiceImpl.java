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
package org.eclipse.osee.ats.core.client.internal.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.IAttribute;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.internal.workdef.AttributeWrapper;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.utility.AttributeTypeToXWidgetName;

/**
 * @author Donald G. Dunne
 */
public class AtsAttributeResolverServiceImpl implements IAttributeResolver {

   @Override
   public boolean isAttributeNamed(String attributeName) throws OseeCoreException {
      return AttributeTypeManager.typeExists(attributeName);
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

   @Override
   public <T> T getSoleAttributeValue(IAtsWorkItem workItem, IAttributeType attributeType, T defaultReturnValue) throws OseeCoreException {
      return AtsClientService.get().getArtifact(workItem).getSoleAttributeValue(attributeType, defaultReturnValue);

   }

   @Override
   public Collection<String> getAttributesToStringList(IAtsWorkItem workItem, IAttributeType attributeType) throws OseeCoreException {
      return AtsClientService.get().getArtifact(workItem).getAttributesToStringList(attributeType);
   }

   @Override
   public boolean isAttributeTypeValid(IAtsWorkItem workItem, IAttributeType attributeType) throws OseeCoreException {
      return AtsClientService.get().getArtifact(workItem).isAttributeTypeValid(attributeType);
   }

   @Override
   public String getSoleAttributeValueAsString(IAtsWorkItem workItem, IAttributeType attributeType, String defaultValue) throws OseeCoreException {
      return AtsClientService.get().getArtifact(workItem).getSoleAttributeValueAsString(attributeType, defaultValue);
   }

   @Override
   public void setSoleAttributeValue(IAtsWorkItem workItem, IAttributeType attributeType, Object value) throws OseeCoreException {
      AtsClientService.get().getArtifact(workItem).setSoleAttributeValue(attributeType, value);
   }

   @Override
   public int getAttributeCount(IAtsWorkItem workItem, IAttributeType attributeType) throws OseeCoreException {
      return AtsClientService.get().getArtifact(workItem).getAttributeCount(attributeType);
   }

   @Override
   public void addAttribute(IAtsWorkItem workItem, IAttributeType attributeType, Object value) throws OseeCoreException {
      AtsClientService.get().getArtifact(workItem).addAttribute(attributeType, value);
   }

   @Override
   public <T> Collection<IAttribute<T>> getAttributes(IAtsWorkItem workItem, IAttributeType attributeType) throws OseeCoreException {
      List<IAttribute<T>> attrs = new ArrayList<IAttribute<T>>();
      for (Attribute<Object> attr : AtsClientService.get().getArtifact(workItem).getAttributes(attributeType)) {
         attrs.add(new AttributeWrapper<T>((Attribute<T>) attr));

      }
      return attrs;
   }
}
