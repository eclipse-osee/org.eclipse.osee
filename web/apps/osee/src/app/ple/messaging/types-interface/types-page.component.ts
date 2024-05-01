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
	ChangeDetectionStrategy,
	Component,
	OnInit,
	effect,
	input,
} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MessagingControlsComponent } from '@osee/messaging/shared/main-content';
import { PlMessagingTypesUIService } from './lib/services/pl-messaging-types-ui.service';
import { TypesInterfaceComponent } from './lib/types-interface/types-interface.component';

@Component({
	selector: 'osee-messaging-types-interface',
	template: `<div class="tw-inline-block tw-min-w-[100vw]">
		<osee-messaging-controls
			[actionControls]="true"></osee-messaging-controls>
		<osee-types-interface></osee-types-interface>
	</div>`,
	styles: [
		':host{ height: 94vh; min-height: calc(94vh - 10%); max-height: 94vh; width: 100vw; min-width: calc(100vw - 10%); display: inline-block;}',
	],
	standalone: true,
	imports: [TypesInterfaceComponent, MessagingControlsComponent],
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TypesPageComponent implements OnInit {
	type = input('', {
		transform: (value: string | undefined) =>
			value ? value.trim().toLowerCase() : '',
	});

	updateFilter = effect(
		() => {
			this.uiService.filterString = this.type();
		},
		{ allowSignalWrites: true }
	);

	constructor(
		private route: ActivatedRoute,
		private uiService: PlMessagingTypesUIService
	) {}

	ngOnInit(): void {
		this.route.paramMap.subscribe((values) => {
			this.uiService.BranchIdString = values.get('branchId') || '';
			this.uiService.branchType =
				(values.get('branchType') as 'working' | 'baseline' | '') || '';
		});
	}
}

export default TypesPageComponent;
