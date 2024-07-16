/*********************************************************************
 * Copyright (c) 2016 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.core.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author Donald G. Dunne
 */
public class ArtifactValueProvider implements IValueProvider {

   private final ArtifactToken artifact;
   private final AttributeTypeToken attrTypeToken;
   private final AtsApi atsApi;

   public ArtifactValueProvider(ArtifactToken artifact, WidgetDefinition widgetDef, AtsApi atsApi) {
      this.artifact = artifact;
      this.atsApi = atsApi;
      this.attrTypeToken = widgetDef.getAttributeType();
   }

   public ArtifactValueProvider(ArtifactToken artifact, AttributeTypeToken attributeType, AtsApi atsApi) {
      this.artifact = artifact;
      this.atsApi = atsApi;
      this.attrTypeToken = attributeType;
   }

   @Override
   public boolean isEmpty() {
      AttributeTypeToken attributeType = getAtributeType();
      if (attributeType != null) {
         return atsApi.getAttributeResolver().getAttributeCount(artifact, attributeType) == 0;
      }
      return true;
   }

   @Override
   public Collection<String> getValues() {
      AttributeTypeToken attributeType = getAtributeType();
      if (attributeType != null) {
         return atsApi.getAttributeResolver().getAttributesToStringList(artifact, attributeType);
      }
      return Collections.emptyList();
   }

   public AttributeTypeToken getAtributeType() {
      return attrTypeToken;
   }

   @Override
   public String getName() {
      return artifact.getName();
   }

   @Override
   public Collection<Date> getDateValues() {
      AttributeTypeToken attributeType = getAtributeType();
      if (attributeType != null && attributeType.isDate()) {
         return atsApi.getAttributeResolver().getAttributeValues(artifact, attributeType);
      }
      return Collections.emptyList();

   }

   public ArtifactId getArtifact() {
      return artifact;
   }

   public Object getObject() {
      return artifact;
   }
}