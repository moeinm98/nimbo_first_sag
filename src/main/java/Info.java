public abstract class Info {
    private String name;
    private int commitCommentNum;
    private int forkNum;
    private int issueCommentNum;
    private int issueNum;
    private int labelNum;
    private int milestoneNum;
    private int pageBuildNum;
    private int pullRequestNum;
    private int commitNum;
    private int releaseNum;
    private int gollumNum;
    private int watchNum;

    public abstract void computeValue();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCommitCommentNum() {
        return commitCommentNum;
    }

    public void setCommitCommentNum(int commitCommentNum) {
        this.commitCommentNum = commitCommentNum;
    }

    public int getForkNum() {
        return forkNum;
    }

    public void setForkNum(int forkNum) {
        this.forkNum = forkNum;
    }

    public int getIssueCommentNum() {
        return issueCommentNum;
    }

    public void setIssueCommentNum(int issueCommentNum) {
        this.issueCommentNum = issueCommentNum;
    }

    public int getIssueNum() {
        return issueNum;
    }

    public void setIssueNum(int issueNum) {
        this.issueNum = issueNum;
    }

    public int getLabelNum() {
        return labelNum;
    }

    public void setLabelNum(int labelNum) {
        this.labelNum = labelNum;
    }

    public int getMilestoneNum() {
        return milestoneNum;
    }

    public void setMilestoneNum(int milestoneNum) {
        this.milestoneNum = milestoneNum;
    }

    public int getPageBuildNum() {
        return pageBuildNum;
    }

    public void setPageBuildNum(int pageBuildNum) {
        this.pageBuildNum = pageBuildNum;
    }

    public int getPullRequestNum() {
        return pullRequestNum;
    }

    public void setPullRequestNum(int pullRequestNum) {
        this.pullRequestNum = pullRequestNum;
    }

    public int getCommitNum() {
        return commitNum;
    }

    public void setCommitNum(int commitNum) {
        this.commitNum = commitNum;
    }

    public int getReleaseNum() {
        return releaseNum;
    }

    public void setReleaseNum(int releaseNum) {
        this.releaseNum = releaseNum;
    }

    public int getGollumNum() {
        return gollumNum;
    }

    public void setGollumNum(int gollumNum) {
        this.gollumNum = gollumNum;
    }

    public int getWatchNum() {
        return watchNum;
    }

    public void setWatchNum(int watchNum) {
        this.watchNum = watchNum;
    }
}
