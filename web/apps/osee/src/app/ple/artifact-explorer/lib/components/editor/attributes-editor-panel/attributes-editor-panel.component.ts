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
import { Component, computed, effect, inject, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { artifactTab } from '../../../types/artifact-explorer';
import { ArtifactExplorerHttpService } from '../../../services/artifact-explorer-http.service';
import { ArtifactExplorerTabService } from '../../../services/artifact-explorer-tab.service';
import { attribute } from '@osee/attributes/types';
import {
	ATTRIBUTETYPEID,
	BASEATTRIBUTETYPEIDENUM,
	ATTRIBUTETYPEIDENUM,
} from '@osee/attributes/constants';
import { PersistedApplicabilityDropdownComponent } from '@osee/applicability/persisted-applicability-dropdown';
import { CurrentBranchInfoService } from '@osee/shared/services';
import { FormDirective } from '@osee/shared/directives';
import { provideOptionalControlContainerNgForm } from '@osee/shared/utils';
import { PersistedArtifactAttributeEditorComponent } from './persisted-artifact-attribute-editor/persisted-artifact-attribute-editor.component';
import {
	NativeContentEditorComponent,
	NativeEditorAttributes,
	NameAttribute,
	ExtensionAttribute,
	NativeContentAttribute,
} from '../../../../../../shared/components/attributes-editor/native-content-editor/native-content-editor.component';
import { CurrentTransactionService } from '@osee/transactions/services';
import { take } from 'rxjs';

@Component({
	selector: 'osee-attributes-editor-panel',
	imports: [
		FormsModule,
		MatIcon,
		MatIconButton,
		MatTooltip,
		PersistedApplicabilityDropdownComponent,
		FormDirective,
		PersistedArtifactAttributeEditorComponent,
		NativeContentEditorComponent,
	],
	viewProviders: [provideOptionalControlContainerNgForm()],
	templateUrl: './attributes-editor-panel.component.html',
	styles: [
		`
			:host ::ng-deep .mdc-text-field--filled:hover::before {
				opacity: 0 !important;
			}
			:host ::ng-deep .mdc-text-field--filled:hover {
				background: transparent !important;
			}
		`,
	],
})
export class AttributesEditorPanelComponent {
	private currBranchInfoService = inject(CurrentBranchInfoService);
	private artExpHttpService = inject(ArtifactExplorerHttpService);
	private tabService = inject(ArtifactExplorerTabService);
	private currentTxService = inject(CurrentTransactionService);

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

	/** Sync artifact name back to tab title when resource refreshes with a new name. */
	private _nameSyncEffect = effect(() => {
		const name = this.artifactResource.value()?.name;
		if (name && name !== this.tab().artifact.name) {
			this.tabService.updateTabTitle(this.tab().artifact.id, name);
		}
	});

	/**
	 * Computed that returns the current attributes sorted by typeId.
	 * Intentionally caches the last known good value in `_lastAttributes` as a side effect
	 * so that the UI does not flash empty while the resource is refetching.
	 */
	protected attributes = computed<attribute<string, ATTRIBUTETYPEID>[]>(
		() => {
			const resourceAttrs = this.artifactResource.value()?.attributes;
			if (resourceAttrs) {
				this._lastAttributes = [...resourceAttrs].sort((a, b) =>
					a.typeId.localeCompare(b.typeId)
				);
			}
			return (
				this._lastAttributes ??
				[...this.tab().artifact.attributes].sort((a, b) =>
					a.typeId.localeCompare(b.typeId)
				)
			);
		}
	);

	private _lastAttributes: attribute<string, ATTRIBUTETYPEID>[] | null =
		null;

	/** The Name attribute (always shown first). */
	protected nameAttr = computed(() =>
		this.attributes().find(
			(a) => a.name?.toLowerCase() === 'name'
		)
	);

	/** All attributes except Name and native-content-related ones (shown after applicability). */
	protected otherAttrs = computed(() =>
		this.attributes().filter(
			(a) =>
				a.name?.toLowerCase() !== 'name' &&
				a.typeId !== ATTRIBUTETYPEIDENUM.NATIVE_CONTENT &&
				a.typeId !== ATTRIBUTETYPEIDENUM.EXTENSION
		)
	);

	/** Detect if this artifact has native content (Name + Extension + Native Content). */
	protected nativeEditorAttrs = computed<NativeEditorAttributes | null>(
		() => {
			const attrs = this.attributes();
			const name = attrs.find(
				(a) => a.typeId === BASEATTRIBUTETYPEIDENUM.NAME
			) as NameAttribute | undefined;
			const ext = attrs.find(
				(a) => a.typeId === ATTRIBUTETYPEIDENUM.EXTENSION
			) as ExtensionAttribute | undefined;
			const native = attrs.find(
				(a) => a.typeId === ATTRIBUTETYPEIDENUM.NATIVE_CONTENT
			) as NativeContentAttribute | undefined;
			return name && native
				? { name, extension: ext, nativeContent: native }
				: null;
		}
	);

	protected readonly pendingNativeName = signal<string | null>(null);
	protected readonly pendingNativeExtension = signal<string | null>(null);
	protected readonly hasUnsavedNativeChanges = signal<boolean>(false);
	private nativeContentChanges: attribute<string, ATTRIBUTETYPEID>[] = [];

	protected handleNativeContentChanges(
		changes: attribute<string, ATTRIBUTETYPEID>[]
	) {
		this.nativeContentChanges = changes;

		const nameChange = changes.find(
			(a) => a.typeId === BASEATTRIBUTETYPEIDENUM.NAME
		);
		const extChange = changes.find(
			(a) => a.typeId === ATTRIBUTETYPEIDENUM.EXTENSION
		);

		this.pendingNativeName.set(nameChange?.value ?? null);
		this.pendingNativeExtension.set(extChange?.value ?? null);
		this.hasUnsavedNativeChanges.set(changes.length > 0);
	}

	protected saveNativeContent() {
		if (this.nativeContentChanges.length === 0) return;

		// Build a single modifyArtifact call with all changes
		const setAttrs = this.nativeContentChanges.filter(
			(a) => !(a.id === '-1' && a.gammaId === '-1')
		);
		const addAttrs = this.nativeContentChanges.filter(
			(a) => a.id === '-1' && a.gammaId === '-1'
		);

		this.currentTxService
			.modifyArtifactAndMutate(
				'Updating native content',
				this.artifactId(),
				this.applicability(),
				{
					...(setAttrs.length > 0 ? { set: setAttrs } : {}),
					...(addAttrs.length > 0 ? { add: addAttrs } : {}),
				}
			)
			.pipe(take(1))
			.subscribe({
				next: () => {
					this.nativeContentChanges = [];
					this.pendingNativeName.set(null);
					this.pendingNativeExtension.set(null);
					this.hasUnsavedNativeChanges.set(false);
				},
				error: () => {
					// Leave hasUnsavedNativeChanges as true so the user knows the save failed
				},
			});
	}

	protected editable = computed<boolean>(
		() =>
			this.artifactResource.value()?.editable ??
			this.tab().artifact.editable
	);

	protected artifactId = computed<`${number}`>(
		() =>
			(this.artifactResource.value()?.id ??
				this.tab().artifact.id) as `${number}`
	);

	protected applicability = computed(
		() =>
			this.artifactResource.value()?.applicability ??
			this.tab().artifact.applicability
	);
}
