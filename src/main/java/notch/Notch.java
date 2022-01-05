package notch;

import redis.clients.jedis.Jedis;

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
}
