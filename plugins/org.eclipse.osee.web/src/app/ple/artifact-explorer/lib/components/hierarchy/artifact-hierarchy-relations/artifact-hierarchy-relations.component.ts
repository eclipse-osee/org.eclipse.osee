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
import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Observable } from 'rxjs';
import { MatIconModule } from '@angular/material/icon';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { MatButtonModule } from '@angular/material/button';
import { UiService } from '@osee/shared/services';
import { ArtifactHierarchyRelationSideComponent } from '../artifact-hierarchy-relation-side/artifact-hierarchy-relation-side.component';
import { ArtifactExplorerTabService } from '../../../services/artifact-explorer-tab.service';
import { ArtifactHierarchyOptionsService } from '../../../services/artifact-hierarchy-options.service';
import { relation, relationSide } from '../../../types/artifact-explorer.data';

@Component({
	selector: 'osee-artifact-hierarchy-relations',
	standalone: true,
	templateUrl: './artifact-hierarchy-relations.component.html',
	imports: [
		CommonModule,
		MatIconModule,
		MatIconModule,
		DragDropModule,
		MatButtonModule,
		ArtifactHierarchyRelationSideComponent,
	],
})
export class ArtifactHierarchyRelationsComponent {
	@Input() relation$!: Observable<relation[]>;

	branchId$ = this.uiService.id;

	constructor(
		private optionsService: ArtifactHierarchyOptionsService,
		private tabService: ArtifactExplorerTabService,
		private uiService: UiService
	) {}

	option$ = this.optionsService.options$;

	trackRelations(index: number, item: relation) {
		return item.relationTypeToken.id;
	}

	trackRelationSides(index: number, item: relationSide) {
		return item.name;
	}
}
