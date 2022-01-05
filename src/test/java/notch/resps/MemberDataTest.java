package notch.resps;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import notch.resps.MemberData;

public class MemberDataTest {
	private MemberData m1;

	@BeforeEach
	public void createDefaultObject() {
		m1 = new MemberData("member_one", 67.0, 5L);
	}

	@Test
	public void testSameInstance() {
		assertEquals(m1, m1);
	}

	@Test
	public void testDifferentInstance() {
		MemberData m2 = new MemberData("member_two", 90.0, 2L);

		assertNotEquals(m1, m2);
		assertNotEquals(m2, m1);
	}

	@Test
	public void testDifferentObject() {
		assertNotEquals(m1, new Object());
		assertNotEquals(m1, null);
	}

	@Test
	public void testRetrieveMemberName() {
		assertEquals("member_one", m1.getMemberName());
	}

	@Test
	public void testRetrieveMemberScore() {
		assertEquals(67.0, m1.getScore());
	}

	@Test
	public void testRetrieveMemberRank() {
		assertEquals(5, m1.getRank());
	}
}
