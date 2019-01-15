package uk.gov.hmcts.reform.finrem.payments.controllers;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.reform.finrem.payments.BaseTest;
import uk.gov.hmcts.reform.finrem.payments.service.PBAPaymentService;

abstract class BaseControllerTest extends BaseTest {

    @Autowired
    protected WebApplicationContext applicationContext;

    protected MockMvc mvc;

    @MockBean
    protected PBAPaymentService pbaPaymentService;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
    }
}
