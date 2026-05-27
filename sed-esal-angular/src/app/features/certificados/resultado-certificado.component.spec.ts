import { TestBed } from '@angular/core/testing';
import { convertToParamMap, ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { ResultadoCertificadoComponent } from './resultado-certificado.component';
import { ApiService } from '../../core/services/api.service';
import { CertificadoDto } from '../../core/models/esal.model';

describe('ResultadoCertificadoComponent', () => {
  const certificado: CertificadoDto = {
    certificadoId: 1,
    esalId: 10,
    numeroCertificado: 'ESAL-2026-000001',
    nombreArchivo: 'certificado.pdf',
    estadoCertificado: 'GENERADO',
    fechaExpedicion: '2026-05-27T16:16:00',
    idSipej: '54211',
    nit: '900519721-6',
    hashSha256: 'hash',
    tamanoBytes: 4096,
    plantillaVersion: 'I3-v1',
    firmanteNombre: 'Lida Diaz Velandia',
    firmanteCargo: 'Directora de Inspeccion y Vigilancia',
    versionDatos: '2026-05-27T16:16:00',
    errorDetalle: null,
    createdAt: '2026-05-27T16:16:00',
    createdBy: 'admin@educacionbogota.edu.co',
  };

  it('descarga el PDF usando el ApiService autenticado', () => {
    const api = jasmine.createSpyObj('ApiService', ['get', 'download']);
    api.get.and.returnValue(of(certificado));
    api.download.and.returnValue(of(new Blob(['pdf'], { type: 'application/pdf' })));

    const router = jasmine.createSpyObj<Router>('Router', ['navigate']);

    TestBed.configureTestingModule({
      imports: [ResultadoCertificadoComponent],
      providers: [
        { provide: ApiService, useValue: api },
        { provide: Router, useValue: router },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({ certificadoId: '1' }),
            },
          },
        },
      ],
    });

    const fixture = TestBed.createComponent(ResultadoCertificadoComponent);
    fixture.detectChanges();

    spyOn(URL, 'createObjectURL').and.returnValue('blob:certificado');
    spyOn(URL, 'revokeObjectURL');
    const enlace = jasmine.createSpyObj<HTMLAnchorElement>('HTMLAnchorElement', ['click']);
    const createElementOriginal = document.createElement.bind(document);
    spyOn(document, 'createElement').and.callFake(((tagName: string) => {
      if (tagName.toLowerCase() === 'a') {
        return enlace;
      }
      return createElementOriginal(tagName);
    }) as typeof document.createElement);

    const botones = Array.from(
      fixture.nativeElement.querySelectorAll('button')
    ) as HTMLButtonElement[];
    const boton = botones.find((element) =>
      element.textContent?.includes('Descargar PDF')
    ) as HTMLButtonElement;

    expect(boton).toBeTruthy();

    boton.click();

    expect(api.download).toHaveBeenCalledWith('/api/certificados/1/descargar');
    expect(enlace.click).toHaveBeenCalled();
    expect(URL.revokeObjectURL).toHaveBeenCalledWith('blob:certificado');
  });
});
