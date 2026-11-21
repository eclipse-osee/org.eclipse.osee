import {
	ChangeDetectionStrategy,
	Component,
	input,
	model,
} from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatOption } from '@angular/material/core';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatSelect } from '@angular/material/select';

@Component({
	selector: 'osee-assignee-search',
	imports: [MatFormField, MatLabel, MatSelect, MatOption, MatButton],
	templateUrl: './assignee-search.component.html',
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AssigneeSearchComponent {
	readonly options = input.required<readonly string[]>();
	readonly selectedAssignees = model<string[]>([]);
}
