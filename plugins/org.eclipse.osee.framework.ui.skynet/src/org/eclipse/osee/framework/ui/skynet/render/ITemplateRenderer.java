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
package org.eclipse.osee.framework.ui.skynet.render;

/**
 * @author Ryan D. Brooks
 */
public interface ITemplateRenderer extends IRenderer {
   public static final String TEMPLATE_OPTION = "template";
   public static final String TRANSACTION_OPTION = "skynetTransaction";
   public static final String PREVIEW_WITH_RECURSE_VALUE = "PREVIEW_WITH_RECURSE";
   public static final String PREVIEW_WITH_RECURSE_NO_ATTRIBUTES_VALUE = "PREVIEW_WITH_RECURSE_NO_ATTRIBUTES";
   public static final String DIFF_VALUE = "DIFF";
   public static final String DIFF_NO_ATTRIBUTES_VALUE = "DIFF_NO_ATTRIBUTES";
   public static final Object[] PREVIEW_WITH_RECURSE_OPTION_PAIR =
         new String[] {TEMPLATE_OPTION, PREVIEW_WITH_RECURSE_VALUE};
}
