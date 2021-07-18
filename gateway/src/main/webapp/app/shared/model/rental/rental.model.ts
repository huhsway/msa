export const enum RentalStatus {
  RENT_AVAILABLE = 'RENT_AVAILABLE',
  RENT_UNAVAILABLE = 'RENT_UNAVAILABLE',
}

export interface IRental {
  id?: number;
  userId?: number;
  rentalStatus?: RentalStatus;
}

export class Rental implements IRental {
  constructor(public id?: number, public userId?: number, public rentalStatus?: RentalStatus) {}
}
