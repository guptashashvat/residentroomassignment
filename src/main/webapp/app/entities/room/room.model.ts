import { IFacility } from 'app/entities/facility/facility.model';

export interface IRoom {
  id?: number;
  room_number?: number;
  facility?: IFacility;
}

export class Room implements IRoom {
  constructor(public id?: number, public room_number?: number, public facility?: IFacility) {}
}

export function getRoomIdentifier(room: IRoom): number | undefined {
  return room.id;
}
