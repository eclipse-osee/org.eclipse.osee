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
 *
 * Author: Eihab Khudhair (ekhudhai)
 * Task 162 - Moved Advanced Search Form implementations into Advanced Search Page
 **********************************************************************/
//import { Component, computed, signal, inject, effect } from '@angular/core'; // Author: Kris Graham (kgraha16) Task 138 - Added effect import to handle attribute column change
import { Component, computed, signal, inject, effect, ViewChild, OnInit } from '@angular/core'; // Author: Eihab Khudhair (ekhudhai) Task 178 - Preserve search state after navigating
import { toSignal } from '@angular/core/rxjs-interop'; // Author: Eihab Khudhair (ekhudhai) Task 178 - Required for artifactTypes/attributeTypes signals
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import {
	// MatAutocomplete,
	MatAutocompleteSelectedEvent,
	// MatAutocompleteTrigger,
} from '@angular/material/autocomplete';
//import { MatMenuModule } from '@angular/material/menu'; // Author: Kris Graham (kgraha16) Task 122 - Added MatMenu to stylize Column button.
import { MatMenuModule, MatMenuTrigger } from '@angular/material/menu';
import { MatButtonModule } from '@angular/material/button'; // Author: Kris Graham (kgraha16) Task 112 - Added MatButton to stylize New Search.
import { MatDividerModule } from '@angular/material/divider'; // Author: Kris Graham (kgraha16) Task 131 - Added MatDivider to divide Columns menu.
import { MatSelectModule } from '@angular/material/select'; // Author: Kris Graham (kgraha16) Task 153 - Added MatSelect to display sorting options.
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatCheckboxChange } from '@angular/material/checkbox'; // Author: Kris Graham (kgraha16) Task 139 - Added MatCheckboxChange to capture checkbox toggle event.
import { MatFormField, MatLabel, MatSuffix } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatIconButton } from '@angular/material/button';
import { ArtifactUiService } from '@osee/shared/services';
import { NamedId } from '@osee/shared/types';
import { BehaviorSubject, switchMap, combineLatest, forkJoin, of } from 'rxjs'; // Author: Eihab Khudhair (ekhudhai) Task 182 - Resolve branchType for navigation
import { Router } from '@angular/router'; //Author: Eihab Khudhair (ekhudhai) Task 175 - Implement artifact navigation logic (Router navigation to Artifact Explorer)
import { BranchPickerComponent } from '@osee/shared/components';
import { apiURL } from '@osee/environments';
/**
 * Task 162 - Updated relative import paths because logic moved from lib/components into the page folder
 * (previously ../../../../../..., now ../lib/...)
 */
import {
	AdvancedSearchCriteria,
	defaultAdvancedSearchCriteria,
} from '../lib/types/artifact-search';

/**
 * Author: Eihab Khudhair (ekhudhai)
 * Task 143 - Populate dynamic results table with query search results
 */

import { catchError, map, take } from 'rxjs/operators';
import { UiService } from '@osee/shared/services';

/**
 * Task 162 - Updated relative import path to match page location
 */
import { ArtifactExplorerHttpService } from '../lib/services/artifact-explorer-http.service';

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
	[key: string]: string; // Task 154 - Dynamic cells such as: attr_<attributeTypeId>
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

/**
 * Author: Kris Graham (kgraha16)
 * Task 153 - Create AttributeSort interface type to model sorting for Attribute Columns
 */
type AttributeSort = 'selectedFirst' | 'az' | 'za';

/**
 * Author: Eihab Khudhair (ekhudhai)
 * Task 178 - Persisted state model for Advanced Search Page
 */
type AdvancedSearchPageState = {
	data: AdvancedSearchCriteria;
	searchValue: string;
	searchResults: SearchResultRow[];
	hasSearched: boolean;
	baseColumns: ColumnConfig[];
	attributeColumns: ColumnConfig[];
	attributeSortSelect: AttributeSort;
	showSearchError: boolean;
	isLoading: boolean;
	searchInputState: 'idle' | 'valid' | 'invalid' | 'searching';
	searchValidationMessage: string;
	expandedIds: string[]; // Task 179 compatibility
};

/**
 * Author: Daria Berezianska (dvydybor)
 * Task 148 - Populate the Saved Searches Table with save search object
 */
type SavedSearch = {
	id?: number;
	title: string;
	query: string;
	columns?: string[];
	timestamp?: number;
};

@Component({
	selector: 'osee-advanced-search-page',
	imports: [
		FormsModule,
		CommonModule,
		MatFormField,
		MatLabel,
		MatInput,
		MatSuffix,
		MatIconButton,
		MatCheckboxModule,
		MatButtonModule, // Author: Kris Graham (kgraha16) Task 112 - Added MatButton to stylize New Search.
		MatMenuModule, // Author: Kris Graham (kgraha16) Task 122 - Added MatMenu to stylize Column button.
		MatDividerModule, // Author: Kris Graham (kgraha16) Task 131 - Added MatDivider to divide Columns menu.
		MatSelectModule, // Author: Kris Graham (kgraha16) Task 153 - Added MatSelect to display sorting options.
		MatIconModule,
		BranchPickerComponent,
	],
	templateUrl: './advanced-search-page.component.html',
})
export class AdvancedSearchPageComponent implements OnInit {
	private artifactService = inject(ArtifactUiService);

	private http = inject(HttpClient);

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 175 - Router used to navigate to Artifact Explorer
	 */
	private router = inject(Router);

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 143 - Inject services needed to run the same backend search used by ArtifactSearchComponent
	 */
	private uiService = inject(UiService);
	private artExpHttpService = inject(ArtifactExplorerHttpService);
	
	/**
    * Author: Sofiia Holovko (sholovko)
    * Task 183 - Disable Search button and show warning when no branch is selected
    */
   branchId = toSignal(this.uiService.id, { initialValue: '' });
   branchSelected = computed(() => {
  const id = this.branchId();
  if (typeof id !== 'string') return false;
  const trimmed = id.trim();
  return trimmed !== '' && trimmed !== '-1' && trimmed !== '0' && /^\d+$/.test(trimmed);
});

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 178 - Session storage key for preserving Advanced Search state
	 */
	private readonly ADV_SEARCH_STATE_KEY = 'osee.advancedSearchPage.state.v1';
	/**
	 * Author: Daria Berezianska (dvydybor)
	 * Task 148 - Populate the Saved Searches Table with save search object
	 */
	private readonly SAVED_SEARCH_URL = `${apiURL}/orcs/savedSearch`;

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 143 - Track whether user has performed a search (controls "no results" message)
	 */
	hasSearched = false;

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 162 - Page now owns its own AdvancedSearchCriteria state (no longer @Input because this is a standalone page)
	 */
	data: AdvancedSearchCriteria = {
		...defaultAdvancedSearchCriteria,
	};

	searchValue = '';

	/**
	 * Author: Sofiia Holovko (sholovko)
	 * Task 140 - Track search input validation state with visual feedback
	 */
	searchInputState = signal<'idle' | 'valid' | 'invalid' | 'searching'>('idle');
	searchValidationMessage = signal('');
	private readonly MIN_SEARCH_LENGTH = 2;

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 143 - Strongly typed results rows for the dynamic search table
	 */
	searchResults: SearchResultRow[] = [];

	isLoading = false; // Author: Sofiia Holovko (sholovko) Task 144 - Show loading state during search

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 174 - Make Search Result rows clickable (right-click dropdown menu)
	 *
	 */
	@ViewChild('rowContextMenuTrigger', { read: MatMenuTrigger })
	private rowContextMenuTrigger?: MatMenuTrigger;

	// Tracks the mouse position for the right-click menu anchor
	contextMenuPosition = { x: 0, y: 0 };

	// Holds the row the user right-clicked on (used by Task 175 navigation logic)
	private selectedSearchRow: SearchResultRow | null = null;

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 174 - Open right-click dropdown menu on a search result row
	 */
	onRowRightClick(event: MouseEvent, row: SearchResultRow): void {
		event.preventDefault();
		event.stopPropagation();

		this.selectedSearchRow = row;

		this.contextMenuPosition = {
			x: event.clientX,
			y: event.clientY,
		};

		// Open the Material menu at the cursor position
		this.rowContextMenuTrigger?.openMenu();
	}

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 177 - Pass correct artifact ID from row to navigation handler
	 *
	 * Ensures we always navigate using the true artifact id from the selected row.
	 */
	private resolveArtifactIdFromRow(row: SearchResultRow | null): string {
		if (!row) return '';

		// Primary source: row.id (SearchResultRow contract)
		const direct = (row.id ?? '').toString().trim();
		if (direct) return direct;

		const alt = ((row as unknown as Record<string, unknown>)['ID'] ??
			(row as unknown as Record<string, unknown>)['Id'] ??
			(row as unknown as Record<string, unknown>)['artifactId'] ??
			'') as unknown;

		const altStr = String(alt ?? '').trim();
		return altStr;
	}

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 175 - Implement artifact navigation logic (navigate to Artifact Explorer)
	 *
	 */
	private navigateToArtifactExplorer(artifactId: string): void {
		if (!artifactId) {
			console.warn('Task 175 navigation aborted: missing artifact id');
			return;
		}

		/**
		 * Author: Eihab Khudhair (ekhudhai)
		 * Task 182 - Navigate to Artifact Explorer with branch context in the URL path and artifactId/viewId in query params
		 *
		 * Artifact Explorer routes expect:
		 *   /ple/artifact/explorer/:branchType/:branchId
		 * so we must not rely on query params for branch context.
		 */
		combineLatest([
			this.uiService.id.pipe(take(1)),
			this.uiService.viewId.pipe(take(1)),
			this.uiService.type.pipe(take(1)),
		]).subscribe({
			next: ([branchId, viewId, branchType]) => {
				// Close context menu before navigating
				this.rowContextMenuTrigger?.closeMenu();

				// Preserve state so user can come back to the same Advanced Search state
				this.persistAdvancedSearchState(); // Author: Eihab Khudhair (ekhudhai) Task 178 - Preserve Advanced Search state before navigating away

				const safeBranchType = (branchType || 'working').toString();

				this.router.navigate(['/ple/artifact/explorer', safeBranchType, branchId], {
					queryParams: {
						artifactId,
						viewId,
					},
				});
			},
			error: (err: unknown) => {
				const message = err instanceof Error ? err.message : String(err);
				console.error('Failed to resolve branch/view/type for navigation:', message);
			},
		});
	}


	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 174 - Menu action placeholder for "Open in Artifact Explorer"
	 *
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 175 - Menu action: Open selected artifact in Artifact Explorer
	 */
	onOpenArtifactFromContextMenu(): void {
		/**
		 * Author: Eihab Khudhair (ekhudhai)
		 * Task 177 - Pass correct artifact ID from row to navigation handler
		 */
		const artifactId = this.resolveArtifactIdFromRow(this.selectedSearchRow);

		if (!artifactId) {
			console.warn('Task 177: missing artifact id for selected row', this.selectedSearchRow);
			return;
		}

		this.navigateToArtifactExplorer(artifactId);
	}

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

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 154 - Format attribute.value for display in table cells
	 */
	private formatAttrValue(value: unknown): string {
		if (value === null || value === undefined) return '';
		if (Array.isArray(value)) return value.map((v) => String(v)).join(', ');
		if (typeof value === 'object') return JSON.stringify(value);
		return String(value);
	}

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 154 - Safely extract attribute type id from various backend shapes
	 */
	private extractAttrTypeId(a: unknown): string {
		const attr = a as Record<string, unknown>;

		const typeId = attr['typeId'];
		if (typeof typeId === 'string') return typeId;

		// Sometimes typeId is an object token
		if (typeId && typeof typeId === 'object') {
			const t = typeId as Record<string, unknown>;
			if (typeof t['id'] === 'string') return t['id'];
			if (typeof t['idString'] === 'string') return t['idString'];
			if (typeof t['idIntValue'] === 'number') return String(t['idIntValue']);
		}

		return '';
	}

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 154 - Safely extract attribute value from various backend shapes
	 */
	private extractAttrValue(a: unknown): unknown {
		const attr = a as Record<string, unknown>;
		if ('value' in attr) return attr['value'];
		if ('displayValue' in attr) return attr['displayValue'];
		if ('stringValue' in attr) return attr['stringValue'];
		if ('values' in attr) return attr['values'];
		return '';
	}

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 154 - Map backend attribute type id to the UI column key (attr_<id>)
	 *
	 * This prevents key mismatches when backend attribute type ids differ in shape.
	 */
	private buildAttrColumnKeyMap(): Map<string, string> {
		const map = new Map<string, string>();

		// attributeColumns() already contains keys like "attr_<id>"
		for (const col of this.attributeColumns()) {
			// Extract "<id>" from "attr_<id>"
			const id = col.key.startsWith('attr_') ? col.key.substring('attr_'.length) : '';
			if (id) map.set(id, col.key);
		}

		return map;
	}

	public showSearchError = false;
	/**
	 * Author: Daria Berezianska (dvydybor)
	 * Task 148 - Populate the Saved Searches Table with save search object
	 */
	savedSearches: SavedSearch[] = [];
	savedSearchesLoading = false;
	savedSearchesErrorMessage = '';

	// Save status flags for Save Search operation
	saveInProgress = false;
	saveErrorMessage = '';

	// Author: Kris Graham (kgraha16) - Created to have a state model of expanded rows.
	expanded = new Set<string>();

	/**
	 * Author: Kris Graham (kgraha16)
	 * Task 179 - Helper method to expand relations column and track which rows are expanded.
	 */
	expandToggle(row: SearchResultRow) {
		if(this.expanded.has(row.id)) {
			this.expanded.delete(row.id);
		} else {
			this.expanded.add(row.id);
			forkJoin({
				branchId: this.uiService.id.pipe(take(1)),
				viewId: this.uiService.viewId.pipe(take(1)),
			}).subscribe(({ branchId, viewId }) => {
				this.loadRelations(row, String(branchId), String(viewId));
			});
		}
	}

	/**
	 * Author: Kris Graham (kgraha16)
	 * Task 179 - Helper method to expand relations column and track which rows are expanded.
	 */
	isExpanded(row: SearchResultRow) {
		return this.expanded.has(row.id);
	}

	/**
	 * Author: Kris Graham (kgraha16)
	 * Task 131 - Create base available columns for Column customization button.
	 */
	baseColumns = signal<ColumnConfig[]>([
		{ key: 'id', label: 'ID', visible: true, locked: true },
		{ key: 'type', label: 'Type', visible: true, locked: false },
	]);

	/**
	 * Author: Kris Graham (kgraha16)
	 * Task 131 - Create available attribute columns for Column customization button.
	 * Task 138 - Change attribute column to be a signal to update visibility for binding
	 * and build a constructor/effect.
	 */
	attributeColumns = signal<ColumnConfig[]>([]);

	constructor() {
		effect(() => {
			const attrTypes = this.allAttributeTypes() ?? [];
			if (attrTypes.length === 0) return;
			this.attributeColumns.update((existing) =>
				attrTypes.map((attr) => {
					const key = `attr_${String(attr.id)}`;
					const prev = existing.find((c) => c.key === key);
					return {
						key,
						label: attr.name,
						visible: prev?.visible ?? attr.name.toLowerCase() === 'name',
					};
				})
			);
		});
	}

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 178 - Restore preserved Advanced Search state on page load
	 */
	ngOnInit(): void {
		this.restoreAdvancedSearchState();
		/**
		 * Author: Daria Berezianska (dvydybor)
		 * Task 148 - Populate the Saved Searches Table with save search object
		 */
		this.loadSavedSearches();
	}

	/**
	 * Author: Daria Berezianska (dvydybor)
	 * Task 148 - Populate the Saved Searches Table with save search object
	 */
	private loadSavedSearches(): void {
		this.savedSearchesLoading = true;
		this.savedSearchesErrorMessage = '';

		this.http
			.get<SavedSearch[]>(this.SAVED_SEARCH_URL)
			.pipe(take(1))
			.subscribe({
				next: (savedSearches) => {
					this.savedSearches = Array.isArray(savedSearches) ? savedSearches : [];
					this.savedSearchesLoading = false;
				},
				error: (err: unknown) => {
					this.savedSearches = [];
					this.savedSearchesLoading = false;
					this.savedSearchesErrorMessage =
						err instanceof Error ? err.message : String(err);
					console.error('Failed to load saved searches:', this.savedSearchesErrorMessage);
				},
			});
	}

	/**
	 * Author: Daria Berezianska (dvydybor)
	 * Task 148 - Populate the Saved Searches Table with save search object
	 */
	formatSavedSearchTimestamp(timestamp?: number): string {
		if (!timestamp) return '-';
		const date = new Date(timestamp);
		if (Number.isNaN(date.getTime())) return '-';
		return date.toLocaleString();
	}

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 178 - Persist current Advanced Search state to sessionStorage
	 */
	private persistAdvancedSearchState(): void {
		try {
			const state: AdvancedSearchPageState = {
				data: this.data,
				searchValue: this.searchValue,
				searchResults: this.searchResults,
				hasSearched: this.hasSearched,
				baseColumns: this.baseColumns(),
				attributeColumns: this.attributeColumns(),
				attributeSortSelect: this.attributeSortSelect(),
				showSearchError: this.showSearchError,
				isLoading: this.isLoading,
				searchInputState: this.searchInputState(),
				searchValidationMessage: this.searchValidationMessage(),
				expandedIds: Array.from(this.expanded ?? new Set<string>()),
			};

			sessionStorage.setItem(this.ADV_SEARCH_STATE_KEY, JSON.stringify(state));
		} catch (e) {
			console.warn('failed to persist advanced search state', e);
		}
	}

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 178 - Restore Advanced Search state from sessionStorage (if available)
	 */
	private restoreAdvancedSearchState(): void {
		try {
			const raw = sessionStorage.getItem(this.ADV_SEARCH_STATE_KEY);
			if (!raw) return;

			const parsed = JSON.parse(raw) as Partial<AdvancedSearchPageState>;

			if (parsed.data) this.data = parsed.data;
			if (typeof parsed.searchValue === 'string') this.searchValue = parsed.searchValue;
			if (Array.isArray(parsed.searchResults)) this.searchResults = parsed.searchResults;
			if (typeof parsed.hasSearched === 'boolean') this.hasSearched = parsed.hasSearched;

			if (Array.isArray(parsed.baseColumns)) this.baseColumns.set(parsed.baseColumns);
			if (Array.isArray(parsed.attributeColumns)) this.attributeColumns.set(parsed.attributeColumns);
			if (parsed.attributeSortSelect) this.attributeSortSelect.set(parsed.attributeSortSelect);

			if (typeof parsed.showSearchError === 'boolean') this.showSearchError = parsed.showSearchError;
			if (typeof parsed.isLoading === 'boolean') this.isLoading = parsed.isLoading;

			if (parsed.searchInputState) this.searchInputState.set(parsed.searchInputState);
			if (typeof parsed.searchValidationMessage === 'string')
				this.searchValidationMessage.set(parsed.searchValidationMessage);

			// Task 179 compatibility - restore expanded row state (if present)
			if (Array.isArray(parsed.expandedIds)) {
				this.expanded = new Set<string>(parsed.expandedIds);
			}
		} catch (e) {
			console.warn('Task 178: failed to restore advanced search state', e);
		}
	}

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 178 - Clear preserved Advanced Search state
	 */
	private clearAdvancedSearchState(): void {
		try {
			sessionStorage.removeItem(this.ADV_SEARCH_STATE_KEY);
		} catch {
			// ignore
		}
	}

	/**
	 * Author: Kris Graham (kgraha16)
	 * Task 139 - Create visible columns to capture the visbility state of checkboxes
	 * in the Columns menu for implementation into the Search Result Table.
	 * Task 179 - Add Relations/Traceability column to visible columns of the search
	 * results table.
	 */
	visibleColumns = computed<ColumnConfig[]>(() => [
		{ key: 'relations', label: 'REL', visible: true, locked: true },
		...this.baseColumns(),
		...this.attributeColumns(),
	].filter((col) => col.visible));

	attributeSortSelect = signal<AttributeSort>('selectedFirst');

	sortedAttributeColumns = computed<ColumnConfig[]>(() => {
		const cols = [...this.attributeColumns()];
		const sortSelect = this.attributeSortSelect();

		switch (sortSelect) {
			case 'selectedFirst':
				return cols.sort((a, b) => {
					if (a.visible !== b.visible) {
						return Number(b.visible) - Number(a.visible);
					}
					return a.label.localeCompare(b.label);
				});
			case 'za':
				return cols.sort((a, b) => b.label.localeCompare(a.label));
			case 'az':
			default:
				return cols.sort((a, b) => a.label.localeCompare(b.label));
		}
	});

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
			this.data.artifactTypes.filter((a) => a.id === event.option.value.id).length === 0
		) {
			this.data.artifactTypes.push(event.option.value);
		}
		this.artTypesFilter.set('');
		this._selectedArtifactTypes.next(this.data.artifactTypes);
	}

	removeArtType(artType: NamedId) {
		this.data.artifactTypes = this.data.artifactTypes.filter((a) => a.id !== artType.id);
		this.artTypesFilter.set('');
		this._selectedArtifactTypes.next(this.data.artifactTypes);
	}

	attributeTypes = toSignal(
		this._selectedArtifactTypes.pipe(
			switchMap((artTypes) => this.artifactService.getAttributeTypes(artTypes))
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
			this.data.attributeTypes.filter((a) => a.id === event.option.value.id).length === 0
		) {
			this.data.attributeTypes.push(event.option.value);
		}
		this.attrTypesFilter.set('');
	}

	removeAttrType(attrType: NamedId) {
		this.data.attributeTypes = this.data.attributeTypes.filter((a) => a.id !== attrType.id);
		this.attrTypesFilter.set('');
	}

	/**
	 * Author: Kris Graham (kgraha16)
	 * Task 131 - Create signal to get all attribute types for Columns menu checkboxes.
	 */
	allAttributeTypes = toSignal(this.artifactService.allAttributeTypes, { initialValue: [] });

	compareWith(o1: NamedId, o2: NamedId) {
		return o1.id === o2.id;
	}

	displayWith(val: NamedId) {
		return val?.name;
	}

	/**
	 * Author: Daria Berezianska (dvydybor)
	 * Task 146 - Implement the Save Search button behavior to save a search and prevent a save if required data is missing
	 */
	onSaveSearch(): void {
		const title = (this.data.searchTitle || '').trim();
		if (!title) {
			this.saveErrorMessage = 'Search title is required';
			return;
		}

		this.saveErrorMessage = '';
		this.saveInProgress = true;
		const query = (this.searchValue || '').trim();
		const columns = this.visibleColumns().map((c) => c.key);

		this.artifactService.saveSearch(title, query, columns).pipe(take(1)).subscribe({
			next: () => {
				this.saveInProgress = false;
				this.loadSavedSearches();
			},
			error: (err: unknown) => {
				this.saveInProgress = false;
				this.saveErrorMessage = err instanceof Error ? err.message : String(err);
				console.error('Save search failed:', this.saveErrorMessage);
			},
		});
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
		this.searchInputState.set('searching');
		// Author: Sofiia Holovko (sholovko) Task 144 - Set loading state
		this.isLoading = true;

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
											.getartifactWithRelations(branchId, t.id, viewId, true)
											.pipe(
												catchError(() =>
													// Task 143 - do not fail whole table if one artifact fails
													of(null)
												)
											)
									)
								).pipe(
									map((details) => details.filter(Boolean) as artifactWithRelations[])
								);
							}),
							map((details: artifactWithRelations[]) => {
								// Task 154 - Debug: compare column keys vs row keys
								const cols = this.visibleColumns().map((c) => c.key);
								console.log('DEBUG visible column keys:', cols.slice(0, 15));

								const visibleColPairs = this.visibleColumns().map((c) => ({
									key: c.key,
									label: c.label,
								}));

								console.log('DEBUG visible columns (key->label):', visibleColPairs);
								console.table(visibleColPairs);

								const attrKeyMap = this.buildAttrColumnKeyMap();

								const rows: SearchResultRow[] = details.map((d) => {
									const row: SearchResultRow = {
										type: d.typeName ?? '',
										id: d.id ?? '',
										name: d.name ?? '',
									};
									console.log('DEBUG attributes sample:', d.id, d.attributes);

									// Task 154 - populate "attr_<id>" cells using ONLY the UI column keys (checkbox keys)
									(d.attributes ?? []).forEach((a) => {
										const typeId = this.extractAttrTypeId(a);
										if (!typeId) return;

										// Map backend attribute type id -> UI column key (attr_<id>)
										const colKey = attrKeyMap.get(String(typeId));
										if (!colKey) return; // Do NOT fallback, prevents mismatched keys + blanks

										const rawVal = this.extractAttrValue(a);
										row[colKey] = this.formatAttrValue(rawVal);
									});

									console.log(
										'DEBUG sample value for visible attr columns:',
										d.id,
										visibleColPairs
											.filter((c) => c.key.startsWith('attr_'))
											.map((c) => ({ key: c.key, label: c.label, value: row[c.key] }))
									);

									console.log(
										'DEBUG row keys sample:',
										d.id,
										Object.keys(row).filter((k) => k.startsWith('attr_')).slice(0, 15)
									);
									return row;
								});
								return rows;
							})
						)
				)
			)
			.subscribe({
				next: (rows: SearchResultRow[]) => {
					this.searchResults = rows;
					this.isLoading = false; // Author: Sofiia Holovko (sholovko) Task 144 - Clear loading state
					// Author: Sofiia Holovko (sholovko) Task 140 - Reset state after successful search
					this.searchInputState.set('valid');
					this.searchValidationMessage.set('Search complete');
				},
				error: (err: unknown) => {
					const message = err instanceof Error ? err.message : String(err);
					console.error('Advanced search failed:', message);
					this.searchResults = [];
					this.isLoading = false; // Author: Sofiia Holovko (sholovko) Task 144 - Clear loading state on error
					// Author: Sofiia Holovko (sholovko) Task 140 - Show error state on search failure
					this.searchInputState.set('invalid');
					this.searchValidationMessage.set('Search failed. Please try again.');
				},
			});
	}

	onSearchValueChange(): void {
		this.showSearchError = false; // Author: Mariia Gordieieva (mgordiei) Task 156 - Improve empty-search inline validation UX.
		//Author: Sofiia Holovko (sholovko)
		//Task 140 - Enhanced search input state handling with real-time validation and visual feedback
		const value = (this.searchValue || '').trim();

		// Validate input and update state
		if (value.length === 0) {
			this.searchInputState.set('idle');
			this.searchValidationMessage.set('');
		} else if (value.length < this.MIN_SEARCH_LENGTH) {
			this.searchInputState.set('invalid');
			this.searchValidationMessage.set(`Minimum ${this.MIN_SEARCH_LENGTH} characters required`);
		} else {
			this.searchInputState.set('valid');
			this.searchValidationMessage.set('Ready to search');
		}
	}

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 150 - Prevent Enter key from triggering Clear (X) in inputs
	 *
	 * Stops default Enter behavior so it does NOT activate the suffix clear button.
	 *
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 151 - Trigger Advanced Search when pressing Enter key
	 */
	onSearchEnter(event: Event): void {
		event.preventDefault();
		event.stopPropagation();
		// Task 151 - Enter key triggers search (same rule as Search button)
		if ((this.searchValue || '').trim().length > 0) {
			this.onSearch();
		} else {
			// keep existing UX consistent with onSearch() empty behavior
			this.showSearchError = true;
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
		//Author: Sofiia Holovko (sholovko)
		//Task 140 - Reset validation state on clear
		this.showSearchError = false;
		this.searchInputState.set('idle');
		this.searchValidationMessage.set('');
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
		this.data = { ...defaultAdvancedSearchCriteria };
		this.searchValue = '';
		this.data.searchTitle = '';

		//Author: Sofiia Holovko (sholovko) Task 145 - Clear search results on new search
		this.searchResults = [];

		/**
		 * Author: Eihab Khudhair (ekhudhai)
		 * Task 143 - Reset search performed flag so "no results" message doesn't show on a fresh form
		 */
		this.hasSearched = false;
		/**
		 * Author: Eihab Khudhair (ekhudhai)
		 * Task 178 - Clear preserved state when starting a new search
		 */
		this.clearAdvancedSearchState();
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
		this.baseColumns.update((cols) =>
			cols.map((c) => (c.key === col.key ? { ...c, visible: event.checked } : c))
		);
	}

	/**
	 * Author: Kris Graham (kgraha16)
	 * Task 139 - Create helper function to capture change from a checkbox toggle. Captures
	 * the Mutability within the Attributes Columns.
	 */
	onAttributeColumnToggle(col: ColumnConfig, event: MatCheckboxChange) {
		this.attributeColumns.update((cols) =>
			cols.map((c) => (c.key === col.key ? { ...c, visible: event.checked } : c))
		);
	}

	/**
	 * Author: Mariia Gordieieva
	 * Task 168 - Implement "Clear all selections" button for Advanced Search
	 */
  clearAllColumnSelections(): void {
    this.baseColumns.update((cols) =>
      cols.map((c) => (c.locked ? { ...c, visible: true } : { ...c, visible: false }))
    );

    // Attribute columns: clear all
    this.attributeColumns.update((cols) => cols.map((c) => ({ ...c, visible: false })));
  }

	/**
	 * Author: Kris Graham (kgraha16)
	 * Task 138 - Create helper function to help bind the columns to the row search results.
	 */
	getCellValue(row: SearchResultRow, col: ColumnConfig): string {
		const v = (row as Record<string, unknown>)[col.key];
		return v === null || v === undefined ? '' : String(v);
	}
	
	/**
	 * Author: Kris Graham (kgraha16)
	 * Task 180 - Map a list of related artifacts to the searched artifact
	 */
	relatedNames = new Map<string, string[]>();
	
	loadRelations(result: SearchResultRow, branchId: string, viewId: string) {
		this.artExpHttpService
		.getartifactWithRelations(branchId, result.id, viewId, true)
		.pipe(
			map((response: artifactWithRelations) => 
				(response.relations ?? [])
				.flatMap(rel => rel.relationSides ?? [])
				.flatMap(side => side.artifacts ?? [])
				.map(artifact => artifact.name ?? '')
			)
		)
		.subscribe((names: string[]) => {
			this.relatedNames.set(result.id, names);
		});
	}
}
