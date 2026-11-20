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
	selector: 'osee-search-workflow',
	imports: [MatFormField, MatLabel, MatSelect, MatOption, MatButton],
	templateUrl: './search-workflow.component.html',
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SearchWorkflowComponent {
	readonly options =
		input.required<readonly { id: string; name: string }[]>();
	readonly selectedWorkflowIds = model<string[]>([]);
}
