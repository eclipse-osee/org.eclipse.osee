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
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { MatTooltipModule } from '@angular/material/tooltip';
import { map, take } from 'rxjs';
import { ArtifactHierarchyOptionsService } from '../../../services/artifact-hierarchy-options.service';

@Component({
	selector: 'osee-artifact-hierarchy-options',
	standalone: true,
	imports: [
		CommonModule,
		MatIconModule,
		MatMenuModule,
		MatButtonModule,
		MatIconModule,
		RouterLink,
		MatTooltipModule,
	],
	templateUrl: './artifact-hierarchy-options.component.html',
})
export class ArtifactHierarchyOptionsComponent {
	option$ = this.optionsService.options$;

	constructor(private optionsService: ArtifactHierarchyOptionsService) {}

	toggleShowRelations() {
		this.option$
			.pipe(
				take(1),
				map((currentOptions) => {
					const updatedOptions = {
						...currentOptions,
						showRelations: !currentOptions.showRelations,
					};
					this.optionsService.updateOptions(updatedOptions);
				})
			)
			.subscribe();
	}
}
