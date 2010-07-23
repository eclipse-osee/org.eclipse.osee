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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.osee.framework.core.model.access.PermissionStatus;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * @author Roberto E. Escobar
 */
public class XWidgetDecorator {
	private final int decorationPosition = SWT.LEFT | SWT.BOTTOM;
	private final Map<XWidget, Decorator> decoratorMap = new HashMap<XWidget, Decorator>();

	public XWidgetDecorator() {
	}

	public void addWidget(XWidget xWidget) {
		Control controlToDecorate = xWidget.getErrorMessageControl();
		Decorator decorator = new Decorator(controlToDecorate, decorationPosition);
		decoratorMap.put(xWidget, decorator);
	}

	public void update() {
		Displays.ensureInDisplayThread(new Runnable() {
			@Override
			public void run() {
				for (Decorator decorator : decoratorMap.values()) {
					decorator.update();
				}
			}
		});
	}

	public void dispose() {
		for (Decorator decorator : decoratorMap.values()) {
			decorator.dispose();
		}
		decoratorMap.clear();
	}

	private final static class Decorator {
		private ControlDecoration decoration;
		private String description;
		private int position;
		private Image image;
		private boolean isVisible;
		private boolean requiresCreation;
		private final Control control;

		public Decorator(Control control, int position) {
			this.control = control;
			setPosition(position);
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public void setImage(Image image) {
			this.image = image;
		}

		public void setPosition(int position) {
			if (getPosition() != position) {
				this.position = position;
				this.requiresCreation = true;
			}
		}

		public void setVisible(boolean isVisible) {
			this.isVisible = isVisible;
		}

		public boolean isVisible() {
			return isVisible;
		}

		public int getPosition() {
			return position;
		}

		public void dispose() {
			if (decoration != null) {
				decoration.dispose();
			}
		}

		public void update() {
			if (requiresCreation) {
				if (decoration != null) {
					decoration.dispose();
				}
				decoration = new ControlDecoration(control, position, control.getParent());
				requiresCreation = false;
			}

			if (isVisible()) {
				if (image != null) {
					decoration.setImage(image);
				}
				decoration.setDescriptionText(description);
				decoration.show();
			} else {
				decoration.setDescriptionText(null);
				decoration.hide();
			}
		}
	}

	//	public static interface DecorationProvider {
	//		int getPriority();
	//
	//		void onUpdate(XWidget widget, Decorator decorator);
	//	}

	//	public void addProvider(DecorationProvider provider) {
	//
	//	}

	public void onUpdate(XWidget xWidget, Decorator decorator) {
		// TODO separate onUpdate - make extensible
		// TODO Add AccessControlService

		if (xWidget instanceof IAttributeWidget) {
			IAttributeWidget attributeWidget = (IAttributeWidget) xWidget;
			String attributeType = attributeWidget.getAttributeType();
			//			Artifact artifact = null;
			PermissionStatus permissionStatus = new PermissionStatus(true, "You are not cool enough");
			//			try {
			//				AccessDataQuery query = AccessControlManager.getAccessData(null);
			//				query.attributeTypeMatches(PermissionEnum.WRITE, artifact, attributeType, permissionStatus);
			//			} catch (OseeCoreException ex) {
			//				OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
			//			}

			// Get Info from AccessControlService;
			boolean isLocked = permissionStatus.matches();
			String reason = permissionStatus.getReason();

			Control control = xWidget.getControl();
			if (Widgets.isAccessible(control)) {
				xWidget.setEditable(!isLocked);
			}
			Label label = xWidget.getLabelWidget();
			if (Widgets.isAccessible(label)) {
				label.setEnabled(!isLocked);
			}

			Image image = ImageManager.getImage(FrameworkImage.LOCK_OVERLAY);

			decorator.setImage(isLocked ? image : null);
			decorator.setDescription(isLocked ? reason : null);
			decorator.setVisible(isLocked);
		}
	}
}
