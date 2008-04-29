/*
 * Created on Apr 17, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.util;

import org.eclipse.osee.framework.skynet.core.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class MultipleAttributesExist extends OseeCoreException {

   private static final long serialVersionUID = 1L;

   public MultipleAttributesExist(String message) {
      super(message);
   }
}