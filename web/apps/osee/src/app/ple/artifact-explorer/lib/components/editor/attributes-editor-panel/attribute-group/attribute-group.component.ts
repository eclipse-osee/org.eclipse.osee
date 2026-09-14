/*********************************************************************
 * Copyright (c) 2026 Boeing
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
	input,
	output,
	signal,
} from '@angular/core';
import { attribute } from '@osee/attributes/types';
import {
	ATTRIBUTETYPEID,
	isAttributeInstanceDeletable,
} from '@osee/attributes/constants';
import { applic } from '@osee/applicability/types';
import { PersistedArtifactAttributeEditorComponent } from '../persisted-artifact-attribute-editor/persisted-artifact-attribute-editor.component';
import {
	AttributeFieldGroupComponent,
	AttributeDeleteButtonComponent,
} from '@osee/shared/components';

/** Maximum instances shown before collapsing. */
const COLLAPSE_LIMIT = 5;

@Component({
	selector: 'osee-attribute-group',
	imports: [
		PersistedArtifactAttributeEditorComponent,
		AttributeFieldGroupComponent,
		AttributeDeleteButtonComponent,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
	template: `
		<!-- Shared presentational shell (header + bordered box); the persisted
		     editors and delete controls are projected into it. -->
		<osee-attribute-field-group
			[name]="name()"
			[count]="attrs().length">
			@for (attr of visibleAttrs(); track attr.id) {
				<div class="tw-flex tw-items-center tw-gap-1">
					<div class="tw-min-w-0 tw-flex-1">
						<osee-persisted-artifact-attribute-editor
							[attr]="attr"
							[artifactId]="artifactId()"
							[artifactApplicability]="artifactApplicability()"
							[disabled]="disabled()"
							[showLabel]="false" />
					</div>
					@if (deleteMode()) {
						<osee-attribute-delete-button
							[deletable]="isDeletable(attr)"
							disabledReason="Minimum required count reached."
							(delete)="deleteAttribute.emit(attr)" />
					}
				</div>
			}
			@if (attrs().length > collapseLimit) {
				<button
					class="tw-mt-2 tw-cursor-pointer tw-border-none tw-bg-transparent tw-p-0 tw-text-xs tw-text-primary"
					(click)="toggleExpanded()">
					@if (expanded()) {
						Show less
					} @else {
						Show
						{{ attrs().length - collapseLimit }}
						more...
					}
				</button>
			}
		</osee-attribute-field-group>
	`,
})
export class AttributeGroupComponent {
	/** Group name (attribute type name). */
	name = input.required<string>();
	/** All attribute instances in this group. */
	attrs = input.required<attribute<string, ATTRIBUTETYPEID>[]>();
	/** Artifact ID for the persisted editors. */
	artifactId = input.required<`${number}`>();
	/** Artifact applicability. */
	artifactApplicability = input.required<applic>();
	/** Whether editing is disabled. */
	disabled = input(false);
	/** Whether delete mode is active. */
	deleteMode = input(false);
	/** All attributes on the artifact (needed for multiplicity checks). */
	allAttributes = input.required<attribute<string, ATTRIBUTETYPEID>[]>();

	/** Emitted when user clicks delete on an instance. */
	deleteAttribute = output<attribute<string, ATTRIBUTETYPEID>>();

	protected readonly collapseLimit = COLLAPSE_LIMIT;
	protected readonly expanded = signal(false);

	protected readonly visibleAttrs = computed(() => {
		if (this.expanded() || this.attrs().length <= COLLAPSE_LIMIT) {
			return this.attrs();
		}
		return this.attrs().slice(0, COLLAPSE_LIMIT);
	});

	protected toggleExpanded() {
		this.expanded.update((v) => !v);
	}

	protected isDeletable(attr: attribute<string, ATTRIBUTETYPEID>): boolean {
		// Shared min-count rule (name excluded; required type needs >1 instance).
		return isAttributeInstanceDeletable(attr, this.allAttributes());
	}
}
