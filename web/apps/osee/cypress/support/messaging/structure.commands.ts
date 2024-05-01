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
	'navigateToStructurePage',
	(message: string, submessage: string, isMessageFirst: boolean) => {
		cy.openMessage(message, isMessageFirst);
		cy.intercept(
			'/mim/branch/*/connections/*/messages/*/submessages/*/structures/**/*'
		).as('structures');
		cy.get(
			`[data-cy="sub-message-table-row-${submessage}"] > [data-cy="sub-msg-field- -undefined"]`
		).as('navigateButton');
		return cy
			.get('@navigateButton')
			.click()
			.url()
			.should('contain', '/elements')
			.wait('@structures')
			.url()
			.should('contain', '/elements');
	}
);
Cypress.Commands.add('structureRightClick', (structure: string) => {
	cy.get(`[data-cy="structure-table-row-${structure}"]`).as('row');
	cy.get('@row').should('have.length.at.least', 1);
	return cy.get('@row').should('be.visible').first().rightclick();
});
Cypress.Commands.add(
	'createStructureDialog',
	(
		name: string,
		description: string,
		maxSimultaneity: string,
		minSimultaneity: string,
		taskFileType: string,
		category: string
	) => {
		cy.intercept('POST', 'orcs/txs').as('txs');
		cy.intercept('GET', '/ats/action/**/*').as('action');
		cy.intercept('/ats/teamwf/**/*').as('teamwf');
		cy.intercept('/ats/config/teamdef/*/leads').as('leads');
		cy.intercept('/ats/ple/action/*/approval').as('approval');
		cy.intercept(
			'/mim/branch/*/connections/*/messages/*/submessages/*/structures/**/*'
		).as('structures');
		return cy
			.get('mat-horizontal-stepper')
			.should('be.visible')
			.get(`[data-cy="create-new-btn"]`)
			.click()
			.get('[data-cy="field-name"]')
			.focus()
			.type(name, { force: true })
			.get('[data-cy="field-description"]')
			.focus()
			.type(description, { force: true })
			.get('[data-cy="field-max-simultaneity"]')
			.focus()
			.type(maxSimultaneity, { force: true })
			.get('[data-cy="field-min-simultaneity"]')
			.focus()
			.type(minSimultaneity, { force: true })
			.get('[data-cy="field-task-file-type"]')
			.focus()
			.type(taskFileType, { force: true })
			.get('[data-cy="field-category"]')
			.click({ force: true })
			.get(`[data-cy="option-${category}"]`)
			.click()
			.get('[data-cy="stepper-next"]')
			.click({ force: true })
			.get('[data-cy="submit-btn"]')
			.click()
			.wait('@txs')
			.wait('@structures')
			.wait('@teamwf')
			.wait('@action')
			.wait('@leads')
			.get('[data-cy="submit-btn"]')
			.should('not.exist')
			.wait(5000)
			.get('mat-progress-bar')
			.should('not.exist');
	}
);
Cypress.Commands.add(
	'createStructure',
	(
		name: string,
		description: string,
		maxSimultaneity: string,
		minSimultaneity: string,
		taskFileType: string,
		category: string
	) => {
		cy.intercept('/mim/user/*').as('mimUser');
		cy.intercept('/orcs/branches/*').as('branches');
		return cy
			.get('#addButton')
			.click()
			.get('[data-cy="base-add-button"]')
			.click({ timeout: 10000 })
			.createStructureDialog(
				name,
				description,
				maxSimultaneity,
				minSimultaneity,
				taskFileType,
				category
			)
			.wait('@mimUser')
			.wait('@branches')
			.wait(5000)
			.get('#addButton')
			.click();
	}
);
Cypress.Commands.add(
	'editStructureDescription',
	(name: string, description: string) => {
		cy.intercept('POST', 'orcs/txs').as('txs');
		cy.intercept(
			'/mim/branch/*/connections/*/messages/*/submessages/*/structures/**/*'
		).as('structures');
		return cy
			.structureRightClick(name)
			.get(`[data-cy="structure-open-description-btn"]`)
			.click()
			.editFreeText(description)
			.wait('@txs')
			.wait('@structures')
			.wait(5000)
			.get('mat-progress-bar')
			.should('not.exist');
	}
);

Cypress.Commands.add('insertStructureBottom', (associatedStructure: string) => {
	cy.intercept('POST', 'orcs/txs').as('txs');
	cy.intercept(
		'/mim/branch/*/connections/*/messages/*/submessages/*/structures/**/*'
	).as('structures');
	return cy
		.structureRightClick(associatedStructure)
		.get(`[data-cy="structure-insert-end-btn"]`)
		.click()
		.createStructureDialog(
			'Bottom Structure',
			'Debug Structure Description',
			'1',
			'0',
			'0',
			'N/A'
		);
});
Cypress.Commands.add('insertStructureTop', (associatedStructure: string) => {
	cy.intercept('POST', 'orcs/txs').as('txs');
	cy.intercept(
		'/mim/branch/*/connections/*/messages/*/submessages/*/structures/**/*'
	).as('structures');
	return cy
		.structureRightClick(associatedStructure)
		.get(`[data-cy="structure-insert-top-btn"]`)
		.click()
		.createStructureDialog(
			'Top Structure',
			'Debug Structure Description',
			'1',
			'0',
			'0',
			'N/A'
		);
});
Cypress.Commands.add('removeStructure', (associatedStructure: string) => {
	cy.intercept('POST', 'orcs/txs').as('txs');
	cy.intercept(
		'/mim/branch/*/connections/*/messages/*/submessages/*/structures/**/*'
	).as('structures');
	cy.intercept('GET', '/ats/action/**/*').as('action');
	cy.intercept('/ats/teamwf/**/*').as('teamwf');
	cy.intercept('/ats/config/teamdef/*/leads').as('leads');
	cy.intercept('/ats/ple/action/*/approval').as('approval');
	cy.intercept('/orcs/branch/*/applic').as('applic');
	return cy
		.structureRightClick(associatedStructure)
		.get(`[data-cy="structure-remove-btn"]`)
		.click()
		.get('[data-cy=submit-btn]')
		.click()
		.wait('@txs')
		.wait('@structures')
		.wait('@action')
		.wait('@teamwf')
		.wait('@approval')
		.wait('@leads')
		.wait('@applic')
		.wait(5000)
		.get('mat-progress-bar')
		.should('not.exist');
});
Cypress.Commands.add('deleteStructure', (associatedStructure: string) => {
	cy.intercept('POST', 'orcs/txs').as('txs');
	cy.intercept(
		'/mim/branch/*/connections/*/messages/*/submessages/*/structures/**/*'
	).as('structures');
	cy.intercept('GET', '/ats/action/**/*').as('action');
	cy.intercept('/ats/teamwf/**/*').as('teamwf');
	cy.intercept('/ats/config/teamdef/*/leads').as('leads');
	cy.intercept('/ats/ple/action/*/approval').as('approval');
	cy.intercept('/orcs/branch/*/applic').as('applic');
	return cy
		.structureRightClick(associatedStructure)
		.get(`[data-cy="structure-delete-btn"]`)
		.click()
		.get('[data-cy=submit-btn]')
		.click()
		.wait('@txs')
		.wait('@structures')
		.wait('@action')
		.wait('@teamwf')
		.wait('@approval')
		.wait('@leads')
		.wait('@applic')
		.wait(5000)
		.get('mat-progress-bar')
		.should('not.exist');
});
Cypress.Commands.add('openStructure', (structure: string) => {
	cy.get(`[data-cy="expand-structure-btn-${structure}"]`).as('btn');
	// cy.get('mat-progress-bar').as('progress')
	return cy
		.get('mat-progress-bar')
		.should('not.exist')
		.get('@btn')
		.scrollIntoView()
		.should('be.visible')
		.should('have.length.at.least', 1)
		.click();
});
Cypress.Commands.add('closeStructure', (structure: string) => {
	cy.get(`[data-cy="close-structure-btn-${structure}"]`).as('btn');
	cy.get('mat-progress-bar').as('progress');
	return cy
		.get('@progress')
		.should('not.exist')
		.get('@btn')
		.scrollIntoView()
		.should('be.visible')
		.should('have.length.at.least', 1)
		.click();
});

Cypress.Commands.add('validateStructureHeaderExists', (header: string) => {
	cy.get(`[data-cy=structure-table-header-${header}]`)
		.scrollIntoView()
		.should('exist');
});
