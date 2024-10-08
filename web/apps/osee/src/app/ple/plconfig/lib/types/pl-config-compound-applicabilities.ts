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
export type compoundApplicabilityRelationship = {
	name: string;
	symbol: string;
};
export const compApplicRelationshipStructure: compoundApplicabilityRelationship[] =
	[
		{
			name: 'OR',
			symbol: '|',
		},
	];
export type compoundApplicability = {
	name: string;
	applicabilities: applicability[];
	relationships: string[];
};
export type applicability = {
	featureName: string;
	featureValue: string;
};
export class defaultCompoundApplicability implements compoundApplicability {
	public name = '';
	public applicabilities = [
		{ featureName: '', featureValue: '' },
		{ featureName: '', featureValue: '' },
	];
	public relationships = [''];
}
export type PLAddCompoundApplicabilityData = {
	compoundApplicability: defaultCompoundApplicability;
} & PLCompoundApplicabilityData;
type PLCompoundApplicabilityData = {
	currentBranch: string | number | undefined;
};
