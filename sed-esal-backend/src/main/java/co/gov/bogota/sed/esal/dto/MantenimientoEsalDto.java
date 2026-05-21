package co.gov.bogota.sed.esal.dto;

public class MantenimientoEsalDto {

    private Long id;
    private EsalInformacionPrincipalDto informacionPrincipal;
    private PersoneriaJuridicaDto personeriaJuridica;

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
}
