import { ChangeDetectionStrategy, Component, model } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';

@Component({
	selector: 'osee-assignee-search',
	imports: [FormsModule, MatFormField, MatLabel, MatInput],
	templateUrl: './assignee-search.component.html',
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AssigneeSearchComponent {
	searchTerm = model('');
}
