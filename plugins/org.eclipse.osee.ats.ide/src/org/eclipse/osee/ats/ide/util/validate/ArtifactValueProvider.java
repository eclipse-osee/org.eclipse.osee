/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;

/**
 * @author Donald G. Dunne
 */
public class ArtifactValueProvider implements IValueProvider {

   private final Artifact artifact;
   private final AttributeTypeToken attributeType;

   public ArtifactValueProvider(Artifact artifact, IAtsWidgetDefinition widgetDef) {
      this.artifact = artifact;
      this.attributeType = widgetDef.getAttributeType();
   }

   public ArtifactValueProvider(Artifact artifact, AttributeTypeToken attributeType) {
      this.artifact = artifact;
      this.attributeType = attributeType;
   }

   @Override
   public boolean isEmpty() {
      AttributeType attributeType = getAtributeType();
      if (attributeType != null) {
         return artifact.getAttributeCount(attributeType) == 0;
      }
      return true;
   }

   @Override
   public Collection<String> getValues() {
      AttributeType attributeType = getAtributeType();
      if (attributeType != null) {
         return artifact.getAttributesToStringList(attributeType);
      }
      return Collections.emptyList();
   }

   public AttributeType getAtributeType() {
      return AttributeTypeManager.getType(attributeType);
   }

   @Override
   public String getName() {
      return artifact.getName();
   }

   @Override
   public Collection<Date> getDateValues() {
      AttributeType attributeType = getAtributeType();
      if (attributeType != null) {
         List<Date> dates = new ArrayList<>();
         for (Attribute<?> attr : artifact.getAttributes(attributeType)) {
            if (attr instanceof DateAttribute) {
               dates.add(((DateAttribute) attr).getValue());
            }
         }
         return dates;
      }
      return Collections.emptyList();

   }

   public Artifact getArtifact() {
      return artifact;
   }

   public Object getObject() {
      return artifact;
   }

}
