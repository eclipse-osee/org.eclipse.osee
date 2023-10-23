/*********************************************************************
 * Copyright (c) 2023 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
export interface Artifact {
	icdSystem: string;
	workflows: workflow[];
}

export interface workflow {
	workflowID: string;
	program: string;
	build: string;
	subsystem: string;
	type: string;
	state: string;
	enhancement: string;
	title: string;
	changeType: string;
	changeReport: string;
	icdDiff: string;
	webExported: string;
	requirements: requirement[];
}

export interface requirement {
	tests: test[];
	name: string;
}

export interface test {
	name: string;
	script: string;
}
