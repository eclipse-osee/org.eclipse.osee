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
import { AsyncPipe } from '@angular/common';
import { Component, inject, input } from '@angular/core';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { RouterLink } from '@angular/router';
import { CurrentActionDropDownComponent } from '@osee/configuration-management/components';
import {
	MimRouteService,
	PreferencesUIService,
} from '@osee/messaging/shared/services';
import {
	BranchPickerComponent,
	UndoButtonBranchComponent,
} from '@osee/shared/components';
import { UiService } from '@osee/shared/services';
import { iif, of, switchMap } from 'rxjs';
import { PeerReviewButtonComponent } from '../../../peer-review/components/peer-review-button/peer-review-button.component';
import { toSignal } from '@angular/core/rxjs-interop';

@Component({
	selector: 'osee-messaging-controls',
	imports: [
		AsyncPipe,
		RouterLink,
		MatIcon,
		MatTooltip,
		MatIconButton,
		CurrentActionDropDownComponent,
		BranchPickerComponent,
		UndoButtonBranchComponent,
		PeerReviewButtonComponent,
	],
	template: `<div
		class="tw-flex tw-flex-row tw-flex-wrap tw-items-end tw-justify-between tw-gap-4 tw-py-4 tw-pl-4 tw-pr-8">
		<div class="tw-flex tw-flex-wrap tw-items-end tw-gap-4">
			@if (branchControls()) {
				<osee-branch-picker
					class="tw-min-w-[350px] tw-max-w-lg"
					category="3"
					[excludeCategory]="excludedBranchCategory()"
					workType="MIM"></osee-branch-picker>
			}

			<!-- Any content can be inserted between the branch picker and the action controls -->
			<ng-content></ng-content>
		</div>

		<div
			class="tw-flex tw-flex-row tw-items-center tw-justify-end tw-gap-2">
			@if (actionControls() && branchId() && branchType()) {
				<div
					class="tw-flex tw-gap-2 tw-rounded-full tw-bg-background-app-bar tw-shadow-inner">
					@if (inEditMode()) {
						<osee-undo-button-branch></osee-undo-button-branch>
					}
					@if (branchType() === 'working' && diff()) {
						@if (inDiffMode | async; as _diff) {
							<button
								mat-icon-button
								[routerLink]="diffRouteLink()"
								queryParamsHandling="merge"
								matTooltip="Show differences between current branch and product line">
								@if (_diff === 'false') {
									<mat-icon>change_history</mat-icon>
								}
								@if (_diff === 'true') {
									<mat-icon>visibility_off</mat-icon>
								}
							</button>
						}
					}
				</div>
				<osee-current-action-drop-down
					category="3"
					workType="MIM" />
			}
			@if (peerReview()) {
				<osee-peer-review-button />
			}
		</div>
	</div>`,
})
export class MessagingControlsComponent {
	private preferencesService = inject(PreferencesUIService);
	private ui = inject(UiService);
	private mimRoutes = inject(MimRouteService);

	branchControls = input(true);
	actionControls = input(false);
	peerReview = input(true);
	diff = input(false);
	excludedBranchCategory = input<`${number}`>('5'); // Exclude peer review branches by default
	//the type below is driven by angular itself
	//eslint-disable-next-line @typescript-eslint/no-explicit-any
	diffRouteLink = input<string | any | null | undefined>(null);

	inEditMode = toSignal(this.preferencesService.inEditMode);
	branchId = toSignal(this.ui.id);
	branchType = toSignal(this.ui.type);

	inDiffMode = this.mimRoutes.isInDiff.pipe(
		switchMap((val) => iif(() => val, of('true'), of('false')))
	);
}
