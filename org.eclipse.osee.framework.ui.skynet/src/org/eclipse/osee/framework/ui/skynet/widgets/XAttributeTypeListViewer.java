/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.ArrayList;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Jeff C. Phillips
 */
public class XAttributeTypeListViewer extends XTypeListViewer {
   private static final String NAME = "XAttributeTypeListViewer";

   /**
    * @param name
    */
   public XAttributeTypeListViewer(String keyedBranchName, String defaultValue) {
      super(NAME);

      setContentProvider(new DefaultBranchContentProvider(new AttributeContentProvider()));
      ArrayList<Object> input = new ArrayList<Object>(1);
      input.add(resolveBranch(keyedBranchName));

      setInput(input);

      if (defaultValue != null) {
         try {
            AttributeType attributeType = AttributeTypeManager.getType(defaultValue);
            setDefaultSelected(attributeType);
         } catch (Exception ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, true);
         }
      }
   }
}