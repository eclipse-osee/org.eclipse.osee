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
package org.eclipse.osee.framework.manager.servlet.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.osee.framework.resource.management.Options;

/**
 * @author Roberto E. Escobar
 */
public class HttpBranchExchangeInfo {

	public enum BranchExchangeFunctions {
		exportBranch, importBranch, checkExchange;
	};

	private BranchExchangeFunctions function;
	private String exchangeFileName;
	private String path;
	private List<Integer> selectedBranchIds;
	private boolean sendExportFile;
	private boolean deleteExportFile;
	private Options options;

	@SuppressWarnings("unchecked")
	public HttpBranchExchangeInfo(HttpServletRequest request) throws Exception {
		this.options = new Options();
		this.function = null;
		this.selectedBranchIds = new ArrayList<Integer>();
		this.sendExportFile = false;
		this.deleteExportFile = false;
		this.exchangeFileName = null;
		this.path = null;

		Enumeration<String> enumeration = request.getParameterNames();
		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();
			String value = request.getParameter(name);
			if (name.equalsIgnoreCase("filename")) {
				this.exchangeFileName = value;
			} else if (name.equalsIgnoreCase("uri")) {
				this.path = value;
			} else if (name.equalsIgnoreCase("send.export.file")) {
				this.sendExportFile = Boolean.valueOf(value);
			} else if (name.equalsIgnoreCase("delete.export.file")) {
				this.deleteExportFile = Boolean.valueOf(value);
			} else if (name.equalsIgnoreCase("function")) {
				isFunctionValid(value);
			} else if (name.equalsIgnoreCase("branchIds")) {
				for (String entry : value.split(",")) {
					selectedBranchIds.add(new Integer(entry));
				}
			} else {
				options.put(name.toUpperCase(), value);
			}
		}
	}

	public BranchExchangeFunctions getFunction() {
		return this.function;
	}

	public boolean isSendExportFile() {
		return this.sendExportFile;
	}

	public boolean isDeleteExportFile() {
		return this.deleteExportFile;
	}

	public String getExchangeFileName() {
		return this.exchangeFileName;
	}

	public String getPath() {
		return this.path;
	}

	public List<Integer> getSelectedBranchIds() {
		return this.selectedBranchIds;
	}

	public Options getOptions() {
		return this.options;
	}

	private void isFunctionValid(String function) throws Exception {
		if (function == null) {
			throw new Exception("A 'function' parameter must be defined.");
		}
		try {
			this.function = BranchExchangeFunctions.valueOf(function);
		} catch (IllegalArgumentException ex) {
			throw new Exception(String.format("[%s] is not a valid function.",
					function), ex);
		}
	}
}
