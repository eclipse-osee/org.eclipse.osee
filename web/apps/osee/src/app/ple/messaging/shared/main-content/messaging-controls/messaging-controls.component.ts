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
import { MatAnchor, MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { RouterLink, RouterOutlet } from '@angular/router';
import { CurrentActionDropDownComponent } from '@osee/configuration-management/components';
import {
	MimRouteService,
	PreferencesUIService,
} from '@osee/messaging/shared/services';
import {
	BranchPickerComponent,
	UndoButtonBranchComponent,
	CurrentViewSelectorComponent,
} from '@osee/shared/components';
import { UiService } from '@osee/shared/services';
import { iif, of, switchMap } from 'rxjs';

@Component({
	selector: 'osee-messaging-controls',
	standalone: true,
	imports: [
		AsyncPipe,
		RouterLink,
		RouterOutlet,
		MatAnchor,
		MatIcon,
		MatTooltip,
		MatButton,
		MatIconButton,
		CurrentActionDropDownComponent,
		BranchPickerComponent,
		UndoButtonBranchComponent,
		CurrentViewSelectorComponent,
	],
	template: `<div
		class="tw-flex tw-flex-row tw-items-end tw-justify-between tw-gap-4 tw-py-4 tw-pl-4 tw-pr-8">
		@if (branchControls()) {
			<osee-branch-picker
				class="tw-min-w-[350px] tw-max-w-lg"
				category="3"
				workType="MIM"></osee-branch-picker>
		}

		<!-- Any content can be inserted between the branch picker and the action controls -->
		<ng-content></ng-content>

		<div
			class="tw-ml-auto tw-flex tw-min-w-[480px] tw-flex-row tw-items-center tw-justify-end tw-gap-2">
			@if (
				actionControls() && (branchId | async) && (branchType | async)
			) {
				@if ((inEditMode | async) === true) {
					<osee-undo-button-branch></osee-undo-button-branch>
				}
				@if (diff()) {
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
				<osee-current-action-drop-down
					category="3"
					workType="MIM" />
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
	diff = input(false);
	//the type below is driven by angular itself
	//eslint-disable-next-line @typescript-eslint/no-explicit-any
	diffRouteLink = input<string | any | null | undefined>(null);

	inEditMode = this.preferencesService.inEditMode;
	branchId = this.ui.id;
	branchType = this.ui.type;

	inDiffMode = this.mimRoutes.isInDiff.pipe(
		switchMap((val) => iif(() => val, of('true'), of('false')))
	);
}
