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

package org.eclipse.osee.define.operations.synchronization.markdownsynchronizationartifactbuilder;

import java.io.InputStream;
import java.util.function.Function;
import org.apache.commons.io.input.CharSequenceInputStream;
import org.eclipse.osee.define.operations.synchronization.publishingdom.Document;
import org.eclipse.osee.define.operations.synchronization.publishingdom.DocumentMap;
import org.eclipse.osee.define.operations.synchronization.publishingdom.DocumentObject;
import org.eclipse.osee.define.operations.synchronization.publishingdom.HierarchicalNode;
import org.eclipse.osee.define.operations.synchronization.publishingdom.NodeWrongTypeException;

/**
 * Publishes a Mark Down Document from a Publishing DOM.
 *
 * @author Loren K. Ashley
 */

public class MarkdownSynchronizationArtifactSerializer implements Function<DocumentMap, InputStream> {

   /**
    * The size of buffers to use for serializing the mark down document.
    */

   private static int bufferSize = 8 * 1024 * 1024;

   /**
    * Array of mark down heading string. The level 1 (#) mark down heading is reserved for the document. Levels 2-6 are
    * used for the hierarchical level of the document object.
    */

   private static String[] headings = {"", "#", "##", "###", "####", "#####", "######"};

   /**
    * When <code>true</code>, mark down headings will include the object type in parenthesis after the heading name.
    */

   private static boolean printHeadingsWithTypes = false;

   /**
    * Recursively serializes the Publishing DOM {@link DocumentObject} {@link HierarchicalNode}s.
    *
    * @param hierarchicalNode the Publishing DOM {@link HierarchicalNode} to serialize.
    * @param stringBuilder serialization results are written to this {@link StringBuilder}.
    * @throws NodeWrongTypeException when the <code>hierarchicalNode</code> is not a {@link DocumentObject}.
    */

   void serializeDocumentObject(HierarchicalNode hierarchicalNode, StringBuilder stringBuilder) {

      if (!(hierarchicalNode instanceof DocumentObject)) {
         throw new NodeWrongTypeException(hierarchicalNode.getIdentifier(), hierarchicalNode, DocumentObject.class);
      }

      var documentObject = (DocumentObject) hierarchicalNode;

      var level = documentObject.getHierarchyLevel();

      level = level <= 4 ? level : 4;

      //@formatter:off
      stringBuilder
         .append( headings[level ] )
         .append( " " )
         .append( documentObject.getHierarchyLevelString() )
         .append( " " )
         .append( documentObject.getNameAttributeValue().orElse( "\"Name\" attribute not found.") )
         ;

      if( MarkdownSynchronizationArtifactSerializer.printHeadingsWithTypes ) {
         stringBuilder
            .append( " ( " )
            .append( documentObject.getName() )
            .append( " - " )
            .append( documentObject.getType() )
            .append( " ) " )
            ;
      }

      stringBuilder
         .append( "\n" )
         .append( "\n" )
         .append( documentObject.getPrimaryAttributeValue().orElse( "\"Primary Attribute\" or referenced attribute not found." ) )
         .append( "\n" )
         .append( "\n" )
         ;

      documentObject.streamHierarchicalChildren().forEach
         (
            ( childDocumentObject ) -> this.serializeDocumentObject( childDocumentObject, stringBuilder )
         );
      //@formatter:on
   }

   /**
    * Serializes the Publishing DOM {@link Document} {@link HierarchicalNode}s and starts the recursive serialization
    * process for each {@link Document}'s {@link DocumentObject} {@link HierarchicalNode}s.
    *
    * @param hierarchicalNode the Publishing DOM {@link HierarchicalNode} to serialize.
    * @param stringBuilder serialization results are written to this {@link StringBuilder}.
    * @throws NodeWrongTypeException when the <code>hierarchicalNode</code> is not a {@link Document}.
    */

   void serializeDocument(HierarchicalNode hierarchicalNode, StringBuilder stringBuilder) {

      if (!(hierarchicalNode instanceof Document)) {
         throw new NodeWrongTypeException(hierarchicalNode.getIdentifier(), hierarchicalNode, Document.class);
      }

      var document = (Document) hierarchicalNode;

      //@formatter:off
      stringBuilder
         .append( "# 1 " )
         .append( document.getName() )
         ;

      if( MarkdownSynchronizationArtifactSerializer.printHeadingsWithTypes ) {
         stringBuilder
            .append( " ( " )
            .append( document.getTypeDescription() )
            .append( " )" )
            ;
      }

      stringBuilder
         .append( "\n" )
         .append( "\n" )
         ;

      document.streamHierarchicalChildren().forEach
         (
            ( documentObject ) -> this.serializeDocumentObject( documentObject, stringBuilder )
         );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    * <p>
    * Publishes the Mark Down Document from the Publishing DOM.
    *
    * @param documentMap the Publishing DOM.
    * @return an {@link InputStream} the published Mark Down Document can be read from.
    */

   @Override
   public InputStream apply(DocumentMap documentMap) {

      var stringBuilder = new StringBuilder(MarkdownSynchronizationArtifactSerializer.bufferSize);

      //@formatter:off
      documentMap.streamHierarchicalChildren().forEach
         (
            ( document ) -> this.serializeDocument( document, stringBuilder )
         );
      //@formatter:on

      //@formatter:off
      return
         new CharSequenceInputStream
                (
                   stringBuilder,
                   "UTF-8",
                   MarkdownSynchronizationArtifactSerializer.bufferSize
                );
      //@formatter:on
   }

}

/* EOF */
