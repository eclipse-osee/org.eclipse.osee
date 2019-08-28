/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.importing.parsers;

import java.net.URI;
import java.util.Collection;
import java.util.Queue;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifactKind;

/**
 * @author David W. Miller
 */
public class DoorsArtifactBuilder {
   private final DoorsTableRowCollector rowCollector;
   private final DoorsArtifactExtractor extractor;

   public DoorsArtifactBuilder(DoorsArtifactExtractor extractor, DoorsTableRowCollector rowCollector) {
      this.rowCollector = rowCollector;
      this.extractor = extractor;
   }

   public RoughArtifact populateArtifact(DoorsTableRow dr) {
      Conditions.checkNotNull(dr, "Table Row");
      RoughArtifact toReturn = initRoughArtifact(dr);
      translateRequirementsColumn(dr, toReturn);
      translateIDColumn(dr, toReturn);
      translateSubsystemColumn(dr, toReturn);
      translateEffectivityColumn(dr, toReturn);
      translateVerificationCriteriaColumn(dr, toReturn);
      return toReturn;
   }

   // This method takes all of the DoorsTableRows in the given queue and combines them
   // into one rough Artifact. The first row is immediately converted, then all of the
   // other rows are converted and combined.
   public RoughArtifact populateArtifact(Queue<DoorsTableRow> queue) {
      Conditions.checkNotNullOrEmpty(queue, "Queue");

      RoughArtifact combined = populateArtifact(queue.remove());
      while (!queue.isEmpty()) {
         combineDoorsArtifacts(combined, populateArtifact(queue.remove()));
      }
      return combined;
   }

   private RoughArtifact initRoughArtifact(DoorsTableRow dr) {
      RoughArtifactKind rk = kindFromDataType(dr);
      RoughArtifact toReturn = new RoughArtifact(rk, rowCollector.getPreferredName(dr));
      String paragraph = rowCollector.getSimpleText(dr, DoorsColumnType.OBJECT_NUMBER);
      toReturn.setSectionNumber(paragraph);
      toReturn.addAttribute(CoreAttributeTypes.ParagraphNumber, paragraph);
      if (rk == RoughArtifactKind.SECONDARY) {
         toReturn.setPrimaryArtifactType(CoreArtifactTypes.HeadingHtml);
      }
      String guidString = rowCollector.getSimpleText(dr, DoorsColumnType.GUID);

      if (!GUID.isValid(guidString) && dr.isMainRow()) {
         guidString = GUID.create();
      }
      if (GUID.isValid(guidString)) {
         toReturn.setGuid(guidString);
      }
      return toReturn;
   }

   private void translateRequirementsColumn(DoorsTableRow dr, RoughArtifact ra) {
      String reqTextHTML = rowCollector.getHTML(dr, DoorsColumnType.REQUIREMENTS);
      if (dr.getDataType() == DoorsDataType.LIST) {
         reqTextHTML = extractor.processList(reqTextHTML);
      }
      extractor.handleRequirement(reqTextHTML, ra);
   }

   private void translateIDColumn(DoorsTableRow dr, RoughArtifact ra) {
      String id = rowCollector.getSimpleText(dr, DoorsColumnType.ID);
      ra.addAttribute(CoreAttributeTypes.LegacyId, id);
   }

   private boolean isValidForTranslation(String s) {
      return Strings.isValid(s) && !s.equalsIgnoreCase("<br></br>") && !s.equalsIgnoreCase("<br />");
   }

   private void translateSubsystemColumn(DoorsTableRow dr, RoughArtifact ra) {
      String subsystem = rowCollector.getSimpleText(dr, DoorsColumnType.SUBSYSTEM);
      if (isValidForTranslation(subsystem)) {
         ra.addAttribute(CoreAttributeTypes.Subsystem, subsystem);
      }
   }

   private void translateEffectivityColumn(DoorsTableRow dr, RoughArtifact ra) {
      String effectivity = rowCollector.getSimpleText(dr, DoorsColumnType.EFFECTIVITY);
      if (isValidForTranslation(effectivity)) {
         ra.addAttribute(CoreAttributeTypes.Effectivity, effectivity);
      }
   }

   private void translateVerificationCriteriaColumn(DoorsTableRow dr, RoughArtifact ra) {
      String criteria = rowCollector.getSimpleText(dr, DoorsColumnType.VERIFICATION_CRITERIA);
      if (isValidForTranslation(criteria)) {
         extractor.processVerification(criteria, ra);
      }
   }

   private RoughArtifactKind kindFromDataType(DoorsTableRow dr) {
      RoughArtifactKind kind = null;
      DoorsDataType dt = dr.getDataType();
      switch (dt) {
         case HEADER:
         case HEADING:
            kind = RoughArtifactKind.SECONDARY;
            break;
         default:
            kind = RoughArtifactKind.PRIMARY;
            break;
      }
      return kind;
   }

   private void combineDoorsArtifacts(RoughArtifact combined, RoughArtifact add) {
      Conditions.checkNotNull(combined, "Combining artifact");
      Conditions.checkNotNull(add, "Artifact to combine");

      mergeArtifactKind(combined, add);
      mergeImages(combined, add);
      mergeAttribute(combined, add, CoreAttributeTypes.LegacyId.getName(), ",");
      mergeAttribute(combined, add, CoreAttributeTypes.HtmlContent.getName(), "<br>");
      mergeAttribute(combined, add, CoreAttributeTypes.QualificationMethod.getName(), ",");
      mergeAttribute(combined, add, CoreAttributeTypes.VerificationEvent.getName(), ",");
      mergeAttribute(combined, add, CoreAttributeTypes.VerificationLevel.getName(), ",");
      mergeAttribute(combined, add, CoreAttributeTypes.VerificationAcceptanceCriteria.getName(), ",");
   }

   private void mergeArtifactKind(RoughArtifact combined, RoughArtifact add) {
      if (add.getRoughArtifactKind().equals(RoughArtifactKind.PRIMARY)) {
         combined.setPrimaryArtifactType(CoreArtifactTypes.HtmlArtifact);
         combined.setRoughArtifactKind(RoughArtifactKind.PRIMARY);
      }
   }

   private void mergeImages(RoughArtifact combined, RoughArtifact add) {
      Collection<URI> requirementUri = add.getURIAttributes();
      if (requirementUri.size() > 0) {
         for (URI uri : requirementUri) {
            combined.addAttribute(CoreAttributeTypes.ImageContent.getName(), uri);
         }
      }
   }

   private void mergeAttribute(RoughArtifact combined, RoughArtifact add, String attrName, String joiner) {
      String addendum = add.getRoughAttribute(attrName);
      String current = combined.getRoughAttribute(attrName);
      String result = combineStrings(current, addendum, joiner);
      if (Strings.isValid(result)) {
         combined.setAttribute(attrName, result);
      }
   }

   private String combineStrings(String result, String addendum, String joiner) {
      if (result == null && addendum == null) {
         return "";
      }
      StringBuilder sb = new StringBuilder();
      if (result == null) {
         sb.append(addendum);
      } else if (addendum == null) {
         sb.append(result);
      } else {
         sb.append(result);
         sb.append(joiner);
         sb.append(addendum);
      }
      return sb.toString();
   }
}
