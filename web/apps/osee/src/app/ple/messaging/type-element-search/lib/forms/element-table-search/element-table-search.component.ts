/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
	MatFormField,
	MatLabel,
	MatPrefix,
	MatHint,
} from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { SearchService } from '../../services/search.service';

@Component({
	selector: 'osee-typesearch-element-table-search',
	templateUrl: './element-table-search.component.html',
	styles: [],
	standalone: true,
	imports: [
		FormsModule,
		MatFormField,
		MatLabel,
		MatInput,
		MatIcon,
		MatPrefix,
		MatHint,
	],
})
export class ElementTableSearchComponent {
	private searchService = inject(SearchService);

	//TODO: Luciano refactor searchTerm to be a signal here, this can be a lot more ergonomic
	searchTerm = '';

	/** Inserted by Angular inject() migration for backwards compatibility */
	constructor(...args: unknown[]);
	constructor() {
		this.searchService.searchTerm.subscribe((val) => {
			this.searchTerm = val;
		});
	}

	applyFilter(event: Event) {
		this.searchService.search = (event.target as HTMLInputElement).value;
	}
}
