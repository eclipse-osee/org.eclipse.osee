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
	take,
	tap,
} from 'rxjs';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { ArtifactHierarchyPathService } from '../../../services/artifact-hierarchy-path.service';
import { NamedId } from '@osee/shared/types';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { AdvancedSearchDialogComponent } from './advanced-search-dialog/advanced-search-dialog.component';
import { AdvancedArtifactSearchService } from '../../../services/advanced-artifact-search.service';
import { MatChipsModule } from '@angular/material/chips';
import { AdvancedSearchCriteria } from '../../../types/artifact-search';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
	selector: 'osee-artifact-search',
	standalone: true,
	imports: [
		CommonModule,
		MatFormFieldModule,
		MatAutocompleteModule,
		MatButtonModule,
		MatInputModule,
		MatIconModule,
		MatDialogModule,
		FormsModule,
		MatChipsModule,
		MatTooltipModule,
	],
	templateUrl: './artifact-search.component.html',
})
export class ArtifactSearchComponent {
	searchText = new BehaviorSubject<string>('');
	searchResults: Observable<NamedId[]> = of([]);

	branchId$ = this.uiService.id;
	viewId$ = this.uiService.viewId;
	advancedSearchCriteria = this.advancedSearchService.advancedSearchCriteria;

	constructor(
		private artExpHttpService: ArtifactExplorerHttpService,
		private uiService: UiService,
		private artHierPathService: ArtifactHierarchyPathService,
		public dialog: MatDialog,
		private advancedSearchService: AdvancedArtifactSearchService
	) {}

	onSearchTextChange(searchTerm: Event) {
		const value = (searchTerm.target as HTMLInputElement).value;
		this.searchText.next(value);

		this.searchResults = combineLatest([
			this.branchId$,
			this.viewId$,
			this.searchText,
			this.advancedSearchCriteria,
		]).pipe(
			filter(
				([branchId, viewId, filter, _]) =>
					branchId != '-1' &&
					branchId != '0' &&
					branchId != '' &&
					viewId != '' &&
					filter != ''
			),
			debounceTime(500),
			switchMap(([branchId, viewId, filter, criteria]) =>
				this.artExpHttpService
					.getArtifactTokensByFilter(
						branchId,
						filter,
						viewId,
						criteria
					)
					.pipe(repeat({ delay: () => this.uiService.update }))
			),
			shareReplay({ bufferSize: 1, refCount: true })
		);
	}

	artifactSelected(id: string) {
		this.artHierPathService.initializePaths(id);
	}

	removeArtTypeFilter(currentCriteria: AdvancedSearchCriteria) {
		console.log('test');
		this.advancedSearchService.AdvancedSearchCriteria = {
			...currentCriteria,
			artifactTypes: [],
		};
	}

	removeAttrTypeFilter(currentCriteria: AdvancedSearchCriteria) {
		this.advancedSearchService.AdvancedSearchCriteria = {
			...currentCriteria,
			attributeTypes: [],
		};
	}

	removeExactMatchFilter(currentCriteria: AdvancedSearchCriteria) {
		this.advancedSearchService.AdvancedSearchCriteria = {
			...currentCriteria,
			exactMatch: false,
		};
	}

	getToolTip(vals: NamedId[]) {
		return vals.map((v) => v.name).join('\n');
	}
	openAdvancedSearchDialog(event?: Event) {
		event?.stopPropagation();
		this.advancedSearchCriteria
			.pipe(
				take(1),
				switchMap((criteria) =>
					this.dialog
						.open(AdvancedSearchDialogComponent, {
							data: structuredClone(criteria),
							minWidth: '40%',
							width: '40%',
						})
						.afterClosed()
						.pipe(
							take(1),
							filter((v) => v !== undefined),
							tap(
								(newCriteria) =>
									(this.advancedSearchService.AdvancedSearchCriteria =
										newCriteria)
							)
						)
				)
			)
			.subscribe();
	}
}
