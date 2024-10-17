/*********************************************************************
 * Copyright (c) 2021 Boeing
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
	BreakpointObserver,
	Breakpoints,
	BreakpointState,
} from '@angular/cdk/layout';
import {
	ChangeDetectionStrategy,
	Component,
	input,
	inject,
} from '@angular/core';
import { PlatformTypeCardComponent } from '@osee/messaging/shared/main-content';
import { PlatformType } from '@osee/messaging/shared/types';
import { combineLatest } from 'rxjs';
import { PlMessagingTypesUIService } from '../services/pl-messaging-types-ui.service';

@Component({
	selector: 'osee-messaging-types-type-grid',
	template: `@for (card of platformTypes(); track card.id) {
		<osee-messaging-types-platform-type-card
			[typeData]="card"
			[attr.width]="getWidthString()"
			[style.margin]="getMarginString()"
			class="tw-box-content tw-flex-1">
		</osee-messaging-types-platform-type-card>
	}`,
	standalone: true,
	imports: [PlatformTypeCardComponent],
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TypeGridComponent {
	private breakpointObserver = inject(BreakpointObserver);
	private uiService = inject(PlMessagingTypesUIService);

	platformTypes = input.required<PlatformType[]>();
	columnCount = this.uiService.columnCount;
	gutterSize = '';
	rowHeight = '';

	constructor() {
		const breakpoint = this.breakpointObserver.observe([
			Breakpoints.XSmall,
			Breakpoints.Small,
			Breakpoints.Medium,
			Breakpoints.Large,
			Breakpoints.XLarge,
			Breakpoints.Web,
		]);
		const _combined = combineLatest([
			breakpoint,
			this.uiService.singleLineAdjustment,
		]).subscribe((result) => {
			this.updateColumnsCount(result);
		});
	}

	/**
	 * Adjusts the layout of the page based on CDK Layout Observer
	 * @param state Array containing the state of the page (i.e. what breakpoints) and whether or not to adjust the layout due to being on a single line
	 */
	updateColumnsCount(state: [BreakpointState, number]) {
		if (state[0].matches) {
			if (state[0].breakpoints[Breakpoints.XSmall]) {
				this.uiService.columnCountNumber = 1;
				this.gutterSize = '16';
			}
			if (state[0].breakpoints[Breakpoints.Small]) {
				this.uiService.columnCountNumber = 2;
				this.gutterSize = '16';
			}
			if (state[0].breakpoints[Breakpoints.Medium]) {
				this.rowHeight = 45 + state[1] + '%';
				//this.rowHeight="30%"
				this.uiService.columnCountNumber = 3;
				this.gutterSize = '24';
			}
			if (
				state[0].breakpoints[Breakpoints.Large] &&
				!state[0].breakpoints[Breakpoints.Medium]
			) {
				this.rowHeight = 45 + state[1] + '%'; //37
				this.uiService.columnCountNumber = 4;
				this.gutterSize = '24';
			}
			if (
				state[0].breakpoints[Breakpoints.XLarge] &&
				!state[0].breakpoints[Breakpoints.Large]
			) {
				//this.rowHeight = "45%";
				this.rowHeight = 45 + state[1] + '%';
				this.uiService.columnCountNumber = 5;
				this.gutterSize = '24';
			}
			if (state[0].breakpoints[Breakpoints.Web]) {
				this.rowHeight = 45 + state[1] + '%';
				this.uiService.columnCountNumber = 5;
				this.gutterSize = '24';
			}
		}
	}

	getWidthString() {
		return (
			100 / this.columnCount.getValue() + '% -' + this.gutterSize + 'px'
		);
	}
	getMarginString() {
		return this.gutterSize + 'px';
	}
}
