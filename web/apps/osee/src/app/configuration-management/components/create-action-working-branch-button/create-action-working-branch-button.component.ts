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
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { UserDataAccountService } from '@osee/auth';
import { ActionService } from '@osee/configuration-management/services';
import { ActionBranchDataImpl } from '@osee/configuration-management/types';
import { BranchRoutedUIService, UiService } from '@osee/shared/services';
import { teamWorkflowDetails } from '@osee/shared/types/configuration-management';
import { combineLatest, map, switchMap, tap } from 'rxjs';

@Component({
	selector: 'osee-create-action-working-branch-button',
	imports: [MatButton, MatIcon],
	template: `<button
		mat-raised-button
		(click)="createWorkingBranch()"
		class="tw-bg-primary tw-text-background-background">
		<mat-icon>alt_route</mat-icon>Create Branch
	</button>`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CreateActionWorkingBranchButtonComponent {
	teamWorkflow = input.required<teamWorkflowDetails>();

	teamWorkflow$ = toObservable(this.teamWorkflow);

	actionService = inject(ActionService);
	userService = inject(UserDataAccountService);
	branchRoutedUiService = inject(BranchRoutedUIService);
	uiService = inject(UiService);

	createWorkingBranch() {
		combineLatest([this.teamWorkflow$, this.userService.user])
			.pipe(
				map(
					([teamWf, user]) =>
						new ActionBranchDataImpl(teamWf, user, true)
				),
				switchMap((data) =>
					this.actionService.createWorkingBranchForAction(data).pipe(
						tap((res) => {
							if (
								res.results &&
								!res.results.errors &&
								res.results.ids.length > 0
							) {
								this.uiService.updatedArtifact =
									data.associatedArt.id;
								this.branchRoutedUiService.position = {
									type: 'working',
									id: res.results.ids[0],
								};
							}
						})
					)
				)
			)
			.subscribe();
	}
}
