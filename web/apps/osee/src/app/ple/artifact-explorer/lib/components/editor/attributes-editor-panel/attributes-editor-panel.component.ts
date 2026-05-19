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
import { NgClass } from '@angular/common';
import { Component, computed, inject, input, viewChild } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { AttributesEditorComponent } from '@osee/shared/components';
import { FormDirective } from '@osee/shared/directives';
import {
	legacyAttributeType,
	legacyModifyArtifact,
	legacyTransaction,
} from '@osee/transactions/types';
import { BehaviorSubject, tap } from 'rxjs';
import { artifactTab } from '../../../types/artifact-explorer';
import { ArtifactExplorerHttpService } from '../../../services/artifact-explorer-http.service';
import { MatIcon } from '@angular/material/icon';
import { ExpansionPanelComponent } from '@osee/shared/components';
import { attribute } from '@osee/shared/types';
import { TransactionService } from '@osee/transactions/services';
import { PersistedApplicabilityDropdownComponent } from '@osee/applicability/persisted-applicability-dropdown';
import { MatTooltipModule } from '@angular/material/tooltip';
import { CurrentBranchInfoService, UiService } from '@osee/shared/services';

@Component({
	selector: 'osee-attributes-editor-panel',
	imports: [
		NgClass,
		FormsModule,
		AttributesEditorComponent,
		FormDirective,
		MatIcon,
		ExpansionPanelComponent,
		PersistedApplicabilityDropdownComponent,
		MatTooltipModule,
	],
	templateUrl: './attributes-editor-panel.component.html',
})
export class AttributesEditorPanelComponent {
	private transactionService = inject(TransactionService);
	private currBranchInfoService = inject(CurrentBranchInfoService);
	private uiService = inject(UiService);
	private artExpHttpService = inject(ArtifactExplorerHttpService);

	tab = input.required<artifactTab>();

	branchHasPleCategory = this.currBranchInfoService.branchHasPleCategory;

	// Derived signals for the resource
	private branchId = computed(() => this.tab().branchId);
	private _artifactId = computed(() => this.tab().artifact.id);
	private viewId = computed(() => this.tab().viewId);

	// Reactive artifact resource that auto-refreshes via uiService.updateCount()
	private artifactResource =
		this.artExpHttpService.getArtifactWithRelationsResource(
			this.branchId,
			this._artifactId,
			this.viewId
		);

	protected attributes = computed<attribute[]>(
		() =>
			this.artifactResource.value()?.attributes ??
			this.tab().artifact.attributes
	);

	protected editable = computed<boolean>(
		() =>
			this.artifactResource.value()?.editable ??
			this.tab().artifact.editable
	);

	protected artifactId = computed<string>(
		() => this.artifactResource.value()?.id ?? this.tab().artifact.id
	);

	saveChanges() {
		if (this.updatedAttributes.value.length > 0) {
			const t = this.tab();
			const tx: legacyTransaction = {
				branch: t.branchId,
				txComment: 'Web Attribute Save',
			};

			const existingAttributes: legacyAttributeType[] = [];
			const newAttributes: legacyAttributeType[] = [];

			for (const attr of this.updatedAttributes.value) {
				const legacyAttr: legacyAttributeType = {
					typeId: attr.typeId,
					value: attr.value,
				};
				if (attr.id === '-1') {
					newAttributes.push(legacyAttr);
				} else {
					existingAttributes.push(legacyAttr);
				}
			}

			const modifyArtifact: legacyModifyArtifact = {
				id: t.artifact.id,
			};
			if (existingAttributes.length > 0) {
				modifyArtifact.setAttributes = existingAttributes;
			}
			if (newAttributes.length > 0) {
				modifyArtifact.addAttributes = newAttributes;
			}

			tx.modifyArtifacts = [modifyArtifact];
			this.transactionService
				.performMutation(tx)
				.pipe(
					tap(() => {
						this.updatedAttributes.next([]);
						this._attributesEditor()?.resetAfterSave();
						this.uiService.updated = true;
					})
				)
				.subscribe();
		}
	}

	// Handle output attributes returned from attribute editor

	updatedAttributes = new BehaviorSubject<attribute[]>([]);
	handleUpdatedAttributes(updatedAttributes: attribute[]) {
		this.updatedAttributes.next(updatedAttributes);
	}

	hasChanges(): boolean {
		return this.updatedAttributes.value.length > 0;
	}

	// Handle form status change

	protected _attributesEditorForm = viewChild.required(
		'attributesEditorForm',
		{ read: NgForm }
	);

	protected _attributesEditor =
		viewChild<AttributesEditorComponent>('attributesEditor');
}
