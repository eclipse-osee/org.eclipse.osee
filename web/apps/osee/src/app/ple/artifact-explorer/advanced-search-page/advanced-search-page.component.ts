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
import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import { HttpClient } from '@angular/common/http';
import {
	MatAutocomplete,
	MatAutocompleteSelectedEvent,
	MatAutocompleteTrigger,
} from '@angular/material/autocomplete';
//import { MatMenuModule } from '@angular/material/menu'; // Author: Kris Graham (kgraha16) Task 122 - Added MatMenu to stylize Column button.
import { MatMenuModule, MatMenuTrigger } from '@angular/material/menu';
import { MatDialog, MatDialogModule } from '@angular/material/dialog'; // Author: Eihab Khudhair (ekhudhai) Task 207 - Open Mass Edit dialog
import { MatButtonModule } from '@angular/material/button'; // Author: Kris Graham (kgraha16) Task 112 - Added MatButton to stylize New Search.
import { MatDividerModule } from '@angular/material/divider'; // Author: Kris Graham (kgraha16) Task 131 - Added MatDivider to divide Columns menu.
import { MatSelectModule } from '@angular/material/select'; // Author: Kris Graham (kgraha16) Task 153 - Added MatSelect to display sorting options.
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatCheckboxChange } from '@angular/material/checkbox'; // Author: Kris Graham (kgraha16) Task 139 - Added MatCheckboxChange to capture checkbox toggle event.
import { MatChip, MatChipRemove, MatChipSet } from '@angular/material/chips';
import { MatOption } from '@angular/material/core';
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
import { MassEditDialogComponent, MassEditDialogResult } from './mass-edit-dialog.component'; // Author: Eihab Khudhair (ekhudhai) Task 207
import {
    SavedSearchesDialogComponent,
	SavedSearchesDialogResult,
} from './saved-searches-dialog.component';


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
	/**
	 * Author: Daria Berezianska(dvydybor)
	 * Task 198 - Implement drag-and-drop for table headers (column reordering)
	 */
	columnOrder: string[];
};

/**
 * Author: Daria Berezianska (dvydybor)
 * Task 148 - Populate the Saved Searches Table with save search object
 */
type SavedSearch = {
	id?: number;
	title: string;
	query: string;
	timestamp?: number;
	artifactTypes?: NamedId[];
	attributeTypes?: NamedId[];
	exactMatch?: boolean;
	searchById?: boolean;
};

@Component({
	selector: 'osee-advanced-search-page',
	imports: [
		FormsModule,
		CommonModule,
		DragDropModule,
		MatAutocomplete,
		MatAutocompleteTrigger,
		MatFormField,
		MatLabel,
		MatInput,
		MatSuffix,
		MatIconButton,
		MatCheckboxModule,
		MatChipSet,
		MatChip,
		MatChipRemove,
		MatButtonModule, // Author: Kris Graham (kgraha16) Task 112 - Added MatButton to stylize New Search.
		MatMenuModule, // Author: Kris Graham (kgraha16) Task 122 - Added MatMenu to stylize Column button.
		MatDialogModule, // Author: Eihab Khudhair (ekhudhai) Task 207 - Provide dialog providers for Mass Edit
		MatDividerModule, // Author: Kris Graham (kgraha16) Task 131 - Added MatDivider to divide Columns menu.
		MatSelectModule, // Author: Kris Graham (kgraha16) Task 153 - Added MatSelect to display sorting options.
		MatOption,
		MatIconModule,
		BranchPickerComponent,
	],
	templateUrl: './advanced-search-page.component.html',
	styles: [
		`
			.column-header-cell {
				transition:
					background-color 160ms ease,
					box-shadow 160ms ease,
					transform 160ms ease;
				position: relative;
				padding-inline: 0.85rem;
			}

			.column-drop-row .column-header-cell {
				border-bottom: 1px solid #dbe3ee;
			}

			.column-header-cell:hover {
				filter: brightness(0.98);
			}

			.column-header-draggable {
				min-width: 150px;
			}

			.column-drag-handle {
				display: inline-flex;
				align-items: center;
				gap: 0.3rem;
				padding: 0.2rem 0.5rem;
				border-radius: 9999px;
				cursor: grab;
				color: inherit;
				border: 1px solid transparent;
				transition:
					background-color 140ms ease,
					border-color 140ms ease,
					transform 140ms ease;
			}

			.column-drag-handle:hover {
				background: rgba(37, 99, 235, 0.08);
				border-color: rgba(37, 99, 235, 0.2);
			}

			.column-drag-handle:active {
				cursor: grabbing;
				transform: translateY(1px) scale(0.99);
				background: rgba(37, 99, 235, 0.14);
			}

			.column-drag-icon {
				font-size: 16px;
				width: 16px;
				height: 16px;
				color: currentColor;
			}

			.column-locked-label {
				color: inherit;
				font-weight: 600;
			}

			.column-header-cell.cdk-drag-dragging {
				opacity: 0.35;
				background: #ffffff;
			}

			:host-context(.dark) .column-header-cell.cdk-drag-dragging {
				background: #000000;
				color: #ffffff;
			}

			.cdk-drop-list-dragging
				.column-header-cell:not(.cdk-drag-placeholder) {
				transition: transform 220ms cubic-bezier(0.2, 0.8, 0.2, 1);
			}

			.column-drag-preview {
				display: inline-flex;
				align-items: center;
				gap: 0.4rem;
				padding: 0.45rem 0.75rem;
				border-radius: 10px;
				border: 1px solid #cbd5e1;
				background: #ffffff;
				color: #0f172a;
				box-shadow: 0 6px 16px rgba(15, 23, 42, 0.12);
			}

			.column-drag-preview-icon {
				font-size: 16px;
				width: 16px;
				height: 16px;
				color: #1d4ed8;
			}

			.column-drag-placeholder {
				min-width: 150px;
				height: 36px;
				border-radius: 8px;
				border: 2px dashed #93c5fd;
				background: #eff6ff;
			}

			:host-context(.dark) .column-drag-preview {
				border-color: #1f2937;
				background: #000000;
				color: #ffffff;
				box-shadow: 0 12px 30px rgba(15, 23, 42, 0.2);
			}

			:host-context(.dark) .column-drag-preview-icon {
				color: #ffffff;
			}

			:host-context(.dark) .column-drag-placeholder {
				border-color: #374151;
				background: #0b0b0b;
			}

			.dark .column-drag-preview.cdk-drag-preview {
				border-color: #1f2937;
				background: #000000;
				color: #ffffff;
				box-shadow: 0 12px 30px rgba(15, 23, 42, 0.2);
			}

			.dark
				.column-drag-preview.cdk-drag-preview
				.column-drag-preview-icon {
				color: #ffffff;
			}

			.column-order-dialog-backdrop {
				position: fixed;
				inset: 0;
				display: flex;
				align-items: center;
				justify-content: center;
				padding: 1rem;
				z-index: 1100;
			}

			.column-order-dialog-backdrop-surface {
				position: absolute;
				inset: 0;
			}

			.column-order-dialog {
				position: relative;
				width: min(640px, 100%);
				max-height: min(80vh, 760px);
				border-radius: 14px;
				box-shadow: 0 20px 44px rgba(15, 23, 42, 0.28);
				display: flex;
				flex-direction: column;
			}

			.column-order-list {
				overflow: auto;
				min-height: 120px;
			}

			.column-order-item {
				display: flex;
				align-items: center;
				justify-content: space-between;
				gap: 0.5rem;
				padding: 0.25rem 0.55rem;
				min-height: 34px;
				border-radius: 8px;
				margin-bottom: 0.3rem;
				overflow: hidden;
				transition:
					background-color 140ms ease,
					border-color 140ms ease;
			}

			.column-order-item:last-child {
				margin-bottom: 0;
			}

			.column-order-item.cdk-drag-preview {
				overflow: hidden;
				box-shadow: 0 8px 20px rgba(15, 23, 42, 0.18);
			}

			.column-order-item.cdk-drag-placeholder {
				opacity: 0.45;
				border-style: dashed;
			}
		`,
		/*
		 * Author: Sofiia Holovko (sholovko)
		 * Task 211 - Highlight the row being edited with a left border accent and subtle background tint
		 */
		`
			tr.editing-row {
				transition:
					background-color 200ms ease,
					box-shadow 200ms ease;
				box-shadow: inset 3px 0 0 #3b82f6;
			}

			:host-context(.dark) tr.editing-row {
				background-color: rgba(59, 130, 246, 0.1) !important;
			}
		`,
		`
			input:-webkit-autofill,
			input:-webkit-autofill:hover,
			input:-webkit-autofill:focus,
			input:-webkit-autofill:active {
				-webkit-box-shadow: 0 0 0 1000px #e2e2ea inset !important;
				-webkit-text-fill-color: #000000 !important;
				caret-color: #000000;
				transition: background-color 5000s ease-in-out 0s;
			}

			@media (prefers-color-scheme: dark) {
				input:-webkit-autofill,
				input:-webkit-autofill:hover,
				input:-webkit-autofill:focus,
				input:-webkit-autofill:active {
					-webkit-box-shadow: 0 0 0 1000px #46464e inset !important;
					-webkit-text-fill-color: #f8fafc !important;
					caret-color: #f8fafc;
					transition: background-color 5000s ease-in-out 0s;
				}
			}
		`,
	],
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
	 * Task 207 - Open Mass Edit dialog
	 */
	private dialog = inject(MatDialog);

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
		return (
			trimmed !== '' &&
			trimmed !== '-1' &&
			trimmed !== '0' &&
			/^\d+$/.test(trimmed)
		);
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
	searchInputState = signal<'idle' | 'valid' | 'invalid' | 'searching'>(
		'idle'
	);
	searchValidationMessage = signal('');
	private readonly MIN_SEARCH_LENGTH = 2;

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 143 - Strongly typed results rows for the dynamic search table
	 */
	searchResults: SearchResultRow[] = [];
	searchResultsSig = signal<SearchResultRow[]>([]);
	selectedArtifactType = signal<string | null>(null);
	resultsIdFilter = signal('');
	resultsIdExactMatch = signal(false);

	availableArtifactTypes = computed<string[]>(() => {
		const set = new Set<string>();
		for (const r of this.searchResultsSig() ?? []) {
			const t = String(r?.type ?? '').trim();
			if (t) set.add(t);
		}
		return Array.from(set).sort((a, b) => a.localeCompare(b));
	});

	filteredSearchResults = computed<SearchResultRow[]>(() => {
		const type = (this.selectedArtifactType() ?? '').trim();
		const idNeedle = (this.resultsIdFilter() ?? '').trim();
		let rows = this.searchResultsSig() ?? [];

		if (type) {
			rows = rows.filter((r) => String(r?.type ?? '').trim() === type);
		}
		if (idNeedle) {
			rows = rows.filter((r) => String(r?.id ?? '').includes(idNeedle));
		}

		return rows;
	});

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

				this.router.navigate(
					['/ple/artifact/explorer', safeBranchType, branchId],
					{
						queryParams: {
							artifactId,
							viewId,
						},
					}
				);
			},
			error: (err: unknown) => {
				const message =
					err instanceof Error ? err.message : String(err);
				console.error(
					'Failed to resolve branch/view/type for navigation:',
					message
				);
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
		const artifactId = this.resolveArtifactIdFromRow(
			this.selectedSearchRow
		);

		if (!artifactId) {
			console.warn(
				'Task 177: missing artifact id for selected row',
				this.selectedSearchRow
			);
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
			if (typeof t['idIntValue'] === 'number')
				return String(t['idIntValue']);
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
			const id = col.key.startsWith('attr_')
				? col.key.substring('attr_'.length)
				: '';
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
	savedSearchDateSortAsc = true;

	// Save status flags for Save Search operation
	saveInProgress = false;
	saveErrorMessage = '';
	// Author: Sofiia Holovko (sholovko) Task 243 - Show success notification after a search is saved
	saveSuccessMessage = '';
	// Author: Sofiia Holovko (sholovko) Task 197
	editingSearchId: number | null = null;
	editingSearchTitle = '';
	editingSearchQuery = '';
	editSaveInProgress = false;
	editErrorMessage = '';
	// Author: Sofiia Holovko (sholovko) Task 212 - Show success notification after edit
	editSuccessMessage = '';
	// Author: Sofiia Holovko (sholovko) Task 219 - Track original values to detect changes
	private editOriginalTitle = '';
	private editOriginalQuery = '';
	// Author: Sofiia Holovko (sholovko) Task 210 - Track delete confirmation state
	deletingSearchId: number | null = null;
	deleteInProgress = false;
	deleteErrorMessage = '';
	// Author: Kris Graham (kgraha16) - Created to have a state model of expanded rows.
	expanded = new Set<string>();

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 204 - Track selected rows in Search Results table (checkbox column)
	 */
	selectedRowIds = new Set<string>();

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 205 - Reset row selection state (selection must not persist across searches/saved-state)
	 */
	private resetRowSelection(): void {
		this.selectedRowIds.clear();
	}

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 204 - Check whether a row is selected
	 */
	isRowSelected(row: SearchResultRow): boolean {
		return this.selectedRowIds.has(row.id);
	}

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 204 - Toggle row selection from checkbox
	 */
	onRowSelectionToggle(row: SearchResultRow, event: MatCheckboxChange): void {
		if (!row?.id) return;

		if (event.checked) {
			this.selectedRowIds.add(row.id);
		} else {
			this.selectedRowIds.delete(row.id);
		}
	}

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 206 - Add Mass Edit button (separate from column customization)
	 *
	 * UI-only for now: button + enabled/disabled rules + click handler.
	 * Dialog implementation is handled in Task 207.
	 */
	selectedRowCount(): number {
		return this.selectedRowIds.size;
	}

	hasSelectedRows(): boolean {
		return this.selectedRowIds.size > 0;
	}

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 208 - Implement backend mass update endpoint integration
	 * Mass Edit requires selected artifacts to be the same artifact type.
	 */
	private getSelectedRows(): SearchResultRow[] {
		return this.searchResultsSig().filter((row) =>
			this.selectedRowIds.has(row.id)
		);
	}

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 208 - Implement backend mass update endpoint integration
	 * Safely extract a valid attribute type id from backend attribute metadata.
	 */
	private extractValidAttributeId(attr: unknown): string {
		const obj =
			typeof attr === 'object' && attr !== null
				? (attr as Record<string, unknown>)
				: {};

		if (typeof obj['id'] === 'string' && obj['id'].trim()) {
			return obj['id'].trim();
		}

		if (
			typeof obj['attributeTypeId'] === 'string' &&
			obj['attributeTypeId'].trim()
		) {
			return obj['attributeTypeId'].trim();
		}

		if (typeof obj['typeId'] === 'string' && obj['typeId'].trim()) {
			return obj['typeId'].trim();
		}

		if (obj['id'] && typeof obj['id'] === 'object') {
			const nested = obj['id'] as Record<string, unknown>;

			if (typeof nested['id'] === 'string' && nested['id'].trim()) {
				return nested['id'].trim();
			}

			if (
				typeof nested['idString'] === 'string' &&
				nested['idString'].trim()
			) {
				return nested['idString'].trim();
			}

			if (typeof nested['idIntValue'] === 'number') {
				return String(nested['idIntValue']);
			}
		}

		return '';
	}

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 208 - Implement backend mass update endpoint integration
	 * Safely extract a readable attribute name from backend attribute metadata.
	 */
	private extractValidAttributeName(attr: unknown): string {
		const obj =
			typeof attr === 'object' && attr !== null
				? (attr as Record<string, unknown>)
				: {};

		if (typeof obj['name'] === 'string' && obj['name'].trim()) {
			return obj['name'].trim();
		}

		if (typeof obj['typeName'] === 'string' && obj['typeName'].trim()) {
			return obj['typeName'].trim();
		}

		return 'Unknown Attribute';
	}

	onMassEdit(): void {
		const selectedRows = this.getSelectedRows();
		const selectedIds = selectedRows.map((row) => row.id);

		if (selectedIds.length === 0) {
			return;
		}

		const selectedTypes = Array.from(
			new Set(
				selectedRows
					.map((row) => String(row.type || '').trim())
					.filter(Boolean)
			)
		);

		if (selectedTypes.length !== 1) {
			console.warn(
				'Mass Edit requires all selected artifacts to be the same artifact type.'
			);
			return;
		}

		const selectedTypeName = selectedTypes[0];
		const selectedArtifactType = (this.artifactTypes() ?? []).find(
			(type) => String(type.name).trim() === selectedTypeName
		);

		if (!selectedArtifactType?.id) {
			console.warn(
				'Mass Edit could not resolve the selected artifact type id.'
			);
			alert(
				'Mass Edit could not determine the artifact type for the selected rows.'
			);
			return;
		}

		this.artExpHttpService
			.getArtifactTypeAttributes(String(selectedArtifactType.id))
			.pipe(take(1))
			.subscribe({
				next: (validAttributes) => {
					const dialogRef = this.dialog.open(
						MassEditDialogComponent,
						{
							width: '720px',
							maxWidth: '95vw',
							data: {
								selectedIds,
								attributeTypes: (validAttributes ?? [])
									.map((attr) => ({
										id: this.extractValidAttributeId(attr),
										name: this.extractValidAttributeName(
											attr
										),
									}))
									.filter((attr) => attr.id !== ''),
							},
							disableClose: true,
						}
					);

					dialogRef
						.afterClosed()
						.subscribe((result?: MassEditDialogResult) => {
							if (!result || result.action === 'cancel') {
								return;
							}

							const attributeTypeId = (
								result.attributeTypeId || ''
							).trim();
							const value = (result.value || '').trim();

							if (!attributeTypeId || !value) {
								console.warn(
									'attributeTypeId and value are required.'
								);
								return;
							}

							this.uiService.id.pipe(take(1)).subscribe({
								next: (branchId) => {
									this.artExpHttpService
										.massEditAttribute({
											branchId: String(branchId),
											artifactIds: selectedIds,
											attributeTypeId,
											value,
											operation: 'replace',
										})
										.pipe(take(1))
										.subscribe({
											next: () => {
												this.refreshSearchResultsAfterMassEdit();
											},
											error: (err: unknown) => {
												const message =
													err instanceof Error
														? err.message
														: String(err);
												console.error(
													'Mass Edit failed:',
													err
												);
												alert(
													`Mass Edit failed: ${message}`
												);
											},
										});
								},
								error: (err: unknown) => {
									const message =
										err instanceof Error
											? err.message
											: String(err);
									console.error(
										'Failed to resolve branch for Mass Edit:',
										message
									);
								},
							});
						});
				},
				error: (err: unknown) => {
					const message =
						err instanceof Error ? err.message : String(err);
					console.error(
						'Failed to load valid attributes for Mass Edit:',
						message
					);
				},
			});
	}
	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 209 - Refresh table after successful mass edit
	 *
	 * Clear selection and rerun the current search so the Search Results
	 * table immediately shows updated values after Mass Edit completes.
	 */
	private refreshSearchResultsAfterMassEdit(): void {
		this.resetRowSelection();
		this.onSearch();
	}
	/**
	 * Author: Sofiia Holovko (sholovko)
	 * Task 236 - Open Saved Searches dialog
	 */
	onOpenSavedSearchesDialog(): void {
		const dialogRef = this.dialog.open(SavedSearchesDialogComponent, {
			width: '1120px',
			maxWidth: '95vw',
			data: {},
			autoFocus: false,
		});

		dialogRef
			.afterClosed()
			.pipe(take(1))
			.subscribe((result?: SavedSearchesDialogResult) => {
				if (result?.action === 'load') {
					this.applySavedSearch(result.savedSearch, true);
				}
			});
	}
	/**
	 * Author: Kris Graham (kgraha16)
	 * Task 179 - Helper method to expand relations column and track which rows are expanded.
	 */
	expandToggle(row: SearchResultRow) {
		if (this.expanded.has(row.id)) {
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
	/**
	 * Author: Daria Berezianska(dvydybor)
	 * Task 198 - Implement drag-and-drop for table headers (column reordering)
	 */
	columnOrder = signal<string[]>([]);
	columnOrderDialogOpen = signal(false);
	columnOrderDraft = signal<string[]>([]);

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
						visible:
							prev?.visible ?? attr.name.toLowerCase() === 'name',
					};
				})
			);
		});

		effect(() => {
			/**
			 * Author: Daria Berezianska(dvydybor)
			 * Task 198 - Implement drag-and-drop for table headers (column reordering)
			 */
			this.syncColumnOrder();
		});
	}

	/**
	 * Author: Daria Berezianska(dvydybor)
	 * Task 198 - Implement drag-and-drop for table headers (column reordering)
	 */
	private syncColumnOrder(): void {
		const allReorderableKeys = [
			...this.baseColumns().map((c) => c.key),
			...this.attributeColumns().map((c) => c.key),
		];
		const existing = this.columnOrder();
		const kept = existing.filter((k) => allReorderableKeys.includes(k));
		const missing = allReorderableKeys.filter((k) => !kept.includes(k));
		const next = [...kept, ...missing];
		const same =
			existing.length === next.length &&
			existing.every((value, idx) => value === next[idx]);
		if (!same) {
			this.columnOrder.set(next);
		}
	}

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 178 - Restore preserved Advanced Search state on page load
	 */
	ngOnInit(): void {
		this.restoreAdvancedSearchState();
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
					this.savedSearches = Array.isArray(savedSearches)
						? savedSearches
						: [];
					this.sortSavedSearchesByTimestamp();
					this.savedSearchesLoading = false;
				},
				error: (err: unknown) => {
					this.savedSearches = [];
					this.savedSearchesLoading = false;
					this.savedSearchesErrorMessage =
						err instanceof Error ? err.message : String(err);
					console.error(
						'Failed to load saved searches:',
						this.savedSearchesErrorMessage
					);
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
		return date.toLocaleString([], {
			year: 'numeric',
			month: '2-digit',
			day: '2-digit',
			hour: '2-digit',
			minute: '2-digit',
			hour12: true,
		});
	}

	private applySavedSearch(
		savedSearch: SavedSearch,
		executeSearch = false
	): void {
		this.data = {
			...defaultAdvancedSearchCriteria,
			searchTitle: savedSearch.title ?? '',
			artifactTypes: [...(savedSearch.artifactTypes ?? [])],
			attributeTypes: [...(savedSearch.attributeTypes ?? [])],
			exactMatch: !!savedSearch.exactMatch,
			searchById: !!savedSearch.searchById,
		};
		this.searchValue = savedSearch.query ?? '';
		this.artTypesFilter.set('');
		this.attrTypesFilter.set('');
		this.showSearchError = false;
		this.searchInputState.set('idle');
		this.searchValidationMessage.set('');
		this._selectedArtifactTypes.next(this.data.artifactTypes);
		this.persistAdvancedSearchState();

		if (
			executeSearch &&
			(this.searchValue || '').trim().length > 0 &&
			this.branchSelected()
		) {
			this.onSearch();
		}
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
				/**
				 * Author: Daria Berezianska(dvydybor)
				 * Task 198 - Implement drag-and-drop for table headers (column reordering)
				 */
				columnOrder: this.columnOrder(),
			};

			sessionStorage.setItem(
				this.ADV_SEARCH_STATE_KEY,
				JSON.stringify(state)
			);
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
			if (typeof parsed.searchValue === 'string')
				this.searchValue = parsed.searchValue;
			if (Array.isArray(parsed.searchResults))
				this.searchResults = parsed.searchResults;
			if (typeof parsed.hasSearched === 'boolean')
				this.hasSearched = parsed.hasSearched;

			if (Array.isArray(parsed.baseColumns))
				this.baseColumns.set(parsed.baseColumns);
			if (Array.isArray(parsed.attributeColumns))
				this.attributeColumns.set(parsed.attributeColumns);
			if (parsed.attributeSortSelect)
				this.attributeSortSelect.set(parsed.attributeSortSelect);

			if (typeof parsed.showSearchError === 'boolean')
				this.showSearchError = parsed.showSearchError;
			if (typeof parsed.isLoading === 'boolean')
				this.isLoading = parsed.isLoading;

			if (parsed.searchInputState)
				this.searchInputState.set(parsed.searchInputState);
			if (typeof parsed.searchValidationMessage === 'string')
				this.searchValidationMessage.set(
					parsed.searchValidationMessage
				);
			/**
			 * Author: Daria Berezianska(dvydybor)
			 * Task 198 - Implement drag-and-drop for table headers (column reordering)
			 */
			if (Array.isArray(parsed.columnOrder))
				this.columnOrder.set(parsed.columnOrder);

			// Task 179 compatibility - restore expanded row state (if present)
			if (Array.isArray(parsed.expandedIds)) {
				this.expanded = new Set<string>(parsed.expandedIds);
			}
			/**
			 * Author: Eihab Khudhair (ekhudhai)
			 * Task 205 - Selection state must not be restored from persisted page state
			 */
			this.resetRowSelection();
		} catch (e) {
			console.warn(
				'Task 178: failed to restore advanced search state',
				e
			);
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
	 * Task 214 - Generate the HTML Table for the current Search Results Table
	 */
	private generateHTMLTable(): string {
		const columns = this.visibleColumns().filter(
			(col) => col.key !== 'relations' && col.key !== 'select'
		);
		const rows = this.filteredSearchResults();

		const cleanHTML = (value: unknown): string => {
			if (value === null || value === undefined) return '';
			return String(value)
				.replace(/&/g, '&amp;')
				.replace(/</g, '&lt;')
				.replace(/>/g, '&gt;')
				.replace(/"/g, '&quot;')
				.replace(/'/g, '&#039;');
		};

		const headerHTML = `
			<tr>
				${columns.map((col) => `<th>${cleanHTML(col.label)}</th>`).join('')}
			</tr>
		`;

		const bodyHTML = rows
			.map(
				(row) => `
			<tr>
				${columns
					.map((col) => {
						const value = row[col.key] ?? '';
						return `<td>${cleanHTML(value)}</td>`;
					})
					.join('')}
			</tr>
		`
			)
			.join('');

		return `
			<table border="1" cellspacing="0" cellpadding="6">
				<thead>${headerHTML}</thead>
				<tbody>${bodyHTML}</tbody>
			</table>
		`;
	}

	/**
	 * Author: Kris Graham (kgraha16)
	 * Task 220 - Build full HTML document to include the Search Results Table for export
	 */
	private buildHTMLDocument(tableHTML: string): string {
		const timestamp = new Date().toLocaleString();
		return `
			<!DOCTYPE html>
			<html>
			<head>
				<meta charset="UTF-8">
				<title>Advanced Search Results</title>
				<style>
					body { font-family: Arial, sans-seriff; padding: 20px; }
					h1 { margin-bottom: 10px; }
					table { border-collapse: collapse; width: 100%; }
					th { background-color: #f2f2f2; }
					th, td { text-align: left; }
				</style>
			</head>
			<body>
				<h1>Advanced Search Results</h1>
				<p>Generated: ${timestamp}</p>
				${tableHTML}
			</body>
			</html>
		`;
	}

	/**
	 * Author: Kris Graham (kgraha16)
	 * Task 139 - Create visible columns to capture the visbility state of checkboxes
	 * in the Columns menu for implementation into the Search Result Table.
	 * Task 179 - Add Relations/Traceability column to visible columns of the search
	 * results table.
	 */
	visibleColumns = computed<ColumnConfig[]>(() => {
		/**
		 * Author: Daria Berezianska(dvydybor)
		 * Task 198 - Implement drag-and-drop for table headers (column reordering)
		 */
		const byKey = new Map(
			[...this.baseColumns(), ...this.attributeColumns()].map(
				(c) => [c.key, c] as const
			)
		);
		const ordered = this.columnOrder()
			.map((key) => byKey.get(key))
			.filter((col): col is ColumnConfig => !!col);
		const sectionColumn = ordered.find((col) => this.isSectionColumn(col));
		const fixedLeading =
			sectionColumn && sectionColumn.visible ? [sectionColumn] : [];
		const relationsColumn: ColumnConfig = {
			key: 'relations',
			label: 'REL',
			visible: true,
			locked: true,
		};
		const orderedWithoutFixed = ordered.filter(
			(col) => !this.isSectionColumn(col)
		);

		return [
			/**
			 * Author: Eihab Khudhair (ekhudhai)
			 * Task 204 - Row selection checkbox column (always visible, not customizable)
			 */
			{ key: 'select', label: '', visible: true, locked: true },
			...fixedLeading,
			relationsColumn,
			...orderedWithoutFixed,
		].filter((col) => col.visible);
	});

	/**
	 * Author: Daria Berezianska(dvydybor)
	 * Task 198 - Implement drag-and-drop for table headers (column reordering)
	 */
	onColumnHeaderDrop(event: CdkDragDrop<ColumnConfig[]>): void {
		const visible = this.visibleColumns();
		const draggablePositions = visible
			.map((col, idx) => (this.isColumnDraggable(col) ? idx : -1))
			.filter((idx) => idx >= 0);
		const previousIndex = draggablePositions.indexOf(event.previousIndex);
		const currentIndex = draggablePositions.indexOf(event.currentIndex);
		if (previousIndex < 0 || currentIndex < 0) return;

		const visibleReorderableKeys = visible
			.filter((c) => this.isColumnDraggable(c))
			.map((c) => c.key);

		if (visibleReorderableKeys.length <= 1) return;

		if (
			previousIndex >= visibleReorderableKeys.length ||
			currentIndex >= visibleReorderableKeys.length
		) {
			return;
		}

		const movedVisibleKeys = [...visibleReorderableKeys];
		moveItemInArray(movedVisibleKeys, previousIndex, currentIndex);
		this.applyVisibleColumnOrder(movedVisibleKeys);
	}

	openColumnOrderDialog(): void {
		const currentVisibleOrder = this.visibleColumns()
			.filter((col) => this.isColumnDraggable(col))
			.map((col) => col.key);
		this.columnOrderDraft.set(currentVisibleOrder);
		this.columnOrderDialogOpen.set(true);
	}

	closeColumnOrderDialog(): void {
		this.columnOrderDialogOpen.set(false);
	}

	saveColumnOrderDialog(): void {
		const draftOrder = this.columnOrderDraft();
		if (draftOrder.length > 1) {
			this.applyVisibleColumnOrder(draftOrder);
		}
		this.closeColumnOrderDialog();
	}

	onColumnOrderDialogDrop(event: CdkDragDrop<string[]>): void {
		const next = [...this.columnOrderDraft()];
		moveItemInArray(next, event.previousIndex, event.currentIndex);
		this.columnOrderDraft.set(next);
	}

	moveColumnInDialog(index: number, direction: -1 | 1): void {
		const target = index + direction;
		const next = [...this.columnOrderDraft()];
		if (
			index < 0 ||
			target < 0 ||
			index >= next.length ||
			target >= next.length
		)
			return;
		moveItemInArray(next, index, target);
		this.columnOrderDraft.set(next);
	}

	getColumnLabelByKey(key: string): string {
		const allColumns = [...this.baseColumns(), ...this.attributeColumns()];
		return allColumns.find((c) => c.key === key)?.label ?? key;
	}

	canOpenColumnOrderDialog(): boolean {
		return (
			this.visibleColumns().filter((col) => this.isColumnDraggable(col))
				.length > 1
		);
	}

	private applyVisibleColumnOrder(movedVisibleKeys: string[]): void {
		const visibleSet = new Set(movedVisibleKeys);
		const mergedOrder: string[] = [];
		let movedIdx = 0;
		for (const key of this.columnOrder()) {
			if (visibleSet.has(key)) {
				mergedOrder.push(movedVisibleKeys[movedIdx++]);
			} else {
				mergedOrder.push(key);
			}
		}
		this.columnOrder.set(mergedOrder);
	}

	isColumnDraggable(col: ColumnConfig): boolean {
		return (
			!['select', 'relations'].includes(col.key) &&
			!this.isSectionColumn(col)
		);
	}

	columnSortPredicate = (index: number): boolean => {
		const col = this.visibleColumns()[index];
		return !!col && this.isColumnDraggable(col);
	};

	private isSectionColumn(col: ColumnConfig): boolean {
		return (
			col.key === 'section' ||
			col.label.trim().toLowerCase() === 'section'
		);
	}

	attributeSearch = signal<string>('');
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

	filteredAttributeColumns = computed<ColumnConfig[]>(() => {
		const search = this.attributeSearch().toLowerCase().trim();

		return this.sortedAttributeColumns().filter(
			(col) => !search || col.label.toLowerCase().startsWith(search)
		);
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
	allAttributeTypes = toSignal(this.artifactService.allAttributeTypes, {
		initialValue: [],
	});

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
		this.artifactService
			.saveSearch(title, query, {
				artifactTypes: this.data.artifactTypes,
				attributeTypes: this.data.attributeTypes,
				exactMatch: this.data.exactMatch,
				searchById: this.data.searchById,
			})
			.pipe(take(1))
			.subscribe({
				next: () => {
					this.saveInProgress = false;
					// Author: Sofiia Holovko (sholovko) Task 243 - Set success message and auto-clear after 3 seconds
					this.saveSuccessMessage = 'Search saved successfully.';
					setTimeout(() => {
						this.saveSuccessMessage = '';
					}, 3000);
				},
				error: (err: unknown) => {
					this.saveInProgress = false;
					// Author: Sofiia Holovko (sholovko) Task 243 - Clear success message on error
					this.saveSuccessMessage = '';
					this.saveErrorMessage =
						err instanceof Error ? err.message : String(err);
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
	// Author: Sofiia Holovko (sholovko) Task 197
	onEditSavedSearch(savedSearch: SavedSearch): void {
		this.editingSearchId = savedSearch.id ?? null;
		this.editingSearchTitle = savedSearch.title;
		this.editingSearchQuery = savedSearch.query;
		this.editErrorMessage = '';
		// Author: Sofiia Holovko (sholovko) Task 219 - Snapshot original values for dirty-check
		this.editOriginalTitle = savedSearch.title;
		this.editOriginalQuery = savedSearch.query;
	}
	/**
	 * Author: Sofiia Holovko (sholovko)
	 * Task 219 - Returns true if the user has changed the title or query from original values
	 */
	hasEditChanged(): boolean {
		return (
			(this.editingSearchTitle || '').trim() !==
				this.editOriginalTitle.trim() ||
			(this.editingSearchQuery || '').trim() !==
				this.editOriginalQuery.trim()
		);
	}

	onConfirmEditSavedSearch(savedSearch: SavedSearch): void {
		const updatedTitle = (this.editingSearchTitle || '').trim();
		if (!updatedTitle) {
			this.editErrorMessage = 'Search name is required.';
			return;
		}
		this.editErrorMessage = '';
		this.editSaveInProgress = true;
		const updatedSearch: SavedSearch = {
			...savedSearch,
			title: updatedTitle,
			query: (this.editingSearchQuery || '').trim(),
			artifactTypes: this.data.artifactTypes,
			attributeTypes: this.data.attributeTypes,
			exactMatch: this.data.exactMatch,
			searchById: this.data.searchById,
		};
		this.http
			.put<SavedSearch>(
				`${this.SAVED_SEARCH_URL}/${savedSearch.id}`,
				updatedSearch
			)
			.pipe(take(1))
			.subscribe({
				next: () => {
					this.editSaveInProgress = false;
					this.editingSearchId = null;
					this.editingSearchTitle = '';
					this.editingSearchQuery = '';
					// Author: Sofiia Holovko (sholovko) Task 212 - Show success message and auto-clear after 3 seconds
					this.editSuccessMessage = 'Search updated successfully';
					setTimeout(() => {
						this.editSuccessMessage = '';
					}, 3000);
					this.loadSavedSearches();
				},
				error: (err: unknown) => {
					this.editSaveInProgress = false;
					this.editErrorMessage =
						err instanceof Error ? err.message : String(err);
				},
			});
	}

	onCancelEditSavedSearch(): void {
		this.editingSearchId = null;
		this.editingSearchTitle = '';
		this.editingSearchQuery = '';
		this.editErrorMessage = '';
		// Author: Sofiia Holovko (sholovko) Task 212 - Clear success message on cancel
		this.editSuccessMessage = '';
		// Author: Sofiia Holovko (sholovko) Task 219 - Clear original value snapshots
		this.editOriginalTitle = '';
		this.editOriginalQuery = '';
	}
	/**
	 * Author: Sofiia Holovko (sholovko)
	 * Task 210 - Enter delete confirmation mode for a saved search
	 */
	onDeleteSavedSearch(savedSearch: SavedSearch): void {
		this.deletingSearchId = savedSearch.id ?? null;
		this.deleteErrorMessage = '';
	}

	/**
	 * Author: Sofiia Holovko (sholovko)
	 * Task 210 - Confirm and execute delete of a saved search
	 */
	onConfirmDeleteSavedSearch(savedSearch: SavedSearch): void {
		this.deleteInProgress = true;
		this.deleteErrorMessage = '';
		this.http
			.delete(`${this.SAVED_SEARCH_URL}/${savedSearch.id}`)
			.pipe(take(1))
			.subscribe({
				next: () => {
					this.deleteInProgress = false;
					this.deletingSearchId = null;
					this.loadSavedSearches();
				},
				error: (err: unknown) => {
					this.deleteInProgress = false;
					this.deleteErrorMessage =
						err instanceof Error ? err.message : String(err);
				},
			});
	}

	/**
	 * Author: Sofiia Holovko (sholovko)
	 * Task 210 - Cancel delete confirmation
	 */
	onCancelDeleteSavedSearch(): void {
		this.deletingSearchId = null;
		this.deleteErrorMessage = '';
	}
	/**
	 * Author: Sofiia Holovko (sholovko)
	 * Task 217 - Allow pressing Enter to confirm and Escape to cancel inline edit
	 */
	onEditKeydown(event: KeyboardEvent, savedSearch: SavedSearch): void {
		if (event.key === 'Enter') {
			event.preventDefault();
			event.stopPropagation();
			if ((this.editingSearchTitle || '').trim()) {
				this.onConfirmEditSavedSearch(savedSearch);
			}
		} else if (event.key === 'Escape') {
			event.preventDefault();
			event.stopPropagation();
			this.onCancelEditSavedSearch();
		}
	}
	onArtifactTypeFilterChange(value: unknown): void {
		if (
			value === null ||
			value === undefined ||
			String(value).trim() === ''
		) {
			this.selectedArtifactType.set(null);
			return;
		}
		this.selectedArtifactType.set(String(value));
	}

	clearResultsFilters(): void {
		this.selectedArtifactType.set(null);
		this.resultsIdFilter.set('');
		this.resultsIdExactMatch.set(false);
	}

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
		this.searchResultsSig.set([]);
		this.selectedArtifactType.set(null);
		this.resultsIdFilter.set('');
		this.resultsIdExactMatch.set(false);

		/**
		 * Author: Eihab Khudhair (ekhudhai)
		 * Task 205 - Clear selection when a new search is executed
		 */
		this.resetRowSelection();

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
											details.filter(
												Boolean
											) as artifactWithRelations[]
									)
								);
							}),
							map((details: artifactWithRelations[]) => {
								// Task 154 - Debug: compare column keys vs row keys
								const cols = this.visibleColumns().map(
									(c) => c.key
								);
								console.log(
									'DEBUG visible column keys:',
									cols.slice(0, 15)
								);

								const visibleColPairs =
									this.visibleColumns().map((c) => ({
										key: c.key,
										label: c.label,
									}));

								console.log(
									'DEBUG visible columns (key->label):',
									visibleColPairs
								);
								console.table(visibleColPairs);

								const attrKeyMap = this.buildAttrColumnKeyMap();

								const rows: SearchResultRow[] = details.map(
									(d) => {
										const row: SearchResultRow = {
											type: d.typeName ?? '',
											id: d.id ?? '',
											name: d.name ?? '',
										};
										console.log(
											'DEBUG attributes sample:',
											d.id,
											d.attributes
										);

										// Task 154 - populate "attr_<id>" cells using ONLY the UI column keys (checkbox keys)
										(d.attributes ?? []).forEach((a) => {
											const typeId =
												this.extractAttrTypeId(a);
											if (!typeId) return;

											// Map backend attribute type id -> UI column key (attr_<id>)
											const colKey = attrKeyMap.get(
												String(typeId)
											);
											if (!colKey) return; // Do NOT fallback, prevents mismatched keys + blanks

											const rawVal =
												this.extractAttrValue(a);
											row[colKey] =
												this.formatAttrValue(rawVal);
										});

										console.log(
											'DEBUG sample value for visible attr columns:',
											d.id,
											visibleColPairs
												.filter((c) =>
													c.key.startsWith('attr_')
												)
												.map((c) => ({
													key: c.key,
													label: c.label,
													value: row[c.key],
												}))
										);

										console.log(
											'DEBUG row keys sample:',
											d.id,
											Object.keys(row)
												.filter((k) =>
													k.startsWith('attr_')
												)
												.slice(0, 15)
										);
										return row;
									}
								);
								return rows;
							})
						)
				)
			)
			.subscribe({
				next: (rows: SearchResultRow[]) => {
					this.searchResults = rows;
					this.searchResultsSig.set(rows);
					this.isLoading = false; // Author: Sofiia Holovko (sholovko) Task 144 - Clear loading state
					// Author: Sofiia Holovko (sholovko) Task 140 - Reset state after successful search
					this.searchInputState.set('valid');
					this.searchValidationMessage.set('Search complete');
				},
				error: (err: unknown) => {
					const message =
						err instanceof Error ? err.message : String(err);
					console.error('Advanced search failed:', message);
					this.searchResults = [];
					this.searchResultsSig.set([]);
					this.isLoading = false; // Author: Sofiia Holovko (sholovko) Task 144 - Clear loading state on error
					// Author: Sofiia Holovko (sholovko) Task 140 - Show error state on search failure
					this.searchInputState.set('invalid');
					this.searchValidationMessage.set(
						'Search failed. Please try again.'
					);
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
			this.searchValidationMessage.set(
				`Minimum ${this.MIN_SEARCH_LENGTH} characters required`
			);
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
	
	clearAttributeSearch(): void {
		this.attributeSearch.set('');
	}

	toggleSavedSearchDateSortDirection(): void {
		this.savedSearchDateSortAsc = !this.savedSearchDateSortAsc;
		this.sortSavedSearchesByTimestamp();
	}

	private sortSavedSearchesByTimestamp(): void {
		const direction = this.savedSearchDateSortAsc ? 1 : -1;
		this.savedSearches = [...this.savedSearches].sort((a, b) => {
			const aTimestamp = this.toSortableTimestamp(a.timestamp);
			const bTimestamp = this.toSortableTimestamp(b.timestamp);
			return (aTimestamp - bTimestamp) * direction;
		});
	}

	private toSortableTimestamp(timestamp?: number): number {
		if (typeof timestamp !== 'number' || Number.isNaN(timestamp)) {
			return 0;
		}
		return timestamp;
	}

	setResultsIdFilter(raw: string): void {
		const digits = String(raw ?? '').replace(/\D+/g, '');
		this.resultsIdFilter.set(digits);
	}

	clearResultsIdFilter(): void {
		this.resultsIdFilter.set('');
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
		this.selectedArtifactType.set(null);

		//Author: Sofiia Holovko (sholovko) Task 145 - Clear search results on new search
		this.searchResults = [];
		this.searchResultsSig.set([]);

		/**
		 * Author: Eihab Khudhair (ekhudhai)
		 * Task 205 - Clear selection on New Search
		 */
		this.resetRowSelection();

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
		this.resultsIdFilter.set('');
		this.resultsIdExactMatch.set(false);
	}

	/**
	 * Author: Kris Graham (kgraha16)
	 * Task 139 - Create helper function to capture change from a checkbox toggle. Captures
	 * the Mutability within the Core Columns.
	 */
	onCoreColumnToggle(col: ColumnConfig) {
		if (col.locked) {
			return;
		}
		this.baseColumns.update((cols) =>
			cols.map((c) =>
				c.key === col.key ? { ...c, visible: !c.visible } : c
			)
		);
	}

	/**
	 * Author: Kris Graham (kgraha16)
	 * Task 139 - Create helper function to capture change from a checkbox toggle. Captures
	 * the Mutability within the Attributes Columns.
	 */
	onAttributeColumnToggle(col: ColumnConfig) {
		this.attributeColumns.update((cols) =>
			cols.map((c) =>
				c.key === col.key ? { ...c, visible: !c.visible } : c
			)
		);
	}

	/**
	 * Author: Mariia Gordieieva
	 * Task 168 - Implement "Clear all selections" button for Advanced Search
	 */
	clearAllColumnSelections(): void {
		this.baseColumns.update((cols) =>
			cols.map((c) =>
				c.locked ? { ...c, visible: true } : { ...c, visible: false }
			)
		);

		// Attribute columns: clear all
		this.attributeColumns.update((cols) =>
			cols.map((c) => ({ ...c, visible: false }))
		);
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
						.flatMap((rel) => rel.relationSides ?? [])
						.flatMap((side) => side.artifacts ?? [])
						.map((artifact) => artifact.name ?? '')
				)
			)
			.subscribe((names: string[]) => {
				this.relatedNames.set(result.id, names);
			});
	}

	onExport(): void {
		const tableHTML = this.generateHTMLTable();
		const fullHTML = this.buildHTMLDocument(tableHTML);
		const blob = new Blob([fullHTML], { type: 'text/html;charset=utf-8;' });
		const url = URL.createObjectURL(blob);
		const a = document.createElement('a');
		a.href = url;
		a.download = 'advanced-search-results.html';
		a.click();
		URL.revokeObjectURL(url);
	}
}
