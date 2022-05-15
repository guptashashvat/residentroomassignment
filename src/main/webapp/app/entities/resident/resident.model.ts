import { IRoom } from 'app/entities/room/room.model';

export interface IResident {
  id?: number;
  name?: string;
  phone_number?: number;
  email?: string | null;
  room?: IRoom;
}

export class Resident implements IResident {
  constructor(public id?: number, public name?: string, public phone_number?: number, public email?: string | null, public room?: IRoom) {}
}

export function getResidentIdentifier(resident: IResident): number | undefined {
  return resident.id;
}
