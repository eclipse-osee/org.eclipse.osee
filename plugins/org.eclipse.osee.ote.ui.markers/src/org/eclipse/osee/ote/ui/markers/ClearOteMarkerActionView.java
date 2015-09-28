/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.markers;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public class ClearOteMarkerActionView implements IViewActionDelegate {

	ArrayList<IResource> selections;
	
	public ClearOteMarkerActionView()  {
		selections = new ArrayList<>();
	}

	@Override
	public void run(IAction action) {
		for(IResource resource:selections){
			MarkerPlugin.findAndRemoveOteMarkers(resource);
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		selections.clear();
		if(selection instanceof StructuredSelection){
			Iterator<?> i = ((StructuredSelection)selection).iterator();
			while (i.hasNext()) {
				Object obj = i.next();
				if (obj instanceof IResource) {
					IResource resource = (IResource) obj;
					if (resource != null) {
						selections.add(resource);
					}
				} 
			}
		} 
	}

	@Override
	public void init(IViewPart view) {

	}
}
