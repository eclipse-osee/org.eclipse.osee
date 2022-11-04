/*********************************************************************
 * Copyright (c) 2022 Boeing
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
describe('PLConfig - Groups(View Mode)', () => {
	before(() => {
		cy.visit('/ple').get('[data-cy="plconfig-nav-button"]').click();
		cy.selectBranch('SAW Product Line', 'baseline');
	});
	it('should have all values in abGroup', () => {
		cy.validatePLConfigValues([
			{
				title: 'abGroup',
				values: [
					{ title: 'ENGINE_5', value: 'A2543' },
					{ title: 'JHU_CONTROLLER', value: 'Included' },
					{ title: 'ROBOT_ARM_LIGHT', value: 'Included' },
					{ title: 'ROBOT_SPEAKER', value: 'SPKR_A' },
				],
			},
		]);
	});
});
describe('PLConfig - Groups(Edit Mode)', () => {
	before(() => {
		cy.visit('/ple').get('[data-cy="plconfig-nav-button"]').click();
		cy.task<Cypress.NameResult>('getLatestBranchName').then(
			(branchname) => {
				const branch: string = branchname.name;
				return cy.selectBranch(branch, 'working');
			}
		);
	});
	it('Should add a Configuration Group and add Product K to it', () => {
		cy.addConfigurationGroup('GroupK');
	});
	it('Should add a Configuration Group and add Product A to it', () => {
		cy.addConfigurationGroup('NewGroup');
	});
	it('should synchronize groups', () => {
		cy.syncConfigGroups();
	});
	it('should remove Product L from abGroup', () => {
		cy.removeConfigFromGroup('abGroup', 'Product L');
	});
	it('should add a Configuration Group, add Product E and Product B to it, and delete it', () => {
		cy.addConfigurationGroup('testGroup')
			.editConfigurationDropdown(
				{
					title: 'Product E',
					productTypes: [
						{
							name: 'Code',
							enabled: false,
						},
						{
							name: 'Continuous_Integration',
							enabled: true,
						},
						{
							name: 'Documentation',
							enabled: false,
						},
						{
							name: 'Requirements',
							enabled: true,
						},
						{
							name: 'Test',
							enabled: false,
						},
						{
							name: 'Unspecified',
							enabled: false,
						},
					],
					groups: [{ name: 'testGroup', enabled: false }],
				},
				{
					title: 'Product E',
					productTypes: [
						{
							name: 'Code',
							enabled: false,
						},
						{
							name: 'Continuous_Integration',
							enabled: true,
						},
						{
							name: 'Documentation',
							enabled: false,
						},
						{
							name: 'Requirements',
							enabled: true,
						},
						{
							name: 'Test',
							enabled: false,
						},
						{
							name: 'Unspecified',
							enabled: false,
						},
					],
					groups: [{ name: 'testGroup', enabled: true }],
				}
			)
			.deleteConfigGroup('testGroup');
	});
});
describe('PLConfig -Groups(Validation)', () => {
	it('Should have the correct values for groups', () => {
		cy.validatePLConfigValues([
			{
				title: 'abGroup',
				values: [
					{ title: 'CUSTOM_FEATURE', value: 'Excluded' },
					{ title: 'CUSTOM_FEATURE2', value: 'VALUE_3' },
					{ title: 'DEFAULT_FEATURE', value: 'Included' },
					{ title: 'ENGINE_5', value: 'A2543' },
					{ title: 'JHU_CONTROLLER', value: 'Included' },
					{ title: 'ROBOT_ARM_LIGHT', value: 'Included' },
					{ title: 'ROBOT_SPEAKER', value: 'SPKR_A' },
				],
			},
			{
				title: 'GroupK',
				values: [
					{ title: 'CUSTOM_FEATURE', value: 'Excluded' },
					{ title: 'CUSTOM_FEATURE2', value: 'VALUE_3' },
					{ title: 'DEFAULT_FEATURE', value: 'Included' },
					{ title: 'ENGINE_5', value: 'C7543' },
					{ title: 'JHU_CONTROLLER', value: 'Included' },
					{ title: 'ROBOT_ARM_LIGHT', value: 'Included' },
					{ title: 'ROBOT_SPEAKER', value: 'SPKR_A' },
				],
			},
			{
				title: 'NewGroup',
				values: [
					{ title: 'CUSTOM_FEATURE', value: 'Excluded' },
					{ title: 'CUSTOM_FEATURE2', value: 'VALUE_3' },
					{ title: 'DEFAULT_FEATURE', value: 'Included' },
					{ title: 'ENGINE_5', value: 'C7543' },
					{ title: 'JHU_CONTROLLER', value: 'Included' },
					{ title: 'ROBOT_ARM_LIGHT', value: 'Included' },
					{ title: 'ROBOT_SPEAKER', value: 'SPKR_A' },
				],
			},
		]);
	});
});
