import { TestBed } from '@angular/core/testing';

import { CompteconjointService } from './compteconjoint.service';

describe('CompteconjointService', () => {
  let service: CompteconjointService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CompteconjointService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
