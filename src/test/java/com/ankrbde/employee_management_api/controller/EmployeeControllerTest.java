package com.ankrbde.employee_management_api.controller;

import com.ankrbde.employee_management_api.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService service;

    @Test
    void shouldReturnEmployee() throws Exception {

        UUID id = UUID.randomUUID();

        when(service.getEmployee(id)).thenReturn(null);

        mockMvc.perform(get("/employees/" + id))
                .andExpect(status().isOk());
    }
}