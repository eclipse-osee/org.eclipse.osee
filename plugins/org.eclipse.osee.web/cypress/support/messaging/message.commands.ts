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
Cypress.Commands.add(
	'createMessage',
	(
		name: string,
		description: string,
		rate: string,
		periodicity: string,
		messageType: string,
		messageNumber: string,
		nodeIsFirst: boolean
	) => {
		cy.intercept('/mim/enums/*').as('enums');
		cy.intercept('/mim/branch/*/nodes/connection/*').as('nodes');
		cy.intercept('POST', 'orcs/txs').as('txs');
		cy.intercept('GET', '/ats/action/**/*').as('action');
		cy.intercept('/ats/teamwf/**/*').as('teamwf');
		cy.intercept('/ats/config/teamdef/*/leads').as('leads');
		cy.intercept('/ats/ple/action/*/approval').as('approval');
		if (nodeIsFirst) {
			return cy
				.get('mat-progress-bar')
				.should('not.exist')
				.get('#addButton')
				.click()
				.get('[data-cy="base-add-button"]')
				.click({ timeout: 10000 })
				.wait('@enums')
				.wait('@enums')
				.wait('@enums')
				.wait('@nodes')
				.get('[data-cy=field-name]')
				.focus()
				.type(name)
				.get('[data-cy=field-description]')
				.focus()
				.type(description)
				.get('[data-cy=field-rate]')
				.click()
				.get(`[data-cy=option-${rate}]`)
				.click()
				.get('[data-cy=field-periodicity]')
				.click()
				.get(`[data-cy=option-${periodicity}]`)
				.click()
				.get('[data-cy=field-message-type]')
				.click()
				.get(`[data-cy=option-${messageType}]`)
				.click()
				.get('[data-cy=field-message-number]')
				.click()
				.focus()
				.type(messageNumber)
				.get('[data-cy=field-init-node]')
				.click()
				.get('mat-option')
				.first()
				.click()
				.get('[data-cy=submit-btn]')
				.click()
				.wait('@txs')
				.wait('@teamwf')
				.wait('@action')
				.wait('@approval')
				.wait('@leads')
				.get('mat-progress-bar')
				.should('not.exist')
				.get('#addButton')
				.click();
		} else {
			return cy
				.get('mat-progress-bar')
				.should('not.exist')
				.get('#addButton')
				.click()
				.get('[data-cy="base-add-button"]')
				.click({ timeout: 10000 })
				.wait('@enums')
				.wait('@enums')
				.wait('@enums')
				.wait('@nodes')
				.get('[data-cy=field-name]')
				.focus()
				.type(name)
				.get('[data-cy=field-description]')
				.focus()
				.type(description)
				.get('[data-cy=field-rate]')
				.click()
				.get(`[data-cy=option-${rate}]`)
				.click()
				.get('[data-cy=field-periodicity]')
				.click()
				.get(`[data-cy=option-${periodicity}]`)
				.click()
				.get('[data-cy=field-message-type]')
				.click()
				.get(`[data-cy=option-${messageType}]`)
				.click()
				.get('[data-cy=field-message-number]')
				.click()
				.focus()
				.type(messageNumber)
				.get('[data-cy=field-init-node]')
				.click()
				.get('mat-option')
				.last()
				.click()
				.get('[data-cy=submit-btn]')
				.click()
				.wait('@txs')
				.wait('@teamwf')
				.wait('@action')
				.wait('@approval')
				.wait('@leads')
				.get('mat-progress-bar')
				.should('not.exist')
				.get('#addButton')
				.click();
		}
	}
);
Cypress.Commands.add('navigateToMessagePage', (connectionName: string) => {
	cy.intercept('/mim/branch/*/graph').as('graph');
	cy.intercept('GET', '/ats/action/**/*').as('action');
	cy.intercept('/ats/teamwf/**/*').as('teamwf');
	cy.intercept('/ats/config/teamdef/*/leads').as('leads');
	cy.intercept('/ats/ple/action/*/approval').as('approval');
	cy.intercept('/mim/branch/*/connections/*/messages/filter/').as('messages');
	return cy
		.get(`[data-cy="link-${connectionName}"]`)
		.rightclick({ force: true, timeout: 10000 })
		.get(`[data-cy="goto-${connectionName}"]`)
		.click()
		.wait('@leads')
		.url()
		.should('contain', '/messages')
		.wait('@messages')
		.wait('@action')
		.wait('@teamwf')
		.wait('@approval')
		.wait('@leads')
		.url()
		.should('contain', '/messages');
});
Cypress.Commands.add('openMessage', (name: string, isMessageFirst) => {
	cy.get(`[data-cy="expand-message-btn-${name}"]`).as('btn');
	if (isMessageFirst) {
		return cy
			.get('@btn')
			.should('have.length.at.least', 1)
			.first()
			.scrollIntoView()
			.should('be.visible')
			.click();
	} else {
		return cy
			.get('@btn')
			.should('have.length.at.least', 1)
			.last()
			.scrollIntoView()
			.should('be.visible')
			.click();
	}
});
Cypress.Commands.add('closeMessage', (name: string, isMessageFirst) => {
	cy.get(`[data-cy="close-message-btn-${name}"]`).as('btn');
	if (isMessageFirst) {
		return cy
			.get('@btn')
			.should('have.length.at.least', 1)
			.first()
			.scrollIntoView()
			.should('be.visible')
			.click();
	} else {
		return cy
			.get('@btn')
			.should('have.length.at.least', 1)
			.last()
			.scrollIntoView()
			.should('be.visible')
			.click();
	}
});

Cypress.Commands.add(
	'editMessageDescription',
	(name: string, description: string, isMessageFirst: boolean) => {
		cy.intercept('POST', 'orcs/txs').as('txs');
		if (isMessageFirst) {
			return cy
				.get(`[data-cy="message-table-row-${name}"]`)
				.first()
				.rightclick()
				.get(`[data-cy="message-description-btn"]`)
				.click()
				.editFreeText(description)
				.wait('@txs')
				.get('mat-progress-bar')
				.should('not.exist');
		} else {
			return cy
				.get(`[data-cy="message-table-row-${name}"]`)
				.last()
				.rightclick()
				.get(`[data-cy="message-description-btn"]`)
				.click()
				.editFreeText(description)
				.wait('@txs')
				.get('mat-progress-bar')
				.should('not.exist');
		}
	}
);
