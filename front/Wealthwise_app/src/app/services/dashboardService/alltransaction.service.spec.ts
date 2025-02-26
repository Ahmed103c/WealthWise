import { TestBed } from '@angular/core/testing';

import { AlltransactionService } from './alltransaction.service';

describe('AlltransactionService', () => {
  let service: AlltransactionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AlltransactionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
