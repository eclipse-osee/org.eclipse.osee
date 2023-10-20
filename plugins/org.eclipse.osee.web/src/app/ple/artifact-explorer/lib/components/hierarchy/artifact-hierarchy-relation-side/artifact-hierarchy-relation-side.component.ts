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
import {
	Component,
	Input,
	OnChanges,
	SimpleChanges,
	computed,
	forwardRef,
	inject,
	signal,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { MatButtonModule } from '@angular/material/button';
import { ArtifactExplorerTabService } from '../../../services/artifact-explorer-tab.service';
import { ArtifactHierarchyComponent } from '../artifact-hierarchy/artifact-hierarchy.component';
import {
	relationSide,
	relationTypeToken,
	fetchIconFromDictionary,
	artifact,
} from '../../../types/artifact-explorer.data';
import { BehaviorSubject } from 'rxjs';
import { ArtifactHierarchyPathService } from '../../../services/artifact-hierarchy-path.service';

@Component({
	selector: 'osee-artifact-hierarchy-relation-side',
	standalone: true,
	imports: [
		CommonModule,
		MatIconModule,
		MatIconModule,
		DragDropModule,
		MatButtonModule,
		forwardRef(() => ArtifactHierarchyComponent),
	],
	templateUrl: './artifact-hierarchy-relation-side.component.html',
})
export class ArtifactHierarchyRelationSideComponent {
	private tabService = inject(ArtifactExplorerTabService);

	@Input({ required: true }) set relationSide(value: relationSide) {
		if (value !== undefined) this._relationSide.set(value);
	}
	@Input() set typeToken(value: relationTypeToken) {
		if (value !== undefined) this._typeToken.set(value);
	}
	protected _relationSide = signal<relationSide>({
		name: '',
		artifacts: [],
		isSideA: false,
		isSideB: false,
	});
	protected _typeToken = signal<relationTypeToken>({
		id: '-1',
		idIntValue: -1,
		idString: '-1',
		multiplicity: '',
		name: '',
		newRelationTable: false,
		order: '',
		ordered: false,
		relationArtifactType: '',
	});
	protected typeName = computed(() => this._typeToken().name);
	protected relationSideName = computed(() => this._relationSide().name);
	protected artifacts = computed(() => this._relationSide().artifacts);
	protected isOpen = signal<boolean>(false);

	protected artifactsOpen = signal<string[]>([]);
	addArtifactsOpen(value: string) {
		this.artifactsOpen.update((rows) => [...rows, value]);
	}
	removeArtifactsOpen(value: string) {
		this.artifactsOpen.update((rows) => rows.filter((v) => v !== value));
	}
	open() {
		this.isOpen.set(true);
	}
	close() {
		this.isOpen.set(false);
	}

	fetchIcon(key: string): string {
		return fetchIconFromDictionary(key);
	}

	addTab(artifact: artifact) {
		this.tabService.addArtifact(artifact);
	}

	trackArtifacts(index: number, item: artifact) {
		return item.id;
	}

	@Input() set paths(value: string[][]) {
		if (value) {
			if (value.length > 0) {
				value.forEach((path) => {
					this.artifacts().find((art) => {
						if (art.id == path[path.length - 1]) {
							this.open();
							if (path.length > 1) {
								this.addArtifactsOpen(art.id);
							}
						}
					});
				});
				// Update the paths array that we are passing down the hierarchy
				this._paths.next([...value]);
			}
		}
	}
	protected _paths = new BehaviorSubject<string[][]>([[]]);
}
