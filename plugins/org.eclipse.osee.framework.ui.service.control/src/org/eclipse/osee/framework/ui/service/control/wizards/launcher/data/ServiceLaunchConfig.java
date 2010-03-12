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

package org.eclipse.osee.framework.ui.service.control.wizards.launcher.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.ui.service.control.ControlPlugin;

/**
 * @author Roberto E. Escobar
 */
public class ServiceLaunchConfig {
   private final String EXTENSION_POINT_NAME = "org.eclipse.osee.framework.ui.service.control.ServiceLaunch";
   private final String APPLICATION_ELEMENT = "Application";

   private enum ExecutionTypesEnum {
      EclipseApplication("EclipseApplicationName"), StandAloneApplication("ExecutionCommand");

      String entryTag;

      ExecutionTypesEnum(String tag) {
         this.entryTag = tag;
      }

      String getEntryTag() {
         return entryTag;
      }
   }

   private enum ArgumentTypesEnum {
      VmArgs, AppArgs;
   }

   private Collection<ServiceItem> localServiceItems;
   private Collection<ServiceItem> remoteServiceItems;
   private Collection<ServiceItem> combinedServiceItems;
   private static final ServiceLaunchConfig instance = new ServiceLaunchConfig();

   public static ServiceLaunchConfig getInstance() {
      return instance;
   }

   private ServiceLaunchConfig() {
      super();
      readServiceItems();
   }

   public Collection<ServiceItem> getRemoteServiceItems() {
      return remoteServiceItems;
   }

   public Collection<ServiceItem> getLocalServiceItems() {
      return localServiceItems;
   }

   public Collection<ServiceItem> getServiceItems() {
      return combinedServiceItems;
   }

   private void readServiceItems() {
      remoteServiceItems = new ArrayList<ServiceItem>();
      localServiceItems = new ArrayList<ServiceItem>();
      combinedServiceItems = new ArrayList<ServiceItem>();

      List<ServiceItem> serviceItems = getServicesToLaunch();
      for (ServiceItem item : serviceItems) {
         if (item.isLocalAllowed()) {
            localServiceItems.add(item);
         }
         if (item.isRemoteAllowed()) {
            remoteServiceItems.add(item);
         }
         combinedServiceItems.add(item);
      }
   }

   private List<ServiceItem> getServicesToLaunch() {
      List<ServiceItem> toReturn = new ArrayList<ServiceItem>();
      List<IConfigurationElement> elements =
            ExtensionPoints.getExtensionElements(EXTENSION_POINT_NAME, APPLICATION_ELEMENT);
      for (IConfigurationElement element : elements) {
         String serviceName = element.getAttribute("ServiceName");
         if (false != Strings.isValid(serviceName)) {
            try {

               ServiceItem serviceItem = new ServiceItem(serviceName);
               serviceItem.setJiniGroupRequired(getBooleanValue("RequiresJiniGroup", element));
               getPluginId(element, serviceItem);
               getApplicationConfig(ExecutionTypesEnum.EclipseApplication, element, serviceItem);
               getApplicationConfig(ExecutionTypesEnum.StandAloneApplication, element, serviceItem);
               toReturn.add(serviceItem);
            } catch (Throwable ex) {
               OseeLog.log(ControlPlugin.class, Level.WARNING, String.format("Error while loading service launch extension for: [%s]",
                     serviceName), ex);
            }
         }
      }
      return toReturn;
   }

   private void getPluginId(IConfigurationElement parent, ServiceItem serviceItem) {
      String plugin = parent.getContributor().getName();
      IConfigurationElement[] elements = parent.getChildren("NotLocalToPlugin");
      if (elements != null && elements.length > 0) {
         String temp = elements[0].getAttribute("PluginId");
         if (false != Strings.isValid(temp)) {
            plugin = temp;
         }
      }
      serviceItem.setPlugin(plugin);
   }

   private void getApplicationConfig(ExecutionTypesEnum configElement, IConfigurationElement element, ServiceItem serviceItem) {
      IConfigurationElement[] elements = element.getChildren(configElement.name());
      if (elements != null && elements.length > 0) {
         for (IConfigurationElement theElement : elements) {
            ExecutionCommandFormatter commandFormatter = getExecutionCommandInstance(configElement, theElement);
            if (null != commandFormatter) {
               serviceItem.setLocalAllowed(getBooleanValue("IsLocalAllowed", theElement));

               getExecutionArguments(ArgumentTypesEnum.VmArgs, theElement, commandFormatter);
               getExecutionArguments(ArgumentTypesEnum.AppArgs, theElement, commandFormatter);

               if (false != serviceItem.isJiniGroupRequired()) {
                  commandFormatter.addJvmArg(ServiceItem.JINI_GROUP_FIELD);
               }

               if (false != getBooleanValue("IsRemoteAllowed", theElement)) {
                  commandFormatter.setRemoteAllowed(serviceItem);
                  getHosts(theElement, serviceItem);
               }
               getApplicationBundle(theElement, serviceItem);
               commandFormatter.setServiceExecutionString(serviceItem);
            }
         }
      }
   }

   private void getApplicationBundle(IConfigurationElement parent, ServiceItem serviceItem) {
      IConfigurationElement[] elements = parent.getChildren("ApplicationBundle");
      for (IConfigurationElement element : elements) {
         String fileName = element.getAttribute("ZipFileName");
         if (false != Strings.isValid(fileName)) {
            serviceItem.setZipName(fileName.trim());
         }

         String unzipLocation = element.getAttribute("UnzipLocation");
         if (false != Strings.isValid(unzipLocation)) {
            serviceItem.setUnzipLocation(unzipLocation.trim());
         }
      }
   }

   private void getHosts(IConfigurationElement parent, ServiceItem serviceItem) {
      IConfigurationElement[] elements = parent.getChildren("Host");
      for (IConfigurationElement element : elements) {
         String host = element.getAttribute("Name");
         if (false != Strings.isValid(host)) {
            serviceItem.getHosts().add(host.trim());
         }
      }
   }

   private boolean getBooleanValue(String tagName, IConfigurationElement element) {
      String value = element.getAttribute(tagName);
      return false != Strings.isValid(value) && false != value.equalsIgnoreCase("true");
   }

   private void getExecutionArguments(ArgumentTypesEnum argType, IConfigurationElement element, ExecutionCommandFormatter commandFormatter) {
      String rawArgs = element.getAttribute(argType.name());
      if (false != Strings.isValid(rawArgs)) {
         String[] args = rawArgs.split(" ");
         for (String arg : args) {
            switch (argType) {
               case VmArgs:
                  commandFormatter.addJvmArg(arg);
                  break;
               case AppArgs:
                  commandFormatter.addApplicationArgs(arg);
                  break;
               default:
                  break;
            }
         }
      }
   }

   private ExecutionCommandFormatter getExecutionCommandInstance(ExecutionTypesEnum configElement, IConfigurationElement config) {
      ExecutionCommandFormatter toReturn = null;
      String name = config.getAttribute(configElement.getEntryTag());
      if (false != Strings.isValid(name)) {
         name = name.trim();
         switch (configElement) {
            case EclipseApplication:
               toReturn = new EclipseApplicationFormatter(name);
               break;
            case StandAloneApplication:
               toReturn = new StandAloneApplicationFormatter(name);
               break;
            default:
               break;
         }
      }
      return toReturn;
   }

}