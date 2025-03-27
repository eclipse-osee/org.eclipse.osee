package org.eclipse.osee.framework.core.publishing;

import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * Exception thrown when an invalid hierarchy is detected for a given artifact.
 */
public class InvalidHierarchyException extends RuntimeException {

   /**
    * Formats the error message for an invalid hierarchy.
    *
    * @param artid The artifact identifier associated with the invalid hierarchy.
    * @return A formatted error message indicating the artifact with an invalid hierarchy.
    */
   private static String formatMessage(ArtifactId artid) {
      return "Invalid hierarchy detected for artifact: " + artid;
   }

   /**
    * Constructs an {@code InvalidHierarchyException} with a message indicating the artifact that caused the issue.
    *
    * @param artid The artifact identifier associated with the invalid hierarchy.
    */
   public InvalidHierarchyException(ArtifactId artid) {
      super(formatMessage(artid));
   }

   /**
    * Constructs an {@code InvalidHierarchyException} with a message indicating the artifact that caused the issue and
    * an underlying cause.
    *
    * @param artid The artifact identifier associated with the invalid hierarchy.
    * @param cause The underlying exception that caused this exception to be thrown.
    */
   public InvalidHierarchyException(ArtifactId artid, Throwable cause) {
      super(formatMessage(artid), cause);
   }
}
