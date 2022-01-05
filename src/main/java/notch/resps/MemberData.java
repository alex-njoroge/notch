package notch.resps;

/**
 * Sets and retrieves information about a single member.
 * The setting is only done internally.
 */
public class MemberData {
	private String memberName;
	private Double score;
	private Long rank;

	public MemberData(String memberName, Double score, Long rank) {
		this.memberName = memberName;
		this.score = score;
		this.rank = rank;
	}

	public String getMemberName() {
		return memberName;
	}

	public Double getScore() {
		return score;
	}

	public Long getRank() {
		return rank;
	}
}
