import {
	ChangeDetectionStrategy,
	Component,
	computed,
	inject,
	model,
	signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatAutocomplete, MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { MatOption } from '@angular/material/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActionService } from '@osee/configuration-management/services';
import { user } from '@osee/shared/types/auth';
import { catchError, map, of } from 'rxjs';

@Component({
	selector: 'osee-assignee-search',
	imports: [FormsModule, MatFormField, MatLabel, MatInput, MatAutocomplete, MatAutocompleteTrigger, MatOption],
	templateUrl: './assignee-search.component.html',
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AssigneeSearchComponent {
	private actionService = inject(ActionService);
	private loadedData = toSignal(
		this.actionService.users.pipe(
			map((users) => ({
				assignees: [...users].sort((a, b) => a.name.localeCompare(b.name)),
				loading: false,
				error: '',
			})),
			catchError(() =>
				of({
					assignees: [] as user[],
					loading: false,
					error: 'Unable to load assignees. Reload the page to try again.',
				})
			)
		),
		{ initialValue: { assignees: [] as user[], loading: true, error: '' } }
	);
	assignees = computed(() => this.loadedData().assignees);
	loading = computed(() => this.loadedData().loading);
	error = computed(() => this.loadedData().error);
	selectedAssigneeId = model<string>('');
	searchTerm = signal<string | user>('');
	displayAssignee = (value: string | user) =>
		typeof value === 'string' ? value : value?.name ?? '';

	updateSearch(value: string | user) {
		this.searchTerm.set(value);
		this.selectedAssigneeId.set(typeof value === 'string' ? '' : value.id);
	}

	filteredAssignees = computed(() => {
		const value = this.searchTerm();
		const query = typeof value === 'string' ? value.trim().toLocaleLowerCase() : '';
		return this.assignees().filter(
			(assignee) =>
				assignee.name.toLocaleLowerCase().includes(query)
		);
	});
}
