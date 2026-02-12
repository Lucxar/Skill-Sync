package wiegand.luca.ThesisIsComing;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ThesisIsComingApplicationTests extends TestPostgresContainer {

    @Test
    void contextLoads() {
    }

}
