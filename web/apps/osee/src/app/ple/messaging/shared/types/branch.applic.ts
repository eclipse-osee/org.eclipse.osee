/*********************************************************************
 * Copyright (c) 2021 Boeing
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
export type branchApplicability = {
	associatedArtifactId: string;
	branch: branch;
	editable: boolean;
	features: feature[];
	groups: configurationGroup[];
	parentBranch: branch;
	views: view[];
};

type branch = {
	id: string;
	viewId: string;
	idIntValue: number;
	name: string;
};

type view = {
	hasFeatureApplicabilities: boolean;
	productApplicabilities?: string[];
	id: string;
	name: string;
};

type feature = {
	name: string;
	description: string;
	valueType: string;
	valueStr?: string;
	defaultValue: string;
	productAppStr?: string;
	values: string[];
	productApplicabilities: string[];
	multiValued: boolean;
	id: string;
	idIntValue?: number;
	idString?: string;
	type: null | undefined;
	configurations: [{ name: string; value: string }];
};

type configurationGroup = {
	configurations: string[];
	id: string;
	name: string;
	hasFeatureApplicabilities: boolean;
	productApplicabilities: string[];
};
