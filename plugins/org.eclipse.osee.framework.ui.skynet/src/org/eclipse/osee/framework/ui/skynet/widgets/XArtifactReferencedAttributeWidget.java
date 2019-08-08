/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;

/**
 * @author Donald G. Dunne
 */
public abstract class XArtifactReferencedAttributeWidget extends XTextFlatDam {

   private final ArtifactTypeToken validArtifactType;

   public XArtifactReferencedAttributeWidget(String displayLabel) {
      this(displayLabel, null);
   }

   /**
    * @param validArtifactType if provided and getArtifact is extended, value will be checked for valid artifact type
    */
   public XArtifactReferencedAttributeWidget(String displayLabel, ArtifactTypeToken validArtifactType) {
      super(displayLabel);
      this.validArtifactType = validArtifactType;
   }

   @Override
   protected String getDisplayValue(String value) {
      return value;
   }

   /**
    * Load from subclass, since only that class can know what branch to load from. If not implemented, the
    * validArtifactType will not be checked.
    */
   protected ArtifactToken getArtifactToken(Long id) {
      return ArtifactToken.SENTINEL;
   }

   @Override
   public void saveToArtifact() {
      List<String> values = getInput();
      List<String> storeValues = new ArrayList<String>(values.size());
      for (String value : values) {
         NamedId namedId = NamedId.getFromStringWithid(value);
         String storeId = null;
         if (namedId.isValid()) {
            storeId = namedId.getIdString();
         } else if (!Strings.isNumeric(value)) {
            String message = String.format("Value must be numeric not [%s]", value);
            AWorkbench.popup(message);
            throw new OseeArgumentException(message);
         } else {
            storeId = value;
         }
         if (validArtifactType != null) {
            ArtifactToken artifact = getArtifactToken(Long.valueOf(storeId));
            if (artifact.isInvalid()) {
               String message =
                  String.format("Artifact type must be [%s], but artifact could not be loaded to validate.",
                     validArtifactType.toStringWithId(), artifact.getArtifactType().toStringWithId());
               AWorkbench.popup(message);
               throw new OseeArgumentException(message);
            }
            if (artifact.isValid() && !validArtifactType.equals(artifact.getArtifactType())) {
               String message = String.format("Artifact type must be [%s], not [%s]",
                  validArtifactType.toStringWithId(), artifact.getArtifactType().toStringWithId());
               AWorkbench.popup(message);
               throw new OseeArgumentException(message);
            }
         }
         storeValues.add(storeId);
      }
      getArtifact().setAttributeValues(getAttributeType(), storeValues);
   }
}
