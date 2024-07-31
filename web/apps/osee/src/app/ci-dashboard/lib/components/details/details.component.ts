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
	signal,
} from '@angular/core';
import { ScriptListComponent } from './script-list/script-list.component';
import { ResultListComponent } from './result-list/result-list.component';
import { TestPointTableComponent } from './test-point-table/test-point-table.component';
import { CiDashboardControlsComponent } from '../ci-dashboard-controls/ci-dashboard-controls.component';
import { CiDashboardUiService } from '../../services/ci-dashboard-ui.service';
import { ScriptTimelineComponent } from './script-timeline/script-timeline.component';
import { RunInfoComponent } from './run-info/run-info.component';
import { toSignal } from '@angular/core/rxjs-interop';
import { BehaviorSubject, iif, of, shareReplay, switchMap, take } from 'rxjs';
import { CiDetailsService } from '../../services/ci-details.service';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';
import { NgClass } from '@angular/common';
import {
	MatFormField,
	MatInput,
	MatLabel,
	MatPrefix,
} from '@angular/material/input';
import { MatTooltip } from '@angular/material/tooltip';
import { resultReferenceSentinel } from '../../types/tmo';

@Component({
	selector: 'osee-scripts',
	standalone: true,
	template: `<osee-ci-dashboard-controls />
		@if (selectedResult().id !== '-1') {
			<div class="tw-flex tw-justify-between tw-gap-8 tw-px-4 tw-pb-2">
				<div
					class="tw-flex tw-w-full tw-flex-grow tw-items-center tw-gap-4 tw-text-center">
					<button
						mat-icon-button
						class="tw-text-primary"
						(click)="setResult('-1')">
						<mat-icon>arrow_back</mat-icon>
					</button>
					<h3 class="md-headline tw-m-0">
						<span class="tw-font-bold">{{
							selectedResult().name
						}}</span>
						-
						{{ selectedResult().executionDate }}
					</h3>
				</div>
				<div class="tw-flex tw-w-full tw-flex-shrink tw-gap-4">
					<mat-form-field
						class="tw-w-full"
						subscriptSizing="dynamic">
						<mat-label>Filter Test Points</mat-label>
						<input
							matInput
							(keyup)="updateTestPointFilter($event)" />
						<mat-icon matPrefix>filter_list</mat-icon>
					</mat-form-field>
					<button
						mat-icon-button
						class="tw-text-primary"
						(click)="downloadTmo()"
						[matTooltip]="'Download TMO'">
						<mat-icon>download</mat-icon>
					</button>
					<button
						mat-icon-button
						class="hover:tw-text-primary"
						[ngClass]="{ 'tw-text-primary': expandTestPoints() }"
						(click)="toggleExpandTestPoints()"
						[matTooltip]="
							expandTestPoints() ? 'Shrink table' : 'Expand table'
						">
						@if (expandTestPoints()) {
							<mat-icon>close_fullscreen</mat-icon>
						} @else {
							<mat-icon>open_in_full</mat-icon>
						}
					</button>
				</div>
			</div>
		}
		<div class="tw-flex tw-flex-col tw-gap-12 tw-px-4">
			<div class="tw-flex tw-gap-x-6">
				<div
					class="tw-h-full"
					[ngClass]="
						selectedResult().id === '-1'
							? 'tw-visible'
							: 'tw-hidden'
					">
					<mat-form-field
						class="tw-w-full"
						subscriptSizing="dynamic">
						<mat-label>Filter Scripts</mat-label>
						<input
							matInput
							(keyup)="updateScriptListFilter($event)" />
						<mat-icon matPrefix>filter_list</mat-icon>
					</mat-form-field>
					<osee-script-list [filterText]="scriptListFilterText()" />
				</div>
				<div>
					<!-- This form field is not visible, but is here to maintain table alignment -->
					<mat-form-field
						subscriptSizing="dynamic"
						class="tw-opacity-0"
						[ngClass]="
							selectedResult().id === '-1'
								? 'tw-visible'
								: 'tw-hidden'
						">
						<input
							matInput
							disabled />
					</mat-form-field>
					<osee-result-list (resultId)="setResult($event)" />
				</div>
				@if (selectedResult().id !== '-1') {
					<div
						class="tw-overflow-x-auto"
						[ngClass]="
							expandTestPoints() ? 'tw-h-[75vh]' : 'tw-h-96'
						">
						<osee-test-point-table
							[scriptResult]="selectedResult()"
							[filterText]="testPointFilterText()" />
					</div>
				}
			</div>
			@if (!expandTestPoints()) {
				<div class="tw-flex tw-gap-x-6">
					<div class="tw-h-1/2 tw-w-3/4">
						<osee-script-timeline />
					</div>
					@if (selectedResult().id !== '-1') {
						<div>
							<osee-run-info [scriptResult]="selectedResult()" />
						</div>
					}
				</div>
			}
		</div>`,
	imports: [
		NgClass,
		ScriptListComponent,
		ResultListComponent,
		TestPointTableComponent,
		ScriptTimelineComponent,
		RunInfoComponent,
		CiDashboardControlsComponent,
		MatIcon,
		MatIconButton,
		MatFormField,
		MatInput,
		MatLabel,
		MatPrefix,
		MatTooltip,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class DetailsComponent {
	uiService = inject(CiDashboardUiService);
	ciDetailsService = inject(CiDetailsService);

	branchId = toSignal(this.uiService.branchId);
	branchType = toSignal(this.uiService.branchType);

	selectedResultId = new BehaviorSubject<`${number}`>('-1');
	scriptListFilterText = signal('');
	testPointFilterText = signal('');
	expandTestPoints = signal(false);

	private _selectedResult$ = this.selectedResultId.pipe(
		switchMap((id) =>
			iif(
				() => id === '-1',
				of(resultReferenceSentinel),
				of(id).pipe(
					switchMap((id) => this.ciDetailsService.getScriptResult(id))
				)
			)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	selectedResult = toSignal(this._selectedResult$, {
		initialValue: resultReferenceSentinel,
	});

	toggleExpandTestPoints() {
		this.expandTestPoints.update((value) => !value);
	}

	downloadTmo() {
		this.ciDetailsService
			.downloadTmo(this.selectedResultId.value.toString())
			.pipe(take(1))
			.subscribe();
	}

	setResult(id: `${number}`) {
		this.selectedResultId.next(id);
	}

	updateScriptListFilter(event: Event) {
		const filterValue = (event.target as HTMLInputElement).value;
		this.scriptListFilterText.set(filterValue);
	}

	updateTestPointFilter(event: Event) {
		const filterValue = (event.target as HTMLInputElement).value;
		this.testPointFilterText.set(filterValue);
	}
}
