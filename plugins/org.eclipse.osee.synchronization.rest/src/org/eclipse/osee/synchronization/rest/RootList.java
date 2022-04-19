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

package org.eclipse.osee.synchronization.rest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.synchronization.api.SynchronizationEndpoint;
import org.eclipse.osee.synchronization.util.IndentedString;
import org.eclipse.osee.synchronization.util.ToMessage;

/**
 * Class to encapsulate a list of the branch and artifact identifiers of the native OSEE things that will be the
 * SpecificationGroveThing roots in the Synchronization Artifact.
 */

public class RootList implements Iterable<Root>, ToMessage {

   /**
    * Delimiter used to separate pairs of branch and artifact lists.
    */

   private final static char specificationDelimiter = ';';

   /**
    * Delimiter used to separate a branch identifier from an artifact identifier list.
    */

   private final static char branchDelimiter = ':';

   /**
    * Delimiter used to separate artifact identifiers in an artifact identifier list.
    */

   private final static char artifactDelimiter = ',';

   /**
    * Enumeration of character classifications.
    */

   private enum CharacterType {
      /**
       * Decimal digits
       */

      DIGIT,

      /**
       * Delimiter used to separate branch identifiers from artifact identifier lists.
       */

      BRANCH_DELIMITER,

      /**
       * Delimiter used to separate artifact identifiers in an artifact identifier list.
       */

      ARTIFACT_DELIMITER,

      /**
       * Delimiter used to separate pairs of branch identifiers and artifact identifier lists.
       */

      SPECIFICATION_DELIMITER,

      /**
       * All other characters are classified as an error.
       */

      ERROR;

      /**
       * Classifies a character as a {@link CharacterType}.
       *
       * @param character the <code>char</code> to be classified.
       * @return the character classification as a {@link CharacterType}.
       */

      public static CharacterType classifyCharacter(char character) {
         if (Character.isDigit(character)) {
            return DIGIT;
         }
         if (character == RootList.branchDelimiter) {
            return BRANCH_DELIMITER;
         }
         if (character == RootList.artifactDelimiter) {
            return ARTIFACT_DELIMITER;
         }
         if (character == RootList.specificationDelimiter) {
            return SPECIFICATION_DELIMITER;
         }
         return ERROR;
      }
   }

   /**
    * The type of synchronization artifact to be produced.
    */

   String synchronizationArtifactType;

   /**
    * The handle to the ORCS OSEE API.
    */

   OrcsApi orcsApi;

   /**
    * A list of the artifact tree roots for artifacts to be included in the synchronization artifact.
    */

   List<Root> rootsList;

   /**
    * Splits a string into substrings using the provided delimiter character and calls the provided {@link Consumer}
    * with each substring.
    *
    * @param delimiter the <code>char</code> used to split the string.
    * @param token the string to be split.
    * @param sink the {@link Consumer} to be called with each substring.
    */

   private static void splitter(char delimiter, String token, Consumer<String> sink) {
      int tokenStartIndex;
      int delimiterIndex;
      String tokenPart;

      //@formatter:off
      for( tokenStartIndex = 0, delimiterIndex = token.indexOf( delimiter );
           delimiterIndex >= 0;
           tokenStartIndex = delimiterIndex + 1, delimiterIndex = token.indexOf( delimiter, delimiterIndex + 1 ) )
         //@formatter:on
      {
         tokenPart = token.substring(tokenStartIndex, delimiterIndex);

         sink.accept(tokenPart);
      }

      tokenPart = token.substring(tokenStartIndex);

      sink.accept(tokenPart);
   }

   /**
    * Creates a new empty {@link RootsList} and sets the artifact type.
    *
    * @param a handle to the ORCS OSEE API.
    * @param synchronizationArtifactType the type of synchronization artifact to be produced.
    */

   private RootList(OrcsApi orcsApi, String synchronizationArtifactType) {
      this.synchronizationArtifactType = synchronizationArtifactType;
      this.orcsApi = orcsApi;
      this.rootsList = new ArrayList<Root>();
   }

   /**
    * Validates a {@link String} contains a valid synchronization artifact type.
    *
    * @param token the {@link String} to be validated.
    * @return <code>true</code> when the token contains a valid synchronization artifact type; otherwise,
    * <code>false</code>.
    */

   private static boolean validateArtifactType(String token) {

      if (token == null || token.isEmpty()) {
         return false;
      }

      return true;
   }

   /**
    * Validates a roots token is properly constructed according to the rules defined by the
    * {@link SynchronizationEndpoint} interface.
    *
    * @param token the {@link String} to be validated.
    * @return <code>true</code>, when the <code>token</code> conforms to the EBNF for &lt;roots&gt;; otherwise,
    * <code>false</code>.
    */

   private static boolean validateRoots(String token) {

      if (token == null || token.isEmpty()) {
         return false;
      }

      int length = token.length();

      boolean requireDigit = true;
      boolean allowBranchDelimiter = true;
      boolean allowSpecificationDelimiter = false;
      boolean allowArtifactDelimiter = false;

      for (int i = 0; i < length; i++) {
         CharacterType characterType = CharacterType.classifyCharacter(token.charAt(i));

         switch (characterType) {
            case ERROR: {
               return false;
            }

            case DIGIT: {
               requireDigit = false;
               continue;
            }

            case BRANCH_DELIMITER: {
               if (requireDigit || allowArtifactDelimiter || allowSpecificationDelimiter) {
                  return false;
               }

               requireDigit = true;
               allowBranchDelimiter = false;
               allowSpecificationDelimiter = true;
               allowArtifactDelimiter = true;

               continue;
            }

            case ARTIFACT_DELIMITER: {
               if (requireDigit || allowBranchDelimiter) {
                  return false;
               }

               requireDigit = true;
               allowBranchDelimiter = false;
               allowSpecificationDelimiter = true;
               allowArtifactDelimiter = true;

               continue;
            }

            case SPECIFICATION_DELIMITER: {
               if (requireDigit || allowBranchDelimiter) {
                  return false;
               }

               requireDigit = true;
               allowBranchDelimiter = true;
               allowSpecificationDelimiter = false;
               allowArtifactDelimiter = false;
            }
         }
      }

      return !(requireDigit || allowBranchDelimiter);
   }

   /**
    * Factory method to create an object implementing the {@link RootList} interface for a Synchronization Artifact with
    * a single specification.
    *
    * @param orcsApi a handle to the {@link OrcsApi} used to obtain the OSEE artifacts for the Synchronization Artifact.
    * @param synchronizationArtifactType a {@link String} identifier for the type of Synchronization Artifact to be
    * used.
    * @param branchId the branch identifier of the OSEE artifact that is the root of a specification in the
    * Synchronization Artifact.
    * @param artifactId the artifact identifier of the OSEE artifact that is the root of a specification in the
    * Synchronization Artifact.
    * @return an object implementing the {@link RootList} interface with the provided parameters encapsulated.
    */

   public static RootList create(OrcsApi orcsApi, String synchronizationArtifactType, BranchId branchId, ArtifactId artifactId) {

      boolean orcsApiValid = Objects.nonNull(orcsApi);
      boolean synchronizationArtifactTypeValid = RootList.validateArtifactType(synchronizationArtifactType);

      if (!orcsApiValid || !synchronizationArtifactTypeValid) {

         StringBuilder message = new StringBuilder(1 * 1024);

         if (!orcsApiValid) {
            //@formatter:off
            message
               .append( "INTERNAL ERROR: \"OrcsApi\" is null." ).append( "\n" );
            //@formatter:on
         }

         if (!synchronizationArtifactTypeValid) {
            //@formatter:off
            message
               .append( "ERROR: \"synchronizationArtifactType\" parameter is invalid." ).append( "\n" )
               .append( "   synchronizationArtifactType: " ).append( synchronizationArtifactType ).append( "\n ");
            //@formatter:on
         }

         throw new RuntimeException(message.toString());
      }

      RootList rootList = new RootList(orcsApi, synchronizationArtifactType);

      Root root = new Root(branchId, artifactId);
      rootList.add(root);

      return rootList;
   }

   /**
    * Factory method to create an object implementing the {@link RootList} interface for a Synchronization Artifact with
    * a multiple specifications.
    *
    * @param orcsApi a handle to the {@link OrcsApi} used to obtain the OSEE artifacts for the Synchronization Artifact.
    * @param synchronizationArtifactType a {@link String} identifier for the type of Synchronization Artifact to be
    * used.
    * @param roots a string describing the branch and artifact identifiers of the OSEE artifacts that are the roots of
    * specifications within the Synchronization Artifact. The format of this string is defined by the
    * <code>org.eclipse.osee.orcs.rest.model.SynchronizationEndpoint</code> interface.
    * @return an object implementing the {@link RootList} interface with the provided parameters encapsulated.
    */

   public static RootList create(OrcsApi orcsApi, String synchronizationArtifactType, String roots) {

      boolean orcsApiValid = Objects.nonNull(orcsApi);
      boolean rootsValid = RootList.validateRoots(roots);
      boolean artifactTypeValid = RootList.validateArtifactType(synchronizationArtifactType);

      if (!orcsApiValid || !rootsValid || !artifactTypeValid) {

         StringBuilder message = new StringBuilder(2 * 1024);

         if (!orcsApiValid) {
            //@formatter:off
            message
               .append( "INTERNAL ERROR: \"OrcsApi\" is null." ).append( "\n" );
            //@formatter:on
         }

         if (!rootsValid) {
            //@formatter:off
            message
               .append( "ERROR: \"roots\" parameter is invalid." ).append( "\n" )
               .append( "   " ).append( "roots: " ).append( roots ).append( "\n" );
            //@formatter:on
         }

         if (!artifactTypeValid) {
            //@formatter:off
            message
               .append( "ERROR: \"artifactType\" parameter is invalid." ).append( "\n" )
               .append( "   artifactType: " ).append( synchronizationArtifactType ).append( "\n ");
            //@formatter:on
         }

         throw new RuntimeException(message.toString());
      }

      RootList rootList = new RootList(orcsApi, synchronizationArtifactType);

      RootList.splitter(RootList.specificationDelimiter, roots, rootList::add);

      return rootList;
   }

   /**
    * Parses a &lt;branch-id-artifact-id-list&gt; token and creating a {@link Root} object with the branch identifier
    * and each artifact identifier. The created {@link Root} objects are added to the list.
    *
    * @param token the &lt;branch-id-artifact-id-list&gt; token to be parsed.
    */

   private void add(String token) {
      List<String> tokens = new ArrayList<String>(2);
      RootList.splitter(RootList.branchDelimiter, token, tokens::add);
      String branchId = tokens.get(0);
      String artifactIdList = tokens.get(1);
      RootList.splitter(RootList.artifactDelimiter, artifactIdList,
         (artifactId) -> this.rootsList.add(new Root(branchId, artifactId)));
   }

   /**
    * Adds a {@link Root} object to the list.
    *
    * @param root the {@link Root} object to be added.
    */

   private void add(Root root) {
      this.rootsList.add(root);
   }

   /**
    * Gets the type of synchronization artifact to be produced.
    *
    * @return the synchronization artifact type.
    */

   public String getSynchronizationArtifactType() {
      return this.synchronizationArtifactType;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Iterator<Root> iterator() {
      return this.rootsList.iterator();
   }

   /**
    * {@inheritDoc}
    */

   ListIterator<Root> listIterator() {
      return this.rootsList.listIterator();
   }

   public OrcsApi getOrcsApi() {
      return this.orcsApi;
   }

   /**
    * Gets a {@link Stream} of the {@link Root} objects on the list.
    *
    * @return {@link Stream} of {@link Root} objects.
    */

   public Stream<Root> stream() {
      return this.rootsList.stream();
   }

   /**
    * Adds a textual message to the provided {@link StringBuilder} or a new {@link StringBuilder} representing the list
    * of OSEE root artifacts. The message is formatted as follows:
    * <ul style="list-style-type:none">
    * <li>"BranchId(" &lt;branch-id&gt; ") ArtifactId(" &lt;artifact-id&gt; ")" { "," "BranchId(" &lt;branch-id&gt; ")
    * ArtifactId(" &lt;artifact-id&gt; ")" }</li>
    * </ul>
    *
    * @param message when not null the message is appended to this {@link StringBuilder}.
    * @return the provided {@link StringBuilder} when not null; otherwise, a new {@link StringBuilder}.
    */

   public StringBuilder toText(StringBuilder message) {
      var outMessage = (message != null) ? message : new StringBuilder(1 * 1024);
      boolean first = true;
      this.rootsList.stream().forEach(root -> {
         if (!first) {
            outMessage.append(", ");
         }
         root.toText(outMessage);
      });

      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public StringBuilder toMessage(int indent, StringBuilder message) {
      var outMessage = (message != null) ? message : new StringBuilder(1 * 1024);
      var indent0 = IndentedString.indentString(indent + 0);

      //@formatter:off
      outMessage
         .append( indent0 ).append( "Root List:" ).append( "\n" )
         ;
      //@formatter:on

      this.rootsList.stream().forEach(root -> root.toMessage(indent + 1, outMessage));

      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String toString() {
      return this.toMessage(0, null).toString();
   }
}

/* EOF */
