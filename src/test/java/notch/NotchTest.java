package notch;

import java.util.Map;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import redis.clients.jedis.Jedis;

import notch.Notch;

public class NotchTest {
	private static Notch notch;
	private static Jedis jedis;

	@BeforeAll
	public static void init() {
		notch = new Notch("board_one");
		jedis = new Jedis("localhost", 6379);
	}

	@AfterEach
	public void cleanup() {
		jedis.flushDB();
		jedis.disconnect();
	}

	@Test
	public void testRankOneMember() {
		notch.rankMember("member_one", 45);

		assertEquals(1L, notch.totalMembers());
	}

	@Test
	public void testRankMultipleMembers() {
		Map<String, Double> members = new HashMap<>();
		members.put("member_one", 99d);
		members.put("member_two", 85d);
		members.put("member_three", 73d);
		members.put("member_four", 64d);
		members.put("member_five", 13d);
		notch.rankMember(members);

		assertEquals(5L, notch.totalMembers());
	}
}
