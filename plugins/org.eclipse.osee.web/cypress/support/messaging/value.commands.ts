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
	'validateMIMValue',
	(
		tableName: string,
		column: string,
		row: string,
		value: string,
		isLast?: boolean
	) => {
		if (isLast) {
			return cy
				.get(`[data-cy="${tableName}-${column}-${row}-${value}"]`)
				.last()
				.scrollIntoView()
				.should('exist');
		}
		return cy
			.get(`[data-cy="${tableName}-${column}-${row}-${value}"]`)
			.first()
			.scrollIntoView()
			.should('exist');
	}
);
