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
import { AsyncPipe } from '@angular/common';
import { Component, inject, input } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { CreateActionService } from '@osee/configuration-management/services';
import { CreateAction } from '@osee/configuration-management/types';
import { map, tap } from 'rxjs';
import { CreateActionFormComponent } from '../../configuration-management/components/create-action-button/create-action-form/create-action-form.component';
import ActraPageTitleComponent from '../actra-page-title/actra-page-title.component';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
	selector: 'osee-actra-create-action-page',
	standalone: true,
	imports: [
		CreateActionFormComponent,
		AsyncPipe,
		MatButton,
		ActraPageTitleComponent,
		FormsModule,
	],
	template: `
		<div class="tw-inline-block tw-w-full">
			<osee-actra-page-title
				icon="add"
				title="Create Action">
			</osee-actra-page-title>

			@if (createActionData | async; as _createActionData) {
				<form #createActionForm="ngForm">
					<osee-create-action-form
						[data]="_createActionData"></osee-create-action-form>

					<button
						mat-flat-button
						color="primary"
						(click)="submit(_createActionData)"
						[disabled]="
							createActionForm.invalid || createActionForm.pending
						">
						Create Action
					</button>
				</form>
			}
		</div>
	`,
})
export class ActraCreateActionPageComponent {
	category = input('0', { alias: 'category' });
	workType = input('', { alias: 'workType' });

	private createActionService = inject(CreateActionService);
	router = inject(Router);

	createActionData = this.createActionService.user.pipe(
		map((thisUser) => {
			return new CreateAction(thisUser, true, this.workType());
		})
	);

	submit(createActionData: CreateAction) {
		if (createActionData !== undefined) {
			this.createActionService
				.createAction(createActionData, this.category())
				.pipe(
					tap((resp) => {
						const newWfId = resp.teamWfs.at(0);
						const urlTree = this.router.createUrlTree(
							['/actra/workflow'],
							{
								queryParams: { id: newWfId },
							}
						);
						const url = this.router.serializeUrl(urlTree);
						window.open(url, '_blank', 'noreferrer');
					})
				)
				.subscribe();
		}
	}
}
