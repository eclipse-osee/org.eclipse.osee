/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.mim.types;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.accessor.types.ArtifactAccessorResult;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.rest.model.transaction.Attribute;
import org.eclipse.osee.orcs.rest.model.transaction.CreateArtifact;

/**
 * @author Ryan T. Baldwin
 */
public class InterfaceElementImportToken extends ArtifactAccessorResult {
   public static final InterfaceElementImportToken SENTINEL = new InterfaceElementImportToken();
   private Boolean InterfaceElementAlterable;
   private String Notes;
   private String Description;
   private String enumLiteral;
   private String InterfaceDefaultValue;
   private Integer InterfaceElementIndexStart;
   private Integer InterfaceElementIndexEnd;

   public InterfaceElementImportToken(Long id, String name) {
      super(id, name);
      setNotes("");
      setDescription("");
      setEnumLiteral("");
      setInterfaceElementIndexStart(0);
      setInterfaceElementIndexEnd(0);
      setInterfaceElementAlterable(false);
      setInterfaceDefaultValue("");
   }

   public InterfaceElementImportToken() {
      super();
   }

   /**
    * @return the description
    */
   public String getDescription() {
      return Description;
   }

   /**
    * @param description the description to set
    */
   public void setDescription(String description) {
      Description = description;
   }

   /**
    * @return the notes
    */
   public String getNotes() {
      return Notes;
   }

   /**
    * @param notes the notes to set
    */
   public void setNotes(String notes) {
      Notes = notes;
   }

   public String getEnumLiteral() {
      return enumLiteral;
   }

   public void setEnumLiteral(String enumLiteral) {
      this.enumLiteral = enumLiteral;
   }

   /**
    * @return the interfaceElementAlterable
    */
   public Boolean getInterfaceElementAlterable() {
      return InterfaceElementAlterable;
   }

   /**
    * @param interfaceElementAlterable the interfaceElementAlterable to set
    */
   public void setInterfaceElementAlterable(Boolean interfaceElementAlterable) {
      InterfaceElementAlterable = interfaceElementAlterable;
   }

   /**
    * @return the interfaceElementIndexStart
    */
   public Integer getInterfaceElementIndexStart() {
      return InterfaceElementIndexStart;
   }

   /**
    * @param interfaceElementIndexStart the interfaceElementIndexStart to set
    */
   public void setInterfaceElementIndexStart(Integer interfaceElementIndexStart) {
      InterfaceElementIndexStart = interfaceElementIndexStart;
   }

   /**
    * @return the interfaceElementIndexEnd
    */
   public Integer getInterfaceElementIndexEnd() {
      return InterfaceElementIndexEnd;
   }

   /**
    * @param interfaceElementIndexEnd the interfaceElementIndexEnd to set
    */
   public void setInterfaceElementIndexEnd(Integer interfaceElementIndexEnd) {
      InterfaceElementIndexEnd = interfaceElementIndexEnd;
   }

   public String getInterfaceDefaultValue() {
      return InterfaceDefaultValue;
   }

   public void setInterfaceDefaultValue(String interfaceDefaultValue) {
      InterfaceDefaultValue = interfaceDefaultValue;
   }

   public CreateArtifact createArtifact(String key, ApplicabilityId applicId) {
      Map<AttributeTypeToken, String> values = new HashMap<>();
      values.put(CoreAttributeTypes.Description, this.getDescription());
      values.put(CoreAttributeTypes.InterfaceDefaultValue, this.getInterfaceDefaultValue());
      values.put(CoreAttributeTypes.InterfaceElementAlterable, this.getInterfaceElementAlterable().toString());
      values.put(CoreAttributeTypes.Notes, this.getNotes());
      values.put(CoreAttributeTypes.InterfaceElementEnumLiteral, this.getEnumLiteral());
      values.put(CoreAttributeTypes.InterfaceElementIndexStart, this.getInterfaceElementIndexStart().toString());
      values.put(CoreAttributeTypes.InterfaceElementIndexEnd, this.getInterfaceElementIndexEnd().toString());

      CreateArtifact art = new CreateArtifact();
      art.setName(this.getName());
      art.setTypeId(CoreArtifactTypes.InterfaceDataElement.getIdString());

      List<Attribute> attrs = new LinkedList<>();

      for (AttributeTypeToken type : CoreArtifactTypes.InterfaceDataElement.getValidAttributeTypes()) {
         String value = values.get(type);
         if (Strings.isInValid(value)) {
            continue;
         }
         Attribute attr = new Attribute(type.getIdString());
         attr.setValue(Arrays.asList(value));
         attrs.add(attr);
      }

      art.setAttributes(attrs);
      art.setApplicabilityId(applicId.getIdString());
      art.setkey(key);
      return art;
   }

}
