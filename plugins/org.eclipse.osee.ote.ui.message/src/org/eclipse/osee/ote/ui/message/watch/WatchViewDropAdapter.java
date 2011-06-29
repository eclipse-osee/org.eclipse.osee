/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.message.watch;

import java.io.IOException;
import java.util.logging.Level;

import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;

public class WatchViewDropAdapter implements DropTargetListener {

	private WatchView watchViewer;
	
	public WatchViewDropAdapter(WatchView watchViewer) {
		this.watchViewer = watchViewer;
	}

	@Override
	public void dragEnter(DropTargetEvent event) {
	}

	@Override
	public void dragLeave(DropTargetEvent event) {
	}

	@Override
	public void dragOperationChanged(DropTargetEvent event) {
	}

	@Override
	public void dragOver(DropTargetEvent event) {
	}

	@Override
	public void drop(DropTargetEvent event) {
		if(event.data instanceof String[]){
			for(String file: (String[])event.data){
				java.io.File realFile = new java.io.File(file);
				if(realFile.exists() && !realFile.isDirectory()){
					try {
						String fileAsString = Lib.fileToString(realFile);
						SignalStripper signalStripper = new SignalStripper();
						String mwi = signalStripper.generateStringToWrite(fileAsString);
						watchViewer.loadWatchFile(mwi);
					} catch (IOException e) {
						OseeLog.log(WatchViewDropAdapter.class, Level.SEVERE, "Failed to read file from drag and drop into message watch.", e);
					}
				}
			}
		}
	}

	@Override
	public void dropAccept(DropTargetEvent event) {
	}

}
