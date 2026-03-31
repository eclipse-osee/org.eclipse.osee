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
 * Author: Sofiia Holovko (sholovko)
 * Task 236 - Create Saved Searches Dialog Component
 *********************************************************************/
import {
	Component,
	inject,
	OnInit,
	signal,
	computed,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { NamedId } from '@osee/shared/types';
import { take } from 'rxjs/operators';
import { apiURL } from '@osee/environments';

/**
 * Author: Sofiia Holovko (sholovko)
 * Task 236 - Shape of a saved search record (mirrors the type in the page component)
 */
export type SavedSearch = {
	id?: number;
	title: string;
	query: string;
	timestamp?: number;
	artifactTypes?: NamedId[];
	attributeTypes?: NamedId[];
	exactMatch?: boolean;
	searchById?: boolean;
	global?: boolean;
};

/**
 * Author: Sofiia Holovko (sholovko)
 * Task 236 - Data injected into the dialog when it is opened
 */
export type SavedSearchesDialogData = Record<string, never>;

/**
 * Author: Sofiia Holovko (sholovko)
 * Task 236 - Result emitted when the dialog is closed
 *
 * action === 'load'   → user clicked a saved search row; caller should apply it
 * action === 'close'  → user dismissed the dialog with no selection
 */
export type SavedSearchesDialogResult =
	| { action: 'close' }
	| { action: 'load'; savedSearch: SavedSearch };

@Component({
	selector: 'osee-saved-searches-dialog',
	standalone: true,
	imports: [
		CommonModule,
		FormsModule,
		MatDialogModule,
		MatButtonModule,
		MatIconModule,
		MatFormFieldModule,
		MatInputModule,
	],
	template: `
		<!-- Dialog header -->
		<div class="tw-flex tw-items-center tw-justify-between tw-px-6 tw-py-4 tw-border-b tw-border-slate-200 dark:tw-border-slate-700">
			<h2 class="tw-text-lg tw-font-semibold tw-m-0">Saved Searches</h2>
			<button
				mat-icon-button
				type="button"
				aria-label="Close dialog"
				(click)="onClose()">
				<mat-icon>close</mat-icon>
			</button>
		</div>

		<!-- Dialog body -->
		<div class="tw-px-6 tw-py-4 tw-overflow-auto" style="min-width: 640px; max-height: 60vh;">
		<!--
			 * Author: Sofiia Holovko (sholovko)
			 * Task 244 - Filter input to quickly find a saved search by name or description
			 -->
			<div class="tw-mb-4">
				<mat-form-field class="tw-w-full" subscriptSizing="dynamic">
					<mat-label>Filter saved searches</mat-label>
					<input
						matInput
						placeholder="Search by name or description..."
						[ngModel]="filterQuery()"
						(ngModelChange)="filterQuery.set($event)"
						name="savedSearchesFilter"
						autocomplete="off" />
					<div matSuffix class="tw-flex tw-items-center">
						<button
							*ngIf="filterQuery().length"
							mat-icon-button
							type="button"
							aria-label="Clear filter"
							(click)="clearFilter()">
							<mat-icon>close</mat-icon>
						</button>
					</div>
				</mat-form-field>
			</div>
		<div class="tw-px-6 tw-py-4 tw-overflow-auto" style="min-width: 1080px; max-height: 60vh;">

			<!-- Loading state -->
			<div *ngIf="loading()" class="tw-flex tw-items-center tw-gap-2 tw-text-sm tw-text-slate-500 tw-py-4">
				<mat-icon class="tw-animate-spin">refresh</mat-icon>
				<span>Loading saved searches…</span>
			</div>

			<!-- Error state -->
			<div
				*ngIf="!loading() && errorMessage()"
				class="tw-text-red-500 tw-text-sm tw-py-4">
				Failed to load saved searches.
			</div>

			<!-- Empty state -->
			<div
				*ngIf="!loading() && !errorMessage() && sortedSearches().length === 0"
				class="tw-text-slate-400 tw-text-sm tw-py-4">
				No saved searches yet. Saved searches will appear here.
			</div>
			
			<!--
			 * Author: Sofiia Holovko (sholovko)
			 * Task 244 - No-match message when filter returns zero results but searches do exist
			 -->
			<div
				*ngIf="!loading() && !errorMessage() && sortedSearches().length > 0 && filteredSearches().length === 0"
				class="tw-text-slate-400 tw-text-sm tw-py-4 tw-text-center">
				No saved searches match "{{ filterQuery() }}".
			</div>

			<!-- Searches table -->
			<table
				*ngIf="!loading() && !errorMessage() && filteredSearches().length > 0"
				class="tw-w-full tw-text-sm tw-text-left tw-border tw-border-slate-700 tw-rounded-lg tw-overflow-hidden">
				<thead class="tw-bg-gray-100 dark:tw-bg-slate-800">
					<tr>
						<th class="tw-px-3 tw-py-2">Name</th>
						<th class="tw-px-3 tw-py-2">Description</th>
						<th class="tw-px-3 tw-py-2">Artifact Types</th>
						<th class="tw-px-3 tw-py-2">Attribute Types</th>
						<th class="tw-px-3 tw-py-2">Exact Match</th>
						<th class="tw-px-3 tw-py-2">Search by ID</th>
						<th class="tw-px-3 tw-py-2">
							<span class="tw-inline-flex tw-items-center tw-gap-1">
								<span>Last Modified</span>
								<button
									type="button"
									class="tw-inline-flex tw-items-center tw-justify-center tw-p-0 tw-bg-transparent tw-border-0 tw-cursor-pointer tw-text-slate-700 dark:tw-text-slate-300"
									aria-label="Toggle last modified sort direction"
									(click)="toggleDateSort()">
									<mat-icon class="tw-text-base" [attr.aria-hidden]="true">
										{{ dateAsc() ? 'arrow_drop_up' : 'arrow_drop_down' }}
									</mat-icon>
								</button>
							</span>
						</th>
						<th class="tw-px-3 tw-py-2">Actions</th>
					</tr>
				</thead>
				<tbody>
					<ng-container *ngFor="let s of filteredSearches()">

						<!-- View row -->
						<tr
							*ngIf="editingId() !== s.id"
							class="hover:tw-bg-slate-50 dark:hover:tw-bg-slate-800/40 tw-transition-colors tw-cursor-pointer"
							(click)="onLoadSearch(s)">
							<td class="tw-px-3 tw-py-2 tw-border-t tw-border-slate-700">{{ s.title }}</td>
							<td class="tw-px-3 tw-py-2 tw-border-t tw-border-slate-700 tw-text-slate-500 dark:tw-text-slate-400">{{ s.query }}</td>
							<td class="tw-px-3 tw-py-2 tw-border-t tw-border-slate-700 tw-text-slate-500 dark:tw-text-slate-400">{{ formatSelections(s.artifactTypes) }}</td>
							<td class="tw-px-3 tw-py-2 tw-border-t tw-border-slate-700 tw-text-slate-500 dark:tw-text-slate-400">{{ formatSelections(s.attributeTypes) }}</td>
							<td class="tw-px-3 tw-py-2 tw-border-t tw-border-slate-700">{{ formatBooleanFlag(s.exactMatch) }}</td>
							<td class="tw-px-3 tw-py-2 tw-border-t tw-border-slate-700">{{ formatBooleanFlag(s.searchById) }}</td>
							<td class="tw-px-3 tw-py-2 tw-border-t tw-border-slate-700 tw-whitespace-nowrap">
								{{ formatTimestamp(s.timestamp) }}
							</td>
							<td class="tw-px-3 tw-py-2 tw-border-t tw-border-slate-700 tw-whitespace-nowrap">
								<button
									mat-icon-button
									type="button"
									aria-label="Edit saved search"
									(click)="onStartEdit(s, $event)">
									<mat-icon class="tw-text-base">edit</mat-icon>
								</button>
								<button
									mat-icon-button
									type="button"
									aria-label="Delete saved search"
									(click)="onStartDelete(s, $event)">
									<mat-icon class="tw-text-base tw-text-red-500">delete</mat-icon>
								</button>
							</td>
						</tr>

						<!-- Inline edit row -->
						<!--
						 * Author: Sofiia Holovko (sholovko)
						 * Task 236 - Inline edit row inside the dialog (mirrors page editing-row styling)
						 -->
						<tr
							*ngIf="editingId() === s.id"
							class="editing-row tw-bg-blue-50 dark:tw-bg-slate-700/40">
							<td class="tw-px-2 tw-py-2 tw-border-t tw-border-slate-700">
								<mat-form-field class="tw-w-full" subscriptSizing="dynamic">
									<mat-label>Name</mat-label>
									<input
										matInput
										[(ngModel)]="editTitle"
										[name]="'dlgEditTitle_' + s.id"
										placeholder="Search name"
										(keydown)="onEditKeydown($event, s)" />
								</mat-form-field>
							</td>
							<td class="tw-px-2 tw-py-2 tw-border-t tw-border-slate-700">
								<mat-form-field class="tw-w-full" subscriptSizing="dynamic">
									<mat-label>Description</mat-label>
									<input
										matInput
										[(ngModel)]="editQuery"
										[name]="'dlgEditQuery_' + s.id"
										placeholder="Search query"
									(keydown)="onEditKeydown($event, s)" />
								</mat-form-field>
							</td>
							<td class="tw-px-3 tw-py-2 tw-border-t tw-border-slate-700 tw-text-xs tw-text-slate-500 dark:tw-text-slate-400">
								{{ formatSelections(s.artifactTypes) }}
							</td>
							<td class="tw-px-3 tw-py-2 tw-border-t tw-border-slate-700 tw-text-xs tw-text-slate-500 dark:tw-text-slate-400">
								{{ formatSelections(s.attributeTypes) }}
							</td>
							<td class="tw-px-3 tw-py-2 tw-border-t tw-border-slate-700 tw-text-xs">
								{{ formatBooleanFlag(s.exactMatch) }}
							</td>
							<td class="tw-px-3 tw-py-2 tw-border-t tw-border-slate-700 tw-text-xs">
								{{ formatBooleanFlag(s.searchById) }}
							</td>
							<td class="tw-px-3 tw-py-2 tw-border-t tw-border-slate-700 tw-text-xs tw-text-slate-400">
								{{ formatTimestamp(s.timestamp) }}
							</td>
							<td class="tw-px-2 tw-py-2 tw-border-t tw-border-slate-700 tw-whitespace-nowrap">
								<button
									mat-icon-button
									type="button"
									aria-label="Confirm edit"
									[disabled]="editSaveInProgress() || !(editTitle || '').trim() || !hasEditChanged()"
									(click)="onConfirmEdit(s)">
									<mat-icon class="tw-text-base tw-text-green-600">check</mat-icon>
								</button>
								<button
									mat-icon-button
									type="button"
									aria-label="Cancel edit"
									[disabled]="editSaveInProgress()"
									(click)="onCancelEdit()">
									<mat-icon class="tw-text-base tw-text-red-500">close</mat-icon>
								</button>
							</td>
						</tr>

						<!-- Inline delete confirmation row -->
						<!--
						 * Author: Sofiia Holovko (sholovko)
						 * Task 236 - Inline delete confirmation (mirrors page delete-confirmation row)
						 -->
						<tr
							*ngIf="deletingId() === s.id"
							class="tw-bg-red-50 dark:tw-bg-red-900/20">
							<td
								class="tw-px-3 tw-py-2 tw-border-t tw-border-slate-700 tw-text-sm tw-text-red-700 dark:tw-text-red-300"
								colspan="7">
								Delete <strong>{{ s.title }}</strong>? This cannot be undone.
							</td>
							<td class="tw-px-2 tw-py-2 tw-border-t tw-border-slate-700 tw-whitespace-nowrap">
								<button
									mat-icon-button
									type="button"
									aria-label="Confirm delete"
									[disabled]="deleteInProgress()"
									(click)="onConfirmDelete(s)">
									<mat-icon class="tw-text-base tw-text-red-600">delete_forever</mat-icon>
								</button>
								<button
									mat-icon-button
									type="button"
									aria-label="Cancel delete"
									[disabled]="deleteInProgress()"
									(click)="onCancelDelete()">
									<mat-icon class="tw-text-base tw-text-slate-500">close</mat-icon>
								</button>
							</td>
						</tr>

					</ng-container>
				</tbody>
			</table>

			<!-- Edit feedback messages -->
			<div *ngIf="editErrorMessage()" class="tw-text-red-600 tw-text-sm tw-mt-2">
				{{ editErrorMessage() }}
			</div>
			<!--
			 * Author: Sofiia Holovko (sholovko)
			 * Task 236 - Success notification after an edit is confirmed (mirrors Task 212 in the page)
			 -->
			<div
				*ngIf="editSuccessMessage()"
				class="tw-text-green-600 tw-text-sm tw-mt-2 tw-flex tw-items-center tw-gap-1">
				<mat-icon class="tw-text-base tw-text-green-600">check_circle</mat-icon>
				{{ editSuccessMessage() }}
			</div>

			<!-- Delete error message -->
			<div *ngIf="deleteErrorMessage()" class="tw-text-red-600 tw-text-sm tw-mt-2">
				{{ deleteErrorMessage() }}
			</div>
		</div>

		<!-- Dialog footer -->
		<div class="tw-flex tw-justify-end tw-px-6 tw-py-3 tw-border-t tw-border-slate-200 dark:tw-border-slate-700">
			<button mat-stroked-button type="button" (click)="onClose()">Close</button>
		</div>
	`,
	styles: [
		`
			/*
			 * Author: Sofiia Holovko (sholovko)
			 * Task 236 - Mirror the editing-row highlight from the page component
			 */
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
	],
})
export class SavedSearchesDialogComponent implements OnInit {
	private readonly http = inject(HttpClient);
	private readonly dialogRef =
		inject<MatDialogRef<SavedSearchesDialogComponent, SavedSearchesDialogResult>>(
			MatDialogRef
		);
	// MAT_DIALOG_DATA is typed as SavedSearchesDialogData but unused for now
	readonly _data = inject<SavedSearchesDialogData>(MAT_DIALOG_DATA);

	private readonly SAVED_SEARCH_URL = `${apiURL}/orcs/savedSearch`;

	// ── state ──────────────────────────────────────────────────────────────
	private readonly _searches = signal<SavedSearch[]>([]);
	readonly loading = signal(false);
	readonly errorMessage = signal('');

	readonly dateAsc = signal(true);

	/**
	 * Author: Sofiia Holovko (sholovko)
	 * Task 236 - Sorted searches derived from raw list and sort direction
	 */
	readonly sortedSearches = computed<SavedSearch[]>(() => {
		const dir = this.dateAsc() ? 1 : -1;
		return [...this._searches()].sort((a, b) => {
			const at = typeof a.timestamp === 'number' ? a.timestamp : 0;
			const bt = typeof b.timestamp === 'number' ? b.timestamp : 0;
			return (at - bt) * dir;
		});
	});
	
	/**
	 * Author: Sofiia Holovko (sholovko)
	 * Task 244 - Filter input state for the Saved Searches dialog
	 */
	readonly filterQuery = signal('');

	/**
	 * Author: Sofiia Holovko (sholovko)
	 * Task 244 - Filters sortedSearches by title or query text in real time
	 */
	readonly filteredSearches = computed<SavedSearch[]>(() => {
		const q = (this.filterQuery() ?? '').trim().toLowerCase();
		if (!q) return this.sortedSearches();
		return this.sortedSearches().filter(
			(s) =>
				s.title.toLowerCase().includes(q) ||
				(s.query ?? '').toLowerCase().includes(q)
		);
	});

	/**
	 * Author: Sofiia Holovko (sholovko)
	 * Task 244 - Clear the filter input
	 */
	clearFilter(): void {
		this.filterQuery.set('');
	}

	// ── edit state ─────────────────────────────────────────────────────────
	readonly editingId = signal<number | null>(null);
	editTitle = '';
	editQuery = '';
	private _editOriginalTitle = '';
	private _editOriginalQuery = '';
	readonly editSaveInProgress = signal(false);
	readonly editErrorMessage = signal('');
	readonly editSuccessMessage = signal('');

	// ── delete state ───────────────────────────────────────────────────────
	readonly deletingId = signal<number | null>(null);
	readonly deleteInProgress = signal(false);
	readonly deleteErrorMessage = signal('');

	// ── lifecycle ──────────────────────────────────────────────────────────
	ngOnInit(): void {
		this.loadSavedSearches();
	}

	// ── data loading ───────────────────────────────────────────────────────
	private loadSavedSearches(): void {
		this.loading.set(true);
		this.errorMessage.set('');

		this.http
			.get<SavedSearch[]>(this.SAVED_SEARCH_URL)
			.pipe(take(1))
			.subscribe({
				next: (list) => {
					this._searches.set(Array.isArray(list) ? list : []);
					this.loading.set(false);
				},
				error: (err: unknown) => {
					this._searches.set([]);
					this.loading.set(false);
					this.errorMessage.set(
						err instanceof Error ? err.message : String(err)
					);
				},
			});
	}

	// ── sort ───────────────────────────────────────────────────────────────
	toggleDateSort(): void {
		this.dateAsc.update((v) => !v);
	}

	// ── timestamp formatting ───────────────────────────────────────────────
	formatTimestamp(timestamp?: number): string {
		if (!timestamp) return '-';
		const d = new Date(timestamp);
		if (Number.isNaN(d.getTime())) return '-';
		return d.toLocaleString([], {
			year: 'numeric',
			month: '2-digit',
			day: '2-digit',
			hour: '2-digit',
			minute: '2-digit',
			hour12: true,
		});
	}

	formatSelections(selections?: NamedId[]): string {
		const names = (selections ?? [])
			.map((selection) => String(selection?.name ?? '').trim())
			.filter((name) => name.length > 0);
		return names.length > 0 ? names.join(', ') : '-';
	}

	formatBooleanFlag(value?: boolean): string {
		return value ? 'Yes' : 'No';
	}

	// ── selection ──────────────────────────────────────────────────────────
	onClose(): void {
		this.dialogRef.close({ action: 'close' });
	}

	onLoadSearch(savedSearch: SavedSearch): void {
		if (this.editingId() === savedSearch.id || this.deletingId() === savedSearch.id) {
			return;
		}
		this.dialogRef.close({ action: 'load', savedSearch });
	}

	// ── inline edit ────────────────────────────────────────────────────────
	onStartEdit(s: SavedSearch, event: MouseEvent): void {
		event.stopPropagation(); // Prevent row click from triggering onLoadSearch
		this.deletingId.set(null); // Close any open delete confirmation
		this.editingId.set(s.id ?? null);
		this.editTitle = s.title;
		this.editQuery = s.query;
		this._editOriginalTitle = s.title;
		this._editOriginalQuery = s.query;
		this.editErrorMessage.set('');
		this.editSuccessMessage.set('');
	}

	/**
	 * Author: Sofiia Holovko (sholovko)
	 * Task 236 - Returns true when title or query has changed from the original snapshot
	 */
	hasEditChanged(): boolean {
		return (
			(this.editTitle || '').trim() !== this._editOriginalTitle.trim() ||
			(this.editQuery || '').trim() !== this._editOriginalQuery.trim()
		);
	}

	onConfirmEdit(s: SavedSearch): void {
		const updatedTitle = (this.editTitle || '').trim();
		if (!updatedTitle) {
			this.editErrorMessage.set('Search name is required.');
			return;
		}

		this.editErrorMessage.set('');
		this.editSaveInProgress.set(true);

		const updatedSearch: SavedSearch = {
			...s,
			title: updatedTitle,
			query: (this.editQuery || '').trim(),
		};

		this.http
			.put<SavedSearch>(`${this.SAVED_SEARCH_URL}/${s.id}`, updatedSearch)
			.pipe(take(1))
			.subscribe({
				next: () => {
					this.editSaveInProgress.set(false);
					this.editingId.set(null);
					this.editTitle = '';
					this.editQuery = '';
					// Show success notification and auto-clear after 3 s (mirrors Task 212)
					this.editSuccessMessage.set('Search updated successfully.');
					setTimeout(() => this.editSuccessMessage.set(''), 3000);
					this.loadSavedSearches();
				},
				error: (err: unknown) => {
					this.editSaveInProgress.set(false);
					this.editErrorMessage.set(
						err instanceof Error ? err.message : String(err)
					);
				},
			});
	}

	onCancelEdit(): void {
		this.editingId.set(null);
		this.editTitle = '';
		this.editQuery = '';
		this.editErrorMessage.set('');
		this.editSuccessMessage.set('');
		this._editOriginalTitle = '';
		this._editOriginalQuery = '';
	}

	/**
	 * Author: Sofiia Holovko (sholovko)
	 * Task 236 - Enter confirms, Escape cancels inline edit (mirrors Task 217 in the page)
	 */
	onEditKeydown(event: KeyboardEvent, s: SavedSearch): void {
		if (event.key === 'Enter') {
			event.preventDefault();
			event.stopPropagation();
			if ((this.editTitle || '').trim()) {
				this.onConfirmEdit(s);
			}
		} else if (event.key === 'Escape') {
			event.preventDefault();
			event.stopPropagation();
			this.onCancelEdit();
		}
	}

	// ── inline delete ──────────────────────────────────────────────────────
	onStartDelete(s: SavedSearch, event: MouseEvent): void {
		event.stopPropagation(); // Prevent row click from triggering onLoadSearch
		this.editingId.set(null); // Close any open edit row
		this.deletingId.set(s.id ?? null);
		this.deleteErrorMessage.set('');
	}

	onConfirmDelete(s: SavedSearch): void {
		this.deleteInProgress.set(true);
		this.deleteErrorMessage.set('');

		this.http
			.delete(`${this.SAVED_SEARCH_URL}/${s.id}`)
			.pipe(take(1))
			.subscribe({
				next: () => {
					this.deleteInProgress.set(false);
					this.deletingId.set(null);
					this.loadSavedSearches();
				},
				error: (err: unknown) => {
					this.deleteInProgress.set(false);
					this.deleteErrorMessage.set(
						err instanceof Error ? err.message : String(err)
					);
				},
			});
	}

	onCancelDelete(): void {
		this.deletingId.set(null);
		this.deleteErrorMessage.set('');
	}
}
