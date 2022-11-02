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
Cypress.Commands.add('navigateToTransportTypesPage', () => {
	cy.intercept('GET', '/mim/branch/*/transportTypes').as('transports');
	cy.intercept('GET', '/ats/action/**/*').as('action');
	cy.intercept('/ats/teamwf/**/*').as('teamwf');
	cy.intercept('/ats/config/teamdef/*/leads').as('leads');
	cy.intercept('/ats/ple/action/*/approval').as('approval');
	cy.visit('/ple')
		.get('[data-cy="messaging-nav-button"]')
		.click()
		.get('[data-cy="transports-nav-button"]')
		.click()
		.task<Cypress.NameResult>('getLatestBranchName')
		.then((branchname) => {
			const branch: string = branchname.name;
			return cy
				.selectBranch(branch, 'working')
				.wait('@transports')
				.wait('@action')
				.wait('@teamwf')
				.wait('@approval');
		});
	return cy.get('mat-progress-bar').should('not.exist');
});

Cypress.Commands.add(
	'createTransportType',
	(
		name: string,
		byteAlignValidation: boolean,
		validationSize: number,
		messageGeneration: boolean,
		messageGenerationType: string,
		messageGenerationPosition: string
	) => {
		cy.intercept('GET', '/mim/branch/*/transportTypes').as('transports');
		cy.intercept('orcs/txs').as('txs');
		cy.intercept('GET', '/ats/action/**/*').as('action');
		cy.intercept('/ats/teamwf/**/*').as('teamwf');
		cy.intercept('/ats/config/teamdef/*/leads').as('leads');
		cy.intercept('/ats/ple/action/*/approval').as('approval');
		cy.get('.add-button').click().get('[data-cy=field-name]').type(name);
		if (byteAlignValidation) {
			cy.get('[data-cy=field-validation]')
				.click()
				.get('[data-cy=field-validation-size]')
				.type(validationSize.toString());
		}
		if (messageGeneration) {
			cy.get('[data-cy=field-generation]')
				.click()
				.get('[data-cy=field-generation-position]')
				.click()
				.type(messageGenerationPosition)
				.get('[data-cy=field-generation-type]')
				.click()
				.get(`[data-cy=option-${messageGenerationType}]`)
				.click()
				.get('.mat-dialog-content')
				.click({ force: true });
		}
		return cy
			.get('[data-cy=submit-btn]')
			.click()
			.wait('@txs')
			.wait('@transports')
			.wait('@action')
			.wait('@teamwf')
			.wait('@approval')
			.wait('@leads')
			.get('mat-progress-bar')
			.should('not.exist')
			.get(`[data-cy=table-element-${name}]`)
			.should('exist')
			.get(`[data-cy=table-element-${byteAlignValidation}]`)
			.should('exist')
			.get(`[data-cy=table-element-${validationSize}]`)
			.should(byteAlignValidation ? 'exist' : 'not.exist')
			.get(`[data-cy=table-element-${messageGeneration}]`)
			.should('exist')
			.get(`[data-cy=table-element-${messageGenerationType}]`)
			.should('exist')
			.get(`[data-cy=table-element-${messageGenerationPosition}]`)
			.should('exist');
	}
);
