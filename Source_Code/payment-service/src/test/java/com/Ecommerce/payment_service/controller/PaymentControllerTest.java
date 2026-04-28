package com.Ecommerce.payment_service.controller;

import com.Ecommerce.payment_service.dto.request.PaymentRequest;
import com.Ecommerce.payment_service.dto.response.PaymentResponse;
import com.Ecommerce.payment_service.security.JwtFilter;
import com.Ecommerce.payment_service.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtFilter jwtFilter;


    private static final String AUTH_HEADER = "Bearer test-token";



    @Test
    @WithMockUser
    void shouldDoPaymentSuccessfully() throws Exception {

        PaymentRequest request =
                new PaymentRequest(null, "CARD");

        PaymentResponse response =
                PaymentResponse.builder()
                        .paymentId(10L)
                        .orderId(1L)
                        .paymentStatus("SUCCESS")
                        .amountPaid(BigDecimal.valueOf(1000))
                        .paymentMode("CARD")
                        .build();

        when(paymentService.doPayment(any(), anyString()))
                .thenReturn(response);

        mockMvc.perform(post("/payments/{orderId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.paymentStatus").value("SUCCESS"))
                .andExpect(jsonPath("$.paymentMode").value("CARD"));
    }



    @Test
    @WithMockUser
    void shouldReturnPaymentExistsTrue() throws Exception {

        when(paymentService.isPaymentDone(1L))
                .thenReturn(true);

        mockMvc.perform(get("/payments/{orderId}/payment-exists", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }


    @Test
    @WithMockUser
    void shouldRefundPaymentInternally() throws Exception {

        PaymentResponse response =
                PaymentResponse.builder()
                        .orderId(1L)
                        .paymentStatus("REFUNDED")
                        .amountPaid(BigDecimal.valueOf(1000))
                        .paymentMode("CARD")
                        .build();

        when(paymentService.refundPayment(anyLong(), anyString()))
                .thenReturn(response);

        mockMvc.perform(post("/payments/internal/refund/{orderId}", 1L)
                        .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentStatus").value("REFUNDED"));
    }


    @Test
    @WithMockUser
    void shouldReturnRefundAlreadyProcessed() throws Exception {

        when(paymentService.isRefundAlreadyProcessed(1L))
                .thenReturn(true);

        mockMvc.perform(get("/payments/{orderId}/refund-exists", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }



    @Test
    @WithMockUser
    void shouldGetPaymentByOrderId() throws Exception {

        PaymentResponse response =
                PaymentResponse.builder()
                        .orderId(1L)
                        .paymentStatus("SUCCESS")
                        .amountPaid(BigDecimal.valueOf(1000))
                        .paymentMode("CARD")
                        .build();

        when(paymentService.getPaymentDetailsByOrderId(1L))
                .thenReturn(response);

        mockMvc.perform(get("/payments/order/{orderId}", 1L)
                        .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.paymentStatus").value("SUCCESS"));
    }



    @Test
    @WithMockUser(roles = "ADMIN")
    void adminShouldGetAllPayments() throws Exception {

        when(paymentService.getAllPaymentsForAdmin())
                .thenReturn(List.of(
                        PaymentResponse.builder().orderId(1L).build()
                ));

        mockMvc.perform(get("/payments/admin"))
                .andExpect(status().isOk());
    }


}
