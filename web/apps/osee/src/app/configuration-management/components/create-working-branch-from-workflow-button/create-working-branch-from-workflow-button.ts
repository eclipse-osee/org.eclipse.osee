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
import {
	ChangeDetectionStrategy,
	Component,
	inject,
	input,
} from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { Location } from '@angular/common';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { UserDataAccountService } from '@osee/auth';
import { ActionService } from '@osee/configuration-management/services';
import {
	ActionBranchDataImpl,
	actionBranchData,
} from '@osee/configuration-management/types';
import { Router } from '@angular/router';
import { MutationService } from '@osee/shared/services/network';
import { UiService } from '@osee/shared/services';
import { teamWorkflowDetails } from '@osee/shared/types/configuration-management';
import { combineLatest, map, switchMap, take, tap } from 'rxjs';

@Component({
	selector: 'osee-create-working-branch-from-workflow-button',
	imports: [MatButton, MatIcon],
	template: `<button
		mat-flat-button
		class="primary-button"
		(click)="createWorkingBranch()">
		<mat-icon>alt_route</mat-icon>Create Branch
	</button>`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CreateWorkingBranchFromWorkflowButtonComponent {
	teamWorkflow = input.required<teamWorkflowDetails>();

	teamWorkflow$ = toObservable(this.teamWorkflow);

	private readonly actionService = inject(ActionService);
	private readonly userService = inject(UserDataAccountService);
	private readonly mutation = inject(MutationService);
	private readonly uiService = inject(UiService);
	private readonly router = inject(Router);
	private readonly location = inject(Location);

	/**
	 * True only when the server reports a genuinely created branch. The create-branch
	 * endpoint returns HTTP 200 even on failure (e.g. "Invalid Parent Branch -1"), so we
	 * must gate on the authoritative `success` flag and a real id — never on `errors`
	 * alone, and never open the explorer on a sentinel id (which would resolve to COMMON).
	 */
	private isSuccess(res: actionBranchData): boolean {
		const id = res.results?.ids?.[0];
		return (
			!!res.results &&
			res.results.success &&
			!!id &&
			id !== '-1' &&
			id !== '0'
		);
	}

	/** The server's human-readable failure reason(s), or a generic fallback. */
	private failureReason(res: actionBranchData): string {
		const reasons = res.results?.results?.filter((r) => !!r) ?? [];
		return reasons.length > 0
			? reasons.join('; ')
			: 'Failed to create working branch.';
	}

	createWorkingBranch() {
		combineLatest([this.teamWorkflow$, this.userService.user])
			.pipe(
				// One click => one create. Bound the stream so repeated clicks don't accumulate
				// live subscriptions on the source signals.
				take(1),
				map(
					([teamWf, user]) =>
						[
							new ActionBranchDataImpl(teamWf, user, true),
							`${teamWf.id}`,
						] as const
				),
				switchMap(([data, workflowArtifactId]) =>
					// Chokepoint (server-ready gated) emits the local branch `created` notification;
					// the workflow editor refreshes off that (matched by associatedArtifactId, so the
					// acting tab reacts to its own emit just like other tabs react to the server
					// event). No artifact emit — branch creation writes only the branch row
					// (associated_art_id column); it doesn't touch the workflow artifact on COMMON.
					this.mutation
						.mutateAndNotify(
							this.actionService.createWorkingBranchForAction(
								data
							),
							(res) =>
								this.isSuccess(res)
									? {
											type: 'branch' as const,
											branchId: res.results!.ids[0],
											changeType: 'created' as const,
											associatedArtifactId:
												workflowArtifactId,
										}
									: null
						)
						.pipe(
							tap((res) => {
								if (!this.isSuccess(res)) {
									// 200-with-failure: surface the real reason and do NOT
									// open the explorer (which would fall back to COMMON on a
									// sentinel id). The mutateNotify above already suppressed
									// the branch-created emit for this same failure.
									this.uiService.ErrorText =
										this.failureReason(res);
									return;
								}
								const newBranchId = res.results!.ids[0];

								// Open the artifact explorer on the new working branch. Build the
								// URL via the Router (createUrlTree/serializeUrl), then run it
								// through Location.prepareExternalUrl so the app's base href
								// (e.g. "/osee/" in production) is applied. serializeUrl alone
								// yields a base-href-agnostic path; window.open needs the base
								// prepended (RouterLink does this for the template's anchor, which
								// is why the "Open branch" link works but this did not in prod).
								const relativeUrl = this.router.serializeUrl(
									this.router.createUrlTree(
										['/ple/artifact/explorer'],
										{
											queryParams: {
												branchId: newBranchId,
												branchType: 'working',
												panel: 'Artifacts',
											},
										}
									)
								);
								const externalUrl =
									this.location.prepareExternalUrl(
										relativeUrl
									);
								window.open(externalUrl, '_blank', 'noopener');
							})
						)
				)
			)
			.subscribe();
	}
}
