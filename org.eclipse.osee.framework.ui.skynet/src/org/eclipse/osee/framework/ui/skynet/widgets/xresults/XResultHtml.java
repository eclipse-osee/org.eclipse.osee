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

package org.eclipse.osee.framework.ui.skynet.widgets.xresults;

import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class XResultHtml {

   /**
    * 
    */
   public XResultHtml() {
      super();
   }

   public static String getOpenHyperlinkHtml(Artifact art) {
      return getOpenHyperlinkHtml(art.getDescriptiveName(), art);
   }

   public static String getOpenHyperlinkHtml(String name, String hrid) {
      return AHTML.getHyperlink(ResultBrowserHyperCmd.getHyperCmdStr(ResultBrowserHyperCmd.openAction, hrid), name);
   }

   public static String getOpenHyperlinkHtml(String name, String hrid, int branchId) {
      return AHTML.getHyperlink(ResultBrowserHyperCmd.getHyperCmdStr(ResultBrowserHyperCmd.openArtifctBranch,
            hrid + "(" + branchId + ")"), name);
   }

   public static String getOpenArtEditHyperlinkHtml(String name, String hrid) {
      return AHTML.getHyperlink(ResultBrowserHyperCmd.getHyperCmdStr(ResultBrowserHyperCmd.openArtifactEditor, hrid),
            name);
   }

   public static String getOpenArtViewHyperlinkHtml(String name, String hrid) {
      return AHTML.getHyperlink(ResultBrowserHyperCmd.getHyperCmdStr(ResultBrowserHyperCmd.openArtifactHyperViewer,
            hrid), name);
   }

   public static String getOpenHyperlinkHtml(String name, Artifact art) {
      return AHTML.getHyperlink(ResultBrowserHyperCmd.getHyperCmdStr(ResultBrowserHyperCmd.openAction, art.getGuid()),
            name);
   }

}
