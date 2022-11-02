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
Cypress.Commands.add('selectBranchType', (type: 'working' | 'baseline') => {
	cy.intercept(`**/branches/**/*`).as('category');
	if (type === 'working') {
		return cy
			.get('[type="radio"]')
			.last()
			.check({ force: true })
			.wait('@category');
	} else {
		return cy
			.get('[type="radio"]')
			.first()
			.check({ force: true })
			.wait('@category');
	}
});
Cypress.Commands.add(
	'selectBranch',
	(name: string, type: 'working' | 'baseline') => {
		cy.intercept(`/orcs/branches/${type}`).as(`${type}`);
		cy.intercept('/orcs/txs').as('txs');
		cy.selectBranchType(type)
			.get('[data-cy="branch-select"]')
			.click()
			.get('[data-cy=mat-option-loading-spinner]')
			.should('not.exist')
			.get(`[data-cy="option-${name}"]`, { timeout: 10000 })
			.click();
	}
);
Cypress.Commands.add(
	'createBranch',
	(
		fromBranch: string,
		actionableItem: string,
		changeType: string,
		targetedVersion: string,
		title: string,
		description: string
	) => {
		cy.intercept('GET', '/ats/action/**/*').as('action');
		cy.intercept('/ats/teamwf/**/*').as('teamwf');
		cy.intercept('POST', '/ats/action/branch').as('createBranch');
		cy.intercept(`**/branches/**/*`).as('category');
		cy.intercept('/ats/config/teamdef/*/leads').as('leads');
		cy.intercept('/ats/ple/action/*/approval').as('approval');
		cy.intercept('/ats/ai/worktype/*').as('workType');
		cy.intercept('/ats/teamwf/*/version?sort=true').as('version');
		cy.intercept('/ats/teamwf/*/changeTypes?sort=true').as('changeTypes');
		const actionNum = Math.floor(Math.random() * (1000000 - 0 + 1) + 0);
		return cy
			.selectBranch(fromBranch, 'baseline')
			.get('osee-action-dropdown')
			.click()
			.wait('@action')
			.wait('@workType')
			.get('[data-cy="select-actionable-item"]')
			.focus()
			.click({ timeout: 10000 })
			.get(`[data-cy="option-${actionableItem}"]`)
			.click()
			.wait('@version')
			.wait('@teamwf')
			.wait('@changeTypes')
			.get('[data-cy=select-change-type]')
			.focus()
			.click()
			.get(`[data-cy=option-${changeType}]`)
			.click()
			.get('[data-cy="select-targeted-version"]')
			.focus()
			.click({ timeout: 10000 })
			.get(`[data-cy="option-${targetedVersion}"]`)
			.click()
			.get('[data-cy="action-title"]')
			.type(title + actionNum)
			.get('[data-cy="action-description"]')
			.type(description)
			.get('[data-cy="submit-btn"]')
			.click()
			.wait('@createBranch')
			.then(({ response }) => {
				const name = response?.body.workingBranchId.shortName;
				cy.task('setLatestBranchName', { name });
			})
			.wait('@category')
			.wait('@action')
			.wait('@teamwf')
			.wait('@approval')
			.wait('@leads')
			.get('mat-progress-bar')
			.should('not.exist');
	}
);
