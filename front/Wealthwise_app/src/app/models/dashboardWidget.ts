import { Type } from '@angular/core';

export interface Wigdet {
  id: number;
  label: string;
  content: Type<unknown>;
  rows?:number;
  columns?:number;
}
