package co.gov.bogota.sed.esal.dto;

import java.util.ArrayList;
import java.util.List;

public class MantenimientoEsalDto {

    private Long id;
    private EsalInformacionPrincipalDto informacionPrincipal;
    private PersoneriaJuridicaDto personeriaJuridica;
    private List<NombramientoDto> representantes = new ArrayList<>();
    private List<OrganoAdministracionDto> organosAdministracion = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EsalInformacionPrincipalDto getInformacionPrincipal() { return informacionPrincipal; }
    public void setInformacionPrincipal(EsalInformacionPrincipalDto informacionPrincipal) {
        this.informacionPrincipal = informacionPrincipal;
    }

    public PersoneriaJuridicaDto getPersoneriaJuridica() { return personeriaJuridica; }
    public void setPersoneriaJuridica(PersoneriaJuridicaDto personeriaJuridica) {
        this.personeriaJuridica = personeriaJuridica;
    }

    public List<NombramientoDto> getRepresentantes() { return representantes; }
    public void setRepresentantes(List<NombramientoDto> representantes) { this.representantes = representantes; }

    public List<OrganoAdministracionDto> getOrganosAdministracion() { return organosAdministracion; }
    public void setOrganosAdministracion(List<OrganoAdministracionDto> organosAdministracion) {
        this.organosAdministracion = organosAdministracion;
    }
}
