/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import {
	Component,
	computed,
	effect,
	inject,
	OnInit,
	signal,
	viewChild,
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatSort, MatSortHeader } from '@angular/material/sort';
import {
	MatCell,
	MatCellDef,
	MatColumnDef,
	MatHeaderCell,
	MatHeaderCellDef,
	MatHeaderRow,
	MatHeaderRowDef,
	MatRow,
	MatRowDef,
	MatTable,
	MatTableDataSource,
} from '@angular/material/table';
import { ActivatedRoute, RouterLink } from '@angular/router';
import {
	catchError,
	distinctUntilChanged,
	filter,
	map,
	merge,
	of,
	repeat,
	retry,
	shareReplay,
	switchMap,
	timer,
} from 'rxjs';
import { ActraWorldHttpService } from '../services/actra-world-http.service';
import { MatButton } from '@angular/material/button';
import { ActraPageTitleComponent } from '../actra-page-title/actra-page-title.component';
import { worldRow, worldDataEmpty } from '../types/actra-types';
import {
	ArtifactChangeNotificationService,
	artifactInvalidation,
	UiService,
} from '@osee/shared/services';
import { UserDataAccountService } from '@osee/auth';
import { COMMON_BRANCH_ID } from '@osee/shared/types';
import { CreateActionButtonComponent } from '../../configuration-management/components/create-action-button/create-action-button.component';

/**
 * ATS work items (workflows, actions, tasks) live on the Common branch.
 * Used only as the branch context for downstream navigation/customizations.
 */
const ATS_BRANCH_ID = COMMON_BRANCH_ID;

/**
 * Bounded retry for reconnect-driven refetches while the server warms up. Small and capped: a
 * genuine, persistent server error should surface after a few attempts, not retry forever.
 */
const RESYNC_REFETCH_RETRIES = 4;
const RESYNC_REFETCH_BASE_DELAY_MS = 500;
const RESYNC_REFETCH_MAX_DELAY_MS = 5_000;

@Component({
	selector: 'osee-actra-world',
	imports: [
		MatFormField,
		MatInput,
		MatTable,
		MatSort,
		MatColumnDef,
		MatSortHeader,
		MatHeaderCell,
		MatHeaderCellDef,
		MatCell,
		MatCellDef,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
		MatButton,
		ActraPageTitleComponent,
		RouterLink,
		CreateActionButtonComponent,
	],
	templateUrl: './actra-world.component.html',
})
export class ActraWorldComponent implements OnInit {
	private routeUrl = inject(ActivatedRoute);
	private worldService = inject(ActraWorldHttpService);
	private changeNotification = inject(ArtifactChangeNotificationService);
	private userService = inject(UserDataAccountService);
	dataSource = new MatTableDataSource<worldRow>([]);
	uiService = inject(UiService);

	/** Current user's id, used to detect changes that newly assign work to this user. */
	private currentUserId = toSignal(
		this.userService.user.pipe(map((u) => `${u.id}`)),
		{ initialValue: '' }
	);

	ngOnInit(): void {
		this.uiService.idValue = ATS_BRANCH_ID;
	}

	params = this.routeUrl.queryParamMap.pipe(
		map((value) => {
			return {
				op: value.get('op') || '',
				collId: value.get('collId') || '',
				custId: value.get('custId') || '',
				diff: value.get('diff') || '',
			};
		}),
		// queryParamMap can emit duplicates on initial navigation; dedup by value so the world GET
		// runs once per distinct query, not per emission.
		distinctUntilChanged(
			(a, b) =>
				a.op === b.op &&
				a.collId === b.collId &&
				a.custId === b.custId &&
				a.diff === b.diff
		)
	);
	private __worldData = this.params.pipe(
		switchMap((value) => {
			// `op === 'my'` (or no collection/customer selected) shows the current user's world;
			// otherwise fetch the specific collection/customer world.
			const worldData$ = (
				value.op === 'my' || (!value.collId && !value.custId)
					? this.worldService.getWorldDataMy()
					: this.worldService.getWorldData(value.collId, value.custId)
			).pipe(
				// Resilience for reconnect-driven refetches: the server may still be warming up
				// after it comes back, so a GET can transiently fail (e.g. 500). Retry a few times
				// with backoff, then swallow the error so it never escapes into `repeat`/`toSignal`
				// (an escaping error kills the stream and breaks change detection / CDK overlays).
				// Keeping the last-known data avoids a blank view during recovery.
				retry({
					count: RESYNC_REFETCH_RETRIES,
					// `attempt` is 1-based (first retry = 1), so subtract 1 for the exponent to
					// make the first retry wait the base delay (500ms), then 1s, 2s, ... capped.
					delay: (_err, attempt) =>
						timer(
							Math.min(
								RESYNC_REFETCH_MAX_DELAY_MS,
								RESYNC_REFETCH_BASE_DELAY_MS *
									2 ** (attempt - 1)
							)
						),
				}),
				catchError(() => of(worldDataEmpty))
			);
			// Re-fetch only when a change on the ATS branch is relevant to THIS user:
			// either the change newly associates the user (e.g. a workflow assigned to
			// them — via associatedUsers), OR it touches a work item already in their
			// list (covers reassignment away / state change — matched by row id). This
			// keeps the view from refreshing on unrelated system-wide workflow changes,
			// with no server-side per-user query.
			return worldData$.pipe(
				repeat({
					// Re-fetch on a relevant change, OR on SSE resync (reconnect) since relevant
					// events may have been missed while disconnected.
					delay: () =>
						merge(
							this.changeNotification
								.forBranch(ATS_BRANCH_ID)
								.pipe(
									filter((inv) => this.isRelevantChange(inv)),
									map(() => void 0)
								),
							this.changeNotification.resync$
						),
				})
			);
		}),
		takeUntilDestroyed(),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private _worldData = toSignal(this.__worldData, {
		initialValue: worldDataEmpty,
	});

	title = computed(() => this._worldData().title);

	titleContainsMyWorld = computed(() => this.title().includes('My World'));

	worldDataLoaded = computed(
		() =>
			this._worldData().orderedHeaders.length > 0 &&
			this._worldData().rows.length > 0 &&
			this._worldData().title.length > 0
	);

	tableData = computed(() => {
		return this._worldData();
	});

	filter = signal('');
	headers = computed(() => this.tableData()?.orderedHeaders || []);
	rows = computed(() => this.tableData()?.rows || []);

	/**
	 * Artifact ids of work items currently displayed — used to detect remote changes to items
	 * already in my list. The row's artifact id is under the `Id` key (capital I; same key the
	 * template uses for the workflow routerLink), NOT `id` — matching keeps this in sync with
	 * `inv.artifactId`.
	 */
	private currentRowIds = computed(
		() =>
			new Set(
				this.rows()
					.map((row) => row['Id'])
					.filter((id) => !!id)
			)
	);

	/**
	 * A change is relevant to this user's world when it either newly associates them (e.g. a
	 * workflow assigned to them, seen via associatedUsers) or touches a work item already in
	 * their list (reassignment away / state change, matched by row id).
	 */
	private isRelevantChange(inv: artifactInvalidation): boolean {
		const userId = this.currentUserId();
		const newlyAssociated =
			!!userId &&
			inv.associatedUsers.some(
				(group) =>
					group.encoding === 'artId' && group.userIds.includes(userId)
			);
		const inMyList = this.currentRowIds().has(inv.artifactId);
		return newlyAssociated || inMyList;
	}

	protected sort = viewChild.required(MatSort);

	private _updateDataSourceSort = effect(() => {
		if (this.sort()) {
			this.dataSource.sort = this.sort();
		}
	});

	private _updateDataSourceData = effect(() => {
		this.dataSource.data = this.rows();
	});

	private _initializeSortAndFilter = effect(() => {
		if (this.sort()) {
			this.dataSource.sortingDataAccessor = (item, property) => {
				return item[property];
			};
			this.dataSource.filterPredicate = (
				row: worldRow,
				filter: string
			) => {
				const filterLower = filter.toLowerCase();
				for (const key of Object.keys(row)) {
					if (row[key].toLowerCase().includes(filterLower)) {
						return true;
					}
				}
				return false;
			};
		}
	});

	updateFilter(event: KeyboardEvent) {
		const filterValue = (event.target as HTMLInputElement).value;
		this.filter.set(filterValue);
		this.dataSource.filter = filterValue.trim().toLowerCase();
	}

	// @todo: replace with loaded default customizations
	widths: Record<string, string> = {
		Name: 'tw-max-w-full tw-font-bold',
		Description: 'tw-max-w-80',
	};

	defaultWidth = 'tw-max-w-80';

	getWidthClass(header: string): string {
		return this.widths[header] ?? this.defaultWidth;
	}
}
export default ActraWorldComponent;
