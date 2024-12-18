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
import { Component, computed, inject, input, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { MatButton } from '@angular/material/button';
import { ActionService } from '@osee/configuration-management/services';
import { teamWorkflowDetailsImpl } from '@osee/shared/types/configuration-management';
import { filter, repeat, switchMap } from 'rxjs';
import { ActionDropDownComponent } from '../action-drop-down/action-drop-down.component';
import { branch } from '@osee/shared/types';
import { UiService } from '@osee/shared/services';

@Component({
	selector: 'osee-manage-action-button',
	imports: [MatButton, ActionDropDownComponent],
	template: `
		@if (
			teamWorkflow().id === -1 || teamWorkflow().id === 0 || isDisabled()
		) {
			<button
				mat-flat-button
				[disabled]="isDisabled()"
				(click)="getTeamWorkflowDetails()">
				{{ label() }}
			</button>
		} @else {
			<osee-action-dropdown
				[branch]="branch()"
				[teamWorkflow]="teamWorkflow()"
				(update)="updateWorkflow()" />
		}
	`,
})
export class ManageActionButtonComponent {
	label = input<string>('Transition');
	branch = input.required<branch>();
	isDisabled = input(false);

	actionService = inject(ActionService);
	uiService = inject(UiService);

	teamWorkflowId = computed(() => this.branch().associatedArtifact);
	_teamWorkflowId = signal('-1');
	_teamWorkflowId$ = toObservable(this._teamWorkflowId);

	teamWorkflow = toSignal(
		this._teamWorkflowId$.pipe(
			filter((id) => id !== '' && id !== '-1' && id !== '0'),
			switchMap((id) =>
				this.actionService
					.getTeamWorkflowDetails(id)
					.pipe(repeat({ delay: () => this.uiService.update }))
			)
		),
		{
			initialValue: new teamWorkflowDetailsImpl(),
		}
	);

	getTeamWorkflowDetails() {
		this._teamWorkflowId.set(this.teamWorkflowId());
	}

	updateWorkflow() {
		this.uiService.updated = true;
	}
}
