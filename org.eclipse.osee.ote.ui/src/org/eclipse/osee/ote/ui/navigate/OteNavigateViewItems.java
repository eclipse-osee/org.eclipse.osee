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
package org.eclipse.osee.ote.ui.navigate;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateViewItems;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public class OteNavigateViewItems extends XNavigateViewItems {
	private static OteNavigateViewItems navigateItems = new OteNavigateViewItems();

	public OteNavigateViewItems() {
		super();
	}

	public static OteNavigateViewItems getInstance() {
		return navigateItems;
	}

	@Override
	public List<XNavigateItem> getSearchNavigateItems() {
		List<XNavigateItem> items = new ArrayList<XNavigateItem>();
		addExtensionPointItems(items);
		return items;
	}

	private void addExtensionPointItems(List<XNavigateItem> items) {
		List<OteExtensionPointData> oteNavigateItemExtensions = getExtensionPointData();
		Map<String, XNavigateItem> categoryToNavigateItem = createCategoriesAndAddToItems(
				items, oteNavigateItemExtensions);
		for (OteExtensionPointData data : oteNavigateItemExtensions) {
			XNavigateItem item = categoryToNavigateItem.get(data.getCategory());
			try {
				if (item == null) {
					items.addAll(data.getNavigateItems());
				} else {

					for (XNavigateItem navItem : data.getNavigateItems()) {
						item.addChild(navItem);
					}
				}
			} catch (Throwable th) {
				OseeLog.log(OteNavigateViewItems.class, Level.SEVERE, th);
			}
		}
	}

	/**
	 * @param oteNavigateItemExtensions
	 * @return
	 */
	private Map<String, XNavigateItem> createCategoriesAndAddToItems(
			List<XNavigateItem> items,
			List<OteExtensionPointData> oteNavigateItemExtensions) {
		Map<String, XNavigateItem> categoryToNavigateItem = new HashMap<String, XNavigateItem>();
		for (OteExtensionPointData data : oteNavigateItemExtensions) {
			if (!categoryToNavigateItem.containsKey(data.getCategory())) {
				String[] path = data.getItemPath();
				String key = "";
				XNavigateItem lastItem = null;
				for (int i = 0; i < path.length; i++) {

					key += path[i];
					XNavigateItem foundItem = categoryToNavigateItem.get(key);
					if (foundItem == null) {
						foundItem = new XNavigateItem(lastItem, path[i],
								FrameworkImage.FOLDER);
						categoryToNavigateItem.put(key, foundItem);
						// if(lastItem != null){
						// lastItem.addChild(foundItem);
						// }
						if (i == 0) {
							items.add(foundItem);
						}
					}
					lastItem = foundItem;
					key += ".";
				}
			}
		}
		return categoryToNavigateItem;
	}

	private List<OteExtensionPointData> getExtensionPointData() {
		List<OteExtensionPointData> data = new ArrayList<OteExtensionPointData>();
		List<IConfigurationElement> elements = ExtensionPoints
				.getExtensionElements(
						"org.eclipse.osee.ote.ui.OteNavigateItem",
						"IOteNavigateItem");
		for (IConfigurationElement element : elements) {
			String className = element.getAttribute("classname");
			String category = element.getAttribute("category");
			String bundleName = element.getContributor().getName();

			if (Strings.isValid(bundleName) && Strings.isValid(className)) {
				try {
					Bundle bundle = Platform.getBundle(bundleName);
					Class<?> taskClass = bundle.loadClass(className);
					Object object;
					try {
						Method getInstance = taskClass.getMethod("getInstance",
								new Class[] {});
						object = getInstance.invoke(null, new Object[] {});
					} catch (Exception ex) {
						object = taskClass.newInstance();
					}
					data.add(new OteExtensionPointData(category,
							(IOteNavigateItem) object));
				} catch (Exception ex) {
					throw new IllegalArgumentException(
							String.format("Unable to Load: [%s - %s]",
									bundleName, className), ex);
				}
			}
		}
		return data;
	}
}
