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

package org.eclipse.osee.framework.core.xml.publishing;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A class of utility methods for parsing and working with the XML DOM for a Word ML document.
 *
 * @author Loren K. Ashley
 */

public class PublishingXmlUtils {

   /**
    * XML tag for the "wordDocument" element.
    */

   public static String WordDocumentTagName = "w:wordDocument";

   /**
    * XML tag for the Word document body element.
    */

   public static String BodyTagName = "w:body";

   /**
    * XML tag for a Word document table element.
    */

   public static String TableTagName = "w:tbl";

   /**
    * XML tag for a Word document table row element.
    */

   public static String TableRowTagName = "w:tr";

   /**
    * XML tag for a Word document table column element.
    */

   public static String TableColumnTagName = "w:tc";

   /**
    * XML tag for a Word document text element.
    */

   public static String TextTagName = "w:t";

   /**
    * Enumeration of method failure reasons. When a public method of this class fails, a {@link Cause} of the failure is
    * saved for later retrieval.
    */

   public static enum Cause {

      /**
       * This {@link Cause} indicates the method invocation terminated due to an exception.
       */

      ERROR,

      /**
       * This {@link Cause} is recorded when multiple items were found when only one was expected.
       */

      MORE_THAN_ONE,

      /**
       * This {@link Cause} is recorded when a {@link org.w3c.dom.Node} was expected to also be an
       * {@link org.w3c.dom.Element} but was not.
       */

      NODE_NOT_ELEMENT,

      /**
       * This {@link Cause} is recorded when an expected item was not found.
       */

      NOT_FOUND,

      /**
       * This {@link Cause} is recorded for normal completion of a method.
       */

      OK;
   }

   /**
    * Saves the {@link Cause} recorded by the last public method invocation.
    */

   private final ThreadLocal<Cause> lastCause;

   /**
    * If the last public method invocation resulted in an exception, saves the exception.
    */

   private final ThreadLocal<Exception> lastError;

   /**
    * Creates a new instance of the utility class and initializes the error tracking logic.
    */

   public PublishingXmlUtils() {
      this.lastCause = new ThreadLocal<>() {
         @Override
         protected Cause initialValue() {
            return Cause.OK;
         }
      };
      this.lastError = new ThreadLocal<>();
   }

   /**
    * Finds the first immediate child {@link org.w3c.dom.Element} of the specified {@link org.w3c.dom.Element} with the
    * specified XML tag.
    *
    * @param parent the {@link org.w3c.dom.Element} to be searched under.
    * @param tagName the XML tag of the immediate child element to be found.
    * @return when an immediate child of the <code>parent</code> with the specified <code>tagName</code> is found, a
    * {@link Optional} containing the child {@link org.w3c.dom.Element}; otherwise, an empty {@link Optional}.
    */

   private Optional<Element> findChildElementByTagName(Element parent, String tagName) {

      for (var node = parent.getFirstChild(); Objects.nonNull(node); node = node.getNextSibling()) {

         if (node.getNodeType() != Node.ELEMENT_NODE) {
            continue;
         }

         var element = (Element) node;

         if (tagName.equals(element.getTagName())) {
            return Optional.of(element);
         }
      }

      return Optional.empty();
   }

   /**
    * Sequentially searches the top level Word tables in the Word document for a table with the expected text in the
    * specified table row, column, and text list position.
    *
    * @param wordDocument the {@link WordDocument} to be searched.
    * @param row the table row index to check text in.
    * @param column the table column index to check text in.
    * @param text the text list index to check text in.
    * @param expectedColumnText the expected text.
    * @return when a Word table is found with the expected text, an {@link Optional} with the found {@link WordTable};
    * otherwise, an empty {@link Optional}.
    */

   public Optional<WordTable> findTableByColumnText(WordDocument wordDocument, int row, int column, int text, String expectedColumnText) {

      this.startOperation();

      try {
         //@formatter:off
         return
            wordDocument.getWordBody()
               .flatMap( ( wordBody ) -> wordBody.getWordTableList() )
               .map( WordTableList::stream )
               .orElseGet( Stream::empty )
               .filter( ( wordTable ) -> PublishingXmlUtils.rawFindTableColumnText(wordTable, row, column, text )
                                            .map( ( foundColumnText ) -> foundColumnText.equals( expectedColumnText ) )
                                            .orElse( false ) )
               .findFirst()
               ;
         //@formatter:on

      } catch (Exception e) {

         this.lastCause.set(Cause.ERROR);
         this.lastError.set(e);
         return Optional.empty();
      }

   }

   /**
    * Extracts table text from the specified row, column, and text list position.
    *
    * @param wordTable the {@link WordTable} to extract text from.
    * @param row the table row index to get text from.
    * @param column the table column index to get text from.
    * @param text the text list index to get text from.
    * @return when the text list in the table row and column contains text at the specified index, an {@link Optional}
    * containing the specified text; otherwise, an empty {@link Optional}.
    */

   public Optional<String> findTableColumnText(WordTable wordTable, int row, int column, int text) {

      this.startOperation();

      try {
         return PublishingXmlUtils.rawFindTableColumnText(wordTable, row, column, text);
      } catch (Exception e) {
         this.lastCause.set(Cause.ERROR);
         this.lastError.set(e);
         return Optional.empty();
      }
   }

   /**
    * Gets the recorded {@link Cause} for the last public utility method invocation.
    *
    * @apiNote This is a status method and does not effect the recorded {@link Cause} or {@link Exception}.
    * @return the last recorded {@link Cause}.
    */

   public Cause getLastCause() {
      return this.lastCause.get();
   }

   /**
    * Predicate to indicate if the last public utility method invocation resulted the recording of an {@link Cause#OK}
    * status.
    *
    * @apiNote This is a status method and does not effect the recorded {@link Cause} or {@link Exception}.
    * @return <code>true</code> when the recorded {@link Cause} is {@link Cause#OK}; otherwise, <code>false</code>.
    */

   public boolean isOk() {
      return this.lastCause.get().equals(Cause.OK);
   }

   /**
    * Predicate to indicate if the last public utility method invocation did not result in the recording of an
    * {@link Cause#OK} status.
    *
    * @apiNote This is a status method and does not effect the recorded {@link Cause} or {@link Exception}.
    * @return <code>true</code> when the recorded {@link Cause} is not {@link Cause#OK}; otherwise, <code>false</code>.
    */

   public boolean isKo() {
      return !this.isOk();
   }

   /**
    * Gets the recorded {@link Exception} for the last public utility method invocation.
    *
    * @apiNote This is a status method and does not effect the recorded {@link Cause} or {@link Exception}.
    * @return when the last invocation of a public utility method threw an {@link Exception}, an {@link Optional}
    * containing the thrown {@link Exception}; otherwise, an empty {@link Optional}.
    */

   public Optional<Exception> getLastError() {
      return Optional.ofNullable(this.lastError.get());
   }

   /**
    * Reads a Word ML document from a compressed input stream ({@link GZIPInputStream}) and parses it into a
    * {@link org.w3c.dom.Document} XML DOM.
    *
    * @param gzipInputStream a compressed input stream containing the Word ML document
    * @return when the stream is successfully parsed, an {@link Optional} containing the {@link org.w3c.dom.Document};
    * otherwise, an empty {@link Optional}.
    */

   public Optional<Document> parse(GZIPInputStream gzipInputStream) {

      this.startOperation();

      try (var inputStream = gzipInputStream) {

         var documentBuilderFactory = DocumentBuilderFactory.newInstance();
         var documentBuilder = documentBuilderFactory.newDocumentBuilder();
         var document = documentBuilder.parse(inputStream);

         return Optional.of(document);

      } catch (Exception e) {

         this.lastCause.set(Cause.ERROR);
         this.lastError.set(e);
         return Optional.empty();
      }
   }

   /**
    * Finds all the {@link org.w3c.dom.Element}s with the specified XML tag.
    *
    * @param <P> the {@link AbstractElementList} parent type.
    * @param <L> an {@link AbstractElementList} type with a parent <code>P</code> and child <code>C</code>.
    * @param <C> the {@link AbstractElementList} child type.
    * @param parent the hierarchical parent to the list.
    * @param listFactory a {@link Function} implementation that will create a new list of type <code>L</code>.
    * @param childFactory a {@link Function} implementation that will create the children of type <code>C</code>.
    * @param childTagName the XML tag of the child elements to be found.
    * @return a possibly empty {@link AbstractElementList} with children representing the first level XML elements with
    * the specified XML tag.
    */

   //@formatter:off
   private <P extends AbstractElement,L extends AbstractElementList<? super P,? super C>,C extends AbstractElement> L
      parseAbstractElementList
         (
            P                       parent,
            Function<P,L>           listFactory,
            BiFunction<P,Element,C> childFactory,
            String                  childTagName
         ) {
   //@formatter:on

      var list = listFactory.apply(parent);

      try {

         /*
          * Collect all child elements with the specified tag. This will include nested occurrences also.
          */

         var nodeList = parent.getElement().getElementsByTagName(childTagName);

         var nodeCount = nodeList.getLength();

         if (nodeCount <= 0) {

            return list;
         }

         for (int i = 0; i < nodeCount; i++) {

            var node = nodeList.item(i);

            if (node.getNodeType() != Node.ELEMENT_NODE) {
               continue;
            }

            var element = (Element) node;

            var child = childFactory.apply(parent, element);
            list.add(child);

         }

         return list;

      } finally {
         list.close();
      }

   }

   /**
    * Finds the first level {@link org.w3c.dom.Element}s with the specified XML tag.
    *
    * @param <P> the {@link AbstractElementList} parent type.
    * @param <L> an {@link AbstractElementList} type with a parent <code>P</code> and child <code>C</code>.
    * @param <C> the {@link AbstractElementList} child type.
    * @param parent the hierarchical parent to the list.
    * @param listFactory a {@link Function} implementation that will create a new list of type <code>L</code>.
    * @param childFactory a {@link Function} implementation that will create the children of type <code>C</code>.
    * @param childTagName the XML tag of the child elements to be found.
    * @return a possibly empty {@link AbstractElementList} with children representing the first level XML elements with
    * the specified XML tag.
    */

   //@formatter:off
   private <P extends AbstractElement,L extends AbstractElementList<? super P,? super C>,C extends AbstractElement> L
      parseNonNestedAbstractElementList
         (
            P                       parent,
            Function<P,L>           listFactory,
            BiFunction<P,Element,C> childFactory,
            String                  childTagName
         ) {
   //@formatter:on

      var list = listFactory.apply(parent);

      try {

         /*
          * Collect all child elements with the specified tag. This will include nested occurrences also.
          */

         var possiblyNestedNodeList = parent.getElement().getElementsByTagName(childTagName);

         var possiblyNestedNodeCount = possiblyNestedNodeList.getLength();

         if (possiblyNestedNodeCount <= 0) {

            return list;
         }

         var possiblyNestedElementList = new ArrayList<Element>(possiblyNestedNodeCount);
         var possiblyNestedElementCount = 0;

         for (int i = 0; i < possiblyNestedNodeCount; i++) {

            var possiblyNestedNode = possiblyNestedNodeList.item(i);

            if (possiblyNestedNode.getNodeType() != Node.ELEMENT_NODE) {
               continue;
            }

            var possiblyNestedElement = (Element) possiblyNestedNode;

            possiblyNestedElementList.add(possiblyNestedElement);
            possiblyNestedElementCount++;
         }

         /*
          * Find the indices of the elements that are nested.
          */

         var nestedElementIndexSet = new HashSet<Integer>(possiblyNestedElementCount);

         for (var i = 0; i < possiblyNestedElementCount; i++) {
            var possiblyNestedElement = possiblyNestedElementList.get(i);

            var subNodeList = possiblyNestedElement.getElementsByTagName(childTagName);
            var subNodeCount = subNodeList.getLength();

            if (subNodeCount <= 0) {
               /*
                * The current element does not contain any nested elements
                */
               continue;
            }

            for (int j = 0; j < subNodeCount; j++) {

               var subNode = subNodeList.item(j);
               var nestedElement = possiblyNestedElementList.get(i + 1 + j);

               if (!nestedElement.isSameNode(subNode)) {
                  throw new RuntimeException("I died");
               }

               nestedElementIndexSet.add(i + 1 + j);
            }

            i += subNodeCount;
         }

         /*
          * Copy just the first level elements to the final list
          */

         for (var i = 0; i < possiblyNestedElementCount; i++) {

            if (!nestedElementIndexSet.contains(i)) {
               var firstLevelElement = possiblyNestedElementList.get(i);

               var child = childFactory.apply(parent, firstLevelElement);
               list.add(child);
            }
         }

         return list;

      } finally {
         list.close();
      }

   }

   /**
    * Finds the immediate child element of the {@link WordDocument} that contains the Word ML document body and creates
    * a {@link WordBody} object to reference the Word body element.
    *
    * @param wordDocument a {@link WordDocument} element reference to the Word document element of the Word ML document.
    * @return when the Word body element is found, an {@link Optional} with an {@link WordBody}; otherwise, an empty
    * {@link Optional}.
    */

   public Optional<WordBody> parseWordBody(WordDocument wordDocument) {

      this.startOperation();

      try {

         var wordBodyElementOptional =
            this.findChildElementByTagName(wordDocument.getElement(), PublishingXmlUtils.BodyTagName);

         if (wordBodyElementOptional.isEmpty()) {
            this.lastCause.set(Cause.NOT_FOUND);
            return Optional.empty();
         }

         var wordBodyElement = wordBodyElementOptional.get();

         var wordBody = new WordBody(wordDocument, wordBodyElement);

         wordDocument.setChild(wordBody);

         return Optional.of(wordBody);

      } catch (Exception e) {
         this.lastCause.set(Cause.ERROR);
         this.lastError.set(e);
         return Optional.empty();
      }
   }

   /**
    * Verifies the tag of the XML document's root element is the expected tag for a Word ML document and creates a
    * {@link WordDocument} object to reference the root element of the Word ML document.
    *
    * @param document the {@link org.w3c.dom.Document} to be parsed.
    * @return when the root element of the {@link org.w3c.dom.Document} has the expected tag for a Word ML document, an
    * {@link Optional} with an {@link WordDocument}; otherwise, an empty {@link Optional}.
    */

   public Optional<WordDocument> parseWordDocument(Document document) {

      this.startOperation();

      try {

         var rootElement = document.getDocumentElement();

         if (PublishingXmlUtils.WordDocumentTagName.equals(rootElement.getTagName())) {
            var wordDocument = new WordDocument(rootElement);

            return Optional.of(wordDocument);
         }

         this.lastCause.set(Cause.NOT_FOUND);
         return Optional.empty();

      } catch (Exception e) {

         this.lastCause.set(Cause.ERROR);
         this.lastError.set(e);
         return Optional.empty();
      }
   }

   /**
    * Parses the first level table columns from a Word table row. The found columns belong to the provided table row and
    * do not include any table columns from nested tables.
    *
    * @param wordTableRow the {@link WordTableRow} handle to the Word ML table row.
    * @return on successful completion, an {@link Optional} with a possibly empty {@link WordTableRowList}; otherwise,
    * an empty {@link Optional}.
    */

   public Optional<WordTableColumnList> parseWordTableColumnListFromWordTableRow(WordTableRow wordTableRow) {

      this.startOperation();

      try {
         var wordTableColumnList = this.parseNonNestedAbstractElementList(wordTableRow, WordTableColumnList::new,
            WordTableColumn::new, PublishingXmlUtils.TableColumnTagName);
         wordTableRow.setChild(wordTableColumnList);
         return Optional.of(wordTableColumnList);
      } catch (Exception e) {
         this.lastCause.set(Cause.ERROR);
         this.lastError.set(e);
         return Optional.empty();
      }

   }

   /**
    * Parses the first level Word tables from the body of a Word ML document. The found tables are not necessarily
    * immediate children of the document body, but are not nested within a table.
    *
    * @param wordBody the {@link WordBody} handle to the Word ML document body.
    * @return on successful completion, an {@link Optional} with a possibly empty {@link WordTableList}; otherwise, an
    * empty {@link Optional}.
    */

   public Optional<WordTableList> parseWordTableListFromWordBody(WordBody wordBody) {

      this.startOperation();

      try {
         var wordTableList = this.parseNonNestedAbstractElementList(wordBody, WordTableList::new, WordTable::new,
            PublishingXmlUtils.TableTagName);
         wordBody.setChild(wordTableList);
         return Optional.of(wordTableList);
      } catch (Exception e) {
         this.lastCause.set(Cause.ERROR);
         this.lastError.set(e);
         return Optional.empty();
      }
   }

   /**
    * Parses the first level table rows from a Word table. The found rows belong to the provided word table and do not
    * include any table rows from nested tables.
    *
    * @param wordTable the {@link WordTable} handle to the WordML table.
    * @return on successful completion, an {@link Optional} with a possibly empty {@link WordTableRowList}; otherwise,
    * an empty {@link Optional}.
    */

   public Optional<WordTableRowList> parseWordTableRowListFromWordTable(WordTable wordTable) {

      this.startOperation();

      try {
         var wordTableRowList = this.parseNonNestedAbstractElementList(wordTable, WordTableRowList::new,
            WordTableRow::new, PublishingXmlUtils.TableRowTagName);
         wordTable.setChild(wordTableRowList);
         return Optional.of(wordTableRowList);
      } catch (Exception e) {
         this.lastCause.set(Cause.ERROR);
         this.lastError.set(e);
         return Optional.empty();
      }

   }

   /**
    * Parses all the Word text elements from a Word table column. This will include text elements from any tables nested
    * within the Word table column being parsed.
    *
    * @param wordTableColumn a {@link WordTableColumn} handle to the the Word ML table column.
    * @return on successful completion, an {@link Optional} with a possibly empty {@link WordTextList}; otherwise, an
    * empty {@link Optional}.
    */
   public Optional<WordTextList> parseWordTextListFromWordTableColumn(WordTableColumn wordTableColumn) {

      this.startOperation();

      try {
         var wordTextList = this.parseAbstractElementList(wordTableColumn, WordTextList::new, WordText::new,
            PublishingXmlUtils.TextTagName);
         wordTableColumn.setChild(wordTextList);
         return Optional.of(wordTextList);
      } catch (Exception e) {
         this.lastCause.set(Cause.ERROR);
         this.lastError.set(e);
         return Optional.empty();
      }

   }

   /**
    * Pretty prints the XML {@link org.w3c.dom.Document} to a {@link String}. The XML DOM is printed with the following
    * properties:
    * <ul>
    * <li>The XML declaration is omitted.</li>
    * <li>Nested XML tags are indented with 3 spaces.</li>
    * </ul>
    *
    * @param document the {@link org.w3c.dom.Document} to be printed to a {@link String}.
    * @return when the print is successful, an {@link Optional} with the generated {@link String}; otherwise, an empty
    * {@link Optional}.
    */

   public Optional<String> prettyPrint(Document document) {

      this.startOperation();

      try {

         var transformerFactory = TransformerFactory.newInstance();
         transformerFactory.setAttribute("indent-number", 3);
         var transformer = transformerFactory.newTransformer();
         transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
         transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
         transformer.setOutputProperty(OutputKeys.INDENT, "yes");
         var stringWriter = new StringWriter();
         transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
         return Optional.of(stringWriter.toString());

      } catch (Exception e) {

         this.lastCause.set(Cause.ERROR);
         this.lastError.set(e);
         return Optional.empty();

      }
   }

   /**
    * Private static method to extract table text from the specified row, column, and text list position. This method
    * performs the extraction but does not provide the exception handling.
    *
    * @param wordTable the {@link WordTable} to extract text from.
    * @param row the table row index to get text from.
    * @param column the table column index to get text from.
    * @param text the text list index to get text from.
    * @return when the text list in the table row and column contains text at the specified index, an {@link Optional}
    * containing the specified text; otherwise, an empty {@link Optional}.
    */

   private static Optional<String> rawFindTableColumnText(WordTable wordTable, int row, int column, int text) {

      //@formatter:off
      return
         wordTable.getWordTableRowList()
            .flatMap( ( wordTableRowList    ) -> wordTableRowList.get( row ) )
            .flatMap( ( wordTableRow        ) -> wordTableRow.getWordTableColumnList() )
            .flatMap( ( wordTableColumnList ) -> wordTableColumnList.get( column ) )
            .flatMap( ( wordTableColumn     ) -> wordTableColumn.getWordTextList() )
            .flatMap( ( wordTextList        ) -> wordTextList.get( text ) )
            .map    ( ( wordText            ) -> wordText.getText() )
            ;
      //@formatter:on
   }

   /**
    * Resets the last {@link Cause} and {@link Exception} to default OK values. This method is invoked at the start of
    * all public utility methods.
    */

   private void startOperation() {
      this.lastCause.remove();
      this.lastError.remove();
   }
}

/* EOF */
