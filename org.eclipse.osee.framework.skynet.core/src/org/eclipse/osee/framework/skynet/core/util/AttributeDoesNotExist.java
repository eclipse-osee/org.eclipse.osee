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
public class AttributeDoesNotExist extends OseeCoreException {

   private static final long serialVersionUID = 1L;

   public AttributeDoesNotExist(String message) {
      super(message);
   }
}