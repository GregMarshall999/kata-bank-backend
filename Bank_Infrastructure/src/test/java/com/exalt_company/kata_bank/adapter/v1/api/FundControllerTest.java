package com.exalt_company.kata_bank.adapter.v1.api;

import com.exalt_company.fund_domain.api.FundAction;
import com.exalt_company.fund_domain.api.resource.Deposit;
import com.exalt_company.fund_domain.api.resource.Withdraw;
import com.exalt_company.fund_domain.shared.FundStatus;
import com.exalt_company.kata_bank.adapter.v1.resource.FundRequest;
import com.exalt_company.kata_bank.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FundController.class)
@AutoConfigureMockMvc(addFilters = false)
class FundControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FundAction fundAction;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void deposit_shouldReturnCreatedWhenStatusIsCreated() throws Exception {
        FundRequest request = sampleRequest();

        when(fundAction.deposit(any(Deposit.class))).thenReturn(FundStatus.CREATED);

        mockMvc.perform(post("/api/v1/fund/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(FundStatus.CREATED.name()));
    }

    @Test
    void deposit_shouldReturnOkWhenStatusIsNotCreated() throws Exception {
        FundRequest request = sampleRequest();

        when(fundAction.deposit(any(Deposit.class))).thenReturn(FundStatus.SUCCESS);

        mockMvc.perform(post("/api/v1/fund/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(FundStatus.SUCCESS.name()));
    }

    @Test
    void withdraw_shouldReturnOk() throws Exception {
        FundRequest request = sampleRequest();

        when(fundAction.withdraw(any(Withdraw.class))).thenReturn(FundStatus.SUCCESS);

        mockMvc.perform(post("/api/v1/fund/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(FundStatus.SUCCESS.name()));
    }

    private FundRequest sampleRequest() {
        return new FundRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(150.50),
                ""
        );
    }
}

