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
describe('PLConfig - Configurations(View Mode)', () => {
	before(() => {
		cy.visit('/ple').get('[data-cy="plconfig-nav-button"]').click();
		cy.selectBranch('SAW Product Line', 'baseline');
	});
	it('Should have the correct values for configurations', () => {
		cy.validatePLConfigValues([
			{
				title: 'Product C',
				values: [
					{ title: 'ENGINE_5', value: 'A2543' },
					{ title: 'JHU_CONTROLLER', value: 'Included' },
					{ title: 'ROBOT_ARM_LIGHT', value: 'Excluded' },
					{ title: 'ROBOT_SPEAKER', value: 'SPKR_B' },
				],
			},
			{
				title: 'Product D',
				values: [
					{ title: 'ENGINE_5', value: 'B5543' },
					{ title: 'JHU_CONTROLLER', value: 'Excluded' },
					{ title: 'ROBOT_ARM_LIGHT', value: 'Excluded' },
					{ title: 'ROBOT_SPEAKER', value: 'SPKR_B' },
				],
			},
			{
				title: 'Product A',
				values: [
					{ title: 'ENGINE_5', value: 'A2543' },
					{ title: 'JHU_CONTROLLER', value: 'Excluded' },
					{ title: 'ROBOT_ARM_LIGHT', value: 'Excluded' },
					{ title: 'ROBOT_SPEAKER', value: 'SPKR_A' },
				],
			},
			{
				title: 'Product B',
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
describe('PLConfig - Configurations(Edit Mode)', () => {
	before(() => {
		cy.visit('/ple').get('[data-cy="plconfig-nav-button"]').click();
		cy.task<Cypress.NameResult>('getLatestBranchName').then(
			(branchname) => {
				const branch: string = branchname.name;
				return cy.selectBranch(branch, 'working');
			}
		);
	});
	it('should create a Product E without a Group', () => {
		cy.createConfiguration({
			title: 'Product E',
			copyFrom: 'Product C',
			groups: [],
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
		});
	});
	it('should create a Product F in abGroup', () => {
		cy.createConfiguration({
			title: 'Product F',
			copyFrom: 'Product A',
			groups: ['abGroup'],
			productTypes: [
				{
					name: 'Code',
					enabled: true,
				},
				{
					name: 'Continuous_Integration',
					enabled: false,
				},
				{
					name: 'Documentation',
					enabled: true,
				},
				{
					name: 'Requirements',
					enabled: false,
				},
				{
					name: 'Test',
					enabled: true,
				},
				{
					name: 'Unspecified',
					enabled: false,
				},
			],
		});
	});
	it('should create a Product G without a Group and delete it', () => {
		cy.createConfiguration({
			title: 'Product G',
			copyFrom: 'Product C',
			groups: [],
			productTypes: [
				{
					name: 'Code',
					enabled: true,
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
		}).deleteConfiguration('Product G');
	});
	it('should create a Product H in abGroup and delete it', () => {
		cy.createConfiguration({
			title: 'Product H',
			copyFrom: 'Product A',
			groups: ['abGroup'],
			productTypes: [
				{
					name: 'Code',
					enabled: true,
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
		}).deleteConfiguration('Product H');
	});
	it('should edit Product D -> Product K', () => {
		cy.editConfigurationClick(
			{
				title: 'Product D',
				productTypes: [
					{
						name: 'Code',
						enabled: false,
					},
					{
						name: 'Continuous_Integration',
						enabled: false,
					},
					{
						name: 'Documentation',
						enabled: false,
					},
					{
						name: 'Requirements',
						enabled: false,
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
				groups: [],
			},
			{
				title: 'Product K',
				productTypes: [
					{
						name: 'Code',
						enabled: false,
					},
					{
						name: 'Continuous_Integration',
						enabled: false,
					},
					{
						name: 'Documentation',
						enabled: true,
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
				groups: [],
			}
		);
	});
	it('should edit Product B -> Product L', () => {
		cy.editConfigurationDropdown(
			{
				title: 'Product B',
				productTypes: [
					{
						name: 'Code',
						enabled: false,
					},
					{
						name: 'Continuous_Integration',
						enabled: false,
					},
					{
						name: 'Documentation',
						enabled: false,
					},
					{
						name: 'Requirements',
						enabled: false,
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
				groups: [{ name: 'abGroup', enabled: true }],
			},
			{
				title: 'Product L',
				productTypes: [
					{
						name: 'Code',
						enabled: false,
					},
					{
						name: 'Continuous_Integration',
						enabled: false,
					},
					{
						name: 'Documentation',
						enabled: false,
					},
					{
						name: 'Requirements',
						enabled: false,
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
				groups: [{ name: 'abGroup', enabled: true }],
			}
		);
	});
	it('should copy Product A into Product F', () => {
		cy.copyConfiguration('Product F', 'Product A', 1);
	});
	it('should copy Product C into Product E', () => {
		cy.copyConfiguration('Product E', 'Product C', 0);
	});
});
describe('PLConfig - Configurations(Validation)', () => {
	it('Should have the correct values for configurations', () => {
		cy.validatePLConfigValues([
			{
				title: 'Product C',
				values: [
					{ title: 'ENGINE_5', value: 'A2543' },
					{ title: 'JHU_CONTROLLER', value: 'Included' },
					{ title: 'ROBOT_ARM_LIGHT', value: 'Excluded' },
					{ title: 'ROBOT_SPEAKER', value: 'SPKR_B' },
				],
			},
			{
				title: 'Product E',
				values: [
					{ title: 'ENGINE_5', value: 'A2543' },
					{ title: 'JHU_CONTROLLER', value: 'Included' },
					{ title: 'ROBOT_ARM_LIGHT', value: 'Excluded' },
					{ title: 'ROBOT_SPEAKER', value: 'SPKR_B' },
				],
			},
			{
				title: 'Product K',
				values: [
					{ title: 'ENGINE_5', value: 'B5543' },
					{ title: 'JHU_CONTROLLER', value: 'Excluded' },
					{ title: 'ROBOT_ARM_LIGHT', value: 'Excluded' },
					{ title: 'ROBOT_SPEAKER', value: 'SPKR_B' },
				],
			},
			{
				title: 'Product A',
				values: [
					{ title: 'ENGINE_5', value: 'A2543' },
					{ title: 'JHU_CONTROLLER', value: 'Excluded' },
					{ title: 'ROBOT_ARM_LIGHT', value: 'Excluded' },
					{ title: 'ROBOT_SPEAKER', value: 'SPKR_A' },
				],
			},
			{
				title: 'Product F',
				values: [
					{ title: 'ENGINE_5', value: 'A2543' },
					{ title: 'JHU_CONTROLLER', value: 'Excluded' },
					{ title: 'ROBOT_ARM_LIGHT', value: 'Excluded' },
					{ title: 'ROBOT_SPEAKER', value: 'SPKR_A' },
				],
			},
			{
				title: 'Product L',
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
