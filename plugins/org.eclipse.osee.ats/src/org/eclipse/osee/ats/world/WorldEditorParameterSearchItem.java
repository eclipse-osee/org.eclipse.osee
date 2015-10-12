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
package org.eclipse.osee.ats.world;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.world.search.WorldSearchItem;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XMembersCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.IDynamicWidgetLayoutListener;
import org.eclipse.osee.framework.ui.skynet.widgets.util.IXWidgetOptionResolver;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public abstract class WorldEditorParameterSearchItem extends WorldSearchItem implements IWorldEditorParameterProvider, IDynamicWidgetLayoutListener, IXWidgetOptionResolver {

   private CustomizeData customizeData;
   private TableLoadOption[] tableLoadOptions;
   private final Map<String, XWidget> xWidgets = new HashMap<String, XWidget>();
   private StringBuilder xmlSb;
   private final Pattern displayName = Pattern.compile("displayName=\"(.*?)\"");
   private String shortName = "";
   private final List<String> widgetOrder = new LinkedList<>();

   public WorldEditorParameterSearchItem(String name, KeyedImage oseeImage) {
      super(name, LoadView.WorldEditor, oseeImage);
   }

   public WorldEditorParameterSearchItem(WorldSearchItem worldSearchItem, KeyedImage oseeImage) {
      super(worldSearchItem, oseeImage);
   }

   @Override
   public String getParameterXWidgetXml() throws OseeCoreException {
      String xml = xmlSb.toString() + "</xWidgets>";
      return xml;
   }

   public abstract Result isParameterSelectionValid() throws OseeCoreException;

   @Override
   public void run(WorldEditor worldEditor, SearchType searchType, boolean forcePend) {
      worldEditor.getWorldComposite().getXViewer().setForcePend(forcePend);
   }

   @Override
   public String[] getWidgetOptions(XWidgetRendererItem widgetData) {
      return null;
   }

   @Override
   public void setCustomizeData(CustomizeData customizeData) {
      this.customizeData = customizeData;
   }

   @Override
   public void setTableLoadOptions(TableLoadOption... tableLoadOptions) {
      this.tableLoadOptions = tableLoadOptions;
   }

   public CustomizeData getCustomizeData() {
      return customizeData;
   }

   public TableLoadOption[] getTableLoadOptions() {
      return tableLoadOptions;
   }

   @Override
   public void handleSaveButtonPressed() {
      // do nothing
   }

   @Override
   public boolean isSaveButtonAvailable() {
      return false;
   }

   public abstract Callable<Collection<? extends Artifact>> createSearch() throws OseeCoreException;

   public void checkOrStartXmlSb() {
      if (xmlSb == null) {
         xmlSb = new StringBuilder("<xWidgets>");
      }
   }

   public void addWidgetXml(String widgetXml) {
      checkOrStartXmlSb();
      xmlSb.append(widgetXml);
      String displayName = getDisplayName(widgetXml);
      xWidgets.put(displayName, null);
      widgetOrder.add(displayName);
   }

   private String getDisplayName(String widgetXml) {
      Matcher matcher = displayName.matcher(widgetXml);
      if (matcher.find()) {
         return matcher.group(1);
      }
      throw new OseeArgumentException("WidgetXml must include displayName; Not found in [%s]", widgetXml);
   }

   @Override
   public void widgetCreated(XWidget widget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      xWidgets.put(widget.getLabel(), widget);
   }

   public void addTitleWidget() {
      addWidgetXml("<XWidget xwidgetType=\"XText\" displayName=\"Title\" horizontalLabel=\"true\"/>");
   }

   public String getTitle() {
      XText text = getTitleWidget();
      if (text != null) {
         return text.get();
      }
      return null;
   }

   public XText getTitleWidget() {
      return (XText) xWidgets.get("Title");
   }

   public void addIncludeCompletedCancelledWidget() {
      addWidgetXml(
         "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Include Completed/Cancelled\" defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
   }

   public Boolean isIncludeCompletedCancelled() {
      XCheckBox checkbox = getIncludeCompletedCanceledWidget();
      if (checkbox != null) {
         return checkbox.isChecked();
      }
      return null;
   }

   private XCheckBox getIncludeCompletedCanceledWidget() {
      return (XCheckBox) xWidgets.get("Include Completed/Cancelled");
   }

   public void setIncludeCompletedCancelled(boolean selected) {
      XCheckBox checkbox = getIncludeCompletedCanceledWidget();
      if (checkbox != null) {
         checkbox.set(selected);
      }
      throw new OseeArgumentException("Include Completed/Cancelled Checkbox could not be found");
   }

   public void addUserWidget(String labelName) {
      addWidgetXml(
         "<XWidget xwidgetType=\"XMembersCombo\" displayName=\"" + labelName + "\" horizontalLabel=\"true\"/>");
   }

   public IAtsUser getUser(String labelName) {
      User assignee = getUserUser(labelName);
      if (assignee != null) {
         return AtsClientService.get().getUserService().getUserById(assignee.getUserId());
      }
      return null;
   }

   public User getUserUser(String labelName) {
      XMembersCombo combo = getUserWidget(labelName);
      if (combo != null) {
         return combo.getUser();
      }
      return null;
   }

   /**
    * @return XMembersCombo of lableName or null
    */
   private XMembersCombo getUserWidget(String labelName) {
      XMembersCombo combo = null;
      XWidget widget = xWidgets.get(labelName);
      if (widget instanceof XMembersCombo) {
         combo = (XMembersCombo) widget;
      }
      return combo;
   }

   public void setUser(String labelName, IAtsUser user) {
      Conditions.checkNotNull(user, "User");
      setUser(labelName, UserManager.getUserByUserId(user.getUserId()));
   }

   public void setUser(String labelName, User assignee) {
      XMembersCombo combo = getUserWidget(labelName);
      if (combo != null) {
         combo.set(assignee);
      }
      throw new OseeArgumentException(labelName + " User Combo not be found");
   }

   @Override
   public String getSelectedName(SearchType searchType) {
      StringBuffer sb = new StringBuffer();
      getSelectedName(searchType, sb);
      return Strings.truncate(getShortName() + sb.toString(), WorldEditor.TITLE_MAX_LENGTH, true);
   }

   private void getSelectedName(SearchType searchType, StringBuffer sb) {
      for (String widgetName : widgetOrder) {
         if (widgetName.equals("Include Completed/Cancelled")) {
            sb.append(" - Include Completed/Cancelled");
         } else if (getUserWidget(widgetName) != null) {
            sb.append(" - ");
            sb.append(widgetName);
            sb.append(": ");
            sb.append(getUserWidget(widgetName).get());
         }
      }
   }

   public String getShortName() {
      return shortName;
   }

   public void setShortName(String shortName) {
      this.shortName = shortName;
   }

   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException {
      // do nothing
   }

   @Override
   public void createXWidgetLayoutData(XWidgetRendererItem layoutData, XWidget xWidget, FormToolkit toolkit, Artifact art, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException {
      // do nothing
   }

   @Override
   public IDynamicWidgetLayoutListener getDynamicWidgetLayoutListener() {
      return null;
   }

   @Override
   public IAtsVersion getTargetedVersionArtifact() {
      return null;
   }

   /**
    * Called in the display thread to allow parameters to be retrieved prior to searching in background thread.
    */
   public void createSearchItem() {
      // do nothing
   }

}
