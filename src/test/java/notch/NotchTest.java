package notch;

import java.util.Map;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import redis.clients.jedis.Jedis;

import notch.Notch;
import notch.resps.MemberData;

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

	@Test
	public void testCheckMember() {
		addMembersToLeaderboard();

		assertTrue(notch.checkMember("seed_one"));
		assertFalse(notch.checkMember("seed_six"));
	}

	@Test
	public void testScoreOf() {
		notch.rankMember("member_one", 45);

		assertEquals(45.0, notch.scoreOf("member_one"));
	}

	@Test
	public void testRankOf() {
		addMembersToLeaderboard();

		assertEquals(5L, notch.rankOf("seed_five"));
		assertEquals(1L, notch.rankOf("seed_one"));
	}

	@Test
	public void testScoreAndRankOf() {
		addMembersToLeaderboard();

		HashMap<String, Object> sr = notch.scoreAndRankOf("seed_three");
		assertEquals(63.0, sr.get("score"));
		assertEquals(3L, sr.get("rank"));
	}

	@Test
	public void testMemberAt() {
		addMembersToLeaderboard();

		MemberData at = notch.memberAt(2);
		assertEquals("seed_two", at.getMemberName());
		assertEquals(75.0, at.getScore());
		assertEquals(2L, at.getRank());
	}

	@Test
	public void testChangeScore() {
		addMembersToLeaderboard();

		notch.changeScore("seed_four", 29);
		assertEquals(29.0, notch.scoreOf("seed_four"));
	}

	@Test
	public void testRemoveMember() {
		addMembersToLeaderboard();

		notch.removeMember("seed_five");
		assertFalse(notch.checkMember("seed_six"));
	}

	private void addMembersToLeaderboard() {
		Map<String, Double> members = new HashMap<>();
		members.put("seed_one", 89d);
		members.put("seed_two", 75d);
		members.put("seed_three", 63d);
		members.put("seed_four", 34d);
		members.put("seed_five", 23d);
		notch.rankMember(members);
	}
}
