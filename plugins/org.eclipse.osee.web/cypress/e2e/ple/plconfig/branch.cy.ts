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
describe('PLConfig - Create Branch', () => {
	before(() => {
		cy.visit('/ple')
			.get('[data-cy="plconfig-nav-button"]')
			.click()
			.url()
			.should('include', 'plconfig');
	});
	describe('branch creation', () => {
		before(() => {
			cy.createBranch(
				'SAW Product Line',
				'SAW PL ARB',
				'Improvement',
				'SAW Product Line',
				'Cypress Test',
				'Cypress Action Description'
			);
		});
		it('should have created a branch', () => {
			cy.task<Cypress.NameResult>('getLatestBranchName').then(
				(branchname) => {
					const branch: string = branchname.name;
					return cy.selectBranch(branch, 'working');
				}
			);
		});
	});
});
