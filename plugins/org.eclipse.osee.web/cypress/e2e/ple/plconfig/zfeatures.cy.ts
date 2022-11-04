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
describe('PLConfig - Features(View Mode)', () => {
	before(() => {
		cy.visit('/ple').get('[data-cy="plconfig-nav-button"]').click();
		cy.selectBranch('SAW Product Line', 'baseline');
	});
	it('validate ENGINE_5', () => {
		cy.get(`[data-cy="feature-ENGINE_5"]`).click();
		cy.validateFeatureInsideDialog(
			{
				title: 'ENGINE_5',
				description: 'Used select type of engine',
				valueType: 'String',
				multiValued: false,
				values: ['B5543', 'A2543'],
				defaultValue: 'A2543',
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
						enabled: true,
					},
					{
						name: 'Unspecified',
						enabled: false,
					},
				],
			},
			false
		)
			.get('[data-cy=ok-btn]')
			.click();
	});
});
describe('PLConfig - Features(Edit Mode)', () => {
	before(() => {
		cy.visit('/ple').get('[data-cy="plconfig-nav-button"]').click();
		cy.task<Cypress.NameResult>('getLatestBranchName').then(
			(branchname) => {
				const branch: string = branchname.name;
				return cy.selectBranch(branch, 'working');
			}
		);
	});
	it('should edit ENGINE_5', () => {
		cy.editFeature(
			'ENGINE_5',
			{
				title: 'ENGINE_5',
				description: 'Used select type of engine',
				valueType: 'String',
				multiValued: false,
				values: ['B5543', 'A2543'],
				defaultValue: 'A2543',
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
						enabled: true,
					},
					{
						name: 'Unspecified',
						enabled: false,
					},
				],
			},
			{
				title: 'ENGINE_5',
				description: 'Used select type of engine',
				valueType: 'String',
				multiValued: false,
				values: ['B5543', 'A2543', 'C7543', 'D9543'],
				defaultValue: 'C7543',
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
			}
		);
	});
	describe('Adding features', () => {
		it('should add a default feature', () => {
			cy.addFeature({
				title: 'default_feature',
				description: 'default description',
			});
		});
		it('should add a feature', () => {
			cy.addFeature({
				title: 'custom_feature',
				description: 'new feature',
				valueType: 'Boolean',
				defaultValue: 'Excluded',
				productTypes: ['Code', 'Continuous_Integration', 'Test'],
			});
		});
		it('should add a multi valued feature', () => {
			cy.addFeature({
				title: 'custom_feature2',
				description: 'new feature',
				valueType: 'Integer',
				multiValued: true,
				values: ['VALUE_1', 'VALUE_2', 'VALUE_3'],
				defaultValue: 'VALUE_3',
				productTypes: ['Code', 'Continuous_Integration', 'Test'],
			});
		});
		it('should add a feature and delete it', () => {
			cy.addFeature({
				title: 'default_feature2',
				description: 'default description',
			}).deleteFeature('DEFAULT_FEATURE2');
		});
	});
});
describe('PLConfig - Features(Validation)', () => {
	it('should include all features created, and none that were deleted', () => {
		cy.validateFeatureExists('CUSTOM_FEATURE')
			.validateFeatureExists('DEFAULT_FEATURE')
			.validateFeatureExists('CUSTOM_FEATURE2')
			.validateFeatureExists('ENGINE_5')
			.validateFeatureExists('JHU_CONTROLLER')
			.validateFeatureExists('ROBOT_ARM_LIGHT')
			.validateFeatureExists('ROBOT_SPEAKER')
			.validateFeatureDoesNotExist('DEFAULT_FEATURE2');
	});
});
