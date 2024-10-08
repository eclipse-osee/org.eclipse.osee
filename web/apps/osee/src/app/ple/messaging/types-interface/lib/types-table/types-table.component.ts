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
	computed,
	effect,
	inject,
	input,
	signal,
} from '@angular/core';
import {
	MatCell,
	MatCellDef,
	MatColumnDef,
	MatHeaderCell,
	MatHeaderCellDef,
	MatHeaderRow,
	MatHeaderRowDef,
	MatRow,
	MatRowDef,
	MatTable,
	MatTableDataSource,
} from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { PlatformType } from '@osee/messaging/shared/types';
import { PlMessagingTypesUIService } from '../services/pl-messaging-types-ui.service';
import { AttributeToValuePipe } from '@osee/attributes/pipes';

@Component({
	selector: 'osee-types-table',
	standalone: true,
	imports: [
		MatTable,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
		MatColumnDef,
		MatHeaderCell,
		MatHeaderCellDef,
		MatCell,
		MatCellDef,
		MatTooltip,
		AttributeToValuePipe,
	],
	template: `<mat-table [dataSource]="dataSource">
		@for (column of displayedColumns(); track column; let idx = $index) {
			<ng-container [matColumnDef]="column">
				<mat-header-cell
					*matHeaderCellDef
					[matTooltip]="columnMetaData()[idx].tooltip">
					{{ columnMetaData()[idx].displayName }}
				</mat-header-cell>
				<mat-cell *matCellDef="let row">
					@if (column === 'applicability') {
						{{ row[column].name }}
					} @else {
						{{ row[column] | attributeToValue }}
					}
				</mat-cell>
			</ng-container>
		}
		<mat-header-row
			*matHeaderRowDef="displayedColumns(); sticky: true"
			class="tw-text-primary-500"></mat-header-row>
		<mat-row
			*matRowDef="let row; columns: displayedColumns()"
			(click)="uiService.select(row)"
			[class]="
				uiService.selected().id === row.id
					? 'tw-bg-primary'
					: 'even:tw-bg-background-background hover:tw-bg-background-hover hover:tw-font-extrabold'
			"></mat-row>
	</mat-table>`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TypesTableComponent {
	platformTypes = input.required<PlatformType[]>();
	protected dataSource = new MatTableDataSource<PlatformType>();

	protected uiService = inject(PlMessagingTypesUIService);

	private _updateDataSource = effect(() => {
		this.dataSource.data = this.platformTypes();
	});

	protected columnMetaData = signal<
		{ key: keyof PlatformType; tooltip: string; displayName: string }[]
	>([
		{
			key: 'name',
			tooltip: 'Name of Platform Type',
			displayName: 'Name',
		},
		{
			key: 'description',
			tooltip: 'Description of Platform Type',
			displayName: 'Description',
		},
		{
			key: 'applicability',
			tooltip: 'Applicability of Platform Type',
			displayName: 'Applicability',
		},
		{
			key: 'interfaceLogicalType',
			tooltip:
				'Logical Type of Platform Type(i.e. integer, bool, enum etc.)',
			displayName: 'Type',
		},
		{
			key: 'interfacePlatformType2sComplement',
			tooltip: "Whether or not the type is the 2's complement",
			displayName: "2's Complement",
		},
		{
			key: 'interfacePlatformTypeAnalogAccuracy',
			tooltip: 'Analog accuracy of Platform Type',
			displayName: 'Accuracy',
		},
		{
			key: 'interfacePlatformTypeBitsResolution',
			tooltip: 'Resolution of Platform Type',
			displayName: 'Resolution',
		},
		{
			key: 'interfacePlatformTypeBitSize',
			tooltip: 'Size(in bits) of Platform Type',
			displayName: 'Bit Size',
		},
		{
			key: 'interfacePlatformTypeCompRate',
			tooltip: 'Comp Rate of Platform Type',
			displayName: 'Comp Rate',
		},
		{
			key: 'interfaceDefaultValue',
			tooltip: 'Default Value for Platform Type',
			displayName: 'Default Value',
		},
		{
			key: 'interfacePlatformTypeMaxval',
			tooltip: 'Maximum Value for Platform Type',
			displayName: 'Max Val',
		},
		{
			key: 'interfacePlatformTypeMinval',
			tooltip: 'Minimum Value for Platform Type',
			displayName: 'Min Val',
		},
		{
			key: 'interfacePlatformTypeMsbValue',
			tooltip: 'Most Significant Bit Value for Platform Type',
			displayName: 'MSB Val',
		},
		{
			key: 'interfacePlatformTypeUnits',
			tooltip: 'Units for Platform Type',
			displayName: 'Units',
		},
		{
			key: 'interfacePlatformTypeValidRangeDescription',
			tooltip: 'Valid Range Description for Platform Type',
			displayName: 'Valid Range',
		},
	]);

	protected displayedColumns = computed(() =>
		this.columnMetaData().map((v) => v.key)
	);
}
