/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;

/**
 * Attribute that stores the artifact id of an artifact in the db
 *
 * @author Donald G. Dunne
 */
public abstract class XAbstractArtRefAttributeArtWidget extends XTextFlatArtWidget {

   private final ArtifactTypeToken validArtifactType;

   public XAbstractArtRefAttributeArtWidget(WidgetId widgetId, String displayLabel) {
      this(widgetId, displayLabel, null);
   }

   /**
    * @param validArtifactType if provided and getArtifact is extended, value will be checked for valid artifact type
    */
   public XAbstractArtRefAttributeArtWidget(WidgetId widgetId, String displayLabel, ArtifactTypeToken validArtifactType) {
      super(widgetId, displayLabel);
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
                     validArtifactType.toStringWithId(), getArtifact().getArtifactType().toStringWithId());
               AWorkbench.popup(message);
               throw new OseeArgumentException(message);
            }
            if (artifact.isValid() && !validArtifactType.equals(artifact.getArtifactType())) {
               String message = String.format("Artifact type must be [%s], not [%s]",
                  validArtifactType.toStringWithId(), getArtifact().getArtifactType().toStringWithId());
               AWorkbench.popup(message);
               throw new OseeArgumentException(message);
            }
         }
         storeValues.add(storeId);
      }
      getArtifact().setAttributeValues(getAttributeType(), storeValues);
   }
}
