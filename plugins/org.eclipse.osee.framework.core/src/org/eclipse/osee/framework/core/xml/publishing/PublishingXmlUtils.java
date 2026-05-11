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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.eclipse.osee.framework.jdk.core.type.Result;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A class of utility methods for parsing and working with the XML DOM for a Word ML document.
 *
 * @author Loren K. Ashley
 */

public class PublishingXmlUtils {

   private static Void theVoid = null;

   public static class PublishingXmlError extends RuntimeException {

      /**
       * Default serialization identifier
       */

      private static final long serialVersionUID = 1L;

      PublishingXmlError(Message message, Throwable cause) {
         super(message.toString(), cause);
      }

   }

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
      final var textOptional =
         wordTable.getWordTableRowList()
            .flatMap( ( wordTableRowList    ) -> wordTableRowList.get( row ) )
            .flatMap( ( wordTableRow        ) -> wordTableRow.getWordTableColumnList() )
            .flatMap( ( wordTableColumnList ) -> wordTableColumnList.get( column ) )
            .flatMap( ( wordTableColumn     ) -> wordTableColumn.getWordTextList() )
            .flatMap( ( wordTextList        ) -> wordTextList.get( text ) )
            .map    ( ( wordText            ) -> wordText.getText() )
            ;
      //@formatter:on
      return textOptional;
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

   public Optional<WordTable> findTableByColumnText(WordDocument wordDocument, int row, int column, int text,
      String expectedColumnText) {

      this.startOperation();

      try {

         //@formatter:off
         return
            wordDocument
               .getWordTableList()
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
    * Reads a Word ML document from a file ({@link File}), closes the stream, and parses it into a
    * {@link org.w3c.dom.Document} XML DOM.
    *
    * @param file the file containing the Word ML document.
    * @return when the file is successfully parsed, a {@link Result} containing the {@link org.w3c.dom.Document};
    * otherwise, a {@link Result} with a {@link PublishingXmlError}.
    */

   public static Result<Document, PublishingXmlError> parse(File file) {

      try (var autoCloseInputStream = new FileInputStream(file)) {

         final var document = PublishingXmlUtils.parseInternal(autoCloseInputStream);

         return Result.ofValue(document);

      } catch (Exception e) {
         //@formatter:off
         return
            Result.ofError
               (
                  new PublishingXmlError
                         (
                            new Message()
                                   .title( "PublishingXmlUtils::parse, Failed to parse XML file." )
                                   .reasonFollows( e ),
                            e
                         )
               );
         //@formatter:on
      }
   }

   /**
    * Reads a Word ML document from an input stream ({@link InputStream}), closes the stream, and parses it into a
    * {@link org.w3c.dom.Document} XML DOM.
    *
    * @param inputStream an input stream, possibly compressed, containing the Word ML document
    * @return when the stream is successfully parsed, a {@link Result} containing the {@link org.w3c.dom.Document};
    * otherwise, a {@link Result} with a {@link PublishingXmlError}.
    */

   public static Result<Document, PublishingXmlError> parse(InputStream inputStream) {

      try (var autoCloseInputStream = inputStream) {

         final var document = PublishingXmlUtils.parseInternal(autoCloseInputStream);

         return Result.ofValue(document);

      } catch (Exception e) {
         //@formatter:off
         return
            Result.ofError
               (
                  new PublishingXmlError
                         (
                            new Message()
                                   .title( "PublishingXmlUtils::parse, Failed to parse XML input stream." )
                                   .reasonFollows( e ),
                            e
                         )
               );
         //@formatter:on
      }
   }

   /**
    * Reads a Word ML document from a {@link String} and parses it into a {@link org.w3c.dom.Document} XML DOM.
    *
    * @param xmlString a string containing the Word ML document
    * @return when the {@link String} is successfully parsed, a {@link Result} containing the
    * {@link org.w3c.dom.Document}; otherwise, a {@link Result} with a {@link PublishingXmlError}.
    */

   public static Result<Document, PublishingXmlError> parse(String xmlString) {

      try (var autoCloseInputStream = new ByteArrayInputStream(xmlString.getBytes())) {

         final var document = PublishingXmlUtils.parseInternal(autoCloseInputStream);

         return Result.ofValue(document);

      } catch (Exception e) {
         //@formatter:off
         return
            Result.ofError
               (
                  new PublishingXmlError
                         (
                            new Message()
                                   .title( "PublishingXmlUtils::parse, Failed to parse XML string." )
                                   .reasonFollows( e ),
                            e
                         )
               );
         //@formatter:on
      }
   }

   /**
    * Finds all the {@link org.w3c.dom.Element}s with the specified XML tag and attributes.
    *
    * @param <P> the {@link AbstractElementList} parent type.
    * @param <L> an {@link AbstractElementList} type with a parent <code>P</code> and child <code>C</code>.
    * @param <C> the {@link AbstractElementList} child type.
    * @param parent the hierarchical parent to the list.
    * @param listFactory a {@link Function} implementation that will create a new list of type <code>L</code>.
    * @param childFactory a {@link Function} implementation that will create the children of type <code>C</code>.
    * @param childTagName the XML tag of the child elements to be found.
    * @param childAttributes a {@link Map} of the expected attribute names and values.
    * @return a possibly empty {@link AbstractElementList} with children representing the XML elements with the
    * specified XML tag and attributes.
    */

   //@formatter:off
   public <P extends AbstractElement,L extends AbstractElementList<? super P,? super C>,C extends AbstractElement> Optional<L>
      parseAbstractElementList
         (
            P                       parent,
            Function<P,L>           listFactory,
            BiFunction<P,Element,C> childFactory,
            String                  childTagName,
            Map<String,String>      childAttributes
         ) {
   //@formatter:on

      this.startOperation();

      try (var list = listFactory.apply(parent)) {

         /*
          * Collect all child elements with the specified tag. This will include nested occurrences also.
          */

         var nodeList = parent.getElement().getElementsByTagName(childTagName);

         var nodeCount = nodeList.getLength();

         for (int i = 0; i < nodeCount; i++) {

            var node = nodeList.item(i);

            if (node.getNodeType() != Node.ELEMENT_NODE) {
               continue;
            }

            var element = (Element) node;

            var attributeMap = element.getAttributes();

            var attributesFound = true;

            for (var entry : childAttributes.entrySet()) {
               var attributeNode = attributeMap.getNamedItem(entry.getKey());
               if (attributeNode == null) {
                  attributesFound = false;
                  break;
               }
               if (!entry.getValue().equals(attributeNode.getNodeValue())) {
                  attributesFound = false;
                  break;
               }
            }

            if (attributesFound) {
               var child = childFactory.apply(parent, element);
               list.add(child);
            }

         }

         parent.setChild(list);
         return Optional.of(list);

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
    * @return a possibly empty {@link AbstractElementList} with children representing the XML elements with the
    * specified XML tag.
    */

   //@formatter:off
   public static <
                    P extends AbstractElement,
                    L extends AbstractElementList<? super P,? super C>,
                    C extends AbstractElement
                 >
      Result< L, PublishingXmlError >
         parseDescendantsList
            (
               P                               parent,
               WordElementParserFactory<P,L,C> wordElementParserFactory
            ) {
   //@formatter:on

      try {

         final var listFactory = wordElementParserFactory.getListFactory();
         final var childFactory = wordElementParserFactory.getChildFactory();
         final var childTagName = wordElementParserFactory.getChildTag().getFullname();

         try (var list = listFactory.apply(parent)) {

            /*
             * Collect all child elements with the specified tag. This will include nested occurrences also.
             */

            var nodeList = parent.getElement().getElementsByTagName(childTagName);

            var nodeCount = nodeList.getLength();

            for (int i = 0; i < nodeCount; i++) {

               var node = nodeList.item(i);

               if (node.getNodeType() != Node.ELEMENT_NODE) {
                  continue;
               }

               var element = (Element) node;

               var child = childFactory.apply(parent, element);
               list.add(child);

            }

            parent.setChild(list);

            return Result.ofValue(list);
         }

      } catch (Exception e) {

         //@formatter:off
         return
            Result.ofError
               (
                  new PublishingXmlUtils.PublishingXmlError
                         (
                            new Message()
                                   .title( "Failed to parse descendant nodes." )
                                   .indentInc()
                                   .segment( "Parent",                      parent                   )
                                   .segment( "Word Element Parser Factory", wordElementParserFactory )
                                   .reasonFollows( e )
                                   .indentDec(),
                            e
                         )
               );
         //@formatter:on
      }

   }

   /**
    * Reads a Word ML document from an {@link InputStream} and parses it into a {@link org.w3c.dom.Document} XML DOM.
    *
    * @param xmlString a string containing the Word ML document
    * @return an {@link Optional} containing the {@link org.w3c.dom.Document}.
    * @throws ParserConfigurationException when a DocumentBuildercannot be created which satisfies the configuration
    * requested.
    * @throws IOException when any IO errors occur.
    * @throws SAXException when any parse errors occur.
    */

   private static Document parseInternal(InputStream inputStream)
      throws FactoryConfigurationError, ParserConfigurationException, SAXException, IOException {

      var documentBuilderFactory = DocumentBuilderFactory.newInstance();
      var documentBuilder = documentBuilderFactory.newDocumentBuilder();
      var document = documentBuilder.parse(inputStream);

      return document;
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

   public <P extends AbstractElement, C extends AbstractElement> Optional<C> parseChild(P parent,
      XmlTagSpecification childTag, BiFunction<P, Element, C> childFactory) {

      this.startOperation();

      try {

         final var parentElement = parent.getElement();
         final var tagName = childTag.getFullname();

         for (var node = parentElement.getFirstChild(); Objects.nonNull(node); node = node.getNextSibling()) {

            if (node.getNodeType() != Node.ELEMENT_NODE) {
               continue;
            }

            var element = (Element) node;

            if (tagName.equals(element.getTagName())) {

               final var child = childFactory.apply(parent, element);

               return Optional.of(child);
            }
         }

         return Optional.empty();

      } catch (Exception e) {
         this.lastCause.set(Cause.ERROR);
         this.lastError.set(e);
         return Optional.empty();
      }
   }

   /**
    * Adds the immediate <code>childTagName</code> children of the <code>parent</code> to the
    * {@link AbstractElementList}.
    *
    * @param <P> the {@link AbstractElementList} parent type.
    * @param <L> an {@link AbstractElementList} type with a parent <code>P</code> and child <code>C</code>.
    * @param <C> the {@link AbstractElementList} child type.
    * @param parent the hierarchical parent to the list.
    * @param listFactory a {@link Function} implementation that will create a new list of type <code>L</code>.
    * @param childFactory a {@link Function} implementation that will create the children of type <code>C</code>.
    * @param childTagName the XML tag of the child elements to be found.
    * @return a subclass of {@link AbstractElementList} containing the immediate children with the
    * <code>childNameTag</code> of the <code>parent</code>.
    */

   //@formatter:off
   public static <
                    P extends AbstractElement,
                    L extends AbstractElementList<P,C>,
                    C extends AbstractElement
                 >
      Result<L,PublishingXmlError>
         parseChildList
            (
               P                               parent,
               WordElementParserFactory<P,L,C> wordElementParserFactory
            ) {
   //@formatter:on

      try {

         final var listFactory = wordElementParserFactory.getListFactory();
         final var childFactory = wordElementParserFactory.getChildFactory();
         final var childTagName = wordElementParserFactory.getChildTag().getFullname();

         try (final var list = listFactory.apply(parent)) {

            var parentNode = parent.getElement();
            var childNodeList = parentNode.getChildNodes();
            var childCount = childNodeList.getLength();
            for (var i = 0; i < childCount; i++) {

               var childNode = childNodeList.item(i);

               if (Node.ELEMENT_NODE != childNode.getNodeType()) {
                  continue;
               }

               var childElement = (Element) childNode;

               if (childTagName.equals(childElement.getNodeName())) {

                  var child = childFactory.apply(parent, childElement);
                  list.add(child);
               }
            }

            parent.setChild(list);

            return Result.ofValue(list);
         }

      } catch (Exception e) {

         //@formatter:off
         return
            Result.ofError
               (
                  new PublishingXmlUtils.PublishingXmlError
                         (
                            new Message()
                                   .title( "Failed to parse child nodes." )
                                   .indentInc()
                                   .segment( "Parent",                      parent                   )
                                   .segment( "Word Element Parser Factory", wordElementParserFactory )
                                   .reasonFollows( e )
                                   .indentDec(),
                            e
                         )
               );
         //@formatter:on
      }

   }

   /**
    * Verifies the tag of the XML document's root element is the expected tag for a Word ML document and creates a
    * {@link WordDocument} object to reference the root element of the Word ML document.
    *
    * @param document the {@link org.w3c.dom.Document} to be parsed.
    * @return when the root element of the {@link org.w3c.dom.Document} has the expected tag for a Word ML document a
    * {@link Result} with an {@link WordDocument}; when the root element does not have the expected tag an empty
    * {@link Result}; or when an error occurs a {@link Result} with a {@link PublishingXmlError}.
    */

   public static Result<WordDocument, PublishingXmlError> parseWordDocument(Document document) {

      try {

         var rootElement = document.getDocumentElement();

         if (WordMlTag.WORD_DOCUMENT.isTagName(rootElement.getTagName())) {

            var wordDocument = new WordDocument(rootElement);

            return Result.ofValue(wordDocument);
         }

         return Result.empty();

      } catch (Exception e) {

         //@formatter:off
         return
            Result.ofError
               (
                  new PublishingXmlUtils.PublishingXmlError
                         (
                            new Message()
                                   .title( "Failed to parse child nodes." )
                                   .indentInc()
                                   .segment( "Parent",                      document )
                                   .reasonFollows( e )
                                   .indentDec(),
                            e
                         )
               );
         //@formatter:on
      }
   }

   //@formatter:off
   public static <
                    C  extends AbstractElement,
                    CL extends AbstractElementList<WordTableColumn,C>
                 >
      Result<Void,PublishingXmlUtils.PublishingXmlError>
         parseWordTable
            (
               WordTable                                                                  wordTable,
               Function<WordTableColumn,Result<CL,PublishingXmlUtils.PublishingXmlError>> columnParser
            ) {

      try {

         PublishingXmlUtils
            .parseChildList( wordTable, WordTableRowList.wordTableParentFactory )
            .ifErrorThrow( Result.ErrorToThrowableMapper.identity() )
            .ifValueAction
               (
                  ( wordTableRowList ) ->
                     wordTableRowList.forEach
                        (
                           ( wordTableRow ) ->
                              PublishingXmlUtils
                                 .parseChildList( wordTableRow, WordTableColumnList.wordTableRowParentFactory )
                                 .ifErrorThrow( Result.ErrorToThrowableMapper.identity() )
                                 .ifValueAction
                                    (
                                       ( wordTableColumnList ) ->
                                          wordTableColumnList.forEach
                                             (
                                                ( wordTableColumn ) ->
                                                   columnParser
                                                      .apply( wordTableColumn )
                                                      .ifErrorThrow( Result.ErrorToThrowableMapper.identity() )
                                             )
                                    )
                        )
               );

         return Result.ofValue( PublishingXmlUtils.theVoid );

      } catch( Exception e ) {

         return
            Result.ofError
               (
                  new PublishingXmlUtils.PublishingXmlError
                         (
                            new Message()
                                   .title( "PublishingXmlUtils::parseWordTable, Failed to parse the rows, columns, and column content of a table." )
                                   .indentInc()
                                   .segment( "Parent", wordTable )
                                   .reasonFollows( e ),
                            e
                         )
               );
      }
   }
   //@formatter:on

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
         transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
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
    * Resets the last {@link Cause} and {@link Exception} to default OK values. This method is invoked at the start of
    * all public utility methods.
    */

   private void startOperation() {
      this.lastCause.remove();
      this.lastError.remove();
   }

   /**
    * Checks if a <w:p> block contains "Artifact Id" followed by a numeric value.
    *
    * @param paragraphElement the <w:p> element to check.
    * @return true if the <w:p> contains "Artifact Id" followed by a numeric value; false otherwise.
    */
   public static boolean isArtifactIdMetadataAttribute(Element paragraphElement) {
      // Get all <w:t> elements within this <w:p>
      NodeList textNodes = paragraphElement.getElementsByTagName("w:t");

      boolean foundArtifactIdLabel = false;

      for (int i = 0; i < textNodes.getLength(); i++) {
         Node textNode = textNodes.item(i);

         if (textNode.getNodeType() == Node.ELEMENT_NODE) {
            Element textElement = (Element) textNode;

            // Check if this <w:t> contains "Artifact Id"
            if ("Artifact Id".equals(textElement.getTextContent())) {
               foundArtifactIdLabel = true;
            }

            // Check if this <w:t> contains a numeric artifact ID (e.g., "200406")
            if (foundArtifactIdLabel && textElement.getTextContent().matches("\\d+")) {
               return true;
            }
         }
      }

      // Return false if no matching structure is found
      return false;
   }

   /**
    * Checks if a <w:p> block contains any of the specified artifact names.
    *
    * @param paragraphElement the <w:p> element to check.
    * @param artifactNames an array of artifact names to check for.
    * @return true if the <w:p> contains any of the artifact names; false otherwise.
    */
   public static boolean containsAnyName(Element paragraphElement, String[] artifactNames) {
      // Get all <w:t> elements within this <w:p>
      NodeList textNodes = paragraphElement.getElementsByTagName("w:t");

      for (int i = 0; i < textNodes.getLength(); i++) {
         Node textNode = textNodes.item(i);

         if (textNode.getNodeType() == Node.ELEMENT_NODE) {
            Element textElement = (Element) textNode;

            // Check if the text matches any of the artifact names
            String textContent = textElement.getTextContent();
            for (String artifactName : artifactNames) {
               if (artifactName.equals(textContent)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   /**
    * Checks if a <w:p> block contains an outline number (e.g., <wx:t wx:val="4.1.3.1.1">).
    *
    * @param paragraphElement the <w:p> element to check.
    * @return true if the <w:p> contains an outline number; false otherwise.
    */
   public static boolean hasOutlineNumber(Element paragraphElement) {
      // Get all <wx:t> elements within this <w:p>
      NodeList outlineNodes = paragraphElement.getElementsByTagName("wx:t");

      for (int i = 0; i < outlineNodes.getLength(); i++) {
         Node outlineNode = outlineNodes.item(i);

         if (outlineNode.getNodeType() == Node.ELEMENT_NODE) {
            Element outlineElement = (Element) outlineNode;

            // Check if the wx:val attribute is present
            if (outlineElement.hasAttribute("wx:val")) {
               return true;
            }
         }
      }

      return false;
   }

}

/* EOF */
