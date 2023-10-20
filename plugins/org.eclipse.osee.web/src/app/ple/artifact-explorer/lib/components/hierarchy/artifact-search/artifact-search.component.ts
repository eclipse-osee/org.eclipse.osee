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
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatInputModule } from '@angular/material/input';
import { ArtifactExplorerHttpService } from '../../../services/artifact-explorer-http.service';
import { UiService } from '@osee/shared/services';
import {
	BehaviorSubject,
	Observable,
	combineLatest,
	debounceTime,
	filter,
	of,
	repeat,
	shareReplay,
	switchMap,
} from 'rxjs';
import { FormsModule } from '@angular/forms';
import { artifact } from '../../../types/artifact-explorer.data';
import { MatButtonModule } from '@angular/material/button';
import { ArtifactHierarchyPathService } from '../../../services/artifact-hierarchy-path.service';

@Component({
	selector: 'osee-artifact-search',
	standalone: true,
	imports: [
		CommonModule,
		MatFormFieldModule,
		MatAutocompleteModule,
		MatButtonModule,
		MatInputModule,
		FormsModule,
	],
	templateUrl: './artifact-search.component.html',
})
export class ArtifactSearchComponent {
	searchText = new BehaviorSubject<string>('');
	searchResults: Observable<artifact[]> = of([]);

	branchId$ = this.uiService.id;
	viewId$ = this.uiService.viewId;

	constructor(
		private artExpHttpService: ArtifactExplorerHttpService,
		private uiService: UiService,
		private artHierPathService: ArtifactHierarchyPathService
	) {}

	onSearchTextChange(searchTerm: Event) {
		const value = (searchTerm.target as HTMLInputElement).value;
		this.searchText.next(value);

		this.searchResults = combineLatest([
			this.branchId$,
			this.viewId$,
			this.searchText,
		]).pipe(
			filter(
				([branchId, viewId, filter]) =>
					branchId != '-1' &&
					branchId != '0' &&
					branchId != '' &&
					viewId != '' &&
					filter != ''
			),
			debounceTime(500),
			switchMap(([branchId, viewId, filter]) =>
				this.artExpHttpService
					.getArtifactByFilter(branchId, filter, '', '', viewId)
					.pipe(repeat({ delay: () => this.uiService.update }))
			),
			shareReplay({ bufferSize: 1, refCount: true })
		);
	}

	artifactSelected(id: string) {
		this.artHierPathService.initializePaths(id);
	}
}
