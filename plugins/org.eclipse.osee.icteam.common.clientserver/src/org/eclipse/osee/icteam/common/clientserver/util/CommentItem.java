/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.common.clientserver.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.icteam.common.clientserver.Activator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Comment Item which are required for Comments
 * 
 * @author Ajay Chandrahasan
 */
public class CommentItem {

  private Date date;
  private final String state;
  private String msg;
  private String user;
  private CommentType type = CommentType.Other;
  protected final static String ITEM_TAG = "Item";
  private final static String Comment_Tag = "CommentTag";

  /**
 * @param type
 * @param state
 * @param date1
 * @param user
 * @param msg
 */
public CommentItem(final String type, final String state, final String date1, final String user, final String msg) {
    Long l = Long.valueOf(date1);
    this.date = new Date(l.longValue());
    this.state = state;
    this.msg = msg;
    this.user = user;
    try {
      this.type = CommentType.getType(type);
    }
    catch (OseeArgumentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

/**
 * @return date of the comment
 */
  public Date getDate() {
    return this.date;
  }

  /**
 * @param date set date of the comment 
 */
public void setDate(final Date date) {
    this.date = date;
  }
  
  /**
   * @return message of the comment
   */
  public String getMsg() {
    return this.msg;
  }

  /**
   * @param msg set message for the comment 
   */
  public void setMsg(final String msg) {
    this.msg = msg;
  }

  @Override
  public String toString() {
    return String.format("Note: %s from %s%s on %s - %s", this.user, toStringState(),
        DateUtil.getMMDDYYHHMM(this.date), this.msg);
  }

  private String toStringState() {
    return (this.state.isEmpty() ? "" : " for \"" + this.state + "\"");
  }

  /**
   * @return user of the comment
   */
  public String getUser() {
    return this.user;
  }

  /**
   * @return type of the comment
   */
  public CommentType getType() {
    return this.type;
  }

  /**
   * @param type set type for the comment 
   */
  public void setType(final CommentType type) {
    this.type = type;
  }

public String toHTML() {
    return toString().replaceFirst("^Note: ", "<b>Note:</b>");
  }

  /**
   * @return state of the comment
   */
  public String getState() {
    return this.state;
  }

  /**
   * @param user set user for the comment 
   */
  public void setUser(final String user) {
    this.user = user;
  }

  /**
 * @param xml xml which has to be converted
 * @param hrid 
 * @return logItems required for a comment from the given xml
 */
public static List<CommentItem> fromXml(final String xml, final String hrid) {
    List<CommentItem> logItems = new ArrayList<CommentItem>();
    try {
      // if (Strings.isValid(xml)) {
      NodeList nodes = Jaxp.readXmlDocument(xml).getElementsByTagName(ITEM_TAG);
      for (int i = 0; i < nodes.getLength(); i++) {
        Element element = (Element) nodes.item(i);
        // IAtsUser user = AtsUsers.getUser();

        CommentItem item = new CommentItem(element.getAttribute("type"), element.getAttribute("state"), // NOPMD by b0727536
                                                                                                  // on 9/29/10 8:52 AM
            element.getAttribute("date"), element.getAttribute("userId"), element.getAttribute("msg"));
        logItems.add(item);
      }
      // }
    }
    catch (Exception ex) {
      OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
    }
    return logItems;
  }


  /**
 * @param items Comment items
 * @return xml by converting the list of items given
 */
public static String toXml(final List<CommentItem> items) {
    try {
      Document doc = Jaxp.newDocumentNamespaceAware();
      Element rootElement = doc.createElement(Comment_Tag);
      doc.appendChild(rootElement);
      for (CommentItem item : items) {
        Element element = doc.createElement(CommentItem.ITEM_TAG);
        element.setAttribute("type", item.getType().name());
        element.setAttribute("state", item.getState());
        element.setAttribute("date", String.valueOf(item.getDate().getTime()));
        element.setAttribute("userId", item.getUser());
        element.setAttribute("msg", item.getMsg());
        rootElement.appendChild(element);
      }
      return Jaxp.getDocumentXml(doc);
    }
    catch (Exception ex) {
      OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Can't create ats note document", ex);
    }
    return null;
  }

}