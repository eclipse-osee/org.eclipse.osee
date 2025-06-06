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
import { AsyncPipe } from '@angular/common';
import { Component, Input, inject } from '@angular/core';
import { UiService } from '@osee/shared/services';
import { Observable } from 'rxjs';
import { ArtifactHierarchyOptionsService } from '../../../services/artifact-hierarchy-options.service';
import { ArtifactHierarchyRelationSideComponent } from '../artifact-hierarchy-relation-side/artifact-hierarchy-relation-side.component';
import {
	artifactRelation,
	artifactRelationSide,
} from '@osee/artifact-with-relations/types';

@Component({
	selector: 'osee-artifact-hierarchy-relations',
	templateUrl: './artifact-hierarchy-relations.component.html',
	imports: [AsyncPipe, ArtifactHierarchyRelationSideComponent],
})
export class ArtifactHierarchyRelationsComponent {
	private optionsService = inject(ArtifactHierarchyOptionsService);
	private uiService = inject(UiService);

	@Input() relation$!: Observable<artifactRelation[]>;
	@Input() paths!: string[][];

	branchId$ = this.uiService.id;

	option$ = this.optionsService.options$;

	trackRelations(_index: number, item: artifactRelation) {
		return item.relationTypeToken.id;
	}

	trackRelationSides(_index: number, item: artifactRelationSide) {
		return item.name;
	}
}
