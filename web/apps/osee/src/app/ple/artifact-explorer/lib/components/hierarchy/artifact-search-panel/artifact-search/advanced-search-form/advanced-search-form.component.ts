/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { Component, Input, computed, signal, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import {
	// MatAutocomplete,
	MatAutocompleteSelectedEvent,
	// MatAutocompleteTrigger,
} from '@angular/material/autocomplete';
import { MatMenuModule } from '@angular/material/menu'; // Author: Kris Graham (kgraha16) Task 122 - Added MatMenu to stylize Column button.
import { MatButtonModule } from '@angular/material/button'; // Author: Kris Graham (kgraha16) Task 112 - Added MatButton to stylize New Search.
import { MatDividerModule } from '@angular/material/divider'; // Author: Kris Graham (kgraha16) Task 131 - Added MatDivider to divide Columns menu.
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatCheckboxChange } from '@angular/material/checkbox'; // Author: Kris Graham (kgraha16) Task 139 - Added MatCheckboxChange to capture checkbox toggle event.
// import { MatChip, MatChipRemove, MatChipSet } from '@angular/material/chips';
// import { MatOption } from '@angular/material/core';
import { MatFormField, MatLabel, MatSuffix } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatIconButton } from '@angular/material/button';
import { ArtifactUiService } from '@osee/shared/services';
import { NamedId } from '@osee/shared/types';
import { BehaviorSubject, switchMap } from 'rxjs';
import {
	AdvancedSearchCriteria,
	defaultAdvancedSearchCriteria,
} from '../../../../../types/artifact-search';
/**
 * Author: Eihab Khudhair (ekhudhai)
 * Task 143 - Populate dynamic results table with query search results
 */
import { forkJoin, of } from 'rxjs';
import { catchError, map, take } from 'rxjs/operators';
import { UiService } from '@osee/shared/services';
import { ArtifactExplorerHttpService } from '../../../../../services/artifact-explorer-http.service';
/**
 * Author: Eihab Khudhair (ekhudhai)
 * Task 143 - Use existing typed models instead of any
 */
import {
	artifactTokenWithIcon,
	artifactWithRelations,
} from '@osee/artifact-with-relations/types';

/**
 * Author: Eihab Khudhair (ekhudhai)
 * Task 143 - UI row model for Search Results table
 */
type SearchResultRow = {
	type: string;
	id: string;
	name: string;
	attributes: string;
};

/** 
 * Author: Kris Graham (kgraha16)
 * Task 139 - Create ColumnConfig interface type to model Core and Attribute Columns
 */
type ColumnConfig = {
	key: string;
	label: string;
	visible: boolean;
	locked?: boolean;
};

@Component({
	selector: 'osee-advanced-search-form',
	imports: [
		FormsModule,
		CommonModule,
		MatFormField,
		MatLabel,
		// MatChipSet,
		// MatChip,
		// MatChipRemove,
		MatInput,
		MatSuffix,
		MatIconButton, 
		// MatAutocomplete,
		// MatAutocompleteTrigger,
		// MatOption,
		MatCheckboxModule,
		MatButtonModule, // Author: Kris Graham (kgraha16) Task 112 - Added MatButton to stylize New Search.
		MatMenuModule, // Author: Kris Graham (kgraha16) Task 122 - Added MatStrokedButton to stylize Column button.
		MatDividerModule, // Author: Kris Graham (kgraha16) Task 131 - Added MatDivider to divide Columns menu.
		MatIconModule,
	],
	templateUrl: './advanced-search-form.component.html',
})

export class AdvancedSearchFormComponent {
	private artifactService = inject(ArtifactUiService);

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 143 - Inject services needed to run the same backend search used by ArtifactSearchComponent
	 */
	private uiService = inject(UiService);
	private artExpHttpService = inject(ArtifactExplorerHttpService);

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 143 - Track whether user has performed a search (controls "no results" message)
	 */
	hasSearched = false;

	@Input() data: AdvancedSearchCriteria = {
		...defaultAdvancedSearchCriteria,
	};

	searchValue = '';
	//searchResults: any[] = [];  // Author: Sofiia Holovko (sholovko) Task 145 - Handle "no results found" state
	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 143 - Strongly typed results rows for the dynamic search table
	 */
	searchResults: SearchResultRow[] = [];

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 143 - Local UI model for rendering search results table
	 */
	private mapAttributes(attrs: unknown[]): string {
		if (!attrs || attrs.length === 0) return '';

		return attrs
			.map((a: unknown) => {
				const obj =
					typeof a === 'object' && a !== null
						? (a as Record<string, unknown>)
						: {};

				const key =
					(typeof obj['name'] === 'string' && obj['name']) ||
					(typeof obj['typeName'] === 'string' && obj['typeName']) ||
					(typeof obj['id'] === 'string' && obj['id']) ||
					'attr';

				let val = '';

				if (typeof obj['value'] === 'string') {
					val = obj['value'];
				} else if (Array.isArray(obj['values'])) {
					val = (obj['values'] as unknown[])
						.map((v) => String(v))
						.join(', ');
				} else if (typeof obj['stringValue'] === 'string') {
					val = obj['stringValue'];
				} else if (typeof obj['displayValue'] === 'string') {
					val = obj['displayValue'];
				}

				return val !== '' ? `${key}=${val}` : key;
			})
			.join('; ');
	}

	public showSearchError = false;

	/** 
	* Author: Kris Graham (kgraha16)
	* Task 131 - Create base available columns for Column customization button.
	*/
	baseColumns: ColumnConfig[] = [
		{ key: 'id', label: 'ID', visible: true, locked: true },
		{ key: 'name', label: 'Name', visible: true, locked: false },
		{ key: 'type', label: 'Type', visible: true, locked: false }
	];
	
	/** 
	 * Author: Kris Graham (kgraha16)
	 * Task 131 - Create available attribute columns for Column customization button.
	 */
	attributeColumns = computed<ColumnConfig[]>(() =>
		this.allAttributeTypes().map(attr => ({
			key: `attr_${attr.id}`,
			label: attr.name,
			visible: false,
		}))
	);
	
	/** 
	 * Author: Kris Graham (kgraha16)
	 * Task 139 - Create visible columns to capture the visbility state of checkboxes
	 * in the Columns menu for implementation into the Search Result Table.
	 */
	visibleColumns = computed<ColumnConfig[]>(() => [
		...this.baseColumns.filter(c => c.visible),
		...this.attributeColumns().filter(c => c.visible),
	]);
	
	artifactTypes = toSignal(this.artifactService.allArtifactTypes);
	_selectedArtifactTypes = new BehaviorSubject<NamedId[]>([]);
	artTypesFilter = signal('');
	filteredArtTypes = computed(() =>
		this.artifactTypes()?.filter((a) =>
			a.name.toLowerCase().includes(this.artTypesFilter().toLowerCase())
		)
	);
	updateArtTypesFilter(value: string) {
		this.artTypesFilter.set(value);
	}
	selectArtType(event: MatAutocompleteSelectedEvent) {
		if (
			this.data.artifactTypes.filter(
				(a) => a.id === event.option.value.id
			).length === 0
		) {
			this.data.artifactTypes.push(event.option.value);
		}
		this.artTypesFilter.set('');
		this._selectedArtifactTypes.next(this.data.artifactTypes);
	}
	removeArtType(artType: NamedId) {
		this.data.artifactTypes = this.data.artifactTypes.filter(
			(a) => a.id !== artType.id
		);
		this.artTypesFilter.set('');
		this._selectedArtifactTypes.next(this.data.artifactTypes);
	}

	attributeTypes = toSignal(
		this._selectedArtifactTypes.pipe(
			switchMap((artTypes) =>
				this.artifactService.getAttributeTypes(artTypes)
			)
		)
	);
	attrTypesFilter = signal('');
	filteredAttrTypes = computed(() =>
		this.attributeTypes()?.filter((a) =>
			a.name.toLowerCase().includes(this.attrTypesFilter().toLowerCase())
		)
	);
	updateAttrTypesFilter(value: string) {
		this.attrTypesFilter.set(value);
	}
	selectAttrType(event: MatAutocompleteSelectedEvent) {
		if (
			this.data.attributeTypes.filter(
				(a) => a.id === event.option.value.id
			).length === 0
		) {
			this.data.attributeTypes.push(event.option.value);
		}
		this.attrTypesFilter.set('');
	}
	removeAttrType(attrType: NamedId) {
		this.data.attributeTypes = this.data.attributeTypes.filter(
			(a) => a.id !== attrType.id
		);
		this.attrTypesFilter.set('');
	}
	
	/** 
		* Author: Kris Graham (kgraha16)
		* Task 131 - Create signal to get all attribute types for Columns menu checkboxes.
		*/
	allAttributeTypes = toSignal(
		this.artifactService.allAttributeTypes,
		{ initialValue: [] }
	);

	compareWith(o1: NamedId, o2: NamedId) {
		return o1.id === o2.id;
	}
	displayWith(val: NamedId) {
		return val?.name;
	}
	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 107 - Create save button for Advanced Search Options
	 *
	 * Placeholder handler for the Save Search button.
	 * Future work: integrate with a service to persist the current criteria.
	 */
	onSaveSearch(): void {
		// For now just log the current criteria so we can verify the wiring.
		console.log('Save Search clicked with criteria:', this.data);
	}

	/**
	 * Author: Daria Berezianska (dvydybor)
	 * Task 125 - Change the input alarm from being a popup to being a red written text under the search input bar
	 * 
	 * Handler for the search button in the Advanced Search Options modal.
	 * If the search field is empty show an alert under the field prompting the user.
	 *
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 143 - Populate the Dynamic Search Results Table with the query search results
	 */
	onSearch(): void {
		const filter = (this.searchValue || '').trim();

		if (filter.length === 0) {
			this.showSearchError = true;
			return;
		}

		this.showSearchError = false;

		// Task 143 - mark that a search was performed (controls "no results" row)
		this.hasSearched = true;

		// Task 143 - clear results before running a new search
		this.searchResults = [];

		forkJoin({
			branchId: this.uiService.id.pipe(take(1)),
			viewId: this.uiService.viewId.pipe(take(1)),
		})
			.pipe(
				switchMap(({ branchId, viewId }) =>
					this.artExpHttpService
						.getArtifactTokensByFilter(
							branchId,
							filter,
							viewId,
							100, // pageSize
							1, // pageNum
							this.data
						)
						.pipe(
							switchMap((tokens: artifactTokenWithIcon[]) => {
								if (!tokens || tokens.length === 0) {
									return of([] as artifactWithRelations[]);
								}

								return forkJoin(
									tokens.map((t) =>
										this.artExpHttpService
											.getartifactWithRelations(
												branchId,
												t.id,
												viewId,
												true
											)
											.pipe(
												catchError(() =>
													// Task 143 - do not fail whole table if one artifact fails
													of(null)
												)
											)
									)
								).pipe(
									map(
										(details) =>
											(details.filter(Boolean) as artifactWithRelations[])
									)
								);
							}),
							map((details: artifactWithRelations[]) => {
								const rows: SearchResultRow[] = details.map((d) => ({
									type: d.typeName ?? '',
									id: d.id ?? '',
									name: d.name ?? '',
									attributes: this.mapAttributes(d.attributes ?? []),
								}));
								return rows;
							})
						)
				)
			)
			.subscribe({
				next: (rows: SearchResultRow[]) => {
					this.searchResults = rows;
				},
				error: (err: unknown) => {
					const message = err instanceof Error ? err.message : String(err);
					console.error('Advanced search failed:', message);
					this.searchResults = [];
				},
			});
	}

	onSearchValueChange(): void {
		if (this.searchValue && this.searchValue.trim().length > 0) {
			this.showSearchError = false;
		}
	}

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 128 - Add clear (X) action to Advanced Search input
	 *
	 * Clears the search field and hides any inline error.
	 */
	clearSearch(): void {
	this.searchValue = '';
	this.showSearchError = false;
	}

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 129 - Add clear (X) action to Search Title input
	 *
	 * Clears the Search Title field.
	 */
	clearSearchTitle(): void {
		this.data.searchTitle = '';
	}
	
	/** 
	 * Author: Kris Graham (kgraha16)
	 * Task 113 - Create functionality for clicking New Search button in
	 * the advanced search form.
	 */
	onNewSearch(): void {
		this.data={...defaultAdvancedSearchCriteria};
		this.searchValue='';
		this.data.searchTitle='';
		
		//Author: Sofiia Holovko (sholovko) Task 145 - Clear search results on new search
		 this.searchResults=[];
		/**
		 * Author: Eihab Khudhair (ekhudhai)
		 * Task 143 - Reset search performed flag so "no results" message doesn't show on a fresh form
		 */
		this.hasSearched = false;
	}
	
	/** 
	 * Author: Kris Graham (kgraha16)
	 * Task 139 - Create helper function to capture change from a checkbox toggle. Captures
	 * the Mutability within the Core Columns.
	 */
	onCoreColumnToggle(col: ColumnConfig, event: MatCheckboxChange) {
		if (col.locked) {
			return;
		}
		col.visible = event.checked;
	}
	
	/** 
	 * Author: Kris Graham (kgraha16)
	 * Task 139 - Create helper function to capture change from a checkbox toggle. Captures
	 * the Mutability within the Attributes Columns.
	 */
	onAttributeColumnToggle(col: ColumnConfig, event: MatCheckboxChange) {
		col.visible = event.checked;
	}
}