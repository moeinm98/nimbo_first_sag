import java.io.Serializable;

public abstract class Info implements Serializable, Comparable {
    private String name;
    private int commitCommentNum;
    private int creationNum;
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

    public Info(String name) {
        this.name = name;
    }

    public void mergeInfo(Info info) {
        this.commitCommentNum += info.getCommitCommentNum();
        this.creationNum += info.getCreationNum();
        this.commitNum += info.getCommitNum();
        this.forkNum += info.getForkNum();
        this.gollumNum += info.getGollumNum();
        this.issueCommentNum += info.getIssueCommentNum();
        this.issueNum += info.getIssueNum();
        this.labelNum += info.getLabelNum();
        this.milestoneNum += info.getMilestoneNum();
        this.pageBuildNum += info.getPageBuildNum();
        this.pullRequestNum += info.getPullRequestNum();
        this.releaseNum += info.getReleaseNum();
        this.watchNum += info.getWatchNum();
    }

    public abstract int computeValue();

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

    public int getCreationNum() {
        return creationNum;
    }

    public void setCreationNum(int creationNum) {
        this.creationNum = creationNum;
    }

    @Override
    public String toString()
    {
        return name/*+" " +
                "commitComment:"+ commitCommentNum+" " +
                "creation:"+creationNum+" " +
                "fork:"+forkNum+" " +
                "issueComment:"+issueCommentNum+" " +
                "issue:"+issueNum+" " +
                "label:"+labelNum+" " +
                "milestone:"+milestoneNum+" " +
                "pageBuild:"+pageBuildNum+" " +
                "watch:"+ this.watchNum + " " +
                "release:" + this.releaseNum + " " +
                "pullRequest:" + this.pullRequestNum + " " +
                "gollum:" + this.gollumNum + " " +
                "commit:" + this.commitNum + " " +
                "finalScore:" + this.computeValue()*/;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Info)
            return this.computeValue() > ((Info) o).computeValue() ? 1 : -1;
        return 0;
    }
}
