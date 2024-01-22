package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import com.example.demo.entities.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestInstance(Lifecycle.PER_CLASS)
class EntityUnitTest {

    @Autowired
    private TestEntityManager entityManager;

    private Doctor d1;

    private Patient p1;

    private Room r1;

    private Appointment a1;
    private Appointment a2;
    private Appointment a3;

    @BeforeAll
    void setup() {

        d1 = new Doctor("Perla", "Amalia", 24, "p.amalia@hospital.accwe");
        p1 = new Patient("Jose Luis", "Olaya", 37, "j.olaya@email.com");
        r1 = new Room("Dermatology");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        LocalDateTime startsAt = LocalDateTime.parse("19:30 24/04/2023", formatter);
        LocalDateTime finishesAt = LocalDateTime.parse("20:30 24/04/2023", formatter);

        a1 = new Appointment(p1, d1, r1, startsAt, finishesAt);
        a2 = new Appointment(p1, d1, r1, startsAt, finishesAt);
        a3 = new Appointment(p1, d1, r1, startsAt, finishesAt);
    }


    @Test
    void appointmentOverlaps() {
        assertThat(a1.overlaps(a2)).isTrue();
        assertThat(a1.overlaps(a3)).isTrue();
        assertThat(a2.overlaps(a3)).isTrue();
    }

    @Test
    void appointmentDoesNotOverlap() {

        a2.setStartsAt(a1.getFinishesAt());
        a2.setFinishesAt(a1.getFinishesAt().plusMinutes(30));

        a3.setStartsAt(a2.getFinishesAt());
        a3.setFinishesAt(a3.getStartsAt().plusMinutes(30));

        assertThat(a1.overlaps(a2)).isFalse();
        assertThat(a1.overlaps(a3)).isFalse();
        assertThat(a2.overlaps(a3)).isFalse();

    }
}