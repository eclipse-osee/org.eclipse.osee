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
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import {
	extendedFeature,
	extendedFeatureWithChanges,
} from '../../types/features/base';
import { combineLatest, concatMap, filter, from, map, scan } from 'rxjs';

@Component({
	selector: 'osee-separated-feature-selector',
	standalone: true,
	imports: [
		NgIf,
		NgFor,
		AsyncPipe,
		FormsModule,
		MatFormFieldModule,
		MatSelectModule,
		MatOptionModule,
	],
	templateUrl: './separated-feature-selector.component.html',
	styles: [],
})
export class SeparatedFeatureSelectorComponent {
	features = this.currentBranchService.branchApplicFeatures.pipe(
		concatMap((features) => from(features)),
		filter(
			(feature) =>
				!(feature.name.includes(' | ') || feature.name.includes(' & '))
		),
		scan(
			(acc, curr) => [...acc, curr],
			[] as (extendedFeature | extendedFeatureWithChanges)[]
		)
	);

	@Input() selectedFeature?: extendedFeature & extendedFeatureWithChanges;

	@Output() selectedFeatureChange = new EventEmitter<
		extendedFeature & extendedFeatureWithChanges
	>();

	selectedName = this.selectedFeatureChange.pipe(
		map((feature) => feature.name)
	);
	selectedValue = new EventEmitter<string>();
	@Output() selectedFeatureNameValue = combineLatest([
		this.selectedName,
		this.selectedValue,
	]).pipe(
		map(([name, value]) => {
			return { featureName: name, featureValue: value };
		})
	);
	options = this.selectedFeatureChange.pipe(map((feature) => feature.values));

	constructor(private currentBranchService: PlConfigCurrentBranchService) {}
}
