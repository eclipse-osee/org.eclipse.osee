/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.framework.skynet.core.importing.resolvers;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.CaseInsensitiveString;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughAttributeSet;
import org.eclipse.osee.framework.skynet.core.importing.RoughAttributeSet.RoughAttribute;

public class RoughArtifactTranslatorImpl implements IRoughArtifactTranslator {

   @Override
   public void translate(RoughArtifact roughArtifact, Artifact artifact) {
      RoughAttributeSet attributeSet = roughArtifact.getAttributes();

      for (Entry<CaseInsensitiveString, Collection<RoughAttribute>> entry : attributeSet) {
         String attributeTypeName = entry.getKey().toString();
         AttributeTypeToken attributeType = AttributeTypeManager.getType(attributeTypeName);

         Collection<String> values = attributeSet.getAttributeValueList(attributeType);
         if (!values.isEmpty()) {
            boolean setValues = false;
            if (attributeType.isBoolean()) {
               ArrayList<Boolean> booleanValues = new ArrayList<>();
               for (String state : values) {
                  Boolean value = new Boolean(state.equalsIgnoreCase("True"));
                  booleanValues.add(value);
               }
               artifact.setAttributeFromValues(attributeType, booleanValues);
               setValues = true;
            }
            if (!setValues) {
               artifact.setAttributeFromValues(attributeType, values);
            }
         } else {
            Collection<RoughAttribute> roughAttributes = entry.getValue();
            Collection<InputStream> streams = new LinkedList<>();
            try {
               for (RoughAttribute attribute : roughAttributes) {
                  streams.add(attribute.getContent());
               }
               artifact.setBinaryAttributeFromValues(attributeType, streams);
            } catch (Exception ex) {
               OseeCoreException.wrapAndThrow(ex);
            } finally {
               for (InputStream inputStream : streams) {
                  Lib.close(inputStream);
               }
            }
         }
      }
   }
}
