/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.ui.skynet.compare;

import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.ui.skynet.DslGrammar;
import org.eclipse.osee.framework.ui.skynet.DslGrammarStorageAdapter;
import org.eclipse.osee.framework.ui.skynet.internal.DslGrammarManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class AttributeCompareItem extends CompareItem {
   private final AttributeConflict attributeConflict;

   public AttributeCompareItem(AttributeConflict attributeConflict, String name, String contents, boolean isEditable, Image image, String diffFilename) {
      super(name, contents, System.currentTimeMillis(), isEditable, image, diffFilename);

      this.attributeConflict = attributeConflict;
   }

   @Override
   public void persistContent() {
      try {
         String stringContent = getStringContent();
         DslGrammar dslGrammar = DslGrammarManager.getGrammar(attributeConflict.getAttributeType());
         if (dslGrammar != null) {
            DslGrammarStorageAdapter dslGrammarStorageAdapter = dslGrammar.getStorageAdapter();
            if (dslGrammarStorageAdapter != null) {
               stringContent = dslGrammarStorageAdapter.postProcess(attributeConflict.getArtifact(), stringContent);
            }
         }
         attributeConflict.setAttributeValue(stringContent);
      } catch (OseeCoreException ex) {
         OseeLog.log(AttributeCompareItem.class, Level.SEVERE, ex);
      }
   }
}
