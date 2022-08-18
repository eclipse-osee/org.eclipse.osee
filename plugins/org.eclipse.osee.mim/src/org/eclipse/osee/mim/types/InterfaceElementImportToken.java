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

/**
 * @author Ryan T. Baldwin
 */
public class InterfaceElementImportToken extends PLGenericDBObject {
   public static final InterfaceElementImportToken SENTINEL = new InterfaceElementImportToken();
   private Boolean InterfaceElementAlterable;
   private String Notes;
   private String Description;
   private Integer InterfaceElementIndexStart;
   private Integer InterfaceElementIndexEnd;

   public InterfaceElementImportToken(Long id, String name) {
      super(id, name);
      setNotes("");
      setDescription("");
      setInterfaceElementIndexStart(0);
      setInterfaceElementIndexEnd(0);
      setInterfaceElementAlterable(false);
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

}
