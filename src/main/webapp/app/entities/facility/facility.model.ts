export interface IFacility {
  id?: number;
  facility_name?: string;
}

export class Facility implements IFacility {
  constructor(public id?: number, public facility_name?: string) {}
}

export function getFacilityIdentifier(facility: IFacility): number | undefined {
  return facility.id;
}
