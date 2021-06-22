package uk.gov.hmcts.reform.finrem.payments.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Features {

    PAYMENT_REQUEST_USING_CASE_TYPE("pba_case_type");

    private final String name;
}
