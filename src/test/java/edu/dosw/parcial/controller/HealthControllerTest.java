package edu.dosw.parcial.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HealthControllerTest {

    @Test
    void healthReturnsUpStatusAndServiceName() {
        HealthController controller = new HealthController();

        var response = controller.health();

        assertEquals("UP", response.get("status"));
        assertEquals("DOSW-ParcialT2", response.get("service"));
    }
}
