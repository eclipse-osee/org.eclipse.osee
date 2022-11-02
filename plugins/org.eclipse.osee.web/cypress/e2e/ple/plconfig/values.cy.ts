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
describe('PLConfig - Changing Applicabilities', () => {
	before(() => {
		cy.visit('/ple').get('[data-cy="plconfig-nav-button"]').click();
		cy.task<Cypress.NameResult>('getLatestBranchName').then(
			(branchname) => {
				const branch: string = branchname.name;
				return cy.selectBranch(branch, 'working');
			}
		);
	});
	it('should change Product K- Custom Feature', () => {
		cy.changeApplicability(
			'CUSTOM_FEATURE',
			false,
			'Product K',
			'Excluded',
			'Included'
		);
	});
	it('should change Product F- Custom Feature', () => {
		cy.changeApplicability(
			'CUSTOM_FEATURE',
			false,
			'Product F',
			'Excluded',
			'Included'
		);
	});
	it('should change Product E - Custom Feature 2', () => {
		cy.changeApplicability(
			'CUSTOM_FEATURE2',
			true,
			'Product E',
			['VALUE_3'],
			['VALUE_3', 'VALUE_2']
		);
	});
	it('should change Product C - Custom Feature 2', () => {
		cy.changeApplicability(
			'CUSTOM_FEATURE2',
			true,
			'Product L',
			['VALUE_3'],
			['VALUE_2']
		);
	});
	it('should change Product F - Custom Feature 2', () => {
		cy.changeApplicability(
			'CUSTOM_FEATURE2',
			true,
			'Product F',
			['VALUE_3'],
			['VALUE_3', 'VALUE_2']
		);
	});
	it('should change Product A - Custom Feature 2', () => {
		cy.changeApplicability(
			'CUSTOM_FEATURE2',
			true,
			'Product A',
			['VALUE_3'],
			['VALUE_2']
		);
	});
});
