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

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.widgets.Composite;

public class XHyperlabelMemberSelDam extends XHyperlabelMemberSelection implements IArtifactWidget {

   private Artifact artifact;
   private String attributeTypeName;

   public XHyperlabelMemberSelDam(String displayLabel) {
      super(displayLabel);
   }

   public void setArtifact(Artifact artifact, String attrName) {
      this.artifact = artifact;
      this.attributeTypeName = attrName;

      super.setSelectedUsers(getUsers());
   }

   public Set<User> getUsers() {
      Set<User> users = new HashSet<User>();
      try {
         Matcher m =
               Pattern.compile("<userId>(.*?)</userId>").matcher(artifact.getSoleAttributeValue(attributeTypeName, ""));
         while (m.find()) {
            users.add(SkynetAuthentication.getUserByUserId(m.group(1)));
         }
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, false);
      }

      return users;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XText#createWidgets(org.eclipse.swt.widgets.Composite, int,
    *      boolean)
    */
   @Override
   public void createWidgets(Composite parent, int horizontalSpan) {
      super.createWidgets(parent, horizontalSpan);
   }

   @Override
   public void saveToArtifact() throws OseeCoreException, SQLException {
      try {
         String selectedStrValue = getSelectedStringValue();
         if (selectedStrValue == null || selectedStrValue.equals("")) {
            artifact.deleteSoleAttribute(attributeTypeName);
         } else {
            artifact.setSoleAttributeValue(attributeTypeName, selectedStrValue);
         }
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
   }

   public String getSelectedStringValue() throws SQLException, MultipleAttributesExist {
      StringBuffer sb = new StringBuffer();
      for (User user : getSelectedUsers()) {
         sb.append(AXml.addTagData("userId", user.getUserId()));
      }
      return sb.toString();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget#isDirty()
    */
   @Override
   public Result isDirty() throws OseeCoreException, SQLException {
      try {
         String enteredValue = getSelectedStringValue();
         String storedValue = artifact.getSoleAttributeValue(attributeTypeName);
         if (!enteredValue.equals(storedValue)) {
            return new Result(true, attributeTypeName + " is dirty");
         }
      } catch (AttributeDoesNotExist ex) {
         if (!artifact.getSoleAttributeValue(attributeTypeName, "").equals("")) return new Result(true,
               attributeTypeName + " is dirty");
      } catch (NumberFormatException ex) {
         // do nothing
      }
      return Result.FalseResult;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget#revert()
    */
   @Override
   public void revert() throws OseeCoreException, SQLException {
      setArtifact(artifact, attributeTypeName);
   }
}
