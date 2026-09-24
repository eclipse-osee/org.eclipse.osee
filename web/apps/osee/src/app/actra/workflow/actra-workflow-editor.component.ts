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
	ChangeDetectionStrategy,
	Component,
	computed,
	DestroyRef,
	inject,
	input,
	OnInit,
	signal,
} from '@angular/core';
import { teamWorkflowDetailsImpl } from '@osee/shared/types/configuration-management';
import { COMMON_BRANCH_ID } from '@osee/shared/types';
import { ExpansionPanelComponent } from '@osee/shared/components';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import {
	catchError,
	defer,
	distinctUntilChanged,
	EMPTY,
	filter,
	map,
	merge,
	Observable,
	of,
	repeat,
	retry,
	shareReplay,
	Subject,
	switchMap,
	take,
	timer,
} from 'rxjs';
import {
	ArtifactChangeNotificationService,
	ArtifactUiService,
	BranchChangeEventService,
	BranchRoutedUIService,
	UiService,
	UserPresenceService,
} from '@osee/shared/services';
import { SseEventService } from '@osee/shared/services/network';
import {
	AttributesEditorComponent,
	PresenceAvatarsComponent,
} from '@osee/shared/components';
import { MatIcon } from '@angular/material/icon';
import { CurrentTransactionService } from '@osee/transactions/services';
import {
	CommitManagerButtonComponent,
	CreateWorkingBranchFromWorkflowButtonComponent,
} from '@osee/configuration-management/components';
import { attribute, isNewAttr } from '@osee/attributes/types';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';
import { applicabilitySentinel } from '@osee/applicability/types';
import { transactionResult } from '@osee/transactions/types';
import { ActionDropDownComponent } from '@osee/configuration-management/components';
import { ActionService } from '@osee/configuration-management/services';
import {
	CommitManagerDialogComponent,
	UpdateFromParentButtonComponent,
} from '@osee/commit/components';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { MatDialog } from '@angular/material/dialog';
import {
	ConflictResolutionBannerComponent,
	ConflictResolutionService,
} from '@osee/shared/conflict-resolution';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ActraPageTitleComponent } from '../actra-page-title/actra-page-title.component';
import { WorkflowAttachmentsComponent } from '../components/workflow-attachments/workflow-attachments.component';
import { ChangeReportButtonComponent } from '../../ple/artifact-explorer/lib/components/hierarchy/change-report-button/change-report-button.component';

/**
 * ATS work items (team workflows, actions, tasks) live on the Common branch.
 * Used for both the workflow-detail branch context and the SSE change filter.
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
	selector: 'osee-actra-workflow-editor',
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [
		ExpansionPanelComponent,
		CreateWorkingBranchFromWorkflowButtonComponent,
		ActionDropDownComponent,
		AttributesEditorComponent,
		UpdateFromParentButtonComponent,
		WorkflowAttachmentsComponent,
		MatButton,
		MatIcon,
		MatTooltip,
		CommitManagerButtonComponent,
		MatIconButton,
		ActraPageTitleComponent,
		RouterLink,
		ChangeReportButtonComponent,
		PresenceAvatarsComponent,
		ConflictResolutionBannerComponent,
	],
	templateUrl: './actra-workflow-editor.component.html',
})
export class ActraWorkflowEditorComponent implements OnInit {
	actionService = inject(ActionService);
	private readonly currentTxService = inject(CurrentTransactionService);
	uiService = inject(UiService);
	routeUrl = inject(ActivatedRoute);
	router = inject(Router);
	branchedRouter = inject(BranchRoutedUIService);
	dialog = inject(MatDialog);
	artifactUiService = inject(ArtifactUiService);
	private readonly presenceService = inject(UserPresenceService);
	private readonly changeNotification = inject(
		ArtifactChangeNotificationService
	);
	private readonly branchChangeService = inject(BranchChangeEventService);
	private readonly sseEventService = inject(SseEventService);
	private readonly conflictResolution = inject(ConflictResolutionService);
	private readonly destroyRef = inject(DestroyRef);

	/** True when the server is connected and ready; gates the save action. */
	protected readonly serverReady = this.sseEventService.serverReady;

	/**
	 * Manual refetch trigger. Emits to force the workflow to reload from the
	 * server (e.g. after discarding local changes during conflict resolution),
	 * bypassing the usual "skip refetch while dirty" guard.
	 */
	private readonly _forceRefetch$ = new Subject<void>();

	ngOnInit(): void {
		this.uiService.idValue = ATS_BRANCH_ID;
	}

	readonly id = input.required<`${number}`>();

	/**
	 * Presence context for this workflow. The workflow ID is globally unique,
	 * so the key is not branch-scoped — anyone viewing the same workflow shares
	 * this context. Empty until the routed ID is available (watchContext ignores
	 * empty keys).
	 */
	private readonly presenceContext = computed(() => {
		const workflowId = this.id();
		return workflowId ? `workflow/${workflowId}` : '';
	});

	/** Signal of other users currently viewing this same workflow. */
	protected readonly presence = this.presenceService.watchContext(
		this.presenceContext,
		this.destroyRef
	);
	workflowId$ = this.routeUrl.queryParamMap.pipe(
		map((params) => params.get('id')),
		filter((id): id is string => !!id),
		// queryParamMap can emit the same id more than once during initial navigation; collapse
		// duplicates so the details GET runs once per distinct workflow id, not per emission.
		distinctUntilChanged()
	);

	get wf() {
		return this.workflow();
	}

	workflow = toSignal(
		this.workflowId$.pipe(
			switchMap((id) =>
				this.actionService.getTeamWorkflowDetails(id).pipe(
					// Resilience for reconnect-driven refetches: the server may still be warming up
					// when it comes back, so a GET can transiently fail (e.g. 500). Retry a few
					// times with backoff, then swallow the error so it never escapes into
					// `repeat`/`toSignal` (an escaping error kills the stream and breaks change
					// detection / CDK overlays). Keep the last-known workflow to avoid flashing empty.
					retry({
						count: RESYNC_REFETCH_RETRIES,
						delay: (_err, attempt) =>
							timer(
								Math.min(
									RESYNC_REFETCH_MAX_DELAY_MS,
									RESYNC_REFETCH_BASE_DELAY_MS * 2 ** attempt
								)
							),
					}),
					catchError(() => of(new teamWorkflowDetailsImpl())),
					repeat({
						// Refetch when the workflow artifact changes (edits, transitions -- via the
						// scoped SSE stream, local + remote), when its working branch changes
						// (e.g. update-from-parent, keyed on the working branch), or on reconnect.
						// `defer` so the forward references to `workingBranchChanges$` /
						// `branchCreatedWhileNoBranch$` (declared below) resolve at subscription
						// time rather than at field-initialization time.
						delay: () =>
							defer(() =>
								merge(
									this.changeNotification
										.forArtifact(ATS_BRANCH_ID, id)
										.pipe(
											// The workflow-details payload reflects the workflow artifact's
											// own attributes/state. Reload only when those changed (edits,
											// transitions, assignee changes all emit `attribute_modified`).
											// A pure relation change that merely names the workflow as a
											// touched writeable — e.g. relating/unrelating an attachment
											// (a separate resource) — must not reload the whole editor.
											filter((inv) =>
												inv.changeTypes.includes(
													'attribute_modified'
												)
											),
											filter(() => !this.hasChanges()),
											map(() => void 0)
										),
									this.workingBranchChanges$.pipe(
										filter(() => !this.hasChanges())
									),
									this.branchCreatedWhileNoBranch$.pipe(
										filter(() => !this.hasChanges())
									),
									this.changeNotification.resync$,
									// Manual refetch (e.g. discard during conflict
									// resolution) -- always reloads, ignoring dirty.
									this._forceRefetch$
								)
							),
					})
				)
			)
		),
		{ initialValue: new teamWorkflowDetailsImpl() }
	);

	workflow$ = toObservable(this.workflow);

	/**
	 * Emits when the workflow's current working branch changes (e.g. update-from-parent). Tracks
	 * the working branch id from the loaded workflow and subscribes to branch changes for it.
	 * `defer` ensures `workflow$` is read at subscription time (after all fields initialize).
	 */
	private readonly workingBranchChanges$: Observable<void> = defer(() =>
		this.workflow$.pipe(
			map((wf) => wf.workingBranch?.id),
			filter((id) => !!id && id !== '-1'),
			distinctUntilChanged(),
			switchMap((branchId) =>
				this.branchChangeService.forBranch(branchId)
			),
			map(() => void 0)
		)
	);

	/**
	 * Emits when a working branch is created *for this workflow* while it has none. A newly created
	 * branch's id is unknown to the editor, so {@link workingBranchChanges$} (keyed on the current
	 * branch id) can't catch it. Instead match the `created` event by its `associatedArtifactId`
	 * (the branch's associated artifact = this workflow) — precise, so it does not refetch on
	 * unrelated branch creations. Gated on the workflow currently having no branch.
	 */
	private readonly branchCreatedWhileNoBranch$: Observable<void> = defer(() =>
		this.workflow$.pipe(
			map((wf) => ({
				hasNoBranch: (wf.workingBranch?.id ?? '-1') === '-1',
				workflowArtifactId: `${wf.artifact?.id ?? ''}`,
			})),
			distinctUntilChanged(
				(a, b) =>
					a.hasNoBranch === b.hasNoBranch &&
					a.workflowArtifactId === b.workflowArtifactId
			),
			switchMap(({ hasNoBranch, workflowArtifactId }) =>
				hasNoBranch && workflowArtifactId
					? this.branchChangeService.branchChanges$.pipe(
							filter(
								(event) =>
									event.changeType === 'created' &&
									event.associatedArtifactId ===
										workflowArtifactId
							)
						)
					: EMPTY
			),
			map(() => void 0)
		)
	);

	allBranchesCommitted = computed(
		() => this.wf.branchesToCommitTo.length === 0
	);

	assigneesString = computed(() =>
		this.workflow()
			.Assignees.map((assignee) => assignee.name)
			.join(', ')
	);

	updatedAttributes = signal<attribute<string, ATTRIBUTETYPEID>[]>([]);
	hasChanges = computed(() => this.updatedAttributes().length > 0);

	/**
	 * Conflict controller: owns remote-change-while-dirty detection and the
	 * resolve/discard flow. The workflow supplies only its page-specific bits
	 * (how to fetch/persist/refresh and the typeId key contract). Pending edits
	 * are keyed by `typeId` because a workflow's edited attributes can originate
	 * from type definitions (placeholder instance id) and the save path is
	 * typeId/value based.
	 */
	protected readonly conflict = this.conflictResolution.controller(
		{
			changes: toObservable(this.id).pipe(
				filter((id): id is `${number}` => !!id),
				switchMap((id) =>
					this.changeNotification.forArtifact(ATS_BRANCH_ID, id)
				)
			),
			hasUnsavedChanges: () => this.hasChanges(),
			entityName: () => this.workflow().Name,
			entityId: () => `${this.workflow().id}`,
			baseAttrs: () => this.workflow().artifact?.attributes ?? [],
			fetchServerAttrs: () =>
				this.actionService
					.getTeamWorkflowDetails(this.id())
					.pipe(map((wf) => wf.artifact?.attributes ?? [])),
			pendingValues: () =>
				new Map<string, string>(
					this.updatedAttributes().map((a) => [
						a.typeId,
						`${a.value}`,
					])
				),
			keyOptions: { keyOf: (a) => a.typeId },
			commit: (ops) =>
				this.saveAttributes({ set: ops.set, add: ops.add }).pipe(
					// Surface optimistic-concurrency rejections so a stale write is
					// re-resolved against the latest value instead of silently applied.
					map((result) => ({
						staleGammas: result.failedGammas ?? [],
					}))
				),
			refresh: () => this._forceRefetch$.next(),
			clearLocalState: () => this.clearEditState(),
			onError: (message) => (this.uiService.ErrorText = message),
		},
		this.destroyRef
	);

	workDef = toSignal(
		this.workflow$.pipe(
			filter((workflow) => workflow.id !== 0),
			switchMap((workflow) =>
				this.actionService.getWorkDefinition(workflow.id)
			)
		)
	);

	previousStates = computed(() => this.workflow().previousStates);

	twAttributeTypes = toSignal(
		this.workflow$.pipe(
			switchMap((wf) =>
				this.artifactUiService
					.getArtifactTypeAttributes(wf.artifact.typeId)
					.pipe(shareReplay({ bufferSize: 1, refCount: true }))
					.pipe(map((attrs) => structuredClone(attrs)))
			)
		)
	);

	stateAttributes = computed(() => {
		const states = new Map<string, attribute<string, ATTRIBUTETYPEID>[]>();
		if (!this.twAttributeTypes()) {
			return states;
		}
		this.workflow().previousStates.forEach((state) => {
			const attrIds = this.workDef()
				?.states.find((s) => s.name === state.state)
				?.layoutItems.filter(
					(item) =>
						item.attributeType !== null &&
						item.attributeType !== '-1'
				)
				.map((item) => item.attributeType);

			if (!attrIds) {
				return;
			}

			const attributes: attribute<string, ATTRIBUTETYPEID>[] = [];
			attrIds.forEach((attrId) => {
				let attr = this.workflow().artifact.attributes.find(
					(a) => a.typeId === attrId
				);
				if (attr) {
					attributes.push(attr);
					return;
				}
				attr = this.twAttributeTypes()?.find(
					(a) => a.typeId === attrId
				);
				if (attr) {
					attributes.push(attr);
				}
			});
			states.set(state.state, attributes);
			return;
		});

		return states;
	});

	handleUpdatedAttributes(
		updatedAttributes: attribute<string, ATTRIBUTETYPEID>[]
	) {
		updatedAttributes.forEach((attr) => {
			const index = this.updatedAttributes().findIndex(
				(a) => a.typeId === attr.typeId
			);
			if (index >= 0) {
				this.updatedAttributes.update((current) => {
					current[index] = attr;
					return current;
				});
			} else {
				this.updatedAttributes.update((current) => [...current, attr]);
			}
		});
	}

	saveChanges() {
		// Save is disabled while a conflict is pending; the banner drives resolution.
		if (
			!this.hasChanges() ||
			!this.serverReady() ||
			this.conflict.conflicted()
		) {
			return;
		}
		// Normal (non-conflict) save: persist the pending edits, then clear local
		// state and refetch so the fields reflect the saved values. Split by whether the attribute
		// already has a stored instance: existing instances (real id/gamma) are `set`, while a
		// never-before-saved attribute -- a type-template with id "-1" (e.g. a Boolean the user just
		// toggled on) -- must be `add`. Without this split the new attribute is dropped by the
		// transaction's set-mapping (which requires a valid id) and silently never persists.
		const pending = this.updatedAttributes();
		this.saveAttributes({
			set: pending.filter((a) => !isNewAttr(a)),
			add: pending.filter((a) => isNewAttr(a)),
		})
			.pipe(take(1))
			.subscribe({
				next: (res) => {
					if (res.results.success) {
						this.clearEditState();
						this._forceRefetch$.next();
					}
				},
			});
	}

	/** Clears local edit state (does not persist or refetch). */
	private clearEditState() {
		this.updatedAttributes.set([]);
	}

	/**
	 * Persists workflow attribute changes via the gamma-aware transaction path so the
	 * server can reject stale writes (optimistic concurrency). {@code set} attributes
	 * carry their instance id + gamma; {@code add} attributes are new instances. Callers
	 * own clearing state and refreshing, so this is reused by both the normal save and
	 * the shared conflict-resolution commit.
	 *
	 * Uses {@link applicabilitySentinel} because workflow attribute edits do not change
	 * applicability (matching the workflow attachment save path).
	 */
	private saveAttributes(ops: {
		set: attribute<string, ATTRIBUTETYPEID>[];
		add?: attribute<string, ATTRIBUTETYPEID>[];
	}): Observable<transactionResult> {
		return this.currentTxService.modifyArtifactAndMutate(
			'Attribute changes for workflow: ' + this.workflow().AtsId,
			`${this.workflow().id}`,
			applicabilitySentinel,
			{ set: ops.set, add: ops.add ?? [] }
		);
	}

	openCommitManager() {
		this.workflow$
			.pipe(
				take(1),
				switchMap((workflow) =>
					this.dialog
						.open(CommitManagerDialogComponent, {
							data: workflow,
							minWidth: '60%',
							width: '60%',
						})
						.afterClosed()
				)
			)
			.subscribe();
	}
}
