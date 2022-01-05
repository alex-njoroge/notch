package notch;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import redis.clients.jedis.resps.Tuple;
import redis.clients.jedis.Jedis;

import notch.resps.MemberData;

/**
 * The main entity that creates a leaderboard and performs various
 * actions on it
 */
public class Notch {
	private static String redisHost = "localhost";
	private static int redisPort = 6379;
	private static int redisDatabase = 0;
	private static long pageSize = 25;
	public String leaderboardName;
	private Jedis jedis;

	/**
	 * Creates a new leaderboard providing only its name.
	 *
	 * @param name the leaderboard's name
	 */
	public Notch(String name) {
		this(name, redisHost, redisPort);
	}

	/**
	 * Creates a new leaderboard providing its name and extra configuration
	 * details.
	 *
	 * @param name the leaderboard's name
	 * @param host the host name the Redis instance is running on
	 * @param port the port number the Redis instance is running on
	 */
	public Notch(String name, String host, int port) {
		leaderboardName = name;
		jedis = new Jedis(host, port);
		jedis.select(redisDatabase);
	}

	public void setHost(String host) {
		this.redisHost = host;
	}

	public String getHost() {
		return redisHost;
	}

	public void setPort(int port) {
		this.redisPort = port;
	}

	public int getPort() {
		return redisPort;
	}

	/**
	 * Sets the zero-based numeric index of a Redis logical database.
	 *
	 * @param database the database index
	 */
	public void setDatabase(int database) {
		this.redisDatabase = database;
	}

	/**
	 * Gets the zero-based numeric index of the Redis logical database in use.
	 *
	 * @return the database index
	 */
	public int getDatabase() {
		return redisDatabase;
	}

	/**
	 * Sets the number of members in a page.
	 *
	 * @param size the number of members
	 */
	public void setPageSize(long size) {
		this.pageSize = size;
	}

	/**
	 * Gets the number of members in a page.
	 *
	 * @return the number of members
	 */
	public long getPageSize() {
		return pageSize;
	}

	/**
	 * Adds one member to the leaderboard.
	 *
	 * @param memberName the name of the member
	 * @param score the score of the member
	 * @return the number of members added (excluding score updates)
	 */
	public long rankMember(String memberName, double score) {
		return jedis.zadd(leaderboardName, score, memberName);
	}

	/**
	 * Adds multiple members to the leaderboard.
	 *
	 * @param memberScores a key/value collection of members with their scores
	 * @return the number of members added (excluding score updates)
	 */
	public long rankMember(Map<String, Double> memberScores) {
		return jedis.zadd(leaderboardName, memberScores);
	}

	/**
	 * Checks whether a member is in the leaderboard.
	 *
	 * @param memberName the name of the member
	 * @return whether or not the member exists
	 */
	public boolean checkMember(String memberName) {
		if (jedis.zscore(leaderboardName, memberName) == null) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the score of a member.
	 *
	 * @param memberName the name of the member
	 * @return the score of a member
	 */
	public Double scoreOf(String memberName) {
		return jedis.zscore(leaderboardName, memberName);
	}

	/**
	 * Gets the rank of a member.
	 *
	 * @param memberName the name of the member
	 * @return the 1-based (the member with the highest score has rank 1)
	 * rank of a member
	 */
	public Long rankOf(String memberName) {
		Long rank = jedis.zrevrank(leaderboardName, memberName);
		if (rank != null) {
			rank++;
		}
		return rank;
	}

	/**
	 * Gets the score and rank of a member.
	 *
	 * @param memberName the name of the member
	 * @return a key/value collection of a member's score and rank
	 */
	public HashMap<String, Object> scoreAndRankOf(String memberName) {
		HashMap<String, Object> data = new HashMap<>();
		data.put("score", scoreOf(memberName));
		data.put("rank", rankOf(memberName));
		return data;
	}

	/**
	 * Retrieves information about all members.
	 *
	 * @return all members in the leaderboard with their names, scores and ranks
	 */
	public List<MemberData> allMembers() {
		return kneadRawMemberData(jedis.zrevrangeWithScores(leaderboardName, 0, -1));
	}

	/**
	 * Retrieves information about members within a score range.
	 *
	 * @param min the lower score limit
	 * @param max the upper score limit
	 * @return members within the score range with their names, scores and ranks
	 */
	public List<MemberData> membersinScoreRange(double min, double max) {
		return kneadRawMemberData(jedis.zrevrangeByScoreWithScores(leaderboardName, max, min));
	}

	/**
	 * Retrieves information about members within a rank range.
	 *
	 * @param start the lower rank limit
	 * @param stop the upper rank limit
	 * @return members within the rank range with their names, scores and ranks
	 */
	public List<MemberData> membersinRankRange(long start, long stop) {
		return kneadRawMemberData(jedis.zrevrangeWithScores(leaderboardName, start-1, stop-1));
	}

	/**
	 * Retrieves information about the member at a specific rank.
	 *
	 * @param rank the rank of the member
	 * @return the member's name, score and rank
	 */
	public MemberData memberAt(long rank) {
		Tuple raw = (jedis.zrevrangeWithScores(leaderboardName, rank-1, rank-1)).get(0);
		return new MemberData(raw.getElement(), raw.getScore(), rankOf(raw.getElement()));
	}

	/**
	 * Retrieves information about the first page of the leaderboard.
	 *
	 * @return members in the first page with their names, scores and ranks
	 */
	public List<MemberData> firstPage() {
		return kneadRawMemberData(jedis.zrevrangeWithScores(leaderboardName, 0, pageSize-1));
	}

	/**
	 * Retrieves information about the last page of the leaderboard.
	 *
	 * @return members in the last page with their names, scores and ranks
	 */
	public List<MemberData> lastPage() {
		return kneadRawMemberData(jedis.zrevrangeWithScores(leaderboardName, -pageSize, -1));
	}

	/**
	 * Retrieves information about members above and below a specific member.
	 *
	 * @param memberName the name of the member
	 * @return members around with their names, scores and ranks
	 */
	public List<MemberData> around(String memberName) {
		Long rank = rankOf(memberName);
		List<Tuple> upper = jedis.zrevrangeWithScores(leaderboardName, 0, rank-2);
		List<Tuple> lower = jedis.zrevrangeWithScores(leaderboardName, rank, -1);
		upper.addAll(lower);
		return kneadRawMemberData(upper);
	}

	/**
	 * Changes the score of a member.
	 *
	 * @param memberName the name of the member
	 * @param score the new score
	 * @return the number of members whose score has been changed
	 */
	public long changeScore(String memberName, double score) {
		return jedis.zadd(leaderboardName, score, memberName);
	}

	/**
	 * Changes the score of a member by some amount.
	 *
	 * @param memberName the name of the member
	 * @param delta the amount to change by
	 * @return the new score of the member
	 */
	public double updateScore(String memberName, double delta) {
		return jedis.zincrby(leaderboardName, delta, memberName);
	}

	/**
	 * Removes member(s) from the leaderboard.
	 *
	 * @param memberName the name of the member
	 * @return the number of members removed
	 */
	public long removeMember(String... memberName) {
		return jedis.zrem(leaderboardName, memberName);
	}

	/**
	 * Removes members with and below a specific score.
	 *
	 * @param score
	 * @return the number of members removed
	 */
	public long removeMembersBelow(String score) {
		return jedis.zremrangeByScore(leaderboardName, "-inf", String.valueOf(Integer.parseInt(score)-1));
	}

	/**
	 * Utility method to sanitize element data returned by {@code Jedis} and return
	 * it in an organized format with additional information.
	 *
	 * @param rawMemberData data returned by a {@code Jedis} method
	 * @return sanitized data objects with utility methods
	 */
	private List<MemberData> kneadRawMemberData(List<Tuple> rawMemberData) {
		List<MemberData> kneaded = new ArrayList<>();
		for(Tuple raw: rawMemberData) {
			kneaded.add(new MemberData(raw.getElement(), raw.getScore(), rankOf(raw.getElement())));
		}
		return kneaded;
	}
}
