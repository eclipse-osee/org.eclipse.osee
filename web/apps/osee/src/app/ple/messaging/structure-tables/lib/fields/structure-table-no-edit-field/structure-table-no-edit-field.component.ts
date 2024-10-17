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
import { AsyncPipe, NgClass } from '@angular/common';
import {
	ChangeDetectionStrategy,
	Component,
	computed,
	inject,
	input,
} from '@angular/core';
import { MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import {
	displayableStructureFields,
	message,
	structure,
} from '@osee/messaging/shared/types';
import { HighlightFilteredTextDirective } from '@osee/shared/utils';
import { StructureTableLongTextFieldComponent } from '../structure-table-long-text-field/structure-table-long-text-field.component';
import { attribute } from '@osee/attributes/types';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';
import { LayoutNotifierService } from '@osee/layout/notification';
import {
	trigger,
	state,
	style,
	transition,
	animate,
} from '@angular/animations';
import { MatIcon } from '@angular/material/icon';

@Component({
	selector: 'osee-structure-table-no-edit-field',
	standalone: true,
	imports: [
		MatIcon,
		MatIconButton,
		NgClass,
		MatTooltip,
		HighlightFilteredTextDirective,
		StructureTableLongTextFieldComponent,
		AsyncPipe,
	],
	template: `@switch (header()) {
		@case (' ') {
			@if (
				{
					value: rowIsExpanded(structure().id),
				};
				as _expanded
			) {
				<button
					mat-icon-button
					[@expandButton]="!_expanded.value ? 'closed' : 'open'"
					(click)="rowChange(structure(), !_expanded.value)"
					[ngClass]="
						structure().hasElementChanges
							? 'tw-bg-accent-100 tw-text-background-app-bar'
							: ''
					">
					<mat-icon
						[ngClass]="
							structure().hasElementChanges
								? 'tw-text-background-app-bar'
								: ''
						"
						>expand_more</mat-icon
					>
				</button>
			}
		}
		@case ('applicability') {
			{{ displayValue() }}
		}
		@case ('sizeInBytes') {
			<div
				[ngClass]="
					structure().incorrectlySized === true
						? 'tw-bg-warning-100 dark:tw-bg-background-app-bar dark:tw-text-warning-300'
						: ''
				"
				[matTooltip]="
					structure().incorrectlySized === true
						? 'Structure has incorrect number of bytes for byte alignment rules or word alignment rules, add spares to correct.'
						: 'Structure is properly byte aligned and word aligned.'
				"
				oseeHighlightFilteredText
				[searchTerms]="structureFilter()"
				[text]="$any(displayValue())"
				classToApply="tw-text-accent-900"
				[attr.data-cy]="
					'structure-table-' +
					header() +
					'-' +
					structure().name.value +
					'-' +
					displayValue()
				">
				{{ displayValue() }}
			</div>
		}
		@case ('name') {
			<osee-structure-table-long-text-field
				[text]="$any(displayValue())"
				[searchTerms]="structureFilter()"
				[width]="(layout | async)?.tableRecommendations?.width"
				[data_cy]="
					'structure-table-' +
					header() +
					'-' +
					structure().name.value +
					'-' +
					displayValue()
				" />
		}
		@case ('description') {
			<osee-structure-table-long-text-field
				[text]="$any(displayValue())"
				[searchTerms]="structureFilter()"
				[width]="(layout | async)?.tableRecommendations?.width"
				[data_cy]="
					'structure-table-' +
					header +
					'-' +
					structure().name.value +
					'-' +
					$any(displayValue())
				" />
		}
		@case ('txRate') {
			{{ message().interfaceMessageRate.value }}
		}
		@case ('messagePeriodicity') {
			{{ message().interfaceMessagePeriodicity.value }}
		}
		@case ('publisher') {
			@for (node of message().publisherNodes; track node) {
				{{ node.name.value }}
			}
		}
		@case ('messageNumber') {
			{{ message().interfaceMessageNumber.value }}
		}
		@default {
			@if (isAttribute(displayValue())) {
				<div
					oseeHighlightFilteredText
					[searchTerms]="structureFilter()"
					[text]="$any(displayValue()).value"
					classToApply="tw-text-accent-900"
					[attr.data-cy]="
						'structure-table-' +
						header +
						'-' +
						structure().name.value +
						'-' +
						$any(displayValue()).value
					">
					{{ $any(displayValue()).value }}
				</div>
			} @else {
				<div
					oseeHighlightFilteredText
					[searchTerms]="structureFilter()"
					[text]="$any(displayValue())"
					classToApply="tw-text-accent-900"
					[attr.data-cy]="
						'structure-table-' +
						header +
						'-' +
						structure().name.value +
						'-' +
						displayValue()
					">
					{{ displayValue() }}
				</div>
			}
		}
	}`,
	animations: [
		trigger('expandButton', [
			state('closed', style({ transform: 'rotate(0)' })),
			state('open', style({ transform: 'rotate(-180deg)' })),
			transition(
				'open <=> closed',
				animate('225ms cubic-bezier(0.42, 0.0, 0.58, 1)')
			),
		]),
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StructureTableNoEditFieldComponent {
	header = input.required<
		| keyof displayableStructureFields
		| ' '
		| 'txRate'
		| 'publisher'
		| 'subscriber'
		| 'messageNumber'
		| 'messagePeriodicity'
	>();
	structure = input.required<structure>();
	message = input.required<message>();
	private structureService = inject(STRUCTURE_SERVICE_TOKEN);
	protected structureFilter = this.structureService.structureFilter;
	private layoutNotifier = inject(LayoutNotifierService);
	protected layout = this.layoutNotifier.layout;
	protected displayValue = computed(() => {
		const header = this.header();
		const structure = this.structure();
		switch (header) {
			case 'applicability':
				if (structure[header].name !== 'Base') {
					return structure[header].name;
				}
				return '';
			case 'sizeInBytes':
				return structure[header];
			case 'name':
			case 'description':
				return structure[header].value;
			case ' ':
			case 'messageNumber':
			case 'messagePeriodicity':
			case 'txRate':
			case 'subscriber':
			case 'publisher':
				return '';
			default:
				return structure[header];
		}
	});

	rowIsExpanded(value: `${number}`) {
		return this.structureService
			.expandedRows()
			.map((s) => s.id)
			.includes(value);
	}
	rowChange(value: structure, type: boolean) {
		if (type) {
			this.expandRow(value);
		} else {
			this.hideRow(value);
		}
	}
	expandRow(value: structure) {
		this.structureService.addExpandedRow = value;
	}
	hideRow(value: structure) {
		this.structureService.removeExpandedRow = value;
	}
	protected isAttribute(
		value: unknown
	): value is attribute<unknown, ATTRIBUTETYPEID> {
		return (
			value !== undefined &&
			value !== null &&
			typeof value === 'object' &&
			'id' in value &&
			'gammaId' in value &&
			'typeId' in value &&
			'value' in value
		);
	}
}
