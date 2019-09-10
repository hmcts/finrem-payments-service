package uk.gov.hmcts.reform.finrem.payments.model.pba.validation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactInformationResponse {

    @JsonProperty
    private String addressLine1;
    @JsonProperty
    private String addressLine2;
    @JsonProperty
    private String addressLine3;
    @JsonProperty
    private String townCity;
    @JsonProperty
    private String county;
    @JsonProperty
    private String country;
    @JsonProperty
    private String postCode;
    @JsonProperty
    private List<DxAddressResponse> dxAddress;
}
