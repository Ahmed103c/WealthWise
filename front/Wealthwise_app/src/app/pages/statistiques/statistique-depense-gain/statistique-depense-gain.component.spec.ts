import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StatistiqueDepenseGainComponent } from './statistique-depense-gain.component';

describe('StatistiqueDepenseGainComponent', () => {
  let component: StatistiqueDepenseGainComponent;
  let fixture: ComponentFixture<StatistiqueDepenseGainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StatistiqueDepenseGainComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StatistiqueDepenseGainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
