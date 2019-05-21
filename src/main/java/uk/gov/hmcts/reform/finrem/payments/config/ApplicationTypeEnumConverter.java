package uk.gov.hmcts.reform.finrem.payments.config;

import uk.gov.hmcts.reform.finrem.payments.model.ApplicationType;

import java.beans.PropertyEditorSupport;

public class ApplicationTypeEnumConverter extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) {
        setValue(ApplicationType.from(text));
    }

}