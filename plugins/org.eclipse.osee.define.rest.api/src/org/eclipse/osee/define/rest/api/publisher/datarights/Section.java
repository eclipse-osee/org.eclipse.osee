/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.define.rest.api.publisher.datarights;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * A section is a run of Artifacts for a publish that have identical data rights. The section specifies the required
 * header and footer statements for the section and the list of Artifacts in the section.
 *
 * @author Md I. Khan
 */

public class Section implements ToMessage {

   /**
    * The header statement list contains the identifiers of the header statements required for the section.
    */

   private Integer[] headerStatementList;

   /**
    * The footer statement list contains the identifiers of the footer statements required for the section.
    */

   private Integer[] footerStatementList;

   /**
    * This is a list of the artifact-identifiers for the Artifacts in the section. The list is arranged in the
    * publishing order of the artifacts.
    */

   private Long[] artifactIdList;

   /**
    * Creates a new empty {@link Section} object for JSON deserialization
    */

   public Section() {
      this.headerStatementList = null;
      this.footerStatementList = null;
      this.artifactIdList = null;
   }

   /**
    * Creates a new {@link Section} object with data for JSON serialization
    *
    * @param headerStatementList list of header statement identifiers as {@link Integer[]}
    * @param footerStatemetList list of footer statement identifiers as {@link Integer[]}
    * @param artifactIdList list of artifact identifiers as {@link Long[]}
    * @throws NullPointerException when any of the parameters are null
    */

   public Section(Integer[] headerStatementList, Integer[] footerStatemetList, Long[] artifactIdList) {
      this.headerStatementList =
         Objects.requireNonNull(headerStatementList, "Section::new, parameter \"headerStatementList\" cannot be null.");
      this.footerStatementList =
         Objects.requireNonNull(headerStatementList, "Section::new, parameter \"footerStatementList\" cannot be null.");
      this.artifactIdList =
         Objects.requireNonNull(artifactIdList, "Section::new, parameter \"artifactIdList\" cannot be null.");
   }

   /**
    * Gets the list of header statement identifier
    *
    * @return headerStatementList of type {@link Integer[]}
    * @throws IllegalStateException when {@link #headerStatementList} has not been set
    */

   public Integer[] getHeaderStatementList() {
      if (Objects.isNull(this.headerStatementList)) {
         throw new IllegalStateException(
            "Section::getHeaderStatementList, member \"headerStatementList\" has not been set.");
      }
      return this.headerStatementList;
   }

   /**
    * Gets the list of footer statement identifier
    *
    * @return footerStatementList of type {@link Integer[]}
    * @throws IllegalStateException when {@link #footerStatementList} has not been set
    */

   public Integer[] getFooterStatementList() {
      if (Objects.isNull(this.footerStatementList)) {
         throw new IllegalStateException(
            "Section::getFooterStatementList, member \"footerStatementList\" has not been set.");
      }
      return this.footerStatementList;
   }

   /**
    * Gets the list of artifact identifiers
    *
    * @return artifactIdList of type {@link Long[]}
    * @throws IllegalStateException when {@link #artifactIdList} has not been set
    */

   public Long[] getArtifactIdlist() {
      if (Objects.isNull(this.artifactIdList)) {
         throw new IllegalStateException("Section::getArtifactIdList, member \"artifactIdList\" has not been set.");
      }
      return this.artifactIdList;
   }

   /**
    * Predicates to test the validity of {@link Section} object
    *
    * @return <code>true</code> when all members are non-<code>null</code>; otherwise <code>false</code>
    */

   @JsonIgnore
   public boolean isValid() {
      //@formatter:off
      return
         Objects.nonNull(this.headerStatementList) &&
         Objects.nonNull(this.footerStatementList) &&
         Objects.nonNull(this.artifactIdList);
      //@formatter:on
   }

   /**
    * Sets the list of header statement identifiers
    *
    * @param headerStatementList list of header statement identifiers as {@link Integer[]}
    * @throws IllegalStatementException when the member {@link #headerStatementList} has already been set
    * @throws NullPointerException when the parameter <code>headerStatement</code> is <code>null</code>
    */

   public void setHeaderStatementList(Integer[] headerStatementList) {
      if (Objects.nonNull(this.headerStatementList)) {
         throw new IllegalStateException(
            "Section::setHeaderStatementList, member \"headerStatementList\" has already been set.");
      }
      this.headerStatementList = Objects.requireNonNull(headerStatementList,
         "Section::setHeaderStatementList, parameter \"headerStatementlist\" cannot be null.");
   }

   /**
    * Sets the list of footer statement identifiers
    *
    * @param footerStatementList list of footer statement identifiers as {@link Integer[]}
    * @throws IllegalStateException when the member {@link #footerStatementList} has already been set
    * @throws NullPointerException when the parameter <code>footerStatementList</code> is <code>null</code>
    */

   public void setFooterStatementList(Integer[] footerStatementList) {
      if (Objects.nonNull(this.footerStatementList)) {
         throw new IllegalStateException(
            "Section::setFooterStatementList, member \"footerStatementList\" has already been set.");
      }
      this.footerStatementList = Objects.requireNonNull(footerStatementList,
         "Section::setFooterStatementList, parameter \"footerStatementlist\" cannot be null.");
   }

   /**
    * Sets the list of artifact identifiers
    *
    * @param artifactIdList list of artifact identifiers as {@link Long[]}
    * @throws IllegalStateException when the member {@link #artifactIdList} has already been set
    * @throws NullPointerException when the parameter <code>artifactIdList</code> is <code>null</code>
    */

   public void setArtifactIdList(Long[] artifactIdList) {
      if (Objects.nonNull(this.artifactIdList)) {
         throw new IllegalStateException("Section::setArtifactIdList, member \"artifactIdList\" has already been set.");
      }
      this.artifactIdList = Objects.requireNonNull(artifactIdList,
         "Section::setArtifactIdList, parameter \"artifactIdlist\" cannot be null.");
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {
      var outMessage = (message != null) ? message : new Message();

      //@formatter:off
      outMessage
         .indent( indent )
         .title( "Section" )
         .indentInc()
         .segment( "Header Statement List",   this.headerStatementList )
         .segment( "Footer Statement List", this.footerStatementList )
         .segment( "Artifacts List", this.artifactIdList )
         .indentDec()
         ;
      //@formatter:on

      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String toString() {
      return this.toMessage(0, (Message) null).toString();
   }

}
/* EOF */