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
export type Artifact = {
	icdSystem: string;
	workflows: workflow[];
};

export type workflow = {
	actionId: string;
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
	expanded: boolean;
};

export type requirement = {
	tests: test[];
	name: string;
};

export type test = {
	name: string;
	script: string;
};
