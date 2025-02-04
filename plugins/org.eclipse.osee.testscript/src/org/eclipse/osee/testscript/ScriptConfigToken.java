/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.testscript;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.accessor.types.ArtifactAccessorResultWithGammas;
import org.eclipse.osee.accessor.types.AttributePojo;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.rest.model.transaction.Attribute;
import org.eclipse.osee.orcs.rest.model.transaction.CreateArtifact;

public class ScriptConfigToken extends ArtifactAccessorResultWithGammas {

   public static final ScriptConfigToken SENTINEL = new ScriptConfigToken();

   private AttributePojo<Integer> testResultsToKeep =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.TestResultsToKeep, GammaId.SENTINEL, 10, "");

   public ScriptConfigToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public ScriptConfigToken(ArtifactReadable art) {
      super(art);
      this.setTestResultsToKeep(AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.TestResultsToKeep, -1)));
   }

   public ScriptConfigToken() {
   }

   public ApplicabilityToken getApplicability() {
      return ApplicabilityToken.BASE;
   }

   public AttributePojo<Integer> getTestResultsToKeep() {
      return testResultsToKeep;
   }

   /**
    * @param description the description to set
    */
   @JsonProperty
   public void setTestResultsToKeep(AttributePojo<Integer> testResultsToKeep) {
      this.testResultsToKeep = testResultsToKeep;
   }

   public void setTestResultsToKeep(Integer testResultsToKeep) {
      this.testResultsToKeep = AttributePojo.valueOf(this.testResultsToKeep.getId(), this.testResultsToKeep.getTypeId(),
         this.testResultsToKeep.getGammaId(), testResultsToKeep, this.testResultsToKeep.getDisplayableString());
   }

   public BranchId getBranch() {
      return BranchId.valueOf(this.getArtifactReadable().getBranch().getId());
   }

   public CreateArtifact createArtifact(String key) {
      Map<AttributeTypeToken, String> values = new HashMap<>();
      values.put(CoreAttributeTypes.TestResultsToKeep, Integer.toString(this.getTestResultsToKeep().getValue()));

      CreateArtifact art = new CreateArtifact();
      art.setName(this.getName().getValue());
      art.setTypeId(CoreArtifactTypes.ScriptConfiguration.getIdString());

      List<Attribute> attrs = new LinkedList<>();

      for (AttributeTypeToken type : CoreArtifactTypes.ScriptConfiguration.getValidAttributeTypes()) {
         String value = values.get(type);
         if (Strings.isInValid(value)) {
            continue;
         }
         Attribute attr = new Attribute(type.getIdString());
         attr.setValue(Arrays.asList(value));
         attrs.add(attr);
      }

      art.setAttributes(attrs);

      art.setkey(key);

      return art;
   }

}
