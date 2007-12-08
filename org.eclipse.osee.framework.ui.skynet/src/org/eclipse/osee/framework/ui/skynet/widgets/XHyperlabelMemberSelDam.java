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
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.widgets.Composite;

public class XHyperlabelMemberSelDam extends XHyperlabelMemberSelection implements IDamWidget {

   private Artifact artifact;
   private String attrName;

   public XHyperlabelMemberSelDam(String displayLabel) {
      super(displayLabel);
   }

   public void setArtifact(Artifact artifact, String attrName) {
      this.artifact = artifact;
      this.attrName = attrName;

      super.setSelectedUsers(getUdatUsers());
   }

   public DynamicAttributeManager getUdat() throws SQLException {
      return artifact.getAttributeManager(attrName);
   }

   public String getUdatStringValue() throws SQLException {
      DynamicAttributeManager udat = getUdat();
      if (udat == null) return "";
      return udat.getSoleAttributeValue();
   }

   public Set<User> getUdatUsers() {
      Set<User> users = new HashSet<User>();
      try {
         Matcher m = Pattern.compile("<userId>(.*?)</userId>").matcher(getUdatStringValue());
         while (m.find()) {
            users.add(SkynetAuthentication.getInstance().getUserByIdWithError(m.group(1)));
         }
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, false);
      }

      return users;
   }

   @Override
   public boolean handleSelection() {
      boolean changed = super.handleSelection();
      if (changed) {
         try {
            save();
         } catch (SQLException ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, true);
         }
         return true;
      }
      return false;
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
   public void save() throws SQLException {
      if (isDirty()) {
         DynamicAttributeManager udat = getUdat();
         udat.setSoleAttributeValue(getSelectedStringValue());
      }
   }

   public String getSelectedStringValue() {
      StringBuffer sb = new StringBuffer();
      for (User user : getSelectedUsers()) {
         sb.append(AXml.addTagData("userId", user.getUserId()));
      }
      return sb.toString();
   }

   @Override
   public boolean isDirty() throws SQLException {
      return (!getUdatStringValue().equals(getSelectedStringValue()));
   }
}
