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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

public class XHyperlabelMemberSelDam extends XHyperlabelMemberSelection implements IAttributeWidget {

   private static final Pattern USER_PATTERN = Pattern.compile("<userId>(.*?)</userId>");

   private Artifact artifact;
   private IAttributeType attributeType;

   public XHyperlabelMemberSelDam(String displayLabel) {
      super(displayLabel);
   }

   @Override
   public Artifact getArtifact() {
      return artifact;
   }

   @Override
   public IAttributeType getAttributeType() {
      return attributeType;
   }

   @Override
   public void setAttributeType(Artifact artifact, IAttributeType attributeType) {
      this.artifact = artifact;
      this.attributeType = attributeType;

      super.setSelectedUsers(getUsers());
   }

   public Set<User> getUsers() {
      Set<User> users = new HashSet<User>();
      try {
         String value = getArtifact().getSoleAttributeValue(getAttributeType(), "");
         Matcher m = USER_PATTERN.matcher(value);
         while (m.find()) {
            users.add(UserManager.getUserByUserId(m.group(1)));
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }

      return users;
   }

   @Override
   public void saveToArtifact() {
      try {
         String selectedStrValue = getSelectedStringValue();
         if (selectedStrValue == null || selectedStrValue.equals("")) {
            getArtifact().deleteSoleAttribute(getAttributeType());
         } else {
            getArtifact().setSoleAttributeValue(getAttributeType(), selectedStrValue);
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public String getSelectedStringValue() throws OseeCoreException {
      StringBuffer sb = new StringBuffer();
      for (User user : getSelectedUsers()) {
         sb.append(AXml.addTagData("userId", user.getUserId()));
      }
      return sb.toString();
   }

   @Override
   public Result isDirty() throws OseeCoreException {
      try {
         String enteredValue = getSelectedStringValue();
         String storedValue = artifact.getSoleAttributeValue(attributeType);
         if (!enteredValue.equals(storedValue)) {
            return new Result(true, attributeType + " is dirty");
         }
      } catch (AttributeDoesNotExist ex) {
         if (!artifact.getSoleAttributeValue(attributeType, "").equals("")) {
            return new Result(true, attributeType + " is dirty");
         }
      } catch (NumberFormatException ex) {
         // do nothing
      }
      return Result.FalseResult;
   }

   @Override
   public void revert() {
      setAttributeType(getArtifact(), getAttributeType());
   }
}
