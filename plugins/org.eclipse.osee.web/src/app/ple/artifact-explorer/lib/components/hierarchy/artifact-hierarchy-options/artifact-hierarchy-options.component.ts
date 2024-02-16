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
import { AsyncPipe } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { MatTooltipModule } from '@angular/material/tooltip';
import { map, take } from 'rxjs';
import { ArtifactHierarchyOptionsService } from '../../../services/artifact-hierarchy-options.service';
import { CurrentBranchInfoService, UiService } from '@osee/shared/services';
import { toSignal } from '@angular/core/rxjs-interop';
import { ArtifactExplorerTabService } from '../../../services/artifact-explorer-tab.service';

@Component({
	selector: 'osee-artifact-hierarchy-options',
	standalone: true,
	imports: [
		AsyncPipe,
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
	branchId = toSignal(this.uiService.id);
	branchType = toSignal(this.uiService.type);
	branchName = toSignal(
		this.currentBranchService.currentBranch.pipe(
			map((branch) => branch.name)
		)
	);

	constructor(
		private optionsService: ArtifactHierarchyOptionsService,
		private tabService: ArtifactExplorerTabService,
		private uiService: UiService,
		private currentBranchService: CurrentBranchInfoService
	) {}

	openChangeReport() {
		this.tabService.addTab(
			'ChangeReport',
			'Change Report - ' + this.branchName()
		);
	}

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
