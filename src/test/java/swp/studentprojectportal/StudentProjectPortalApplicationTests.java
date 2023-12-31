package swp.studentprojectportal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import swp.studentprojectportal.utils.Validate;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
class StudentProjectPortalApplicationTests {

    @Test
    void contextLoads() {
        boolean rs = Validate.validEmail("abc@abc@");
        boolean expected = false;
        Assertions.assertEquals(expected,rs);
    }

    @Test
    public void testAddition() {
        int result = add(2, 3);
        assertEquals(6, result, "Expected the sum to be 5");
    }

    private int add(int a, int b) {
        return a + b;
    }

}
