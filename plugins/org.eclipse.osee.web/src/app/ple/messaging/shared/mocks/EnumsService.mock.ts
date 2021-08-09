import { of } from "rxjs";
import { EnumsService } from "../services/http/enums.service";

export const enumsServiceMock: Partial<EnumsService> = {
    rates:of(['1','10','15','20']),
    types:of(['Network','Connection']),
    periodicities: of(['Periodic', 'Aperiodic', 'OnDemand']),
    categories:of([ "BIT Status", "Flight Test", "Miscellaneous", "N/A", "Network", "Tactical Status", "Taskfile", "Trackfile", "spare" ])
}