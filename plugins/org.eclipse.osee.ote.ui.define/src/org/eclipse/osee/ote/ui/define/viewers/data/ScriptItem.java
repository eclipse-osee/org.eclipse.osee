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
package org.eclipse.osee.ote.ui.define.viewers.data;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class ScriptItem extends DataItem implements IXViewerItem {
   private static final Matcher urlMatcher = Pattern.compile("(.*)?/(branches|trunk|tags)?(.*)").matcher("");
   private static final String DEFAULT_SCRIPT_NAME = "Script Unknown";
   private static final String DEFAULT_REVISION = "?";
   private static boolean isFullDescriptionMode = true;

   private String scriptUrl;
   private String revision;
   private final Integer key;

   public ScriptItem(String scriptUrl, String revision, DataItem parentItem) {
      super(parentItem);
      setScriptUrlAndRevision(scriptUrl, revision);
      this.key = generateKey();
   }

   private void setScriptUrlAndRevision(String scriptUrl, String revision) {
      boolean wasThereAnError = false;

      try {
         new URI(scriptUrl);
         this.scriptUrl = scriptUrl;
      } catch (Exception ex) {
         wasThereAnError = true;
      }

      try {
         if (Strings.isValid(revision)) {
            this.revision = revision;
         } else {
            wasThereAnError = true;
         }
      } catch (Exception ex) {
         wasThereAnError = true;
      }

      if (wasThereAnError == true) {
         setDefaultValues();
      }
   }

   private void setDefaultValues() {
      this.scriptUrl = DEFAULT_SCRIPT_NAME;
      this.revision = DEFAULT_REVISION;
   }

   @Override
   public String getData() {
      return String.format("[%s][%s]", scriptUrl, revision);
   }

   @Override
   public Image getImage() {
      return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
   }

   @Override
   public String getLabel(int index) {
      String toReturn = "";
      if (index == 0) {
         if (scriptUrl.equals(DEFAULT_SCRIPT_NAME) != true && revision.equals(
            DEFAULT_REVISION) != true && isFullDescriptionModeEnabled() != false) {
            toReturn = getFullLabel();
         } else {
            toReturn = getShortLabel();
         }
      }
      return toReturn;
   }

   private String getFullLabel() {
      String repository = null;
      String type = null;
      String path = null;
      urlMatcher.reset(scriptUrl);
      if (urlMatcher.find()) {
         repository = urlMatcher.group(1);
         type = urlMatcher.group(2);
         if (!Strings.isValid(type)) {
            type = "";
         }
         path = urlMatcher.group(3);
      }
      return String.format("%s [%s] [%s, %s: %s]", getScriptName(), revision, repository, StringUtils.capitalize(type),
         path);
   }

   private String getScriptName() {
      String url = scriptUrl;
      int lastIndex = url.lastIndexOf("/") + 1;
      url = url.substring(lastIndex, url.length());
      return Lib.removeExtension(url);
   }

   private String getShortLabel() {
      String url = scriptUrl;
      if (!DEFAULT_SCRIPT_NAME.equals(scriptUrl)) {
         url = getScriptName();
      }
      return String.format("%s [%s]", url, revision);
   }

   @Override
   public Object getKey() {
      return key;
   }

   private Integer generateKey() {
      int value = 0;
      for (byte aByte : (scriptUrl + revision).getBytes()) {
         value += aByte;
      }
      return new Integer(value);
   }

   public static void setFullDescriptionModeEnabled(boolean isEnabled) {
      ScriptItem.isFullDescriptionMode = isEnabled;
   }

   public static boolean isFullDescriptionModeEnabled() {
      return ScriptItem.isFullDescriptionMode;
   }
}
