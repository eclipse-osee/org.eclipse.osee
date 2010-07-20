/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.artifact.editor.sections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

public final class AttributeTypeEditDisplay implements AttributeTypeEditPresenter.Display {

	public AttributeTypeEditDisplay() {
		super();
	}

	@Override
	public void showInformation(String title, String message) {
		MessageDialog.openInformation(PlatformUI.getWorkbench().getDisplay().getActiveShell(), title, message);
	}

	private CheckedTreeSelectionDialog createDialog(String title, String message, KeyedImage keyedImage) {
		CheckedTreeSelectionDialog dialog =
					new CheckedTreeSelectionDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
								new LabelProvider(), new ArrayTreeContentProvider());
		dialog.setTitle(title);
		Image image = ImageManager.getImage(keyedImage);
		dialog.setImage(image);
		dialog.setMessage(message);
		dialog.setValidator(new ISelectionStatusValidator() {

			@Override
			public IStatus validate(Object[] selection) {
				if (selection.length == 0) {
					return new Status(IStatus.ERROR, SkynetGuiPlugin.PLUGIN_ID,
								"Select at least one item or click cancel to exit.");
				}
				return Status.OK_STATUS;
			}
		});
		return dialog;
	}

	@Override
	public Collection<IAttributeType> getSelections(OperationType operationType, String title, String message, List<? extends IAttributeType> input) {
		Collection<IAttributeType> toReturn = Collections.emptyList();
		CheckedTreeSelectionDialog dialog = createDialog(title, message, getImage(operationType));
		dialog.setInput(input);
		int result = dialog.open();
		if (result == Window.OK) {
			toReturn = new ArrayList<IAttributeType>();
			for (Object object : dialog.getResult()) {
				if (object instanceof IAttributeType) {
					toReturn.add((IAttributeType) object);
				}
			}
		}
		return toReturn;
	}

	private KeyedImage getImage(OperationType operationType) {
		KeyedImage toReturn = null;
		switch (operationType) {
			case ADD_ITEM:
				toReturn = FrameworkImage.ADD_GREEN;
				break;
			case REMOVE_ITEM:
				toReturn = FrameworkImage.DELETE;
				break;
			default:
				break;
		}
		return toReturn;
	}
}