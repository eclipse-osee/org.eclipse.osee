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
import { Component, computed, inject, input } from '@angular/core';
import { MatTooltip } from '@angular/material/tooltip';
import { ATTRIBUTETYPEIDENUM } from '@osee/shared/types/constants';
import { DialogService } from '../../services/dialog.service';
import {
	isValidPLConfigAttr,
	plconfigTableEntry,
} from '../../types/pl-config-table';

@Component({
	selector: 'osee-plconfig-feature-cell',
	standalone: true,
	imports: [MatTooltip],
	template: ` <div
		[matTooltip]="description()"
		[class]="
			feature().id !== '-1' && feature().id !== ''
				? 'tw-cursor-pointer'
				: 'tw-cursor-text'
		"
		class="tw-text-inherit"
		(click)="displayFeatureDialog()">
		{{ feature().name }}
	</div>`,
})
export class PlconfigFeatureCellComponent {
	private dialogService = inject(DialogService);
	feature = input.required<plconfigTableEntry>();
	description = computed(() => {
		const attr = this.feature().attributes.find(
			(x) =>
				isValidPLConfigAttr(x) &&
				x.attributeType === ATTRIBUTETYPEIDENUM.DESCRIPTION
		);
		if (attr !== undefined && isValidPLConfigAttr(attr)) {
			return attr.value as string;
		}
		return '';
	});

	displayFeatureDialog() {
		//do not display feature menu for compound applicabilities
		if (
			this.feature().id !== '' &&
			this.feature().id !== '-1' &&
			this.feature().id !== '0'
		) {
			this.dialogService
				.displayFeatureDialog(this.feature().id)
				.subscribe();
		}
	}
}
