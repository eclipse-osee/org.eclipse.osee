/*
 * Created on May 6, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.util;

import org.eclipse.osee.framework.skynet.core.OseeCoreException;

/**
 * @author Theron Virgin
 */
public class ConflictDetectionException extends OseeCoreException {

   private static final long serialVersionUID = 1L;

   public ConflictDetectionException(String message) {
      super(message);
   }
}
