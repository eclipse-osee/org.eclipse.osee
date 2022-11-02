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

import { types } from '../../../../support/messaging/types';

/**
 * Create types before navigating to the connection page.
 */
describe('branch creation', () => {
	before(() => {
		cy.visit('/ple')
			.get('[data-cy="messaging-nav-button"]')
			.click()
			.get('[data-cy="types-nav-button"]')
			.click();
		cy.createBranch(
			'SAW Product Line',
			'SAW PL MIM',
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
describe('Enabling edit mode', () => {
	before('navigate to types page', () => {
		cy.url().then((url) => {
			if (!url.includes('/ple/messaging/types')) {
				cy.visit('/ple')
					.get('[data-cy="messaging-nav-button"]')
					.click()
					.get('[data-cy="types-nav-button"]')
					.click();
				cy.task<Cypress.NameResult>('getLatestBranchName').then(
					(branchname) => {
						const branch: string = branchname.name;
						return cy.selectBranch(branch, 'working');
					}
				);
			}
		});
	});
	it('should set user settings to edit', () => {
		cy.intercept('/ats/config/teamdef/*/leads').as('leads');
		cy.intercept('/ats/ple/action/*/approval').as('approval');
		cy.intercept('/ats/teamwf/**/*').as('teamwf');
		cy.enableMIMEditing().get('mat-progress-bar').should('not.exist');
	});
});
types.forEach((type) => {
	describe(`Creating type ${type}`, () => {
		beforeEach(() => {
			cy.intercept('POST', '/orcs/txs').as('txs');
			cy.intercept('mim/branch/**/*/types/filter').as('filter');
		});
		before('navigate to types page', () => {
			cy.url().then((url) => {
				if (!url.includes('/ple/messaging/types')) {
					cy.visit('/ple')
						.get('[data-cy="messaging-nav-button"]')
						.click()
						.get('[data-cy="types-nav-button"]')
						.click();
					cy.task<Cypress.NameResult>('getLatestBranchName').then(
						(branchname) => {
							const branch: string = branchname.name;
							return cy.selectBranch(branch, 'working');
						}
					);
				}
			});
		});
		it(`should create a type ${type}`, () => {
			cy.createNewPlatformType(type);
		});
	});
});
describe('Disabling edit mode', () => {
	before('navigate to types page', () => {
		cy.url().then((url) => {
			if (!url.includes('/ple/messaging/types')) {
				cy.visit('/ple')
					.get('[data-cy="messaging-nav-button"]')
					.click()
					.get('[data-cy="types-nav-button"]')
					.click();
				cy.task<Cypress.NameResult>('getLatestBranchName').then(
					(branchname) => {
						const branch: string = branchname.name;
						return cy.selectBranch(branch, 'working');
					}
				);
			}
		});
	});
	it('should set user settings to not edit', () => {
		cy.disableMIMEditing();
	});
});
describe('Create Transport Types', () => {
	before('navigate to transport types page', () => {
		cy.navigateToTransportTypesPage();
	});
	it('should create ethernet type', () => {
		cy.createTransportType('ETHERNET', false, 8, true, 'Dynamic', '1');
	});
});
// describe('Type Creation', () => {
//     beforeEach(() => {
//         cy.intercept('/orcs/txs').as('txs');
//         cy.intercept('mim/branch/**/*/types/filter').as('filter');
//     })
//     before('navigate to types page', () => {
//         cy.visit('/ple').get('[data-cy="messaging-nav-button"]').click().get('[data-cy="types-nav-button"]').click();
//     })
//     describe('type creation', () => {
//         it('should have created a branch', () => {
//             cy.task<Cypress.NameResult>('getLatestBranchName').then((branchname) => {
//                 const branch: string = branchname.name;
//                 return cy.selectBranch(branch, 'working');
//             });
//         })
//         it('should set user settings to edit', () => {
//             cy.enableMIMEditing();
//         })

//         it(`should create a type ${type}`, () => {
//             cy.createNewPlatformType(type);
//         })
//         it('should set user settings to not edit', () => {
//             cy.disableMIMEditing();
//         })
//     })
// })
