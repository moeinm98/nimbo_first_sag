public class UserInfo extends Info {

    final static private int COMMIT_COMMENT_PRIORITY = 1;
    final static private int CREATION_PRIORITY = 2;
    final static private int FORK_PRIORITY = 2;
    final static private int ISSUE_COMMENT_PRIORITY = 1;
    final static private int ISSUE_PRIORITY = 2;
    final static private int LABEL_PRIORITY = 2;
    final static private int MILESTONE_PRIORITY = 2;
    final static private int PAGE_BUILD_PRIORITY = 2;
    final static private int PULL_REQUEST_PRIORITY = 2;
    final static private int COMMIT_PRIORITY = 3;
    final static private int RELEASE_PRIORITY = 5;
    final static private int GOLLUM_PRIORITY = 3;
    final static private int WATCH_PRIORITY = 1;

    public UserInfo(String username) {
        super(username);
    }

    @Override
    public int computeValue() {
        int score = COMMIT_COMMENT_PRIORITY * getCommitCommentNum() + CREATION_PRIORITY * getCreationNum() + FORK_PRIORITY * getForkNum() + ISSUE_COMMENT_PRIORITY * getIssueCommentNum();
        score += ISSUE_PRIORITY * getIssueNum() + LABEL_PRIORITY * getLabelNum() + MILESTONE_PRIORITY * getMilestoneNum() + PAGE_BUILD_PRIORITY * getPageBuildNum();
        score += PULL_REQUEST_PRIORITY * getPullRequestNum() + COMMIT_PRIORITY * getCommitNum() + RELEASE_PRIORITY * getReleaseNum() + GOLLUM_PRIORITY * getGollumNum();
        score += WATCH_PRIORITY * getWatchNum();
        return score;
    }
}
