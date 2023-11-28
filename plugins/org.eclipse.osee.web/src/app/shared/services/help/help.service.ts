/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { Injectable } from '@angular/core';
import { HelpPage, navigationElement } from '@osee/shared/types';
import { from, map, reduce, shareReplay, switchMap } from 'rxjs';
import { HelpHttpService } from '@osee/shared/services/help';

@Injectable({
	providedIn: 'root',
})
export class HelpService {
	constructor(private helpHttpService: HelpHttpService) {}

	getHelpPage(id: string) {
		return this.helpHttpService.getHelpPage(id);
	}

	getHelpPages(appName: string) {
		return this.helpHttpService
			.getHelpPages(appName)
			.pipe(shareReplay({ bufferSize: 1, refCount: true }));
	}

	getHelpNavElements(appName: string) {
		return this.getHelpPages(appName).pipe(
			switchMap((pages) =>
				from(pages).pipe(map((h) => this.helpPageToNavElement(h)))
			),
			reduce((acc, curr) => [...acc, curr], [] as navigationElement[])
		);
	}

	private helpPageToNavElement(helpPage: HelpPage) {
		const navElement = {
			label: helpPage.name,
			cypressLabel: 'cy-help-nav-' + helpPage.id,
			pageTitle: '',
			isDropdown: helpPage.header,
			isDropdownOpen: false,
			requiredRoles: [],
			routerLink: 'page/' + helpPage.id,
			icon: '',
			description: '',
			children: helpPage.children.map((c) =>
				this.helpPageToNavElement(c)
			),
		} as navigationElement;
		return navElement;
	}
}
