import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InfopersoComponent } from './infoperso.component';

describe('InfopersoComponent', () => {
  let component: InfopersoComponent;
  let fixture: ComponentFixture<InfopersoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InfopersoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InfopersoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
