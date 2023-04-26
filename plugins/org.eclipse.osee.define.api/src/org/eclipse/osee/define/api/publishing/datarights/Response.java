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
package org.eclipse.osee.define.api.publishing.datarights;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Arrays;
import java.util.Objects;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * Data structure returned by the Data Rights Manager in response to a request.
 *
 * @author Md I. Khan
 */

public class Response implements ToMessage {

   /**
    * This indicates the format of the statements that was requested.
    */

   private FormatIndicator format;

   /**
    * The CUI Header statement for the publish
    */

   private String cuiHeader;

   /**
    * An array of title, header, and footer statements all associated with a unique integer.
    */

   private StatementMap[] statementList;

   /**
    * A list of the title page statements for the publish by their identifiers in the statement-map.
    */

   private Integer[] titlePageStatementList;

   /**
    * A list of the header statements that are required on every page for the publish by their identifiers in the
    * statement-map.
    */

   private Integer[] everyPageHeaderStatementList;

   /**
    * A list of the footer statements that are required on every page for the publish by their identifiers in the
    * statement-map.
    */

   private Integer[] everyPageFooterStatementList;

   /**
    * The section enumerates the header and footer statements required for the section and a list of the artifact
    * identifiers in publishing order for the artifacts in the section.
    */

   private Section[] sectionList;

   /**
    * Creates a new empty {@link Response} object for JSON deserialization
    */

   public Response() {
      this.format = null;
      this.cuiHeader = null;
      this.statementList = null;
      this.titlePageStatementList = null;
      this.everyPageHeaderStatementList = null;
      this.everyPageFooterStatementList = null;
      this.sectionList = null;
   }

   /**
    * Creates a new {@link Response} object with data for JSON serialization
    *
    * @param format format indicator as an object of {@link FormatIndicator}
    * @param cuiHeader CUI header statement as a {@link String}
    * @param statementList array of {@link StatementMap[]} associated with a unique integer
    * @param titlePageStatementList array of {@link Integer[]} associated with title page statements
    * @param everyPageHeaderStatementList array of {@link Integer[]} associated with header statements
    * @param everyPageFooterStatementList array of {@link Integer[]} associated with footer statements
    * @param sectionList array of {@link Section}
    */

   //@formatter:off
   public Response ( FormatIndicator format, String cuiHeader, StatementMap[] statementList,
      Integer[] titlePageStatementList, Integer[] everyPageHeaderStatementList,
      Integer[] everyPageFooterStatementList, Section[] sectionList ) {

      this.format =
         Objects.requireNonNull(format, "Response::new, parameter \"format\" cannot be null.");
      this.cuiHeader =
         Objects.requireNonNull(cuiHeader, "Response::new, parameter \"cuiHeader\" cannot be null.");
      this.statementList =
         Objects.requireNonNull(statementList, "Response::new, parameter \"statementList\" cannot be null.");
      this.titlePageStatementList =
         Objects.requireNonNull(titlePageStatementList, "Response::new, parameter \"titlePageStatementList\" cannot be null.");
      this.everyPageHeaderStatementList =
         Objects.requireNonNull(everyPageHeaderStatementList, "Response::new, parameter \"everyPageHeaderStatementList\" cannot be null.");
      this.everyPageFooterStatementList =
         Objects.requireNonNull(everyPageFooterStatementList, "Response::new, parameter \"everyPageFooterStatementList\" cannot be null.");
      this.sectionList =
         Objects.requireNonNull(sectionList, "Response::new, parameter \"sectionList\" cannot be null.");
   }
   //@formatter:on

   /**
    * Gets the format indicator
    *
    * @return format
    * @throws IllegalStateException when the member {@link #format} has not been set
    */

   public FormatIndicator getFormat() {
      if (Objects.isNull(this.format)) {
         throw new IllegalStateException("Response::getFormat, member \"format\" has not been set.");
      }
      return this.format;
   }

   /**
    * Gets the CUI header statement
    *
    * @return cuiHeader
    * @throws IllegalStateException when the member {@link #cuiHeader} has not been set
    */

   public String getCuiHeader() {
      if (Objects.isNull(this.cuiHeader)) {
         throw new IllegalStateException("Response::getCuiHeader, member \"cuiHeader\" has not been set.");
      }
      return this.cuiHeader;
   }

   /**
    * Gets the list of statements
    *
    * @return statementList
    * @throws IllegalStateException when the member {@link #statementList} has not been set
    */

   public StatementMap[] getStatementList() {
      if (Objects.isNull(this.statementList)) {
         throw new IllegalStateException("Response::getStatementList, member \"statementList\" has not been set.");
      }
      return this.statementList;
   }

   /**
    * Gets the title page statement list identifiers
    *
    * @return titlePageStatementList
    * @throws IllegalStateException when the member {@link #titlePageStatementList} has not been set
    */

   public Integer[] getTitlePageStatementList() {
      if (Objects.isNull(this.titlePageStatementList)) {
         throw new IllegalStateException(
            "Response::getTitlePageStatementList, member \"titlePageStatementList\" has not been set.");
      }
      return this.titlePageStatementList;
   }

   /**
    * Gets list of every page header statements identifiers
    *
    * @return everyPageHeaderStatementList
    * @throws IllegalStateException when the member {@link #everyPageHeaderStatementList} has not been set
    */

   public Integer[] getEveryPageHeaderStatementList() {
      if (Objects.isNull(this.everyPageHeaderStatementList)) {
         throw new IllegalStateException(
            "Response::getEveryPageHeaderStatementList, member \"everyPageHeaderStatementList\" has not been set.");
      }
      return this.everyPageHeaderStatementList;
   }

   /**
    * Gets list of every page footer statements identifiers
    *
    * @return everyPageFooterStatementList
    * @throws IllegalStateException when the member {@link #everyPageFooterStatementList} has not been set
    */

   public Integer[] getEveryPageFooterStatementList() {
      if (Objects.isNull(this.everyPageFooterStatementList)) {
         throw new IllegalStateException(
            "Response::getEveryPageFooterStatementList, member \"everyPageFooterStatementList\" has not been set.");
      }
      return this.everyPageFooterStatementList;
   }

   /**
    * Gets the section list
    *
    * @return sectionList
    * @throws IllegalStateException when the member {@link #sectionList} has not been set
    */

   public Section[] getSectionList() {
      if (Objects.isNull(this.statementList)) {
         throw new IllegalStateException("Response::getSectionList, member \"sectionList\" has not been set.");
      }
      return this.sectionList;
   }

   /**
    * Predicates to test the validity of {@link Response} object
    *
    * @return <code>true</code> when all members are non-<code>null</code>; otherwise <code>false</code>
    */

   @JsonIgnore
   public boolean isValid() {
      //@formatter:off
      return
         Objects.nonNull(this.format) && this.format.isValid() &&
         Objects.nonNull(this.cuiHeader) && Strings.isValidAndNonBlank(this.cuiHeader) &&
         Objects.nonNull(this.statementList) && Arrays.stream(this.statementList).allMatch(StatementMap::isValid) &&
         Objects.nonNull(this.titlePageStatementList) &&
         Objects.nonNull(this.everyPageHeaderStatementList) &&
         Objects.nonNull(this.everyPageFooterStatementList) &&
         Objects.nonNull(this.sectionList) && Arrays.stream(this.sectionList).allMatch(Section::isValid);
      //@formatter:on
   }

   /**
    * Sets the format indicator
    *
    * @param format format indicator as an object of {@ FormatIndicator}
    * @throws IllegalStateException when the member {@link #format} has already been set
    * @throws NullPointerException when the parameter <code>format</code> is <code>null</code>
    */

   public void setFormat(FormatIndicator format) {
      if (Objects.nonNull(this.format)) {
         throw new IllegalStateException("Response::setFormat, member \"format\" has already been set.");
      }
      this.format = Objects.requireNonNull(format, "Response::setFormat, parameter \"format\" cannot be null.");
   }

   /**
    * Sets the CUI Header Statement
    *
    * @param cuiHeader CUI header statement as a {@link String}
    * @throws IllegalStateException when the member {@link #cuiHeader} has already been set
    * @throws NullPointerException when the parameter <code>cuiHeader</code> is <code>null</code>
    */

   public void setCuiHeader(String cuiHeader) {
      if (Objects.nonNull(this.cuiHeader)) {
         throw new IllegalStateException("Response::setCuiHeader, member \"cuiHeader\" has already been set.");
      }
      this.cuiHeader =
         Objects.requireNonNull(cuiHeader, "Response::setCuiHeader, parameter \"cuiHeader\" cannot be null.");
   }

   /**
    * Sets the list of statements
    *
    * @param statementList array of {@link StatementMap[]} associated with a unique integer
    * @throws IllegalStateException when the member {@link #statementList} has already been set
    * @throws NullPointerException when the parameter <code>statementList</code> is <code>null</code>
    */

   public void setStatementList(StatementMap[] statementList) {
      if (Objects.nonNull(this.statementList)) {
         throw new IllegalStateException("Response::setStatementList, member \"statementList\" has already been set.");
      }
      this.statementList = Objects.requireNonNull(statementList,
         "Response::setStatementList, parameter \"statementList\" cannot be null.");
   }

   /**
    * Sets the list of title page statement identifiers
    *
    * @param titlePageStatementList array of {@link Integer[]} associated with title page statements
    * @throws IllegalStateException when the member {@link #titlePageStatementList} has already been set
    * @throws NullPointerException when the parameter <code>titlePageStatementList</code> is <code>null</code>
    */

   public void setTitlePageStatementList(Integer[] titlePageStatementList) {
      if (Objects.nonNull(this.titlePageStatementList)) {
         throw new IllegalStateException(
            "Response::setTitlePageStatementList, member \"titlePageStatementList\" has already been set.");
      }
      this.titlePageStatementList = Objects.requireNonNull(titlePageStatementList,
         "Response::setTitlePageStatementList, parameter \"titlePageStatementList\" cannot be null.");
   }

   /**
    * Sets the list of every page header statement identifiers
    *
    * @param everyPageHeaderStatementList array of {@link Integer[]} associated with header statements
    * @throws IllegalStateException when the member {@link #everyPageHeaderStatementList} has already been set
    * @throws NullPointerException when the parameter <code>everyPageHeaderStatementList</code> is <code>null</code>
    */

   public void setEveryPageHeaderStatementlist(Integer[] everyPageHeaderStatementList) {
      if (Objects.nonNull(this.everyPageHeaderStatementList)) {
         throw new IllegalStateException(
            "Response::setEveryPageHeaderStatementList, member \"everyPageHeaderStatementList\" has already been set.");
      }
      this.everyPageHeaderStatementList = Objects.requireNonNull(everyPageHeaderStatementList,
         "Response::setEveryPageHeaderStatementList, parameter \"everyPageHeaderStatementList\" cannot be null.");
   }

   /**
    * Sets the list of every page header statement identifiers
    *
    * @param everyPageFooterStatementList array of {@link Integer[]} associated with footer statements
    * @throws IllegalStateException when the member {@link #everyPageFooterStatementList} has already been set
    * @throws NullPointerException when the parameter <code>everyPageFooterStatementList</code> is <code>null</code>
    */

   public void setEveryPageFooterStatementList(Integer[] everyPageFooterStatementList) {
      if (Objects.nonNull(this.everyPageFooterStatementList)) {
         throw new IllegalStateException(
            "Response::setEveryPageFooterStatementList, member \"everyPageFooterStatementList\" has already been set.");
      }
      this.everyPageFooterStatementList = Objects.requireNonNull(everyPageFooterStatementList,
         "Response::setEveryPageFooterStatementList, parameter \"everyPageFooterStatementList\" cannot be null.");
   }

   /**
    * Sets the list of sections
    *
    * @param sectionList array of {@link Section}
    * @throws IllegalStateException when the member {@link #sectionList} has already been set
    * @throws NullPointerException when the parameter <code>sectionList</code> is <code>null</code>
    */

   public void setSectionList(Section[] sectionList) {
      if (Objects.nonNull(this.sectionList)) {
         throw new IllegalStateException("Response::setSectionList, member \"sectionList\" has already been set.");
      }
      this.sectionList =
         Objects.requireNonNull(sectionList, "Response::setSectionList, parameter \"sectionList\" cannot be null.");
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
         .title( "Data Rights - Response" )
         .indentInc()
         .segment( "Format Indicator",   this.format )
         .segment( "CUI Header", this.cuiHeader )
         .segment( "Statement Map", this.statementList )
         .segment( "Title Page Statement List", this.titlePageStatementList )
         .segment( "Every Page Header Statement List", this.everyPageHeaderStatementList )
         .segment( "Every Page Footer Statement List", this.everyPageFooterStatementList )
         .segment( "Section List", this.sectionList )
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